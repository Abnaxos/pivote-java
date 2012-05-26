package ch.piratenpartei.pivote;

import javax.swing.ImageIcon;

import ch.raffael.util.i18n.Default;
import ch.raffael.util.i18n.ResourceBundle;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public interface PiVoteResources extends ResourceBundle {

    @Default("Pi-Vote")
    String appName();
    @Default("0.1")
    String appVersion();

    @Default("accept.png")
    ImageIcon okIcon();
    @Default("cancel.png")
    ImageIcon cancelIcon();

    @Default("&Close")
    String close();
    @Default("cancel.png")
    ImageIcon closeIcon();

    @Default("information.png")
    ImageIcon infoIcon();
    @Default("error.png")
    ImageIcon errorIcon();
    @Default("help.png")
    ImageIcon helpIcon();

    @Default("forward_green.png")
    ImageIcon forwardIcon();
    @Default("backward_green.png")
    ImageIcon backwardIcon();
    @Default("home.png")
    ImageIcon homeIcon();

    @Default("pivoteBig.png")
    ImageIcon piVoteLogoBig();
    @Default("pivote32.png")
    ImageIcon piVoteLogo32();
    @Default("pp32.png")
    ImageIcon ppLogoSmall();
    @Default("pplogo.png")
    ImageIcon ppLogoFull();

}
