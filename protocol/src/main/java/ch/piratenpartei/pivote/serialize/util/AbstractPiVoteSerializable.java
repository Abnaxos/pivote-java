package ch.piratenpartei.pivote.serialize.util;

import java.beans.PropertyChangeListener;
import java.io.IOException;

import ch.piratenpartei.pivote.serialize.DataInput;
import ch.piratenpartei.pivote.serialize.DataOutput;
import ch.piratenpartei.pivote.serialize.PiVoteSerializable;
import ch.piratenpartei.pivote.serialize.SerializationContext;
import ch.piratenpartei.pivote.serialize.SerializationException;
import ch.piratenpartei.pivote.serialize.Serializer;

import ch.raffael.util.beans.Observable;
import ch.raffael.util.beans.ObservableSupport;
import ch.raffael.util.common.NotImplementedException;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public abstract class AbstractPiVoteSerializable implements PiVoteSerializable, Observable {

    protected final ObservableSupport observable = new ObservableSupport(this);

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        observable.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        observable.removePropertyChangeListener(listener);
    }

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

    @Override
    public void write(DataOutput output) throws IOException {
        throw new NotImplementedException();
    }
}
