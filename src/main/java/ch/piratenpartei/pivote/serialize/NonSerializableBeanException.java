package ch.piratenpartei.pivote.serialize;

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class NonSerializableBeanException extends Exception {

    public NonSerializableBeanException() {
    }

    public NonSerializableBeanException(String message) {
        super(message);
    }

    public NonSerializableBeanException(String message, Throwable cause) {
        super(message, cause);
    }

    public NonSerializableBeanException(Throwable cause) {
        super(cause);
    }
}
