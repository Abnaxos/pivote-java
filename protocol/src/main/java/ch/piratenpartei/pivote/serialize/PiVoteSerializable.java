package ch.piratenpartei.pivote.serialize;

import java.io.IOException;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public interface PiVoteSerializable {

    void read(DataInput input) throws IOException;

}
