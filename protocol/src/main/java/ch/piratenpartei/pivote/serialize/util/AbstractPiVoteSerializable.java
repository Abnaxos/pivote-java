package ch.piratenpartei.pivote.serialize.util;

import java.io.IOException;

import ch.piratenpartei.pivote.serialize.DataInput;
import ch.piratenpartei.pivote.serialize.PiVoteSerializable;
import ch.piratenpartei.pivote.serialize.SerializationContext;
import ch.piratenpartei.pivote.serialize.SerializationException;
import ch.piratenpartei.pivote.serialize.Serializer;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public abstract class AbstractPiVoteSerializable implements PiVoteSerializable {

    protected Serializer buildSerializer(SerializationContext context) throws SerializationException {
        return new SerializerBuilder(getClass(), context).build();
    }

    protected Serializer handler(SerializationContext context) throws SerializationException {
        Serializer serializer = context.getSerializer(getClass());
        if ( serializer == null ) {
            serializer = buildSerializer(context);
            context.setSerializer(getClass(), serializer);
        }
        return serializer;
    }

    @Override
    public void read(DataInput input) throws IOException {
        handler(input.getContext()).read(this, input);
    }
}
