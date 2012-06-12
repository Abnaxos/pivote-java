package ch.piratenpartei.pivote.ui.cert;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.UUID;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

import ch.piratenpartei.pivote.HelpResources;
import ch.piratenpartei.pivote.ui.AbstractActivity;
import ch.piratenpartei.pivote.ui.GradientPanel;
import com.jidesoft.swing.StyledLabel;
import com.jidesoft.swing.StyledLabelBuilder;

import ch.raffael.util.i18n.I18N;
import ch.raffael.util.swing.SwingUtil;
import ch.raffael.util.swing.actions.ActionGroupBuilder;
import ch.raffael.util.swing.actions.ActionPresenter;
import ch.raffael.util.swing.actions.CommonAction;
import ch.raffael.util.swing.actions.CommonActionGroup;
import ch.raffael.util.swing.layout.VerticalFlowLayout;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class CertificateManager extends AbstractActivity<JPanel> {

    private static Resources res = I18N.getBundle(Resources.class);

    private JPanel list;

    public CertificateManager() {
        super("Manage Certificates", "Here you can manage your certificates", I18N.getBundle(HelpResources.class).meta().resource(URL.class, "someHelp"));
    }

    @Override
    protected JPanel buildUi() {
        JPanel root = new JPanel(new BorderLayout());
        JToolBar toolbar = context().require(ActionPresenter.class).builder(JToolBar.class).addFlat(
                new ActionGroupBuilder(new CommonActionGroup("Toolbar"))
                        .add(new CommonAction(res, "newCertificate") {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                // FIXME: Not implemented
                            }
                        })
                        .add(new CommonAction(res, "importCertificate") {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                // FIXME: Not implemented
                            }
                        })
                        .get()
        ).build();
        root.add(toolbar, BorderLayout.NORTH);
        toolbar.add(Box.createHorizontalGlue());
        list = new JPanel(new VerticalFlowLayout(VerticalFlowLayout.Alignment.FILL, 5));
        list.setOpaque(true);
        list.setBackground(Color.WHITE);
        list.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        JScrollPane scrollPane = new JScrollPane(list);
        root.add(scrollPane);
        list.add(buildEntry(true));
        list.add(buildEntry(false));
        scrollPane.getVerticalScrollBar().setUnitIncrement(SwingUtil.getLineHeight(root, 3));
        scrollPane.getHorizontalScrollBar().setUnitIncrement(SwingUtil.getMWidth(root, 3));
        return root;
    }

    private JPanel buildEntry(boolean approved) {
        JPanel entry = new JPanel(new BorderLayout(5, 0));
        entry.setOpaque(false);
        JPanel title = new GradientPanel(new BorderLayout(5, 0));
        title.setBorder(BorderFactory.createEmptyBorder(1, 3, 1, 3));
        JLabel statusLabel = new JLabel(approved ? res.certificateApproved() : res.certificateWaitingForApproval());
        title.add(statusLabel, BorderLayout.WEST);
        StyledLabelBuilder titleLabelBuilder = new StyledLabelBuilder()
                .add("Voter", Font.BOLD)
                .add(": Pirate Party Switzerland");
        if ( !approved ) {
            titleLabelBuilder.add(approved ? "" : " (waiting for approval)", Font.ITALIC);
        }
        StyledLabel titleLabel = titleLabelBuilder.createLabel();
        title.setOpaque(false);
        title.add(titleLabel, BorderLayout.CENTER);
        entry.add(title, BorderLayout.NORTH);
        JPanel data = new JPanel(new VerticalFlowLayout(VerticalFlowLayout.Alignment.LEFT, 0));
        data.setOpaque(false);
        data.add(new JLabel("Raffael Herzog, herzog@raffael.ch"));
        data.add(new JLabel("Certificate Id: " + UUID.randomUUID().toString()));
        data.add(new JLabel("Valid from 1.1.2011 until 1.1.2015"));
        entry.add(data, BorderLayout.CENTER);
        JToolBar entryToolbar = context().require(ActionPresenter.class).builder(JToolBar.class).addFlat(
                new ActionGroupBuilder(new CommonActionGroup("Toolbar"))
                        .add(new CommonAction(res, "setCertificatePassword") {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                // FIXME: Not implemented
                            }
                        })
                        .add(new CommonAction(res, "exportCertificate") {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                // FIXME: Not implemented
                            }
                        })
                        .add(new CommonAction(res, "deleteCertificate") {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                // FIXME: Not implemented
                            }
                        })
                        .get()
        ).build();
        entryToolbar.setOpaque(false);
        entryToolbar.setBorder(null);
        JPanel tbWrapper = new JPanel(new BorderLayout());
        tbWrapper.setOpaque(false);
        tbWrapper.add(entryToolbar, BorderLayout.NORTH);
        entry.add(tbWrapper, BorderLayout.EAST);
        return entry;
    }

}
