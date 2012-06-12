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

import org.joda.time.LocalDateTime;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class DataIO {

    public static final LocalDateTime BASE_DATETIME = new LocalDateTime(1, 1, 1, 0, 0);

    private DataIO() {
    }

    public static long nano100ToMillis(long nano100) {
        return nano100 / 10000; // 100 nanos; 1 milli = 10'000*100 nanos
    }

    public static long millisToNano100(long millis) {
        return millis * 10000;
    }

    public static void reverseBytes(byte[] bytes, int offset, int count) {
        for ( int i = 0; i < count / 2; i++ ) {
            byte b = bytes[i + offset];
            bytes[i + offset] = bytes[i + offset + count - i*2 - 1];
            bytes[i + offset + count - i * 2 - 1] = b;
        }
    }
}
