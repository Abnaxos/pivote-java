package ch.piratenpartei.pivote.model;

import java.util.UUID;

import ch.piratenpartei.pivote.serialize.Data;
import ch.piratenpartei.pivote.serialize.DataStreamFactory;
import ch.piratenpartei.pivote.serialize.NonSerializableBeanException;
import org.joda.time.LocalDateTime;

import ch.raffael.util.beans.Property;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class Signature extends ModelBean {

    private final Property<UUID> signerId = new Property<UUID>("signerId").bound(observableSupport);
    private final Property<Data> signatureData = new Property<Data>("signatureData").bound(observableSupport);
    private final Property<LocalDateTime> validFrom = new Property<LocalDateTime>("validFrom").bound(observableSupport);
    private final Property<LocalDateTime> validUntil = new Property<LocalDateTime>("validUntil").bound(observableSupport);

    public UUID getSignerId() {
        return signerId.get();
    }

    public void setSignerId(UUID signerId) {
        this.signerId.set(signerId);
    }

    public Data getSignatureData() {
        return signatureData.get();
    }

    public void setSignatureData(Data signatureData) {
        this.signatureData.set(signatureData);
    }

    public LocalDateTime getValidFrom() {
        return validFrom.get();
    }

    public void setValidFrom(LocalDateTime validFrom) {
        this.validFrom.set(validFrom);
    }

    public LocalDateTime getValidUntil() {
        return validUntil.get();
    }

    public void setValidUntil(LocalDateTime validUntil) {
        this.validUntil.set(validUntil);
    }

    public static void setupSerialization(DataStreamFactory.Builder builder) throws NonSerializableBeanException {
        builder
                .field("signerId")
                .field("signatureData")
                .field("validFrom")
                .field("validUntil");
    }

}
