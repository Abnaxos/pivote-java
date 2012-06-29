/*
 * Copyright 2012 Piratenpartei Schweiz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.piratenpartei.pivote.rpc;

import java.beans.PropertyChangeListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

import ch.piratenpartei.pivote.rpc.msg.KeepAliveRequest;
import ch.piratenpartei.pivote.serialize.DataInput;
import ch.piratenpartei.pivote.serialize.DataOutput;
import ch.piratenpartei.pivote.serialize.SerializationContext;
import com.google.common.base.Preconditions;
import com.google.common.net.HostAndPort;
import com.google.common.util.concurrent.ForwardingListenableFuture;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.Uninterruptibles;

import ch.raffael.util.beans.EventEmitter;
import ch.raffael.util.beans.Observable;
import ch.raffael.util.beans.ObservableSupport;
import ch.raffael.util.common.logging.LogUtil;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class Connection implements Observable {

    public static final String PROPERTY_STATE = "state";
    public static final String PROPERTY_READ_TIMEOUT_MILLIS = "readTimeoutMillis";
    public static final String PROPERTY_KEEP_ALIVE_FREQUENCE_MILLIS = "keepAliveFrequenceMillis";

    public static final int DEFAULT_PORT = 4242;

    @SuppressWarnings("UnusedDeclaration")
    private final Logger log = LogUtil.getLogger();

    private final ObservableSupport observableSupport = new ObservableSupport(this);
    private final EventEmitter<ConnectionListener> connectionEvents = EventEmitter.newEmitter(ConnectionListener.class);

    private final SerializationContext serializationContext;
    private final HostAndPort piVoteServer;

    private int readTimeoutMillis = (int)TimeUnit.MILLISECONDS.convert(30, TimeUnit.SECONDS);
    private int keepAliveFrequencyMillis = (int)TimeUnit.MILLISECONDS.convert(10, TimeUnit.SECONDS);

    private Socket socket;
    private OutputStream socketOut;
    private InputStream socketIn;

    private LinkedBlockingQueue<RpcRequest> requestQueue = new LinkedBlockingQueue<RpcRequest>();
    private volatile Thread connectionThread;
    private final Object interruptLock = new Object();

    private State state = State.DISCONNECTED;
    private final Object stateLock = new Object();

    public Connection(SerializationContext serializationContext, HostAndPort piVoteServer) {
        //log = new EnhancedLogger(LogUtil.getLogger(), new Function<String, String>() {
        //    @Override
        //    public String apply(@Nullable String input) {
        //        return "Connection:" + Connection.this.piVoteServer + ": " + input;
        //    }
        //});
        this.serializationContext = serializationContext.withLogger(log);
        this.piVoteServer = piVoteServer;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        observableSupport.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        observableSupport.removePropertyChangeListener(listener);
    }

    public void addConnectionListener(ConnectionListener listener) {
        connectionEvents.addListener(listener);
    }

    public void removeConnectionListener(ConnectionListener listener) {
        connectionEvents.removeListener(listener);
    }

    public HostAndPort getPiVoteServer() {
        return piVoteServer;
    }

    public synchronized long getReadTimeoutMillis() {
        return readTimeoutMillis;
    }

    public synchronized void setReadTimeoutMillis(int readTimeoutMillis) {
        if ( socket != null ) {
            return;
        }
        observableSupport.firePropertyChange(PROPERTY_READ_TIMEOUT_MILLIS, this.readTimeoutMillis, this.readTimeoutMillis = readTimeoutMillis);
    }

    public synchronized int getKeepAliveFrequencyMillis() {
        return keepAliveFrequencyMillis;
    }

    public synchronized void setKeepAliveFrequencyMillis(int keepAliveFrequence) {
        if ( socket != null ) {
            return;
        }
        observableSupport.firePropertyChange(PROPERTY_KEEP_ALIVE_FREQUENCE_MILLIS, this.keepAliveFrequencyMillis, this.keepAliveFrequencyMillis = keepAliveFrequence);
    }

    private void setState(State state) {
        synchronized ( stateLock ) {
            observableSupport.firePropertyChange(PROPERTY_STATE, this.state, this.state = state);
        }
    }

    public State getState() {
        synchronized ( stateLock ) {
            return state;
        }
    }

    public synchronized ListenableFuture<RpcResponse> connect() throws IOException {
        Preconditions.checkState(getState() == State.DISCONNECTED, "Already connected");
        setState(State.CONNECTING);
        log.info("Connecting to {}", piVoteServer);
        socket = new Socket(piVoteServer.getHostText(), piVoteServer.getPortOrDefault(DEFAULT_PORT));
        try {
            socket.setSoTimeout(readTimeoutMillis);
            socketOut = new BufferedOutputStream(socket.getOutputStream());
            socketIn = new BufferedInputStream(socket.getInputStream());
        }
        catch ( Exception e ) {
            log.debug("Attempting to close failed connection to {}", piVoteServer);
            try {
                socket.close();
            }
            catch ( Exception ce ) {
                log.error("Error closing connection to {}", piVoteServer, ce);
            }
        }
        connectionThread = new Thread(new Runnable() {
            @Override
            public void run() {
                connectionLoop();
            }
        }, "Connection to " + piVoteServer);
        connectionThread.start();
        RpcRequest handshake = new KeepAliveRequest();
        handshake.setRequestId(UUID.randomUUID());
        log.debug("Enqueueing handshake request: {}", handshake);
        requestQueue.offer(handshake);
        return wrap(handshake.getReceiver());
    }

    public synchronized void disconnect() {
        synchronized ( stateLock ) {
            if ( state == State.CONNECTED || state == State.CONNECTING ) {
                setState(State.DISCONNECTING);
            }
        }
        synchronized ( interruptLock ) {
            Thread t = connectionThread;
            if ( t != null ) {
                t.interrupt();
                Uninterruptibles.joinUninterruptibly(t);
            }
        }
    }

    public synchronized boolean isConnected() {
        return socket != null && socket.isConnected();
    }

    public synchronized ListenableFuture<RpcResponse> sendRequest(RpcRequest request) {
        synchronized ( stateLock ) {
            Preconditions.checkState(state == State.CONNECTED || state == State.CONNECTING, "Invalid connection state: %s", state);
        }
        request.setRequestId(UUID.randomUUID());
        log.debug("Enqueueing request: {}", request);
        requestQueue.offer(request);
        log.trace("Current request queue size: {}", requestQueue.size());
        return wrap(request.getReceiver());
    }

    private ListenableFuture<RpcResponse> wrap(ListenableFuture<RpcResponse> future) {
        return new ForwardingListenableFuture.SimpleForwardingListenableFuture<RpcResponse>(future) {
        };
    }

    private boolean read(byte[] dest) throws IOException {
        int count = 0;
        int c;
        while ( count < dest.length ) {
            c = socketIn.read(dest, count, dest.length - count);
            if ( c < 0 ) {
                //if ( count > 0 ) {
                throw new EOFException("Expected at least " + (dest.length - count) + " more bytes of data");
                //}
                //else {
                //    return false;
                //}
            }
            connectionEvents.emitter().receivedData(new ConnectionEvent(this, c));
            count += c;
        }
        return true;
    }

    private void connectionLoop() {
        RpcRequest request = null;
        try {
            ByteBuffer sizeBuffer = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN);
            long nextKeepAlive = System.currentTimeMillis() + keepAliveFrequencyMillis;
            setState(State.CONNECTED);
            log.info("Successfully connected to {}", piVoteServer);
            while ( getState() == State.CONNECTED ) {
                request = null;
                try {
                    if ( log.isTraceEnabled() ) {
                        log.trace("Next keep-alive: {}", new Date(nextKeepAlive));
                    }
                    request = requestQueue.poll(nextKeepAlive - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
                }
                catch ( InterruptedException e ) {
                    continue;
                }
                synchronized ( interruptLock ) {
                    if ( Thread.interrupted() ) {
                        continue;
                    }
                    if ( request == null ) {
                        request = new KeepAliveRequest();
                        request.setRequestId(UUID.randomUUID());
                        log.debug("Sending keep-alive: {}", request);
                    }
                    else {
                        log.debug("Sending request: {}", request);
                    }
                    ByteArrayOutputStream requestData = new ByteArrayOutputStream();
                    DataOutput dataOut = new DataOutput(requestData, serializationContext);
                    dataOut.writeObject(request);
                    dataOut.close();
                    ConnectionEvent sendEvent = new ConnectionEvent(this, requestData.size());
                    connectionEvents.emitter().sendingData(sendEvent);
                    log.trace("Sending request data ({} bytes)", requestData.size());
                    sizeBuffer.rewind();
                    sizeBuffer.putInt(requestData.size());
                    socketOut.write(sizeBuffer.array());
                    requestData.writeTo(socketOut);
                    socketOut.flush();
                    connectionEvents.emitter().sentData(new ConnectionEvent(this, requestData.size()));
                    log.trace("Awaiting response");
                    sizeBuffer.rewind();
                    connectionEvents.emitter().receivingData(new ConnectionEvent(this, 4));
                    read(sizeBuffer.array());
                    byte[] responseBytes = new byte[sizeBuffer.getInt()];
                    connectionEvents.emitter().receivingData(new ConnectionEvent(this, responseBytes.length));
                    log.trace("Receiving response data ({} bytes)", responseBytes.length);
                    read(responseBytes);
                    DataInput dataInput = new DataInput(new ByteArrayInputStream(responseBytes), serializationContext);
                    Object object = dataInput.readObject();
                    if ( !(object instanceof RpcResponse) ) {
                        throw new RpcException("Received invalid response object: " + object.getClass().getName());
                    }
                    RpcResponse response = (RpcResponse)object;
                    if ( !request.getRequestId().equals(response.getRequestId()) ) {
                        throw new RpcException("Mismatched request ID on response: " + response.getRequestId() + " (expected " + request.getRequestId() + ")");
                    }
                    log.debug("Received RpcResponse {}", response);
                    request.getReceiver().set(response);
                }
                nextKeepAlive = System.currentTimeMillis() + keepAliveFrequencyMillis;
            }
        }
        catch ( Throwable e ) {
            log.error("Connection error, disconnecting", e);
            setState(State.DISCONNECTING);
            if ( request != null ) {
                request.getReceiver().setException(e);
            }
        }
        finally {
            try {
                synchronized ( interruptLock ) {
                    Thread.interrupted(); // clear interrupted flag if set
                    log.info("Disconnecting from {}", piVoteServer);
                    setState(State.DISCONNECTING);
                    while ( !requestQueue.isEmpty() ) {
                        log.debug("Cleaning up pending request: {}", request);
                        requestQueue.poll().getReceiver().setException(new RpcException(piVoteServer + " disconnected"));
                    }
                    connectionThread = null;
                    try {
                        socket.close();
                    }
                    catch ( Exception e ) {
                        log.error("Error closing socket to {}", piVoteServer, e);
                    }
                }
            }
            finally {
                socket = null;
                socketIn = null;
                socketOut = null;
                setState(State.DISCONNECTED);
            }
        }
    }

    public static enum State {
        DISCONNECTED, CONNECTING, CONNECTED, DISCONNECTING
    }

}
