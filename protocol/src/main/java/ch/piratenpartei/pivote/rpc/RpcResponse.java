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

import java.io.IOException;

import ch.piratenpartei.pivote.serialize.DataInput;
import ch.piratenpartei.pivote.serialize.DataOutput;
import ch.piratenpartei.pivote.serialize.util.Field;
import ch.piratenpartei.pivote.serialize.util.Serialize;
import com.google.common.base.Objects;

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

    @Override
    protected void toString(Objects.ToStringHelper toString) {
        super.toString(toString);
        if ( success ) {
            toString.addValue("success");
        }
        else {
            toString.add("exception", exception);
        }
    }

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
            RemoteException exception = new RemoteException(typeName, ErrorCode.byNumeric(numErrCode), message);
            input.context().log().trace("Read exception: {}", exception);
            setException(exception);
        }
        else {
            setException(null);
        }
    }

    private void writeException(DataOutput output) throws IOException {
        throw new NotImplementedException();
    }

}
