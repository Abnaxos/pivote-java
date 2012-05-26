package ch.piratenpartei.pivote.model;

import ch.piratenpartei.pivote.serialize.DataStreamFactory;
import ch.piratenpartei.pivote.serialize.NonSerializableBeanException;

import ch.raffael.util.beans.Property;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class CertificateAttribute extends ModelBean {

    private final Property<Name> name = new Property<Name>("name").value(Name.NONE).notNull().bound(observableSupport);
    // FIXME: value?

    public static void setupSerialization(DataStreamFactory.Builder builder) throws NonSerializableBeanException {
        builder
                .field("name");
                // FIXME: value?
    }

    public static enum Name {
        NONE, GROUP_ID, LANGUAGE
    }

}
