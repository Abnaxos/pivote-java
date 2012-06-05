package ch.piratenpartei.pivote.serialize;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.io.Closeables;

import ch.raffael.util.common.Classes;
import ch.raffael.util.common.logging.LogUtil;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class SerializationContext {

    private final ClassLoader classLoader;

    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LogUtil.getLogger();

    private final BiMap<String, Class<? extends PiVoteSerializable>> mappings = HashBiMap.create();
    private final Map<Class<? extends PiVoteSerializable>, Serializer> serializers = new HashMap<Class<? extends PiVoteSerializable>, Serializer>();

    public SerializationContext() throws ClassNotFoundException {
        this(null);
    }

    public SerializationContext(ClassLoader classLoader) throws ClassNotFoundException {
        this.classLoader = Classes.classLoader(classLoader, getClass());
        loadMappings();
    }

    protected synchronized void loadMappings() throws ClassNotFoundException {
        try {
            Properties properties = null;
            Enumeration<URL> resources = classLoader.getResources("META-INF/ch/piratenpartei/pivote/serialize/class-mappings.properties");
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
                            Class<?> clazz = Class.forName(javaName, false, classLoader);
                            mappings.put(protocolName, piVoteSerializable(clazz));
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

    @SuppressWarnings("unchecked")
    private static Class<? extends PiVoteSerializable> piVoteSerializable(Class<?> clazz) {
        if ( !PiVoteSerializable.class.isAssignableFrom(clazz) ) {
            throw new IllegalArgumentException(clazz + " is not PiVoteSerializable");
        }
        return (Class<? extends PiVoteSerializable>)clazz;
    }

    public String getJavaName(String protocolName) {
        Class<?> clazz = mappings.get(protocolName);
        if ( clazz == null ) {
            return null;
        }
        else {
            return clazz.getName();
        }
    }

    public Class<? extends PiVoteSerializable> getJavaClass(String protocolName) {
        return mappings.get(protocolName);
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    public String getProtocolName(String javaName) throws ClassNotFoundException {
        return mappings.inverse().get(Class.forName(javaName, false, classLoader));
    }

    public String getProtocolName(Class<? extends PiVoteSerializable> clazz) {
        return mappings.inverse().get(clazz);
    }

    public Serializer getSerializer(Class<? extends PiVoteSerializable> clazz) {
        return serializers.get(clazz);
    }

    public void setSerializer(Class<? extends PiVoteSerializable> clazz, Serializer serializer) {
        serializers.put(clazz, serializer);
    }

}
