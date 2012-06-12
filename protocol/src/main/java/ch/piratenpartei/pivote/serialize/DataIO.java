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
