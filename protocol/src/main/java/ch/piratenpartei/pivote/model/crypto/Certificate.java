package ch.piratenpartei.pivote.model.crypto;

import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import ch.piratenpartei.pivote.serialize.DataInput;
import ch.piratenpartei.pivote.serialize.DataOutput;
import ch.piratenpartei.pivote.serialize.types.Data;
import ch.piratenpartei.pivote.serialize.util.AbstractPiVoteSerializable;
import ch.piratenpartei.pivote.serialize.util.Field;
import ch.piratenpartei.pivote.serialize.util.Serialize;
import org.joda.time.LocalDateTime;

import ch.raffael.util.beans.Observable;
import ch.raffael.util.beans.ObservableSupport;
import ch.raffael.util.common.NotImplementedException;

import static ch.piratenpartei.pivote.model.util.CollectionProperties.*;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
@Serialize({
    @Field(name = "magic", type = "data"),
    @Field(name = "id", type = "guid"),
    @Field(name = "creationDate", type = "datetime"),
    @Field(name = "publicKey", type = "data"),
    @Field(name = "selfSignature", type = "data"),
    @Field(name = "attributes", type = "Pirate.PiVote.Crypto.CertificateAttribute[]"),
    @Field(name = "signatures", type = "Pirate.PiVote.Crypto.Signature[]"),
    @Field(name = "privateKeyStatus", type = "enum"),
    @Field(name = "privateKeyData", type = "data"),
    @Field(name = "privateKeySalt", type = "data"),
    @Field(name = "passphraseSalt", type = "data")
})
public class Certificate extends AbstractPiVoteSerializable implements Observable {

    private final ObservableSupport observable = new ObservableSupport(this);

    private Data magic;
    private UUID id;
    private LocalDateTime creationDate;
    private Data publicKey;
    private Data selfSignature;
    private List<CertificateAttribute> attributes;
    private List<Signature> signatures;
    private PrivateKeyStatus privateKeyStatus;
    private Data privateKeyData;
    private Data privateKeySalt;
    private Data passphraseSalt;

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        observable.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        observable.removePropertyChangeListener(listener);
    }

    public Data getMagic() {
        return this.magic;
    }

    public void setMagic(Data magic) {
        observable.firePropertyChange("magic", this.magic, this.magic = magic);
    }

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        observable.firePropertyChange("id", this.id, this.id = id);
    }

    public LocalDateTime getCreationDate() {
        return this.creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        observable.firePropertyChange("creationDate", this.creationDate, this.creationDate = creationDate);
    }

    public Data getPublicKey() {
        return this.publicKey;
    }

    public void setPublicKey(Data publicKey) {
        observable.firePropertyChange("publicKey", this.publicKey, this.publicKey = publicKey);
    }

    public Data getSelfSignature() {
        return this.selfSignature;
    }

    public void setSelfSignature(Data selfSignature) {
        observable.firePropertyChange("selfSignature", this.selfSignature, this.selfSignature = selfSignature);
    }

    public List<CertificateAttribute> getAttributes() {
        return this.attributes;
    }

    public void setAttributes(List<CertificateAttribute> attributes) {
        attributes = copyOf(attributes);
        observable.firePropertyChange("attributes", this.attributes, this.attributes = attributes);
    }

    public List<Signature> getSignatures() {
        return this.signatures;
    }

    public void setSignatures(List<Signature> signatures) {
        signatures = copyOf(signatures);
        observable.firePropertyChange("signatures", this.signatures, this.signatures = signatures);
    }

    public PrivateKeyStatus getPrivateKeyStatus() {
        return this.privateKeyStatus;
    }

    public void setPrivateKeyStatus(PrivateKeyStatus privateKeyStatus) {
        observable.firePropertyChange("privateKeyStatus", this.privateKeyStatus, this.privateKeyStatus = privateKeyStatus);
    }

    public Data getPrivateKeyData() {
        return this.privateKeyData;
    }

    public void setPrivateKeyData(Data privateKeyData) {
        observable.firePropertyChange("privateKeyData", this.privateKeyData, this.privateKeyData = privateKeyData);
    }

    private void readPrivateKeyData(DataInput input) throws IOException {
        if ( getPrivateKeyStatus() == PrivateKeyStatus.UNENCRYPTED || getPrivateKeyStatus() == PrivateKeyStatus.ENCRYPTED ) {
            setPrivateKeyData(input.readData());
        }
        else {
            setPrivateKeyData(null);
        }
    }

    private void writePrivateKeyData(DataOutput output) throws IOException {
        throw new NotImplementedException();
    }

    public Data getPrivateKeySalt() {
        return this.privateKeySalt;
    }

    public void setPrivateKeySalt(Data privateKeySalt) {
        observable.firePropertyChange("privateKeySalt", this.privateKeySalt, this.privateKeySalt = privateKeySalt);
    }

    private void readPrivateKeySalt(DataInput input) throws IOException {
        if ( getPrivateKeyStatus() == PrivateKeyStatus.ENCRYPTED ) {
            setPrivateKeySalt(input.readData());
        }
        else {
            setPrivateKeySalt(null);
        }
    }

    private void writePrivateKeySalt(DataOutput output) throws IOException {
        throw new NotImplementedException();
    }

    public Data getPassphraseSalt() {
        return this.passphraseSalt;
    }

    public void setPassphraseSalt(Data passphraseSalt) {
        observable.firePropertyChange("passphraseSalt", this.passphraseSalt, this.passphraseSalt = passphraseSalt);
    }

    private void readPassphraseSalt(DataInput input) throws IOException {
        if ( getPrivateKeyStatus() == PrivateKeyStatus.ENCRYPTED ) {
            setPassphraseSalt(input.readData());
        }
        else {
            setPassphraseSalt(null);
        }
    }

    private void writePassphraseSalt(DataOutput output) throws IOException {
        throw new NotImplementedException();
    }

}
