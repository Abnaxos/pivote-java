/*
 * Copyright 2012 Piratenpartei Schweiz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.piratenpartei.pivote.serialize.util;

import java.beans.PropertyChangeListener;
import java.io.IOException;

import ch.piratenpartei.pivote.serialize.DataInput;
import ch.piratenpartei.pivote.serialize.DataOutput;
import ch.piratenpartei.pivote.serialize.PiVoteSerializable;
import ch.piratenpartei.pivote.serialize.SerializationContext;
import ch.piratenpartei.pivote.serialize.SerializationException;
import ch.piratenpartei.pivote.serialize.Serializer;
import com.google.common.base.Objects;

import ch.raffael.util.beans.Observable;
import ch.raffael.util.beans.ObservableSupport;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public abstract class AbstractPiVoteSerializable implements PiVoteSerializable, Observable {

    protected final ObservableSupport observable = new ObservableSupport(this);

    @Override
    public String toString() {
        Objects.ToStringHelper toString = Objects.toStringHelper(this);
        toString(toString);
        return toString.toString();
    }

    protected void toString(Objects.ToStringHelper toString) {
    }

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
        handler(input.context()).read(this, input);
    }

    @Override
    public void write(DataOutput output) throws IOException {
        handler(output.context()).write(this, output);
    }
}
