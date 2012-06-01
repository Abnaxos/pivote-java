package ch.piratenpartei.pivote.serialize;

import java.io.IOException;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class SerializationException extends IOException {

    public SerializationException() {
    }

    public SerializationException(String message) {
        super(message);
    }

    public SerializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public SerializationException(Throwable cause) {
        super(cause);
    }
}
