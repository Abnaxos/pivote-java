package ch.piratenpartei.pivote.rpc;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;

import ch.piratenpartei.pivote.serialize.DataInput;
import ch.piratenpartei.pivote.serialize.DataOutput;
import ch.piratenpartei.pivote.serialize.SerializationContext;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ForwardingListenableFuture;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.google.common.util.concurrent.Uninterruptibles;

import ch.raffael.util.common.logging.LogUtil;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class Connection {

    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LogUtil.getLogger();

    private final SerializationContext serializationContext;
    private final String host;
    private final int port;
    private boolean connected;
    private final Map<UUID, ResponseFuture> requestMap = Maps.newConcurrentMap();
    private LinkedBlockingQueue<RpcRequest> requestQueue = new LinkedBlockingQueue<RpcRequest>();
    private Socket socket;
    private BufferedInputStream socketInputStream;
    private BufferedOutputStream socketOutputStream;

    private Thread inputThread;
    private Thread outputThread;

    private volatile boolean disconnect = false;

    public Connection(SerializationContext serializationContext, String host, int port) {
        this.serializationContext = serializationContext;
        this.host = host;
        this.port = port;
    }

    public synchronized void connect() throws IOException {
        boolean success = false;
        Preconditions.checkState(socket == null, "Already connected");
        log.info("Connecting to {}:{}", host, port);
        socket = new Socket(host, port);
        try {
            socketInputStream = new BufferedInputStream(socket.getInputStream());
            socketOutputStream = new BufferedOutputStream(socket.getOutputStream());
            inputThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    reader();
                }
            }, "Connection:" + host + ":" + port + ":reader");
            outputThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    reader();
                }
            }, "Connection:" + host + ":" + port + ":output");
            inputThread.start();
            outputThread.start();
            success = true;
        }
        finally {
            if ( !success ) {
                disconnect = true;
                log.debug("Attempting to close failed connection to {}:{}", host, port);
                try {
                    socket.close();
                }
                catch ( Exception e ) {
                    log.error("Error closing connection to {}:{}", host, port);
                }
            }
        }
    }

    public void disconnect() {
        synchronized ( this ) {
            if ( disconnect || socket == null ) {
                log.debug("Not connected to {}:{}", host, port);
                disconnect = true;
                return;
            }
            log.info("Disconnecting from {}:{}", host, port);
            disconnect = true;
        }
        boolean interrupted = false;
        // wait for all pending requests to be sent
        while ( !requestQueue.isEmpty() ) {
            synchronized ( requestQueue ) {
                try {
                    requestQueue.wait();
                }
                catch ( InterruptedException e ) {
                    interrupted = true;
                }
            }
        }
        // stop our threads
        outputThread.interrupt();
        inputThread.interrupt();
        Uninterruptibles.joinUninterruptibly(outputThread);
        Uninterruptibles.joinUninterruptibly(inputThread); // will exit on empty response map
        // close the socket
        try {
            socket.close();
        }
        catch ( Exception e ) {
            log.error("Error closing socket to " + host + ":" + port, e);
        }
        // re-interrupt if we've been interrupted while disconnecting
        if ( interrupted ) {
            Thread.currentThread().interrupt();
        }
    }

    public synchronized ListenableFuture<RpcResponse> sendRequest(RpcRequest request) {
        Preconditions.checkState(socket != null && !disconnect, "Not connected");
        request.setRequestId(UUID.randomUUID());
        ResponseFuture result = new ResponseFuture();
        requestMap.put(request.getRequestId(), result);
        requestQueue.offer(request);
        return result;
    }

    private void writer() {
        ByteBuffer sizeBuffer = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN);
        while ( !disconnect ) {
            RpcRequest request;
            try {
                request = requestQueue.take();
                synchronized ( requestQueue ) {
                    requestQueue.notifyAll();
                }
            }
            catch ( InterruptedException e ) {
                if ( disconnect ) {
                    log.debug("Writer thread exiting");
                    return;
                }
                else {
                    log.warn("Ignoring interrupt");
                    continue;
                }
            }
            try {
                UUID requestId = UUID.randomUUID();
                request.setRequestId(requestId);
                ByteArrayOutputStream requestData = new ByteArrayOutputStream();
                request.write(new DataOutput(requestData, serializationContext));
                sizeBuffer.reset();
                sizeBuffer.putInt(requestData.size());
                requestData.writeTo(socketOutputStream);
                socketOutputStream.flush();
            }
            catch ( IOException e ) {
                log.error("Error sending request {}", request, e);
                if ( !socket.isConnected() ) {
                    disconnect();
                }
            }
        }
    }

    private void reader() {
        ByteBuffer sizeBuffer = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN);
        while ( true ) {
            if ( requestMap.isEmpty() && disconnect ) {
                return;
            }
            try {
                sizeBuffer.reset();
                if ( !read(sizeBuffer.array()) ) {
                    log.info("Server disconnected");
                    disconnect();
                    return;
                }
                byte[] buf = new byte[sizeBuffer.getInt()];
                read(buf);
                DataInput data = new DataInput(new ByteArrayInputStream(buf), serializationContext);
                Object object = data.readObject();
                if ( !(object instanceof RpcResponse) ) {
                    log.error("Invalid response : {}", object);
                    disconnect();
                    requestMap.clear();
                }
                else {
                    RpcResponse response = (RpcResponse)object;
                    ResponseFuture retval = requestMap.remove(response.getRequestId());
                    if ( retval == null ) {
                        log.error("Umatched response ({}): {}", response.getRequestId(), response);
                        disconnect();
                        requestMap.clear();
                    }
                    else {
                        retval.received(response);
                    }
                }
            }
            catch ( IOException e ) {
                log.error("Error reading data from server " + host + ":" + port, e);
                disconnect();
                requestMap.clear();
            }
        }
    }

    private boolean read(byte[] dest) throws IOException {
        int count = 0;
        int c;
        while ( count < dest.length ) {
            c = socketInputStream.read(dest, count, dest.length - count);
            if ( c < 0 ) {
                if ( count > 0 ) {
                    throw new EOFException("Expected at least " + (dest.length - count) + " more bytes of data");
                }
                else {
                    return false;
                }
            }
            count += c;
        }
        return true;
    }

    private final static class ResponseFuture extends ForwardingListenableFuture.SimpleForwardingListenableFuture<RpcResponse> {
        private ResponseFuture() {
            super(SettableFuture.<RpcResponse>create());
        }
        @SuppressWarnings({ "unchecked", "ThrowableResultOfMethodCallIgnored" })
        private void received(RpcResponse response) {
            if ( response.getException() != null ) {
                ((SettableFuture)delegate()).setException(response.getException());
            }
            else {
                ((SettableFuture)delegate()).set(response);
            }
        }

    }

}
