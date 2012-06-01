package ch.piratenpartei.pivote.serialize.handlers;

import java.io.IOException;

import ch.piratenpartei.pivote.serialize.DataInput;
import ch.piratenpartei.pivote.serialize.Handler;
import com.google.common.collect.ImmutableList;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class ListHandler implements Handler {

    private final Handler elementHandler;

    public ListHandler(Handler elementHandler) {
        this.elementHandler = elementHandler;
    }

    @Override
    public Object read(DataInput input) throws IOException {
        ImmutableList.Builder<Object> builder = ImmutableList.builder();
        int size = input.readInt32();
        for ( int i = 0; i < size; i++ ) {
            builder.add(elementHandler.read(input));
        }
        return builder.build();
    }

}
