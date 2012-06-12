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
package ch.piratenpartei.pivote.serialize;

import java.math.BigInteger;
import java.util.Map;
import java.util.UUID;

import ch.piratenpartei.pivote.serialize.handlers.BigintHandler;
import ch.piratenpartei.pivote.serialize.handlers.BoolHandler;
import ch.piratenpartei.pivote.serialize.handlers.ByteHandler;
import ch.piratenpartei.pivote.serialize.handlers.DataHandler;
import ch.piratenpartei.pivote.serialize.handlers.DateTimeHandler;
import ch.piratenpartei.pivote.serialize.handlers.DoubleHandler;
import ch.piratenpartei.pivote.serialize.handlers.FloatHandler;
import ch.piratenpartei.pivote.serialize.handlers.GuidHandler;
import ch.piratenpartei.pivote.serialize.handlers.Int32Handler;
import ch.piratenpartei.pivote.serialize.handlers.Int64Handler;
import ch.piratenpartei.pivote.serialize.handlers.LangStringHandler;
import ch.piratenpartei.pivote.serialize.handlers.StringHandler;
import ch.piratenpartei.pivote.serialize.handlers.UInt32Handler;
import ch.piratenpartei.pivote.serialize.types.Data;
import ch.piratenpartei.pivote.serialize.types.LangString;
import ch.piratenpartei.pivote.serialize.types.UInt32;
import com.google.common.collect.ImmutableMap;
import org.joda.time.LocalDateTime;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public enum Type {

    BIGINT(new BigintHandler(), BigInteger.class),
    LANGSTRING(new LangStringHandler(), LangString.class),
    BOOL(new BoolHandler(), Boolean.class),
    BYTE(new ByteHandler(), Byte.class),
    DATA(new DataHandler(), Data.class),
    DATETIME(new DateTimeHandler(), LocalDateTime.class),
    DOUBLE(new DoubleHandler(), Double.class),
    GUID(new GuidHandler(), UUID.class),
    INT32(new Int32Handler(), Integer.class),
    INT64(new Int64Handler(), Long.class),
    FLOAT(new FloatHandler(), Float.class),
    STRING(new StringHandler(), String.class),
    UINT32(new UInt32Handler(), UInt32.class),

    ENUM(null, Enum.class);

    private static final Map<String, Type> types;
    static {
        ImmutableMap.Builder<String, Type> builder =ImmutableMap.builder();
        for ( Type t : Type.values() ) {
            builder.put(t.name(), t);
        }
        types = builder.build();
    }

    private final Class<?> javaType;
    private final Handler handler;

    private Type(Handler handler, Class<?> javaType) {
        this.javaType = javaType;
        this.handler = handler;
    }

    public Class<?> getJavaType() {
        return javaType;
    }

    public Handler handler() {
        return handler;
    }

    public static Type forName(String name) {
        return types.get(name.toUpperCase());
    }

}
