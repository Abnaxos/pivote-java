package ch.piratenpartei.pivote.rpc;

import java.io.IOException;

import ch.piratenpartei.pivote.serialize.DataInput;
import ch.piratenpartei.pivote.serialize.DataOutput;
import ch.piratenpartei.pivote.serialize.util.Field;
import ch.piratenpartei.pivote.serialize.util.Serialize;

import ch.raffael.util.common.NotImplementedException;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
@Serialize({
    @Field(name = "success", type = "boolean"),
    @Field(name = "exception", type = "data")
})
public class RpcResponse extends RpcMessage {

    private boolean success = true;
    private RemoteException exception = null;

    public boolean isSuccess() {
        return this.success;
    }

    public void setSuccess(boolean success) {
        observable.firePropertyChange("success", this.success, this.success = success);
    }

    public RemoteException getException() {
        return this.exception;
    }

    public void setException(RemoteException exception) {
        observable.firePropertyChange("exception", this.exception, this.exception = exception);
    }

    private void readException(DataInput input) throws IOException {
        if ( !isSuccess() ) {
            String typeName = input.readString();
            int numErrCode = input.readInt32();
            String message = input.readString();
            setException(new RemoteException(typeName, ErrorCode.byNumeric(numErrCode), message));
        }
        else {
            setException(null);
        }
    }

    private void writeException(DataOutput output) throws IOException {
        throw new NotImplementedException();
    }

}
