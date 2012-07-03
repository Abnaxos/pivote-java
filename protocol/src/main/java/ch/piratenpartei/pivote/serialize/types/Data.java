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
        StringBuilder buf = new StringBuilder("Data[").append(data.length);
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

    public int size() {
        return data.length;
    }

    @NotNull
    public byte[] get() {
        return Arrays.copyOf(data, data.length);
    }
}
