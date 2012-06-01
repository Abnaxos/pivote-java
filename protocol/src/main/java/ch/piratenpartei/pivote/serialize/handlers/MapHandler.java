package ch.piratenpartei.pivote.serialize.handlers;

import java.io.IOException;

import ch.piratenpartei.pivote.serialize.DataInput;
import ch.piratenpartei.pivote.serialize.Handler;
import com.google.common.collect.ImmutableMap;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class MapHandler implements Handler {

    private final Handler keyHandler;
    private final Handler valueHandler;

    public MapHandler(Handler keyHandler, Handler valueHandler) {
        this.keyHandler = keyHandler;
        this.valueHandler = valueHandler;
    }

    @Override
    public Object read(DataInput input) throws IOException {
        ImmutableMap.Builder<Object, Object> builder = ImmutableMap.builder();
        int size = input.readInt32();
        for ( int i = 0; i < size; i++ ) {
            builder.put(keyHandler.read(input), valueHandler.read(input));
        }
        return builder.build();
    }

}
