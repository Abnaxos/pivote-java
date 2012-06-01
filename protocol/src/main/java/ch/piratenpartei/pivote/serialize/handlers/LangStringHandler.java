package ch.piratenpartei.pivote.serialize.handlers;

import java.io.IOException;

import ch.piratenpartei.pivote.serialize.DataInput;
import ch.piratenpartei.pivote.serialize.Handler;
import ch.piratenpartei.pivote.serialize.types.LangString;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class LangStringHandler implements Handler {

    @Override
    public Object read(DataInput input) throws IOException {
        LangString langString = new LangString();
        int count = input.readUInt32().intValue();
        for ( int i = 0; i < count; i++ ) {
            langString.set(input.readEnum(LangString.Language.class), input.readString());
        }
        return langString;
    }
}
