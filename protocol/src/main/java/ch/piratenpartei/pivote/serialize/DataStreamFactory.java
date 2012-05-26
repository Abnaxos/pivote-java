package ch.piratenpartei.pivote.serialize;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.slf4j.Logger;

import ch.piratenpartei.pivote.serialize.impl.BigintHandler;
import ch.piratenpartei.pivote.serialize.impl.BoolHandler;
import ch.piratenpartei.pivote.serialize.impl.ByteHandler;
import ch.piratenpartei.pivote.serialize.impl.DataHandler;
import ch.piratenpartei.pivote.serialize.impl.DateTimeHandler;
import ch.piratenpartei.pivote.serialize.impl.DoubleHandler;
import ch.piratenpartei.pivote.serialize.impl.FloatHandler;
import ch.piratenpartei.pivote.serialize.impl.GuidHandler;
import ch.piratenpartei.pivote.serialize.impl.Handler;
import ch.piratenpartei.pivote.serialize.impl.Int32Handler;
import ch.piratenpartei.pivote.serialize.impl.Int64Handler;
import ch.piratenpartei.pivote.serialize.impl.StringHandler;
import ch.piratenpartei.pivote.serialize.impl.UInt32Handler;
import ch.piratenpartei.pivote.serialize.streams.DataInput;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.io.Closeables;
import com.google.common.primitives.Primitives;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joda.time.LocalDateTime;

import ch.raffael.util.common.Classes;
import ch.raffael.util.common.logging.LogUtil;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class DataStreamFactory {

    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LogUtil.getLogger();

    private final ClassLoader loader;

    private final Map<Class<?>, Handler> handlers = new HashMap<Class<?>, Handler>();
    private final BiMap<String, Class<?>> beans = HashBiMap.create();
    private final BiMap<String, String> mappings = HashBiMap.create();
    private final Map<Class<?>, BeanSerializer> serializers = new HashMap<Class<?>, BeanSerializer>();

    public DataStreamFactory() {
        this(null);
    }

    public DataStreamFactory(ClassLoader loader) {
        this.loader = loader;
        registerType(BigInteger.class, new BigintHandler());
        registerType(boolean.class, new BoolHandler());
        registerType(byte.class, new ByteHandler());
        registerType(Data.class, new DataHandler());
        registerType(LocalDateTime.class, new DateTimeHandler());
        registerType(double.class, new DoubleHandler());
        registerType(UUID.class, new GuidHandler());
        registerType(int.class, new Int32Handler());
        registerType(long.class, new Int64Handler());
        registerType(float.class, new FloatHandler());
        registerType(String.class, new StringHandler());
        registerType(UInt32.class, new UInt32Handler());
        // FIXME: langstrings
        loadMappings();
    }

    private void registerType(Class<?> clazz, Handler handler) {
        handlers.put(clazz, handler);
        if ( clazz.isPrimitive() ) {
            handlers.put(Primitives.wrap(clazz), handler);
        }
    }

    protected synchronized void loadMappings() {
        try {
            Properties properties = null;
            Enumeration<URL> resources = Classes.classLoader(loader, getClass()).getResources("/META-INF/ch.piratenpartei.pivote.serialize.mappings.properties");
            while ( resources.hasMoreElements() ) {
                URL url = resources.nextElement();
                log.debug("Loading mappings from {}", url);
                InputStream input = null;
                try {
                    input = new BufferedInputStream(url.openStream());
                    if ( properties == null ) {
                        properties = new Properties();
                    }
                    else {
                        properties.clear();
                    }
                    properties.load(input);
                    for ( Map.Entry<Object, Object> entry : properties.entrySet() ) {
                        String javaName = (String)entry.getKey();
                        String protocolName = (String)entry.getValue();
                        if ( mappings.containsKey(javaName) || mappings.containsValue(protocolName) ) {
                            log.error("Skipping duplicate mapping: {} => {}", javaName, protocolName);
                        }
                        else {
                            mappings.put(javaName, protocolName);
                        }
                    }
                }
                catch ( IOException e ) {
                    log.error("Error loading mappings from {}", url, e);
                }
                finally {
                    Closeables.closeQuietly(input);
                }
            }
        }
        catch ( IOException e ) {
            log.error("Cannot load class mappings", e);
        }
    }

    public Handler getHandler(Class<?> type) {
        return handlers.get(type);
    }

    public synchronized BeanSerializer serializerByProtocolName(String protocolName) throws ClassNotFoundException, NonSerializableBeanException {
        String javaName = mappings.inverse().get(protocolName);
        if ( javaName == null ) {
            javaName = protocolName;
        }
        return serializerByJavaName(javaName);
    }

    public BeanSerializer serializerByJavaName(String javaName) throws ClassNotFoundException, NonSerializableBeanException {
        Class<?> clazz = Class.forName(javaName, true, Classes.classLoader(loader, getClass()));
        return serializerFor(clazz);
    }

    public synchronized BeanSerializer serializerFor(Class<?> clazz) throws NonSerializableBeanException {
        String protocolName = mappings.get(clazz.getName());
        if ( protocolName == null ) {
            protocolName = clazz.getName();
        }
        return serializerFor(clazz, protocolName);
    }

    protected synchronized BeanSerializer serializerFor(Class<?> clazz, String protocolName) throws NonSerializableBeanException {
        BeanSerializer serializer = serializers.get(clazz);
        if ( serializer == null ) {
            serializer = createSerializer(clazz, protocolName);
            serializers.put(clazz, serializer);
        }
        return serializer;
    }

    protected BeanSerializer createSerializer(Class<?> clazz, String protocolName) throws NonSerializableBeanException {
        BeanSerializerImpl serializer;
        BeanSerializerImpl parent = null;
        if ( clazz.getSuperclass() != Object.class ) {
            parent = (BeanSerializerImpl)serializerFor(clazz.getSuperclass());
        }
        serializer = new BeanSerializerImpl(parent, clazz, protocolName);
        Builder builder;
        try {
            builder = new Builder(serializer);
        }
        catch ( IntrospectionException e ) {
            throw new NonSerializableBeanException("Cannot inspect class " + clazz, e);
        }
        try {
            Method setupMethod = clazz.getDeclaredMethod("setupSerialization", Builder.class);
            if ( (setupMethod.getModifiers() & Modifier.STATIC) == 0 ) {
                throw new NonSerializableBeanException("Method setupSerialization() must be static");
            }
            setupMethod.invoke(null, builder);
        }
        catch ( NoSuchMethodException e ) {
            log.debug("No setup method found for class {}", clazz.getName());
        }
        catch ( InvocationTargetException e ) {
            throw new NonSerializableBeanException("Error setting up serialization for class " + clazz, e.getTargetException());
        }
        catch ( IllegalAccessException e ) {
            throw new NonSerializableBeanException("Error setting up serialization for class " + clazz, e);
        }
        return serializer;
    }

    public final class Builder {

        private final BeanSerializerImpl serializer;
        private final BeanInfo beanInfo;

        private Builder(BeanSerializerImpl serializer) throws IntrospectionException {
            this.serializer = serializer;
            beanInfo = Introspector.getBeanInfo(serializer.getTargetClass());
        }

        public Builder field(String property) throws NonSerializableBeanException {
            return this;
        }

        public Builder list(String property, Class<?> type) throws NonSerializableBeanException {
            return this;
        }

        public Builder map(String property, Class<?> keyType, Class<?> valueType) throws NonSerializableBeanException {
            return this;
        }

    }

    public static interface BeanSerializer {

        @Nullable
        BeanSerializer getParent();

        @NotNull
        Class<?> getTargetClass();

        @NotNull
        String getProtocolName();

        @NotNull
        Object read(DataInput input) throws IOException;
    }

    protected class BeanSerializerImpl implements BeanSerializer {

        private final BeanSerializerImpl parent;
        private final Class<?> targetClass;
        private final String protocolName;
        private final List<PropertyEntry> properties = new LinkedList<PropertyEntry>();

        public BeanSerializerImpl(BeanSerializerImpl parent, Class<?> targetClass, String protocolName) {
            this.parent = parent;
            this.targetClass = targetClass;
            this.protocolName = protocolName;
        }

        protected void add(Handler handler, PropertyDescriptor property) {
            properties.add(new PropertyEntry(handler, property));
        }

        @Override
        public BeanSerializer getParent() {
            return parent;
        }

        @NotNull
        public Class<?> getTargetClass() {
            return targetClass;
        }

        @NotNull
        public String getProtocolName() {
            return protocolName;
        }

        @NotNull
        @Override
        public Object read(DataInput input) throws IOException {
            log.debug("Reading: " + targetClass.getName());
            Object bean;
            try {
                bean = targetClass.newInstance();
            }
            catch ( InstantiationException e ) {
                throw new IOException("Cannot instanciate class " + targetClass.getName(), e);
            }
            catch ( IllegalAccessException e ) {
                throw new IOException("Cannot instanciate class " + targetClass.getName(), e);
            }
            read(bean, input);
            return bean;
        }

        public void read(Object bean, DataInput input) throws IOException {
            if ( parent != null ) {
                parent.read(bean, input);
            }
            for ( PropertyEntry prop : properties ) {
                Method setter = prop.getDescriptor().getWriteMethod();
                if ( setter == null ) {
                    throw new IOException("Error reading property " + targetClass.getName() + "::" + prop.getDescriptor().getName() + ": Property not writable");
                }
                Object value = prop.getHandler().readValue(DataStreamFactory.this, input);
                log.trace("Setting property {} to {}", prop.getDescriptor().getName(), value);
                try {
                    setter.invoke(bean, value);
                }
                catch ( InvocationTargetException e ) {
                    throw new IOException("Error reading property " + targetClass.getName() + "::" + prop.getDescriptor().getName() + ": " + e, e);
                }
                catch ( IllegalAccessException e ) {
                    throw new IOException("Error reading property " + targetClass.getName() + "::" + prop.getDescriptor().getName() + ": " + e, e);
                }
                catch ( ClassCastException e ) {
                    throw new IOException("Error reading property " + targetClass.getName() + "::" + prop.getDescriptor().getName() + ": " + e, e);
                }
            }
        }
    }

    protected static class PropertyEntry {

        private final Handler handler;
        private final PropertyDescriptor descriptor;

        public PropertyEntry(Handler handler, PropertyDescriptor descriptor) {
            this.descriptor = descriptor;
            this.handler = handler;
        }

        public Handler getHandler() {
            return handler;
        }

        public PropertyDescriptor getDescriptor() {
            return descriptor;
        }
    }

}
