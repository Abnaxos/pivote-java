package ch.piratenpartei.pivote.serialize;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Set;

import ch.piratenpartei.pivote.serialize.impl.BigintHandler;
import ch.piratenpartei.pivote.serialize.impl.BoolHandler;
import ch.piratenpartei.pivote.serialize.impl.ByteHandler;
import ch.piratenpartei.pivote.serialize.impl.DataHandler;
import ch.piratenpartei.pivote.serialize.impl.DateTimeHandler;
import ch.piratenpartei.pivote.serialize.impl.DoubleHandler;
import ch.piratenpartei.pivote.serialize.impl.EnumHandler;
import ch.piratenpartei.pivote.serialize.impl.FloatHandler;
import ch.piratenpartei.pivote.serialize.impl.Handler;
import ch.piratenpartei.pivote.serialize.impl.Int32Handler;
import ch.piratenpartei.pivote.serialize.impl.Int64Handler;
import ch.piratenpartei.pivote.serialize.impl.ObjectHandler;
import ch.piratenpartei.pivote.serialize.impl.StringHandler;
import ch.piratenpartei.pivote.serialize.impl.UInt32Handler;
import com.google.common.collect.ImmutableSet;
import org.joda.time.LocalDateTime;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public enum Type {

    BIGINT(new BigintHandler(), BigInteger.class),
    LANGSTRING(null, Object.class), // FIXME: langstring?
    BOOL(new BoolHandler(), boolean.class, Boolean.class),
    BYTE(new ByteHandler(), byte.class, Byte.class),
    DATA(new DataHandler(), Data.class),
    DATETIME(new DateTimeHandler(), LocalDateTime.class),
    DOUBLE(new DoubleHandler(), double.class, Double.class),
    GUID(new ByteHandler(), byte[].class),
    INT32(new Int32Handler(), int.class, Integer.class),
    INT64(new Int64Handler(), long.class, Long.class),
    FLOAT(new FloatHandler(), float.class, Float.class),
    STRING(new StringHandler(), String.class),
    UINT32(new UInt32Handler(), long.class, Long.class),
    ENUM(null, Enum.class) {
        @Override
        public boolean isValidJavaType(Class<?> javaType) {
            return javaType != null && javaType.isEnum();
        }
        @SuppressWarnings( { "unchecked" })
        @Override
        public Handler handler(Class<?> targetType) {
            if ( isValidJavaType(targetType) ) {
                return null;
            }
            return new EnumHandler((Class<? extends Enum>)targetType);
        }
    },
    OBJECT(null, Object.class) {
        @Override
        public boolean isValidJavaType(Class<?> javaType) {
            return !(javaType == null || javaType.isEnum() || javaType.isAnnotation() || javaType.isArray() || javaType.isPrimitive());
        }
        @Override
        public Handler handler(Class<?> targetType) {
            if ( isValidJavaType(targetType) ) {
                return null;
            }
            return new ObjectHandler(targetType);
        }
    };

    private final Set<Class<?>> javaTypes;
    private final Handler handler;

    private Type(Handler handler, Class<?> javaType) {
        this.javaTypes = Collections.<Class<?>>singleton(javaType);
        this.handler = handler;
    }

    private Type(Handler handler, Class<?>... javaTypes) {
        this.javaTypes = ImmutableSet.copyOf(javaTypes);
        this.handler = handler;
    }

    public Set<Class<?>> getJavaTypes() {
        return javaTypes;
    }

    public boolean isValidJavaType(Class<?> javaType) {
        return javaTypes.contains(javaType);
    }
    
    public Handler handler(Class<?> targetType) {
        if ( !javaTypes.contains(targetType) ) {
            return null;
        }
        return handler;
    }

}
