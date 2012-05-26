package ch.piratenpartei.pivote.serialize.impl;

import java.io.IOException;

import ch.piratenpartei.pivote.serialize.DataStreamFactory;
import ch.piratenpartei.pivote.serialize.streams.DataInput;
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
    public Object readValue(DataStreamFactory factory, DataInput input) throws IOException {
        ImmutableList.Builder<Object> builder = ImmutableList.builder();
        int size = input.readInt32();
        for ( int i = 0; i < size; i++ ) {
            builder.add(elementHandler.readValue(factory, input));
        }
        return builder.build();
    }

}
