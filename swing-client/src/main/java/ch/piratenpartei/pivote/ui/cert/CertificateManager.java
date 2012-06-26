/*
 * Copyright 2012 Piratenpartei Schweiz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.piratenpartei.pivote.ui.cert;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.text.MessageFormat;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

import ch.piratenpartei.pivote.HelpResources;
import ch.piratenpartei.pivote.Storage;
import ch.piratenpartei.pivote.model.crypto.AdminCertificate;
import ch.piratenpartei.pivote.model.crypto.AuthorityCertificate;
import ch.piratenpartei.pivote.model.crypto.Certificate;
import ch.piratenpartei.pivote.model.crypto.CertificateAttribute;
import ch.piratenpartei.pivote.model.crypto.NotaryCertificate;
import ch.piratenpartei.pivote.model.crypto.ServerCertificate;
import ch.piratenpartei.pivote.model.crypto.Signature;
import ch.piratenpartei.pivote.model.crypto.VoterCertificate;
import ch.piratenpartei.pivote.ui.AbstractActivity;
import ch.piratenpartei.pivote.ui.GradientPanel;
import com.google.common.base.Objects;
import com.jidesoft.swing.StyledLabel;
import com.jidesoft.swing.StyledLabelBuilder;
import org.joda.time.LocalDateTime;

import ch.raffael.util.i18n.I18N;
import ch.raffael.util.swing.SwingUtil;
import ch.raffael.util.swing.actions.ActionGroupBuilder;
import ch.raffael.util.swing.actions.ActionPresenter;
import ch.raffael.util.swing.actions.CommonAction;
import ch.raffael.util.swing.actions.CommonActionGroup;
import ch.raffael.util.swing.layout.VerticalFlowLayout;

import static java.lang.String.*;


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
        for ( Certificate cert : context().require(Storage.class).getCertificates().values() ) {
            list.add(new CertificateEntry(cert));
        }
        scrollPane.getVerticalScrollBar().setUnitIncrement(SwingUtil.getLineHeight(root, 3));
        scrollPane.getHorizontalScrollBar().setUnitIncrement(SwingUtil.getMWidth(root, 3));
        return root;
    }

    private class CertificateEntry extends JPanel {

        private final JLabel statusLabel;

        private final PropertyChangeListener certUpdater = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                update();
            }
        };

        private Certificate certificate;
        private final StyledLabel titleLabel;
        private final JLabel nameLabel;
        private final JLabel certIdLabel;
        private final JLabel validLabel;

        private CertificateEntry(Certificate cert) {
            super(new BorderLayout(5, 0));
            setOpaque(false);
            JPanel title = new GradientPanel(new BorderLayout(5, 0));
            title.setBorder(BorderFactory.createEmptyBorder(1, 3, 1, 3));
            statusLabel = new JLabel();
            title.add(statusLabel, BorderLayout.WEST);
            titleLabel = new StyledLabel();
            title.setOpaque(false);
            title.add(titleLabel, BorderLayout.CENTER);
            add(title, BorderLayout.NORTH);
            JPanel data = new JPanel(new VerticalFlowLayout(VerticalFlowLayout.Alignment.LEFT, 0));
            data.setOpaque(false);
            nameLabel = new JLabel();
            data.add(nameLabel);
            certIdLabel = new JLabel();
            data.add(certIdLabel);
            validLabel = new JLabel();
            data.add(validLabel);
            add(data, BorderLayout.CENTER);
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
            add(tbWrapper, BorderLayout.EAST);
            setCertificate(cert);
        }

        public Certificate getCertificate() {
            return certificate;
        }

        public void setCertificate(Certificate certificate) {
            if ( !Objects.equal(this.certificate, certificate) ) {
                if ( this.certificate != null ) {
                    this.certificate.removePropertyChangeListener(certUpdater);
                }
                this.certificate = certificate;
                if ( this.certificate != null ) {
                    this.certificate.addPropertyChangeListener(certUpdater);
                }
                update();
            }
        }

        private void update() {
            LocalDateTime validFrom = null;
            LocalDateTime validTo = null;
            for ( Signature sig : certificate.getSignatures() ) {
                if ( validFrom == null || (sig.getValidTo() != null && validFrom.compareTo(sig.getValidFrom()) < 0) ) {
                    validFrom = sig.getValidFrom();
                }
                if ( validTo == null || (sig.getValidTo() != null && validTo.compareTo(sig.getValidTo()) > 0) ) {
                    validTo = sig.getValidTo();
                }
            }
            boolean approved = !(validFrom == null || validTo == null || validFrom.compareTo(validTo) >= 0);
            statusLabel.setIcon(approved ? res.certificateApproved() : res.certificateWaitingForApproval());
            StyledLabelBuilder titleLabelBuilder = new StyledLabelBuilder();
            String certType;
            String certInfo;
            if ( certificate instanceof AdminCertificate ) {
                certType = "Admin";
                certInfo = ((AdminCertificate)certificate).getFullName();
            }
            else if ( certificate instanceof AuthorityCertificate ) {
                certType = "Authority";
                certInfo = ((AuthorityCertificate)certificate).getFullName();
            }
            else if ( certificate instanceof NotaryCertificate ) {
                certType = "Notary";
                certInfo = ((NotaryCertificate)certificate).getFullName();
            }
            else if ( certificate instanceof ServerCertificate ) {
                certType = "Server";
                certInfo = ((ServerCertificate)certificate).getFullName();
            }
            else if ( certificate instanceof VoterCertificate ) {
                certType = "Voter";
                certInfo = null;
            }
            else {
                certType = "Unknown Certificate Type";
                certInfo = certificate.getClass().toString();
            }
            titleLabelBuilder.add(certType, Font.BOLD);
            if ( certInfo != null ) {
                titleLabelBuilder.add(format(" (%s)", certInfo));
            }

            titleLabelBuilder.add(": ").add("[FIXME: group " + certificate.getAttributeValue(CertificateAttribute.Name.GROUP_ID) + "]", Font.ITALIC);
            titleLabelBuilder.configure(titleLabel);
            nameLabel.setText("[FIXME: name? email?]");
            certIdLabel.setText(format("Certificate ID: %s", certificate.getId()));
            if ( approved ) {
                validLabel.setText(MessageFormat.format("Valid from {0,date,long} to {1,date,long}",
                                                        validFrom.toDate(), validTo.toDate()));
            }
            else {
                validLabel.setText("Not valid");
            }
        }

    }

}
