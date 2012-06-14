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

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;

import ch.piratenpartei.pivote.serialize.types.Data;
import ch.piratenpartei.pivote.serialize.types.UInt32;
import com.google.common.base.Charsets;
import org.joda.time.Days;
import org.joda.time.LocalDateTime;

import ch.raffael.util.common.NotImplementedException;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class DataOutput implements Closeable {

    private final OutputStream output;
    private final SerializationContext context;

    private final ByteBuffer buffer = ByteBuffer.allocate(16).order(ByteOrder.LITTLE_ENDIAN);

    public DataOutput(OutputStream output, SerializationContext context) {
        this.output = output;
        this.context = context;
    }

    public SerializationContext context() {
        return context;
    }

    public void writeBigint(BigInteger value) throws IOException {
        throw new NotImplementedException();
    }

    public void writeBool(boolean value) throws IOException {
        output.write(value ? 1 : 0);
    }

    public void writeByte(byte value) throws IOException {
        output.write(value);
    }

    public void writeData(Data data) throws IOException {
        writeInt32(data.size());
        output.write(data.get());
    }

    public void writeDateTime(LocalDateTime value) throws IOException {
        buffer.rewind();
        long millis = Days.daysBetween(DataIO.BASE_DATETIME, value).getDays() * DataIO.DAY_MILLIS;
        millis += value.millisOfDay().get();
        buffer.putLong(DataIO.millisToNano100(millis));
        writebuf(8);
    }

    public void writeDouble(double value) throws IOException {
        buffer.rewind();
        buffer.putDouble(value);
        writebuf(8);
    }

    public void writeGuid(UUID value) throws IOException {
        writeInt32(16);
        buffer.rewind();
        buffer.order(ByteOrder.BIG_ENDIAN);
        try {
            buffer.putLong(value.getMostSignificantBits());
            buffer.putLong(value.getLeastSignificantBits());
            DataIO.reverseBytes(buffer.array(), 0, 4);
            DataIO.reverseBytes(buffer.array(), 4, 2);
            DataIO.reverseBytes(buffer.array(), 6, 2);
            writebuf(16);
        }
        finally {
            buffer.order(ByteOrder.LITTLE_ENDIAN);
        }
    }

    public void writeInt32(int value) throws IOException {
        buffer.rewind();
        buffer.putInt(value);
        writebuf(4);
    }

    public void writeInt64(long value) throws IOException {
        buffer.rewind();
        buffer.putLong(value);
        writebuf(8);
    }

    public void writeFloat(float value) throws IOException {
        buffer.rewind();
        buffer.putFloat(value);
        writebuf(4);
    }

    public void writeString(String value) throws IOException {
        byte[] bytes = value.getBytes(Charsets.UTF_8);
        writeInt32(bytes.length);
        output.write(bytes);
    }

    public void writeCString(String value) throws IOException {
        byte[] bytes = value.getBytes(Charsets.UTF_8);
        if ( bytes.length > 255 ) {
            throw new SerializationException("CString too long: " + bytes.length);
        }
        output.write(bytes.length);
        output.write(bytes);
    }

    public void writeUInt32(UInt32 value) throws IOException {
        buffer.rewind();
        buffer.putInt((int)value.longValue());
        writebuf(4);
    }

    public void writeObject(PiVoteSerializable object) throws IOException {
        String protocolName = context.getProtocolName(object.getClass());
        if ( protocolName == null ) {
            throw new SerializationException("No protocol name for Java class " + object.getClass().getName());
        }
        writeCString(protocolName);
        object.write(this);
    }

    public void writeEnum(Enum value) throws IOException {
        writeInt32(value.ordinal());
    }

    private void writebuf(int count) throws IOException {
        write(buffer.array(), count);
    }

    private void write(byte[] bytes) throws IOException {
        write(bytes, bytes.length);
    }

    private void write(byte[] bytes, int length) throws IOException {
        output.write(bytes, 0, length);
    }

    @Override
    public void close() throws IOException {
        output.close();
    }

}
