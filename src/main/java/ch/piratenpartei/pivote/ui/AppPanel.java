package ch.piratenpartei.pivote.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.List;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ch.piratenpartei.pivote.HelpResources;
import ch.piratenpartei.pivote.PiVote;
import ch.piratenpartei.pivote.ui.cert.CertificateBaseData;
import com.google.common.base.Objects;
import com.jidesoft.swing.JideButton;

import ch.raffael.util.i18n.I18N;
import ch.raffael.util.i18n.ResourceBundle;
import ch.raffael.util.swing.actions.CommonAction;
import ch.raffael.util.swing.context.Context;
import ch.raffael.util.swing.layout.VerticalFlowLayout;

import static ch.piratenpartei.pivote.DevelUtilities.*;
import static ch.raffael.util.swing.context.ContextManager.*;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class AppPanel extends JPanel implements Activity.Navigator {

    private static Resources res = I18N.getBundle(Resources.class);
    private static HelpResources helpRes = I18N.getBundle(HelpResources.class);

    private final ImageIcon logoIcon = res.ppLogoFull();

    private JDialog develDialog = null;
    private PiVoteBanner banner;
    private JPanel mainMenu;

    private Action closeActivityAction = new CommonAction(res, "closeActivity") {
        @Override
        public void actionPerformed(ActionEvent e) {
            open(null);
        }
    };

    private Activity activity;
    private Container content;

    public AppPanel() {
        setLayout(new BorderLayout(5, 5));

        banner = new PiVoteBanner();
        add(banner, BorderLayout.NORTH);

        //JideButton about = new JideButton(res.piVoteLogoBig());
        //about.setButtonStyle(JideButton.HYPERLINK_STYLE);
        //about.addActionListener(new ActionListener() {
        //    @Override
        //    public void actionPerformed(ActionEvent e) {
        //        JOptionPane.showMessageDialog(MainMenu.this, "This is the about text: " + res.appName() + " " + res.appVersion(), "About " + res.appName(), JOptionPane.INFORMATION_MESSAGE);
        //    }
        //});
        //about.setVerticalAlignment(JideButton.TOP);
        //top.add(about, BorderLayout.WEST);
        //add(top, BorderLayout.NORTH);
        //
        //messagesPanel = new JPanel(new VerticalFlowLayout(VerticalFlowLayout.Alignment.FILL, 5));
        //top.add(messagesPanel, BorderLayout.CENTER);

        JPanel menu = new JPanel(new GridLayout(0, 2, 5, 5));
        menu.add(menuButton(new CommonAction(res, "menuCurrentVotings") {
            @Override
            public void actionPerformed(ActionEvent e) {
                open(activityPlaceholder("Current Votings", "See vote votings are currently open and their status"));
            }
        }));
        menu.add(menuButton(new CommonAction(res, "menuArchivedVotings") {
            @Override
            public void actionPerformed(ActionEvent e) {
                open(activityPlaceholder("Archived Votings", "All past votings and their results"));
            }
        }));
        menu.add(menuButton(new CommonAction(res, "menuManageCertificates") {
            @Override
            public void actionPerformed(ActionEvent e) {
                open(activityPlaceholder("Manage Certificate", "Manage your certificates. Also contains a button for the new certificate wizard (for now, use the message on the main screen)"));
            }
        }));
        menu.add(menuButton(new CommonAction(res, "menuSettings") {
            @Override
            public void actionPerformed(ActionEvent e) {
                open(activityPlaceholder("Settings", "Various settings"));
            }
        }));
        mainMenu = new JPanel(new FlowLayout(FlowLayout.CENTER));
        mainMenu.add(menu);
        add(mainMenu, BorderLayout.CENTER);
        JLabel versionLabel = new JLabel(res.appName() + " " + res.appVersion());
        versionLabel.setFont(versionLabel.getFont().deriveFont(8f));
        versionLabel.setVerticalAlignment(JLabel.BOTTOM);
        JLabel logoLabel = new JLabel(res.ppLogoFull());
        logoLabel.setVerticalAlignment(JLabel.BOTTOM);
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(versionLabel, BorderLayout.WEST);
        bottomPanel.add(logoLabel, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        content = mainMenu;
    }

    private static JideButton menuButton(Action action) {
        JideButton button = new JideButton(action);
        button.setButtonStyle(JideButton.TOOLBOX_STYLE);
        button.setBorder(new CompoundBorder(button.getBorder(), BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        return button;
    }

    @Override
    public void open(Activity activity) {
        if ( Objects.equal(activity, this.activity) ) {
            return;
        }
        if ( this.activity != null ) {
            if ( !this.activity.activityClosing() ) {
                return;
            }
        }
        remove(content);
        content = null;
        if ( this.activity != null ) {
            this.activity.activityClosed();
        }
        this.activity = activity;
        if ( activity == null ) {
            add(mainMenu, BorderLayout.CENTER);
            content = mainMenu;
            updateMessages(); // this will also do revalidate()/repaint()
        }
        else {
            Context actCtx = context(this).create();
            JPanel actPanel = new JPanel(new BorderLayout(5, 5));
            actPanel.add(activity.getComponent(), BorderLayout.CENTER);
            JPanel buttons = new JPanel(new GridLayout(1, 0, 5, 0));
            List<Action> actions = activity.getActions();
            if ( actions != null ) {
                for ( Action action : actions ) {
                    buttons.add(new JButton(action));
                }
            }
            buttons.add(new JButton(closeActivityAction));
            JPanel bottom = new JPanel(new BorderLayout());
            bottom.add(buttons, BorderLayout.EAST);
            actPanel.add(bottom, BorderLayout.SOUTH);
            add(actPanel, BorderLayout.CENTER);
            content = actPanel;
            while ( banner.getMessageComponentCount() > 0 ) {
                banner.removeMessageComponent(0);
            }
            banner.addMessage(null, activity.getTitle(), activity.getDescription(), null, activity.getHelp());
            revalidate();
            activity.activityOpenend(this, actCtx);
            repaint();
        }
    }

    @Override
    public void close() {
        open(null);
    }

    public void updateMessages() {
        while ( banner.getMessageComponentCount() > 0 ) {
            banner.removeMessageComponent(0);
        }
        if ( context(this).require(PiVote.class).isDevelopmentMode() ) {
            MessagesState state;
            if ( develDialog == null ) {
                develDialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Development");
                develDialog.setModalityType(Dialog.ModalityType.MODELESS);
                state = new MessagesState();
                state.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                develDialog.setContentPane(state);
                develDialog.pack();
                develDialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
                develDialog.setVisible(true);
            }
            else {
                state = (MessagesState)develDialog.getContentPane();
            }
            boolean hasPPSCert = true;
            boolean hasSectionCert = true;
            if ( !state.hasCert.isSelected() ) {
                banner.addMessage(
                        res.errorIcon(), "You have no certificate",
                        "You need to generate a certificate",
                        new CommonAction("Generate certificate") {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                open(new CertificateBaseData());
                            }
                        }, helpPlaceholder());
                hasPPSCert = false;
            }
            else if ( !state.certVerified.isSelected() ) {
                banner.addMessage(
                        res.errorIcon(), "Your certificate is not verified yet",
                        "You need to contact authorities and submit your certificate request.",
                        null, helpPlaceholder());
                hasPPSCert = false;
            }
            if ( !hasPPSCert ) {
                hasSectionCert = false;
            }
            if ( !state.hasSectionCert.isSelected() ) {
                banner.addMessage(
                        res.errorIcon(), "You have no section certificate",
                        "You need to generate a section certificate",
                        new CommonAction("Generate section certificate") {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                open(new CertificateBaseData());
                            }
                        }, helpPlaceholder());
                hasSectionCert = false;
            }
            else if ( !state.sectionCertVerified.isSelected() ) {
                banner.addMessage(
                        res.errorIcon(), "Your section certificate is not verified yet",
                        "The executive board needs to verify your section certificate",
                        null, helpPlaceholder());
                hasSectionCert = false;
            }
            if ( hasPPSCert && state.ballotAvailable.isSelected() ) {
                banner.addMessage(
                        res.infoIcon(), "There is a new ballot available",
                        "You may now vote",
                        new CommonAction("Vote now") {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                open(activityPlaceholder("Vote", "Well, vote."));
                            }
                        }, helpPlaceholder());
            }
            if ( hasSectionCert && state.sectionBallotAvailable.isSelected() ) {
                banner.addMessage(
                        res.infoIcon(), "There is a new section ballot available",
                        "You may now vote",
                        new CommonAction("Vote now") {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                open(activityPlaceholder("Vote", "Well, vote."));
                            }
                        }, helpPlaceholder());
            }
            if ( state.resultsAvailable.isSelected() ) {
                banner.addMessage(
                        res.infoIcon(), "There are new results available",
                        "New results are available",
                        new CommonAction("View Results") {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                open(activityPlaceholder("View Results", "Some nice view of the current results."));
                            }
                        }, helpPlaceholder());
            }
            if ( banner.getMessageComponentCount() == 0 ) {
                banner.addMessage(
                        res.infoIcon(), "There are currently no messages",
                        null, null, null);
            }
        }
        revalidate();
        repaint();
    }

    private ResourceBundle.Resource<URL> helpPlaceholder() {
        return helpRes.meta().resource(URL.class, "someHelp");
    }

    private class MessagesState extends JPanel implements ChangeListener {

        private final JCheckBox hasCert = new JCheckBox("User has certificate");
        private final JCheckBox certVerified = new JCheckBox("The user's certificate is verified");
        private final JCheckBox hasSectionCert = new JCheckBox("User has section certificate");
        private final JCheckBox sectionCertVerified = new JCheckBox("The user's section certificate is verified");
        private final JCheckBox ballotAvailable = new JCheckBox("There is an open ballot available");
        private final JCheckBox sectionBallotAvailable = new JCheckBox("There is an open section ballot available");
        private final JCheckBox resultsAvailable = new JCheckBox("There are new results available");

        private MessagesState() {
            super(new VerticalFlowLayout(VerticalFlowLayout.Alignment.LEFT, 5));
            add(hasCert);
            add(certVerified);
            add(hasSectionCert);
            add(sectionCertVerified);
            add(ballotAvailable);
            add(sectionBallotAvailable);
            add(resultsAvailable);
            hasCert.addChangeListener(this);
            certVerified.addChangeListener(this);
            hasSectionCert.addChangeListener(this);
            sectionCertVerified.addChangeListener(this);
            ballotAvailable.addChangeListener(this);
            sectionBallotAvailable.addChangeListener(this);
            resultsAvailable.addChangeListener(this);
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            updateMessages();
        }
    }

}
