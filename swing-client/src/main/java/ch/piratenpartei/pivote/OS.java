package ch.piratenpartei.pivote;

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class OS {

    private final static boolean isWindows = System.getProperty("os.name").startsWith("Windows");

    private OS() {
    }

    public static boolean isWindows() {
        return isWindows;
    }
}
