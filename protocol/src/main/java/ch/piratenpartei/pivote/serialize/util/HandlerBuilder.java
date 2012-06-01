package ch.piratenpartei.pivote.serialize.util;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.piratenpartei.pivote.serialize.Handler;
import ch.piratenpartei.pivote.serialize.PiVoteSerializable;
import ch.piratenpartei.pivote.serialize.SerializationContext;
import ch.piratenpartei.pivote.serialize.SerializationException;
import ch.piratenpartei.pivote.serialize.Type;
import ch.piratenpartei.pivote.serialize.handlers.CompoundHandler;
import ch.piratenpartei.pivote.serialize.handlers.EnumHandler;
import ch.piratenpartei.pivote.serialize.handlers.ListHandler;
import ch.piratenpartei.pivote.serialize.handlers.MapHandler;
import ch.piratenpartei.pivote.serialize.handlers.ObjectHandler;
import com.google.common.base.Function;
import com.google.common.primitives.Primitives;

import ch.raffael.util.beans.BeanException;

import static com.google.common.collect.Lists.*;
import static java.util.Arrays.asList;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class HandlerBuilder {

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

    public HandlerBuilder(Class<? extends PiVoteSerializable> targetClass, SerializationContext context) {
        if ( targetClass.isInterface() || targetClass.isAnnotation() || targetClass.isArray() || targetClass.isEnum() ) {
            throw new IllegalArgumentException(targetClass + " is not a class");
        }
        this.targetClass = targetClass;
        this.context = context;
    }

    public Handler build() throws SerializationException {
        List<Definition> definitions = collectDefinitions(new LinkedList<Definition>(), targetClass);
        Map<String, PropertyDescriptor> properties = getProperties(targetClass);
        CompoundHandler serializer = new CompoundHandler(targetClass);
        for ( Definition def : definitions ) {
            PropertyDescriptor property = properties.get(def.propertyName);
            if ( property == null ) {
                throw new IllegalArgumentException(targetClass + ": No such property: " + def.propertyName);
            }
            if ( def.isList ) {
                if ( !property.getPropertyType().isAssignableFrom(List.class) ) {
                    throw new IllegalArgumentException(targetClass + ": Type " + List.class + " is not applicable to Java type " + property.getPropertyType());
                }
                serializer.append(new ListHandler(handler(def.type, property)),
                                  new CompoundHandler.PropertyAccessor(property));
            }
            else if ( def.isMap ) {
                if ( !property.getPropertyType().isAssignableFrom(Map.class) ) {
                    throw new IllegalArgumentException(targetClass + ": Type " + Map.class + " is not applicable to Java type " + property.getPropertyType());
                }
                serializer.append(new MapHandler(handler(def.type, property),
                                                 handler(def.valueType, property)),
                                  new CompoundHandler.PropertyAccessor(property));
            }
            else {
                serializer.append(handler(def.type, property), new CompoundHandler.PropertyAccessor(property));
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
            else if ( !property.getPropertyType().isAssignableFrom(javaClass) ) {
                throw new SerializationException(targetClass.getName() + "::" + property.getName() + ": Incompatible types: " + javaClass + " <-> " + property.getPropertyType());
            }
            Handler handler = context.getHandler(javaClass);
            if ( handler == null ) {
                handler = new ObjectHandler(property.getPropertyType());
            }
            return handler;
        }
        else if ( t == Type.ENUM ) {
            if ( !property.getPropertyType().isEnum() ) {
                throw new SerializationException(targetClass + "::" + "." + property.getName() + ": " + property.getPropertyType() + " is not not an enum");
            }
            return new EnumHandler((Class<? extends Enum>)property.getPropertyType());
        }
        else {
            Class<?> propertyType = Primitives.wrap(property.getPropertyType());
            if ( !t.getJavaType().isAssignableFrom(propertyType) ) {
                throw new SerializationException(targetClass + "::" + "." + property.getName() + ": Property type " + property.getPropertyType() + " is incompatible with " + t);
            }
            return t.handler();
        }
    }

    private static List<Definition> collectDefinitions(List<Definition> target, Class<?> type) {
        if ( type == Object.class ) {
            return target;
        }
        collectDefinitions(target, type.getSuperclass());
        Serialize annotation = type.getAnnotation(Serialize.class);
        if ( annotation != null ) {
            target.addAll(transform(asList(annotation.value()),
                                    new Function<String, Definition>() {
                                        @Override
                                        public Definition apply(String input) {
                                            return new Definition(input);
                                        }
                                    }));
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

    private static class Definition {
        private final String propertyName;
        private final String type;
        private final boolean isList;
        private final boolean isMap;
        private final String valueType;
        private Definition(String definition) {
            definition = definition.trim();
            Matcher matcher = HandlerBuilder.SYNTAX.matcher(definition);
            if ( !matcher.matches() ) {
                throw new IllegalArgumentException("Invalid field definition: " + definition);
            }
            propertyName = matcher.group(1);
            type = matcher.group(2);
            String collection = matcher.group(4);
            if ( collection != null ) {
                if ( collection.equals("*") ) {
                    isList = true;
                    isMap = false;
                    valueType = null;
                }
                else {
                    isList = false;
                    isMap = true;
                    valueType = matcher.group(5);
                }
            }
            else {
                isList = false;
                isMap = false;
                valueType = null;
            }
        }
    }

}
