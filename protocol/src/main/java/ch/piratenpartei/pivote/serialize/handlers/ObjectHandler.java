package ch.piratenpartei.pivote.serialize.handlers;

import java.io.IOException;

import ch.piratenpartei.pivote.serialize.DataInput;
import ch.piratenpartei.pivote.serialize.Handler;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class ObjectHandler implements Handler {

    public ObjectHandler() {
    }

    @Override
    public Object read(DataInput input) throws IOException {
        return input.readObject();
    }
}
