package ch.piratenpartei.pivote.serialize.handlers;

import java.io.IOException;

import ch.piratenpartei.pivote.serialize.DataInput;
import ch.piratenpartei.pivote.serialize.Handler;
import ch.piratenpartei.pivote.serialize.PiVoteSerializable;
import ch.piratenpartei.pivote.serialize.SerializationException;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class ObjectHandler implements Handler {

    protected final Class<?> expectedClass;

    public ObjectHandler(Class<?> expectedClass) {
        this.expectedClass = expectedClass;
    }

    protected PiVoteSerializable newInstance() throws SerializationException {
        try {
            return (PiVoteSerializable)expectedClass.newInstance();
        }
        catch ( InstantiationException e ) {
            throw new SerializationException("Cannot create object of class " + expectedClass.getName(), e);
        }
        catch ( IllegalAccessException e ) {
            throw new SerializationException("Cannot create object of class " + expectedClass.getName(), e);
        }
    }

    @Override
    public Object read(DataInput input) throws IOException {
        PiVoteSerializable target = newInstance();
        target.read(input);
        return target;
    }
}
