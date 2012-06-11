package ch.piratenpartei.pivote.model.crypto;

import java.beans.PropertyChangeListener;

import ch.piratenpartei.pivote.serialize.util.AbstractPiVoteSerializable;
import ch.piratenpartei.pivote.serialize.util.Field;
import ch.piratenpartei.pivote.serialize.util.Serialize;

import ch.raffael.util.beans.Observable;
import ch.raffael.util.beans.ObservableSupport;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
@Serialize(@Field(name = "name", type = "enum"))
public abstract class CertificateAttribute extends AbstractPiVoteSerializable implements Observable {

    private final ObservableSupport observable = new ObservableSupport(this);

    private Name name;
    private Object value;

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        observable.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        observable.removePropertyChangeListener(listener);
    }

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

    //@Override
    //public void read(DataInput input) throws IOException {
    //    setName(input.readEnum(Name.class));
    //    switch ( getName() ) {
    //        case NONE:
    //            setValue(null);
    //            break;
    //        case GROUP_ID:
    //            setValue(input.readString()); // FIXME: string?
    //            break;
    //        case LANGUAGE:
    //            setValue(input.readEnum(LangString.Language.class));
    //            break;
    //        default:
    //            throw new UnreachableCodeException();
    //    }
    //}

    public static enum Name {
        NONE, GROUP_ID, LANGUAGE
    }

}
