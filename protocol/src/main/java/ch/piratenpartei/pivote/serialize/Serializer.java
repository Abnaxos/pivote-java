package ch.piratenpartei.pivote.serialize;

import java.io.IOException;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public interface Serializer {

    void read(Object target, DataInput input) throws IOException;
}
