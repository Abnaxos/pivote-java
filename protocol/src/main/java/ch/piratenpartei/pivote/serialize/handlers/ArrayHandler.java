package ch.piratenpartei.pivote.serialize.handlers;

import java.io.IOException;
import java.lang.reflect.Array;

import ch.piratenpartei.pivote.serialize.DataInput;
import ch.piratenpartei.pivote.serialize.Handler;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class ArrayHandler implements Handler {

    private final Class<?> elementType;
    private final Handler elementHandler;

    public ArrayHandler(Class<?> elementType, Handler elementHandler) {
        this.elementType = elementType;
        this.elementHandler = elementHandler;
    }

    @Override
    public Object read(DataInput input) throws IOException {
        int size = input.readInt32();
        Object array = Array.newInstance(elementType, size);
        for ( int i = 0; i < size; i++ ) {
            Array.set(array, i, elementHandler.read(input));
        }
        return array;
    }
}
