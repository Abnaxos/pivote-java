package ch.piratenpartei.pivote.ui;

import java.awt.Component;
import java.net.URL;
import java.util.List;

import javax.swing.Action;

import ch.raffael.util.i18n.ResourceBundle;
import ch.raffael.util.swing.context.Context;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public interface Activity {

    String getTitle();

    String getDescription();

    ResourceBundle.Resource<URL> getHelp();

    Component getComponent();

    List<Action> getActions();

    void openActivity(Context context, Navigator navigator);

    boolean activityClosing();
    void activityClosed();

    interface Navigator {
        void open(Activity activity);
        void close();
    }

}
