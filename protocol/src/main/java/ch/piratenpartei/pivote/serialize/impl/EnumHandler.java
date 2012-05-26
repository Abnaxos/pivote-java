package ch.piratenpartei.pivote.serialize.impl;

import java.io.IOException;

import ch.piratenpartei.pivote.serialize.DataStreamFactory;
import ch.piratenpartei.pivote.serialize.streams.DataInput;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class EnumHandler implements Handler {

    private final Class<? extends Enum> enumClass;
    private final Enum[] values;

    public EnumHandler(Class<? extends Enum> enumClass) {
        this.enumClass = enumClass;
        values = enumClass.getEnumConstants();
    }

    @Override
    public Object readValue(DataStreamFactory factory, DataInput input) throws IOException {
        int ordinal = input.readInt32();
        if ( ordinal < 0 || ordinal > values.length ) {
            throw new IOException("Invalid value for enum " + enumClass.getName() + ": " + ordinal);
        }
        return values[ordinal];
    }
}
