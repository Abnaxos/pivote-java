package ch.piratenpartei.pivote.rpc;

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class RemoteException extends RpcException {

    private final Code code;

    public RemoteException(Code code, String message) {
        super(message);
        this.code = code;
    }

    public Code getCode() {
        return code;
    }

    public static enum Code {

    }
}
