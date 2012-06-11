package ch.piratenpartei.pivote.serialize.util;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import ch.piratenpartei.pivote.serialize.DataInput;
import ch.piratenpartei.pivote.serialize.DataOutput;
import ch.piratenpartei.pivote.serialize.Handler;
import ch.piratenpartei.pivote.serialize.PiVoteSerializable;
import ch.piratenpartei.pivote.serialize.SerializationContext;
import ch.piratenpartei.pivote.serialize.SerializationException;
import ch.piratenpartei.pivote.serialize.Serializer;
import ch.piratenpartei.pivote.serialize.Type;
import ch.piratenpartei.pivote.serialize.handlers.EnumHandler;
import ch.piratenpartei.pivote.serialize.handlers.ListHandler;
import ch.piratenpartei.pivote.serialize.handlers.MapHandler;
import ch.piratenpartei.pivote.serialize.handlers.ObjectHandler;
import com.google.common.base.Throwables;
import com.google.common.primitives.Primitives;

import ch.raffael.util.beans.BeanException;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class SerializerBuilder {

    /**
     * Groups:
     * <ul>
     *     <li>1: property name</li>
     *     <li>2: type (key type if map)</li>
     *     <li>4: list/map modifier ("[]" if list, map otherwise)</li>
     *     <li>5: value type if map</li>
     * </ul>
     */
    private static Pattern SYNTAX = Pattern.compile(
            "([a-z_][a-z0-9_]*)\\s*:\\s*([a-z_][a-z0-9_]*(\\.[a-z_][a-z0-9_]*)*)\\s*" +
                    "(->\\s*([a-z_][a-z0-9_]*(\\.[a-z_][a-z0-9_]*)*)|\\[\\])?",
            Pattern.CASE_INSENSITIVE);

    private final Class<? extends PiVoteSerializable> targetClass;
    private final SerializationContext context;

    public SerializerBuilder(Class<? extends PiVoteSerializable> targetClass, SerializationContext context) {
        if ( targetClass.isInterface() || targetClass.isAnnotation() || targetClass.isArray() || targetClass.isEnum() ) {
            throw new IllegalArgumentException(targetClass + " is not a class");
        }
        this.targetClass = targetClass;
        this.context = context;
    }

    public Serializer build() throws SerializationException {
        List<Definition> definitions = collectDefinitions(new LinkedList<Definition>(), targetClass);
        Map<String, PropertyDescriptor> properties = getProperties(targetClass);
        DefaultSerializer serializer = new DefaultSerializer();
        for ( Definition def : definitions ) {
            PropertyDescriptor property = properties.get(def.name);
            if ( property == null ) {
                throw new IllegalArgumentException(targetClass + ": No such property: " + def.name);
            }
            if ( def.reader != null ) {
                assert def.writer != null;
                serializer.append(new CustomSerializer(def.reader, def.writer));
            }
            else if ( def.isList ) {
                if ( !property.getPropertyType().isAssignableFrom(List.class) ) {
                    throw new IllegalArgumentException(targetClass + ": Type " + List.class + " is not applicable to Java type " + property.getPropertyType());
                }
                serializer.append(new ListHandler(handler(def.type, property)),
                                  new DefaultSerializer.PropertyAccessor(property));
            }
            else if ( def.isMap ) {
                if ( !property.getPropertyType().isAssignableFrom(Map.class) ) {
                    throw new IllegalArgumentException(targetClass + ": Type " + Map.class + " is not applicable to Java type " + property.getPropertyType());
                }
                serializer.append(new MapHandler(handler(def.type, property),
                                                 handler(def.valueType, property)),
                                  new DefaultSerializer.PropertyAccessor(property));
            }
            else {
                serializer.append(handler(def.type, property), new DefaultSerializer.PropertyAccessor(property));
            }
        }
        return serializer;
    }

    @SuppressWarnings("unchecked")
    private Handler handler(String type, PropertyDescriptor property) throws SerializationException {
        Type t = Type.forName(type);
        if ( t == null ) {
            Class javaClass = context.getJavaClass(type);
            if ( javaClass == null ) {
                throw new SerializationException(targetClass.getName() + "::" + property.getName() + ": Unknown protocol class " + type);
            }
            //else if ( !property.getPropertyType().isAssignableFrom(javaClass) ) {
            //    throw new SerializationException(targetClass.getName() + "::" + property.getName() + ": Incompatible types: " + javaClass + " <-> " + property.getPropertyType());
            //}
            return new ObjectHandler();
        }
        else if ( t == Type.ENUM ) {
            if ( !property.getPropertyType().isEnum() ) {
                throw new SerializationException(targetClass + "::" + "." + property.getName() + ": " + property.getPropertyType() + " is not not an enum");
            }
            return new EnumHandler((Class<? extends Enum>)property.getPropertyType());
        }
        else {
            Class<?> propertyType = Primitives.wrap(property.getPropertyType());
            if ( !propertyType.isAssignableFrom(t.getJavaType()) ) {
                throw new SerializationException(targetClass + "::" + property.getName() + ": Property type " + property.getPropertyType() + " is incompatible with " + t);
            }
            return t.handler();
        }
    }

    private static List<Definition> collectDefinitions(List<Definition> target, final Class<?> type) throws SerializationException {
        if ( type == Object.class ) {
            return target;
        }
        collectDefinitions(target, type.getSuperclass());
        Serialize annotation = type.getAnnotation(Serialize.class);
        if ( annotation != null ) {
            for ( Field f : annotation.value() ) {
                target.add(new Definition(type, f));
            }
        }
        return target;
    }

    private static Map<String, PropertyDescriptor> getProperties(Class<?> type) {
        PropertyDescriptor[] properties;
        try {
            properties = Introspector.getBeanInfo(type).getPropertyDescriptors();
        }
        catch ( IntrospectionException e ) {
            throw new BeanException("Error introspecting " + type);
        }
        Map<String, PropertyDescriptor> result = new HashMap<String, PropertyDescriptor>(properties.length);
        for ( PropertyDescriptor descriptor : properties ) {
            result.put(descriptor.getName(), descriptor);
        }
        return result;
    }

    private static String capitalize(String string) {
        if ( string.isEmpty() ) {
            return string;
        }
        else {
            return Character.toUpperCase(string.charAt(0)) + string.substring(1);
        }
    }

    private static Method getMethod(Class<?> clazz, String name, Class<?>... argumentTypes) {
        try {
            return clazz.getDeclaredMethod(name, argumentTypes);
        }
        catch ( NoSuchMethodException e ) {
            return null;
        }
    }

    private static class Definition {
        private final String name;
        private final String type;
        private final boolean isList;
        private final boolean isMap;
        private final String valueType;
        private final Method reader;
        private final Method writer;
        private Definition(Class<?> clazz, Field field) throws SerializationException {
            name = field.name().trim();
            String type = field.type().trim();
            if ( type.endsWith("[]") ) {
                type = type.substring(0, type.length() - 2).trim();
                valueType = null;
                isList = true;
                isMap = false;
            }
            else if ( type.contains("->") ) {
                int pos = type.indexOf("->");
                type = type.substring(0, pos).trim();
                valueType = type.substring(pos + 2).trim();
                isList = false;
                isMap = true;
            }
            else {
                valueType = null;
                isList = false;
                isMap = false;
            }
            this.type = type;
            reader = getMethod(clazz, "read" + capitalize(name), DataInput.class);
            writer = getMethod(clazz, "write" + capitalize(name), DataOutput.class);
            if ( (reader == null && writer != null) || (writer == null && reader != null) ) {
                throw new IllegalArgumentException(clazz + ": Must specify reader AND writer or none of them");
            }
        }

    }

    private static class CustomSerializer implements Serializer {
        private final Method reader;
        private final Method writer;
        private CustomSerializer(Method reader, Method writer) {
            this.reader = reader;
            this.writer = writer;
        }
        @Override
        public void read(Object target, DataInput input) throws IOException {
            invoke(target, reader, input);
        }
        private void invoke(Object target, Method method, Object... args) throws IOException {
            boolean reset = method.isAccessible();
            try {
                method.setAccessible(true);
                method.invoke(target, args);
            }
            catch ( InvocationTargetException e ) {
                Throwables.propagateIfInstanceOf(e.getTargetException(), IOException.class);
                throw new SerializationException("Exception invoking " + method, e.getTargetException());
            }
            catch ( IllegalAccessException e ) {
                throw new SerializationException("Exception invoking " + method, e);
            }
            finally {
                method.setAccessible(reset);
            }
        }
    }

}
