package ch.piratenpartei.pivote.rpc;

import java.util.UUID;

import ch.piratenpartei.pivote.serialize.util.AbstractPiVoteSerializable;
import ch.piratenpartei.pivote.serialize.util.Field;
import ch.piratenpartei.pivote.serialize.util.Serialize;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
@Serialize({
    @Field(name = "requestId", type = "guid")
})
public class RpcMessage extends AbstractPiVoteSerializable  {

    private UUID requestId = null;

    public UUID getRequestId() {
        return this.requestId;
    }

    public void setRequestId(UUID requestId) {
        observable.firePropertyChange("requestId", this.requestId, this.requestId = requestId);
    }


}
