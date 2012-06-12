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
package ch.piratenpartei.pivote.model.crypto;

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
public class Certificate extends AbstractPiVoteSerializable{

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
        input.getContext().log().trace("Read privateKeyData: {}", getPrivateKeyData());
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
        input.getContext().log().trace("Read privateKeySalt: {}", getPrivateKeySalt());
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
        input.getContext().log().trace("Read passphraseSalt: {}", getPassphraseSalt());
    }

    private void writePassphraseSalt(DataOutput output) throws IOException {
        throw new NotImplementedException();
    }

}
