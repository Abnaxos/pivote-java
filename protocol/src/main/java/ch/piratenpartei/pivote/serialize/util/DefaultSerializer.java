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

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

import ch.piratenpartei.pivote.serialize.DataInput;
import ch.piratenpartei.pivote.serialize.DataOutput;
import ch.piratenpartei.pivote.serialize.Handler;
import ch.piratenpartei.pivote.serialize.SerializationException;
import ch.piratenpartei.pivote.serialize.Serializer;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class DefaultSerializer implements Serializer {

    private final List<Serializer> fields = new LinkedList<Serializer>();

    public DefaultSerializer() {
    }

    @Override
    public void read(Object target, DataInput input) throws IOException {
        for ( Serializer field : fields ) {
            field.read(target, input);
        }
    }

    @Override
    public void write(Object object, DataOutput output) throws IOException {
        for ( Serializer field : fields ) {
            field.write(object, output);
        }
    }

    public Serializer append(Handler handler, Accessor accessor) {
        fields.add(new Field(handler, accessor));
        return this;
    }

    public Serializer append(Serializer serializer) {
        fields.add(serializer);
        return this;
    }

    private static class Field implements Serializer {
        private final Handler handler;
        private final Accessor accessor;
        private Field(Handler handler, Accessor accessor) {
            this.handler = handler;
            this.accessor = accessor;
        }
        @Override
        public String toString() {
            return "Field[" +
                    "handler=" + handler +
                    ",accessor=" + accessor +
                    "]";
        }
        @Override
        public void read(Object target, DataInput input) throws IOException {
            Object value = handler.read(input);
            input.context().log().trace("Read value {} => {}", value, accessor);
            accessor.set(target, value);
        }
        @Override
        public void write(Object object, DataOutput output) throws IOException {
            Object value = accessor.get(object);
            output.context().log().trace("Writing value {} from field {}", value, accessor);
            handler.write(output, value);
        }
    }

    public static interface Accessor {
        Object get(Object target) throws SerializationException;
        void set(Object target, Object value) throws SerializationException;
    }

    public static class PropertyAccessor implements Accessor {
        private final PropertyDescriptor descriptor;

        public PropertyAccessor(PropertyDescriptor descriptor) {
            if ( descriptor.getReadMethod() == null || descriptor.getWriteMethod() == null ) {
                throw new IllegalArgumentException("Property " + descriptor.getName() + " is not readable AND writable");
            }
            this.descriptor = descriptor;
        }

        @Override
        public String toString() {
            return "PropertyAccessor[" + descriptor.getName() + "]";
        }

        @Override
        public Object get(Object target) throws SerializationException {
            try {
                return descriptor.getReadMethod().invoke(target);
            }
            catch ( InvocationTargetException e ) {
                throw new SerializationException("Cannot read property " + descriptor.getName() + " from " + target, e);
            }
            catch ( IllegalAccessException e ) {
                throw new SerializationException("Cannot read property " + descriptor.getName() + " from " + target, e);
            }
        }

        @Override
        public void set(Object target, Object value) throws SerializationException {
            if ( value != null && !descriptor.getPropertyType().isInstance(value) ) {
                throw new SerializationException("Cannot write value " + value + " to property " + descriptor.getName() + ": Incompatible types");
            }
            try {
                descriptor.getWriteMethod().invoke(target, value);
            }
            catch ( InvocationTargetException e ) {
                throw new SerializationException("Cannot write value " + value + " to property " + descriptor.getName() + " from " + target, e);
            }
            catch ( IllegalAccessException e ) {
                throw new SerializationException("Cannot write value " + value + " to property " + descriptor.getName() + " from " + target, e);
            }
        }
    }

}
