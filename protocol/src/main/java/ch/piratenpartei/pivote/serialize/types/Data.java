package ch.piratenpartei.pivote.serialize.types;

import java.util.Arrays;

import org.jetbrains.annotations.NotNull;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public final class Data {

    private final byte[] data;

    public Data(@NotNull byte[] data) {
        this.data = Arrays.copyOf(data, data.length);
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder("Data[").append(data.length).append(":");
        for ( byte b : data ) {
            int i = b & 0xff;
            if ( i < 16 ) {
                buf.append(0);
            }
            buf.append(Integer.toString(i, 16));
        }
        buf.append("]");
        return buf.toString();
    }

    @Override
    public boolean equals(Object o) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }
        Data that = (Data)o;
        return Arrays.equals(data, that.data);

    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(data);
    }

    @NotNull
    public byte[] get() {
        return Arrays.copyOf(data, data.length);
    }
}
