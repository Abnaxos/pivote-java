package ch.piratenpartei.pivote.serialize;

/**
 * Security level of fields. If used with lists, it denotes the security of each element.
 * If used with maps, it denotes the security of each value. Lists/Maps as a whole cannot
 * be secured.
 *
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public enum Security {

    /**
     * No security.
     */
    NONE(false, false),
    /**
     * Signed.
     */
    SIGNED(true, false),
    /**
     * Encrypted.
     */
    ENCRYPTED(false, true),
    /**
     * Signed & encrypted
     */
    SECURE(true, true);

    private final boolean signed;
    private final boolean encrypted;

    private Security(boolean signed, boolean encrypted) {
        this.signed = signed;
        this.encrypted = encrypted;
    }

    public boolean signed() {
        return signed;
    }

    public boolean encrypted() {
        return encrypted;
    }
}
