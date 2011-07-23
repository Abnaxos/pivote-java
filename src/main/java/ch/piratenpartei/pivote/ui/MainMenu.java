package ch.piratenpartei.pivote.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ch.piratenpartei.pivote.HelpResources;
import ch.piratenpartei.pivote.PiVote;
import com.jidesoft.dialog.ButtonPanel;
import com.jidesoft.dialog.StandardDialog;
import com.jidesoft.swing.JideButton;
import com.jidesoft.swing.JideSwingUtilities;
import com.jidesoft.swing.MultilineLabel;
import com.jidesoft.swing.StyledLabelBuilder;

import ch.raffael.util.i18n.I18N;
import ch.raffael.util.i18n.ResourceBundle;
import ch.raffael.util.swing.actions.CommonAction;
import ch.raffael.util.swing.layout.VerticalFlowLayout;

import static ch.piratenpartei.pivote.DevelUtilities.*;
import static ch.piratenpartei.pivote.ui.Colors.*;
import static ch.raffael.util.swing.context.ContextManager.*;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class MainMenu extends JPanel {

    private static Resources res = I18N.getBundle(Resources.class);
    private static HelpResources helpRes = I18N.getBundle(HelpResources.class);

    private final ImageIcon logoIcon = res.ppLogoFull();

    private JDialog develDialog = null;
    private JPanel messagesPanel;

    public MainMenu() {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBackground(Color.WHITE);

        JPanel top = new JPanel(new BorderLayout(5, 5));
        JideButton about = new JideButton(res.piVoteLogoBig());
        about.setButtonStyle(JideButton.HYPERLINK_STYLE);
        about.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(MainMenu.this, "This is the about text: " + res.appName() + " " + res.appVersion(), "About " + res.appName(), JOptionPane.INFORMATION_MESSAGE);
            }
        });
        about.setVerticalAlignment(JideButton.TOP);
        top.add(about, BorderLayout.WEST);
        add(top, BorderLayout.NORTH);

        messagesPanel = new JPanel(new VerticalFlowLayout(VerticalFlowLayout.Alignment.FILL, 5));
        top.add(messagesPanel, BorderLayout.CENTER);

        JPanel menu = new JPanel(new GridLayout(0, 2, 5, 5));
        menu.add(menuButton(new CommonAction(res, "menuCurrentVotings") {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionPlaceholder(MainMenu.this, "Current Votings");
            }
        }));
        menu.add(menuButton(new CommonAction(res, "menuArchivedVotings") {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionPlaceholder(MainMenu.this, "Archived Votings");
            }
        }));
        menu.add(menuButton(new CommonAction(res, "menuManageCertificates") {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionPlaceholder(MainMenu.this, "Manage Certificates");
            }
        }));
        menu.add(menuButton(new CommonAction(res, "menuSettings") {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionPlaceholder(MainMenu.this, "Settings");
            }
        }));
        JPanel menuContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        menuContainer.add(menu);
        menu.setOpaque(false);
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.add(new JSeparator(JSeparator.HORIZONTAL), BorderLayout.NORTH);
        centerPanel.add(menuContainer, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);
        JLabel versionLabel = new JLabel(res.appName() + " " + res.appVersion());
        versionLabel.setFont(versionLabel.getFont().deriveFont(8f));
        versionLabel.setVerticalAlignment(JLabel.BOTTOM);
        JLabel logoLabel = new JLabel(res.ppLogoFull());
        logoLabel.setVerticalAlignment(JLabel.BOTTOM);
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(versionLabel, BorderLayout.WEST);
        bottomPanel.add(logoLabel, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private static JideButton menuButton(Action action) {
        JideButton button = new JideButton(action);
        button.setButtonStyle(JideButton.TOOLBOX_STYLE);
        button.setBorder(new CompoundBorder(button.getBorder(), BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        return button;
    }

    public void updateMessages() {
        while ( messagesPanel.getComponentCount() > 0 ) {
            messagesPanel.remove(0);
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
                messagesPanel.add(new MessageDisplay(
                        res.errorIcon(), "You have no certificate",
                        "You need to generate a certificate",
                        new CommonAction("Generate certificate") {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(messagesPanel));
                                
                                actionPlaceholder(messagesPanel, "Generate Certificate Wizard");
                            }
                        }, helpPlaceholder()));
                hasPPSCert = false;
            }
            else if ( !state.certVerified.isSelected() ) {
                messagesPanel.add(new MessageDisplay(
                        res.errorIcon(), "Your certificate is not verified yet",
                        "You need to contact authorities and submit your certificate request.",
                        null, helpPlaceholder()));
                hasPPSCert = false;
            }
            if ( !hasPPSCert ) {
                hasSectionCert = false;
            }
            if ( !state.hasSectionCert.isSelected() ) {
                messagesPanel.add(new MessageDisplay(
                        res.errorIcon(), "You have no section certificate",
                        "You need to generate a section certificate",
                        new CommonAction("Generate section certificate") {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                actionPlaceholder(messagesPanel, "Generate Section Certificate Wizard");
                            }
                        }, helpPlaceholder()));
                hasSectionCert = false;
            }
            else if ( !state.sectionCertVerified.isSelected() ) {
                messagesPanel.add(new MessageDisplay(
                        res.errorIcon(), "Your section certificate is not verified yet",
                        "The executive board needs to verify your section certificate",
                        null, helpPlaceholder()));
                hasSectionCert = false;
            }
            if ( hasPPSCert && state.ballotAvailable.isSelected() ) {
                messagesPanel.add(new MessageDisplay(
                        res.infoIcon(), "There is a new ballot available",
                        "You may now vote",
                        new CommonAction("Vote now") {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                actionPlaceholder(messagesPanel, "Vote");
                            }
                        }, helpPlaceholder()));
            }
            if ( hasSectionCert && state.sectionBallotAvailable.isSelected() ) {
                messagesPanel.add(new MessageDisplay(
                        res.infoIcon(), "There is a new section ballot available",
                        "You may now vote",
                        new CommonAction("Vote now") {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                actionPlaceholder(messagesPanel, "Vote");
                            }
                        }, helpPlaceholder()));
            }
            if ( state.resultsAvailable.isSelected() ) {
                messagesPanel.add(new MessageDisplay(
                        res.infoIcon(), "There are new results available",
                        "New results are available",
                        new CommonAction("View Results") {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                actionPlaceholder(messagesPanel, "View Results");
                            }
                        }, helpPlaceholder()));
            }
            if ( messagesPanel.getComponentCount() == 0 ) {
                messagesPanel.add(new MessageDisplay(
                        res.infoIcon(), "There are currently no messages",
                        null, null, null));
            }
        }
        revalidate();
        repaint();
    }

    private ResourceBundle.Resource<URL> helpPlaceholder() {
        return helpRes.meta().resource(URL.class, "someHelp");
    }

    private class MessageDisplay extends JPanel {

        private MessageDisplay(Icon icon, String title, String description, Action action, final ResourceBundle.Resource<URL> help) {
            super(new BorderLayout());
            JPanel banner = new JPanel(new BorderLayout(5, 5)) {
                @Override
                protected void paintComponent(Graphics g) {
                    JideSwingUtilities.fillNormalGradient((Graphics2D)g, new Rectangle(0, 0, getWidth(), getHeight()), colors().ppGradientDark(), colors().ppGradientBright(), false);
                    super.paintComponent(g);
                }
            };
            banner.setOpaque(false);
            if ( icon != null ) {
                JLabel iconLabel = new JLabel(icon);
                iconLabel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
                banner.add(iconLabel, BorderLayout.WEST);
            }
            banner.add(new StyledLabelBuilder().add(title, Font.BOLD).createLabel(), BorderLayout.CENTER);
            if ( help != null ) {
                JideButton helpButton = new JideButton(res.helpIcon());
                helpButton.setButtonStyle(JideButton.HYPERLINK_STYLE);
                helpButton.setToolTipText(res.whatIsThis());
                helpButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        HelpBrowser.showDialog(MainMenu.this, help.get());
                    }
                });
                banner.add(helpButton, BorderLayout.EAST);
            }
            add(banner, BorderLayout.NORTH);
            if ( description != null ) {
                MultilineLabel descriptionLabel = new MultilineLabel(description);
                add(descriptionLabel, BorderLayout.CENTER);
            }
            if ( action != null ) {
                JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
                JideButton actionButton = new JideButton(action);
                actionButton.setButtonStyle(JideButton.HYPERLINK_STYLE);
                actionButton.setForeground(colors().link());
                actionPanel.add(actionButton);
                add(actionPanel, BorderLayout.SOUTH);
            }
        }

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
