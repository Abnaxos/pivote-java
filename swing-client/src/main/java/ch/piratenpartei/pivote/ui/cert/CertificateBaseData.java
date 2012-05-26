package ch.piratenpartei.pivote.ui.cert;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.net.URL;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import ch.piratenpartei.pivote.DevelUtilities;
import ch.piratenpartei.pivote.HelpResources;
import ch.piratenpartei.pivote.model.Certificate;
import ch.piratenpartei.pivote.ui.AbstractActivity;

import ch.raffael.util.binding.PresentationModel;
import ch.raffael.util.binding.SimpleBinding;
import ch.raffael.util.binding.validate.ValidationEvent;
import ch.raffael.util.binding.validate.ValidationListener;
import ch.raffael.util.i18n.I18N;
import ch.raffael.util.swing.actions.CommonAction;
import ch.raffael.util.swing.binding.TextComponentAdapter;
import ch.raffael.util.swing.binding.ValidationFeedbackManager;
import ch.raffael.util.swing.components.feedback.FeedbackPanel;

import static ch.raffael.util.binding.validate.Validators.*;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class CertificateBaseData extends AbstractActivity<JComponent> {

    private static Resources res = I18N.getBundle(Resources.class);

    private JTextField firstName;
    private JTextField lastName;
    private JTextField email;
    private JPanel root;

    private final PresentationModel pmodel = new PresentationModel();

    private final SimpleBinding<Certificate> certificate = pmodel.add(new SimpleBinding<Certificate>(new Certificate()));

    private boolean closeable = false;

    public CertificateBaseData() {
        super("Create New Certificate", "Blah blah blah", I18N.getBundle(HelpResources.class).meta().resource(URL.class, "someHelp"));
        pmodel.addValidationListener(new ValidationFeedbackManager());
        pmodel.add(new TextComponentAdapter())
                .install(firstName, certificate.<String>property("firstName")
                        .buffer()
                        .validator(notEmpty()));
        pmodel.add(new TextComponentAdapter())
                .install(lastName, certificate.<String>property("lastName")
                        .buffer()
                        .validator(notEmpty()));
        pmodel.add(new TextComponentAdapter())
                .install(email, certificate.<String>property("lastName")
                        .buffer()
                        .validator(notEmpty().and(email())));
        pmodel.scheduleInitialValidation(root);
    }

    @Override
    public boolean activityClosing() {
        if ( !closeable ) {
            return JOptionPane.showConfirmDialog(root, "Are you sure?", "Closing", JOptionPane.YES_NO_OPTION) == JOptionPane.OK_OPTION;
        }
        else {
            return true;
        }
    }

    @Override
    protected JComponent buildUi() {
        actions(new CommonAction("Continue", res.forwardIcon()) {
            @Override
            protected void init() {
                pmodel.addValidationListener(new ValidationListener() {
                    @Override
                    public void validationChanged(ValidationEvent event) {
                        setEnabled(pmodel.isValid());
                    }
                });
                setEnabled(pmodel.isValid());
            }
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    closeable = true;
                    navigator().open(new AbstractActivity<Component>(getTitle(), "Step Two", I18N.getBundle(HelpResources.class).meta().resource(URL.class, "someHelp")) {
                        @Override
                        protected Component buildUi() {
                            actions(new CommonAction("&Finish", res.okIcon()) {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    navigator().open(new CertificateManager());
                                }
                            });
                            return DevelUtilities.componentPlaceholder("Step two goes here");
                        }
                    });
                }
                finally {
                    closeable = false;
                }
            }
        });
        FeedbackPanel feedbackPanel = new FeedbackPanel(root);
        return feedbackPanel;
    }

}
