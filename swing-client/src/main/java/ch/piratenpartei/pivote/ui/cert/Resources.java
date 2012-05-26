package ch.piratenpartei.pivote.ui.cert;

import javax.swing.ImageIcon;

import ch.piratenpartei.pivote.PiVoteResources;

import ch.raffael.util.i18n.Default;
import ch.raffael.util.i18n.Forward;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public interface Resources extends PiVoteResources {

    @Default("New Certificate...")
    String newCertificate();

    @Default("cert_add.png")
    ImageIcon newCertificateIcon();

    @Default("Delete Certificate")
    String deleteCertificate();

    @Default("cert_delete.png")
    ImageIcon deleteCertificateIcon();

    @Default("Set Certificate Password")
    String setCertificatePassword();

    @Default("cert_pass.png")
    ImageIcon setCertificatePasswordIcon();

    @Default("Export Certificate...")
    String exportCertificate();

    @Default("export.png")
    ImageIcon exportCertificateIcon();

    @Default("Import Certificate...")
    String importCertificate();

    @Default("import.png")
    ImageIcon importCertificateIcon();

    @Default("waiting.png")
    ImageIcon certificateWaitingForApproval();

    @Forward(method = "okIcon")
    ImageIcon certificateApproved();
}
