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
import org.joda.time.Duration;
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
        long offset = DataUtils.nano100ToMillis(readInt64());
        return DataUtils.BASE_DATETIME.plus(new Duration(offset));
    }

    public double readDouble() throws IOException {
        read(8);
        return buffer.getDouble(); // FIXME: big or little endian?
    }

    public UUID readGuid() throws IOException {
        UInt32 length = readUInt32();
        if ( length.intValue() != 16 ) {
            throw new SerializationException("128bit UUID expected");
        }
        byte[] bytes = new byte[16];
        read(bytes, 16);
        // for some strange reason, we need to swap some bytes :( ask Exception ...
        DataUtils.reverseBytes(bytes, 0, 4);
        DataUtils.reverseBytes(bytes, 4, 2);
        DataUtils.reverseBytes(bytes, 6, 2);
        ByteBuffer buf = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN);
        return new UUID(buf.getLong(), buf.getLong());
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

    public String readCString() throws IOException {
        int length = readByte() & 0xff;
        byte[] buf = new byte[length];
        read(buf, length);
        return new String(buf, Charsets.US_ASCII);
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

    public Object readObject() throws IOException {
        String protocolClass = readCString();
        Class<? extends PiVoteSerializable> javaClass = context.getJavaClass(protocolClass);
        PiVoteSerializable target;
        if ( javaClass == null ) {
            throw new SerializationException("Unknown class: " + protocolClass);
        }
        try {
            target = javaClass.newInstance();
        }
        catch ( InstantiationException e ) {
            throw new SerializationException("Cannot create object of class " + javaClass.getName(), e);
        }
        catch ( IllegalAccessException e ) {
            throw new SerializationException("Cannot create object of class " + javaClass.getName(), e);
        }
        target.read(this);
        return target;
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
