package ch.piratenpartei.pivote.serialize.impl;

import java.io.IOException;

import ch.piratenpartei.pivote.serialize.DataStreamFactory;
import ch.piratenpartei.pivote.serialize.streams.DataInput;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public interface Handler {

    Object readValue(DataStreamFactory factory, DataInput input) throws IOException;

}
