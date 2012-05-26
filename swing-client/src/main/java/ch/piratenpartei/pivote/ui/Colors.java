package ch.piratenpartei.pivote.ui;

import java.awt.Color;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public final class Colors {

    private final static Colors INSTANCE = new Colors();

    private final Color ppBase = new Color(249, 178, 0);
    private final Color ppGradientBright = new Color(253, 195, 0);
    private final Color ppGradientDark = new Color(240, 138, 0);
    private final Color link = Color.BLUE;
    
    private Colors() {
    }

    public static Colors colors() {
        return INSTANCE;
    }

    public Color ppBase() {
        return ppBase;
    }

    public Color ppGradientBright() {
        return ppGradientBright;
    }

    public Color ppGradientDark() {
        return ppGradientDark;
    }

    public Color link() {
        return link;
    }

}
