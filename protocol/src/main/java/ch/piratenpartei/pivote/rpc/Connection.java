package ch.piratenpartei.pivote.rpc;

import java.io.IOException;
import java.net.Socket;

import org.slf4j.Logger;

import ch.raffael.util.common.logging.LogUtil;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class Connection {

    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LogUtil.getLogger();

    private final String host;
    private final int port;
    private boolean connected;
    private Socket socket;
    private Thread communicationThread;

    public Connection(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public synchronized void connect() throws IOException {
    }

    public synchronized void disconnect() {
    }

}
