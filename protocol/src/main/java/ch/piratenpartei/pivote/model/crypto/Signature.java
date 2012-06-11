package ch.piratenpartei.pivote.model.crypto;

import java.util.UUID;

import ch.piratenpartei.pivote.serialize.types.Data;
import ch.piratenpartei.pivote.serialize.util.AbstractPiVoteSerializable;
import ch.piratenpartei.pivote.serialize.util.Field;
import ch.piratenpartei.pivote.serialize.util.Serialize;
import org.joda.time.LocalDateTime;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
@Serialize({
    @Field(name = "signerId", type = "guid"),
    @Field(name = "signatureData", type = "data"),
    @Field(name = "validFrom", type = "datetime"),
    @Field(name = "validTo", type = "datetime")
})
public class Signature extends AbstractPiVoteSerializable {

    private UUID signerId;
    private Data signatureData;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;

    public UUID getSignerId() {
        return this.signerId;
    }

    public void setSignerId(UUID signerId) {
        observable.firePropertyChange("signerId", this.signerId, this.signerId = signerId);
    }

    public Data getSignatureData() {
        return this.signatureData;
    }

    public void setSignatureData(Data signatureData) {
        observable.firePropertyChange("signatureData", this.signatureData, this.signatureData = signatureData);
    }

    public LocalDateTime getValidFrom() {
        return this.validFrom;
    }

    public void setValidFrom(LocalDateTime validFrom) {
        observable.firePropertyChange("validFrom", this.validFrom, this.validFrom = validFrom);
    }

    public LocalDateTime getValidTo() {
        return this.validTo;
    }

    public void setValidTo(LocalDateTime validTo) {
        observable.firePropertyChange("validTo", this.validTo, this.validTo = validTo);
    }

}
