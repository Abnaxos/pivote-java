package ch.piratenpartei.pivote.serialize.streams;

import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;

import ch.piratenpartei.pivote.serialize.Data;
import ch.piratenpartei.pivote.serialize.RemotePiException;
import com.google.common.base.Charsets;
import org.joda.time.LocalDateTime;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class DataInput implements Closeable {

    private final PushbackInputStream inputStream;

    private final ByteBuffer buffer = ByteBuffer.allocate(16).order(ByteOrder.LITTLE_ENDIAN);

    private boolean eof = false;

    public DataInput(InputStream inputStream) {
        this.inputStream = new PushbackInputStream(inputStream, 1);
    }

    public BigInteger readBigint() throws IOException {
        // FIXME: implement this
        return null;
    }

    // FIXME: Multi-language string?

    public RemotePiException readException() throws IOException {
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

    public long readUInt32() throws IOException {
        read(4);
        return (readUnsignedByte()
                | (readUnsignedByte() << 8)
                | (readUnsignedByte() << 16)
                | (readUnsignedByte() << 24))
                & 0xffffffffL;
    }

    private int readUnsignedByte() {
        return buffer.get() & 0xff;
    }

    public Object readObject(Class<?> expectedClass) throws IOException {
        return null;
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

    private byte[] readByteArray() throws IOException {
        long len = readUInt32();
        if ( len > Integer.MAX_VALUE ) {
            throw new IOException("Data too long: " + len);
        }
        byte[] data = new byte[(int)len];
        read(data, (int)len);
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
