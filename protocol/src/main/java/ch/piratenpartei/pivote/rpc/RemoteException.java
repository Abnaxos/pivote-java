package ch.piratenpartei.pivote.rpc;

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class RemoteException extends RpcException {

    private final String typeName;
    private final ErrorCode code;

    public RemoteException(String typeName, ErrorCode code, String message) {
        super(typeName+": "+code+": "+message);
        this.typeName = typeName;
        this.code = code;
    }

    @Override
    public String toString() {
        return getClass().getName() + ":" + typeName + ":" + code + ": " + getMessage();
    }

    public String getTypeName() {
        return typeName;
    }

    public ErrorCode getCode() {
        return code;
    }

}
