package ch.piratenpartei.pivote.serialize;

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class UInt32 extends Number {

    public static final long MIN_VALUE = 0;
    public static final long MAX_VALUE = 0xffffffffL;

    private final long value;

    public UInt32(long value) {
        if ( value < MIN_VALUE || value > MAX_VALUE ) {
            throw new IllegalArgumentException("Value out of range for UInt32: " + value);
        }
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public boolean equals(Object o) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        UInt32 uInt32 = (UInt32)o;

        if ( value != uInt32.value ) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return (int)(value ^ (value >>> 32));
    }

    @Override
    public int intValue() {
        return (int)value;
    }

    @Override
    public long longValue() {
        return value;
    }

    @Override
    public float floatValue() {
        return (float)value;
    }

    @Override
    public double doubleValue() {
        return (double)value;
    }
}
