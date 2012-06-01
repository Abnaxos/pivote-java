package ch.piratenpartei.pivote.serialize.handlers;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

import ch.piratenpartei.pivote.serialize.DataInput;
import ch.piratenpartei.pivote.serialize.Handler;
import ch.piratenpartei.pivote.serialize.SerializationException;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class CompoundHandler extends ObjectHandler implements Handler {

    private final List<Field> fields = new LinkedList<Field>();

    public CompoundHandler(Class<?> expectedClass) {
        super(expectedClass);
    }

    @Override
    public Object read(DataInput input) throws IOException {
        Object target = newInstance();
        for ( Field field : fields ) {
            field.accessor.set(target, field.handler.read(input));
        }
        return target;
    }

    public CompoundHandler append(Handler handler, Accessor accessor) {
        fields.add(new Field(handler, accessor));
        return this;
    }

    private static class Field {
        private final Handler handler;
        private final Accessor accessor;
        private Field(Handler handler, Accessor accessor) {
            this.handler = handler;
            this.accessor = accessor;
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
