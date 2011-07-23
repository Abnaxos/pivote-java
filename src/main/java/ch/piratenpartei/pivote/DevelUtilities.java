package ch.piratenpartei.pivote;

import java.awt.Component;

import javax.swing.JOptionPane;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public final class DevelUtilities {

    private DevelUtilities() {
    }

    public static void actionPlaceholder(Component component, String action) {
        JOptionPane.showMessageDialog(component, action, "Action Placeholder", JOptionPane.INFORMATION_MESSAGE);
    }

}
