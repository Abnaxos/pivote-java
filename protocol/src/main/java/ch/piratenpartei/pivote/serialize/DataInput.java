package ch.piratenpartei.pivote.serialize;

import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;

import ch.piratenpartei.pivote.rpc.RemoteException;
import ch.piratenpartei.pivote.serialize.types.Data;
import ch.piratenpartei.pivote.serialize.types.UInt32;
import com.google.common.base.Charsets;
import org.joda.time.LocalDateTime;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class DataInput implements Closeable {

    private final PushbackInputStream inputStream;

    private final ByteBuffer buffer = ByteBuffer.allocate(16).order(ByteOrder.LITTLE_ENDIAN);
    private final SerializationContext context;

    private boolean eof = false;

    public DataInput(InputStream inputStream, SerializationContext context) {
        this.inputStream = new PushbackInputStream(inputStream, 1);
        this.context = context;
    }

    public BigInteger readBigint() throws IOException {
        // FIXME: implement this
        return null;
    }

    // FIXME: Multi-language string?

    public RemoteException readException() throws IOException {
        return null;
    }

    public boolean readBool() throws IOException {
        read(1);
        return buffer.get() != 0;
    }

    public byte readByte() throws IOException {
        read(1);
        return buffer.get();
    }

    public Data readData() throws IOException {
        return new Data(readByteArray());
    }

    public LocalDateTime readDateTime() throws IOException {
        long offset = readInt64()/10000; // 100nanos
        return null;
    }

    public double readDouble() throws IOException {
        read(8);
        return buffer.getDouble(); // FIXME: big or little endian?
    }

    public UUID readGuid() throws IOException {
        read(16);
        try {
            buffer.order(ByteOrder.BIG_ENDIAN);
            return new UUID(buffer.getLong(), buffer.getLong());
        }
        finally {
            buffer.order(ByteOrder.LITTLE_ENDIAN);
        }
    }

    public int readInt32() throws IOException {
        read(4);
        return buffer.getInt();
    }

    public long readInt64() throws IOException {
        read(8);
        return buffer.getLong();
    }

    public float readFloat() throws IOException {
        read(4);
        return buffer.getFloat();
    }

    public String readString() throws IOException {
        byte[] bytes = readByteArray();
        return new String(bytes, Charsets.UTF_8);
    }

    public UInt32 readUInt32() throws IOException {
        read(4);
        return new UInt32(
                ( readUnsignedByte()
                | (readUnsignedByte() << 8)
                | (readUnsignedByte() << 16)
                | (readUnsignedByte() << 24)
                ) & 0xffffffffL);
    }

    @SuppressWarnings("unchecked")
    public <T extends Enum> T readEnum(Class<T> enumClass) throws IOException {
        UInt32 ordinal = readUInt32();
        Enum[] values = enumClass.getEnumConstants();
        if ( ordinal.intValue() < 0 || ordinal.intValue() >= values.length ) {
            throw new SerializationException("Invalid value for " + enumClass.getName() + ": " + ordinal);
        }
        return (T)values[ordinal.intValue()];
    }

    private int readUnsignedByte() {
        return buffer.get() & 0xff;
    }

    public boolean eof() throws IOException {
        if ( eof ) {
            return true;
        }
        else {
            int b = inputStream.read();
            if ( b < 0 ) {
                eof = true;
                return true;
            }
            inputStream.unread(b);
            return false;
        }
    }

    @Override
    public void close() throws IOException {
        inputStream.close();
    }

    public SerializationContext getContext() {
        return context;
    }

    private byte[] readByteArray() throws IOException {
        UInt32 len = readUInt32();
        if ( len.longValue() > Integer.MAX_VALUE ) {
            throw new IOException("Data too long: " + len);
        }
        byte[] data = new byte[len.intValue()];
        read(data, len.intValue());
        return data;
    }

    private void read(int len) throws IOException {
        read(buffer.array(), len);
        buffer.rewind();
    }

    private void read(byte[] dest, int len) throws IOException {
        assert len >= 0;
        if ( eof ) {
            throw new EOFException();
        }
        int count = inputStream.read(dest, 0, len);
        if ( count != len ) {
            eof = true;
            throw new EOFException();
        }
    }

}
