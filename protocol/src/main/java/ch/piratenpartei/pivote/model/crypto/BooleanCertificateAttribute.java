package ch.piratenpartei.pivote.model.crypto;

import ch.piratenpartei.pivote.serialize.util.Serialize;

import static com.google.common.base.Preconditions.*;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
@Serialize("value: bool")
public class BooleanCertificateAttribute extends CertificateAttribute {

    @Override
    public Boolean getValue() {
        return (Boolean)super.getValue();
    }

    @Override
    public void setValue(Object value) {
        checkArgument(value == null || value instanceof Boolean, "Boolean value expected");
        super.setValue(value);
    }

}
