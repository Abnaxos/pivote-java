package ch.piratenpartei.pivote.serialize.util;

import java.io.IOException;

import ch.piratenpartei.pivote.serialize.DataInput;
import ch.piratenpartei.pivote.serialize.Handler;
import ch.piratenpartei.pivote.serialize.PiVoteSerializable;
import ch.piratenpartei.pivote.serialize.SerializationContext;
import ch.piratenpartei.pivote.serialize.SerializationException;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public abstract class AbstractPiVoteSerializable implements PiVoteSerializable {

    protected Handler buildHandler(SerializationContext context) throws SerializationException {
        return new HandlerBuilder(getClass(), context).build();
    }

    protected Handler handler(SerializationContext context) throws SerializationException {
        Handler handler = context.getHandler(getClass());
        if ( handler == null ) {
            handler = buildHandler(context);
            context.setHandler(getClass(), handler);
        }
        return handler;
    }

    @Override
    public void read(DataInput input) throws IOException {
        handler(input.getContext()).read(input);
    }
}
