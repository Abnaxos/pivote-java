package ch.piratenpartei.pivote.serialize;

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class RemotePiException /* extends Exception? */ {

    private final Code code;
    private final String message;

    public RemotePiException(Code code, String message) {
        this.code = code;
        this.message = message;
    }

    public Code getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static enum Code {

    }
}
