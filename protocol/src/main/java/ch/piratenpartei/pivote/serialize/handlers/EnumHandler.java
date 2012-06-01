package ch.piratenpartei.pivote.serialize.handlers;

import java.io.IOException;

import ch.piratenpartei.pivote.serialize.DataInput;
import ch.piratenpartei.pivote.serialize.Handler;


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
    public Object read(DataInput input) throws IOException {
        return input.readEnum(enumClass);
    }
}
