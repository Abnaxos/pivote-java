package ch.piratenpartei.pivote.serialize.impl;

import java.io.IOException;

import ch.piratenpartei.pivote.serialize.DataStreamFactory;
import ch.piratenpartei.pivote.serialize.streams.DataInput;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class ObjectHandler implements Handler {

    private final Class<?> expectedClass;

    public ObjectHandler(Class<?> expectedClass) {
        this.expectedClass = expectedClass;
    }

    @Override
    public Object readValue(DataStreamFactory factory, DataInput input) throws IOException {
        return input.readObject(expectedClass);
    }
}
