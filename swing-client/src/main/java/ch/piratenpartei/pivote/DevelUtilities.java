package ch.piratenpartei.pivote;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.net.URL;

import javax.swing.JOptionPane;

import ch.piratenpartei.pivote.ui.AbstractActivity;
import ch.piratenpartei.pivote.ui.Activity;
import com.jidesoft.swing.StyledLabel;
import com.jidesoft.swing.StyledLabelBuilder;

import ch.raffael.util.i18n.I18N;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public final class DevelUtilities {

    private DevelUtilities() {
    }

    public static void actionPlaceholder(Component component, String action) {
        JOptionPane.showMessageDialog(component, action, "Action Placeholder", JOptionPane.INFORMATION_MESSAGE);
    }

    public static Component componentPlaceholder(String description) {
        StyledLabel label = new StyledLabelBuilder().add("Component Placeholder: ", Font.BOLD).add(description).createLabel();
        label.setBackground(Color.ORANGE);
        label.setOpaque(true);
        label.setHorizontalAlignment(StyledLabel.CENTER);
        return label;
    }

    public static Activity activityPlaceholder(final String title, String description) {
        return new AbstractActivity<Component>(title, description, I18N.getBundle(HelpResources.class).meta().resource(URL.class, "someHelp")) {
            @Override
            protected Component buildUi() {
                return componentPlaceholder(title);
            }
        };
    }

}
