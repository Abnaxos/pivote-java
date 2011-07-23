package ch.piratenpartei.pivote.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import com.jidesoft.swing.JideButton;
import com.jidesoft.swing.JideSwingUtilities;
import com.jidesoft.swing.MultilineLabel;
import com.jidesoft.swing.StyledLabelBuilder;

import ch.raffael.util.i18n.I18N;
import ch.raffael.util.i18n.ResourceBundle;
import ch.raffael.util.swing.layout.VerticalFlowLayout;

import static ch.piratenpartei.pivote.ui.Colors.*;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class PiVoteBanner extends JPanel {

    private static Resources res = I18N.getBundle(Resources.class);

    private JPanel messagePanel;

    public PiVoteBanner() {
        super(new BorderLayout(5, 5));
        JLabel logoLabel = new JLabel(res.piVoteLogoBig());
        logoLabel.setVerticalAlignment(JLabel.TOP);
        add(logoLabel, BorderLayout.WEST);
        messagePanel = new JPanel(new VerticalFlowLayout(VerticalFlowLayout.Alignment.FILL, 5));
        add(messagePanel, BorderLayout.CENTER);
        add(new JSeparator(JSeparator.HORIZONTAL), BorderLayout.SOUTH);
    }

    public void addMessageComponent(Component comp) {
        messagePanel.add(comp);
    }

    public void addMessageComponent(Component comp, int index) {
        messagePanel.add(comp, index);
    }

    public void removeMessageComponent(Component comp) {
        messagePanel.remove(comp);
    }

    public void removeMessageComponent(int index) {
        messagePanel.remove(index);
    }

    public Component getMessageComponent(int index) {
        return messagePanel.getComponent(index);
    }

    public int getMessageComponentCount() {
        return messagePanel.getComponentCount();
    }

    public MessageDisplay addMessage(Icon icon, String title, String description, Action action, final ResourceBundle.Resource<URL> help) {
        MessageDisplay comp = message(icon, title, description, action, help);
        addMessageComponent(comp);
        return comp;
    }

    public static MessageDisplay message(Icon icon, String title, String description, Action action, final ResourceBundle.Resource<URL> help) {
        return new MessageDisplay(icon, title, description, action, help);
    }

    public static class MessageDisplay extends JPanel {

        private MessageDisplay(Icon icon, String title, String description, Action action, final ResourceBundle.Resource<URL> help) {
            super(new BorderLayout());
            JPanel banner = new JPanel(new BorderLayout(5, 5)) {
                @Override
                protected void paintComponent(Graphics g) {
                    JideSwingUtilities.fillNormalGradient((Graphics2D)g, new Rectangle(0, 0, getWidth(), getHeight()), colors().ppGradientDark(), colors().ppGradientBright(), false);
                    super.paintComponent(g);
                }
            };
            banner.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 3));
            banner.setOpaque(false);
            if ( icon != null ) {
                JLabel iconLabel = new JLabel(icon);
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
                        HelpBrowser.showDialog(MessageDisplay.this, help.get());
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

}
