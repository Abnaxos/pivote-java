package ch.piratenpartei.pivote.rpc;

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class RpcTimeoutException extends RpcException {

    public RpcTimeoutException() {
    }

    public RpcTimeoutException(String message) {
        super(message);
    }

    public RpcTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcTimeoutException(Throwable cause) {
        super(cause);
    }
}
