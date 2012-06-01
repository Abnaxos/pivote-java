package ch.piratenpartei.pivote.model;

import java.beans.PropertyChangeListener;
import java.util.UUID;

import ch.piratenpartei.pivote.serialize.types.Data;
import ch.piratenpartei.pivote.serialize.util.AbstractPiVoteSerializable;
import ch.piratenpartei.pivote.serialize.util.Serialize;
import org.joda.time.LocalDateTime;

import ch.raffael.util.beans.Observable;
import ch.raffael.util.beans.ObservableSupport;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
@Serialize({
    "signerId: guid",
    "signatureData: data",
    "validFrom: datetime",
    "validTo: datetime"
})
public class Signature extends AbstractPiVoteSerializable implements Observable {

    private final ObservableSupport observableSupport = new ObservableSupport(this);

    private UUID signerId;
    private Data signatureData;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        observableSupport.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        observableSupport.removePropertyChangeListener(listener);
    }

    public UUID getSignerId() {
        return this.signerId;
    }

    public void setSignerId(UUID signerId) {
        observableSupport.firePropertyChange("signerId", this.signerId, this.signerId = signerId);
    }

    public Data getSignatureData() {
        return this.signatureData;
    }

    public void setSignatureData(Data signatureData) {
        observableSupport.firePropertyChange("signatureData", this.signatureData, this.signatureData = signatureData);
    }

    public LocalDateTime getValidFrom() {
        return this.validFrom;
    }

    public void setValidFrom(LocalDateTime validFrom) {
        observableSupport.firePropertyChange("validFrom", this.validFrom, this.validFrom = validFrom);
    }

    public LocalDateTime getValidTo() {
        return this.validTo;
    }

    public void setValidTo(LocalDateTime validTo) {
        observableSupport.firePropertyChange("validTo", this.validTo, this.validTo = validTo);
    }

}
