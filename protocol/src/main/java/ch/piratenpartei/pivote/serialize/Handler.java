package ch.piratenpartei.pivote.serialize;

import java.io.IOException;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public interface Handler {

    Object read(DataInput input) throws IOException;

}
