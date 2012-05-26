package ch.piratenpartei.pivote.ui;

import javax.swing.ImageIcon;

import ch.piratenpartei.pivote.PiVoteResources;

import ch.raffael.util.i18n.Default;
import ch.raffael.util.i18n.Forward;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public interface Resources extends PiVoteResources {

    @Default("What is this?")
    String whatIsThis();

    @Default("Current Votings")
    String menuCurrentVotings();

    @Default("Archived Votings")
    String menuArchivedVotings();

    @Default("Manage Certificates")
    String menuManageCertificates();

    @Default("Settings")
    String menuSettings();

    @Default("Close")
    String closeActivity();
    @Forward(method = "cancelIcon")
    ImageIcon closeActivityIcon();

}
