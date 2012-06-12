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
package ch.piratenpartei.pivote.serialize.types;

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public final class UInt32 extends Number implements Comparable<UInt32> {
    private static final long serialVersionUID = 2012060101L;
    
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
        return value == uInt32.value;
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

    @Override
    public int compareTo(UInt32 that) {
        if ( value > that.value ) {
            return 1;
        }
        else if ( value < that.value ) {
            return -1;
        }
        else {
            return 0;
        }
    }
}
