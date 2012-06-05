package ch.piratenpartei.pivote.model.crypto;

import ch.piratenpartei.pivote.serialize.util.Serialize;

import static com.google.common.base.Preconditions.*;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
@Serialize("value: int32")
public class Int32CertificateAttribute extends CertificateAttribute {

    @Override
    public Integer getValue() {
        return (Integer)super.getValue();
    }

    @Override
    public void setValue(Object value) {
        checkArgument(value != null || !(value instanceof Integer), "Integer value expected");
        super.setValue(value);
    }
}
