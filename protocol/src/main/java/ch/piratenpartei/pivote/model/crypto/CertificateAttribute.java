package ch.piratenpartei.pivote.model.crypto;

import ch.piratenpartei.pivote.serialize.util.AbstractPiVoteSerializable;
import ch.piratenpartei.pivote.serialize.util.Field;
import ch.piratenpartei.pivote.serialize.util.Serialize;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
@Serialize(@Field(name = "name", type = "enum"))
public abstract class CertificateAttribute extends AbstractPiVoteSerializable {

    private Name name;
    private Object value;

    public Name getName() {
        return this.name;
    }

    public void setName(Name name) {
        observable.firePropertyChange("name", this.name, this.name = name);
    }

    public Object getValue() {
        return this.value;
    }

    public void setValue(Object value) {
        observable.firePropertyChange("value", this.value, this.value = value);
    }

    public static enum Name {
        NONE, GROUP_ID, LANGUAGE
    }

}
