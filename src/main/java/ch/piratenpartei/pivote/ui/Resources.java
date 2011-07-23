package ch.piratenpartei.pivote.ui;

import java.io.InputStream;

import javax.swing.ImageIcon;

import ch.piratenpartei.pivote.PiVoteResources;

import ch.raffael.util.i18n.Default;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public interface Resources extends PiVoteResources {

    @Default("What is this?")
    String whatIsThis();

    @Default("someHelp.html")
    byte[] someHelp();

    @Default("Current Votings")
    String menuCurrentVotings();

    @Default("Archived Votings")
    String menuArchivedVotings();

    @Default("Manage Certificates")
    String menuManageCertificates();

    @Default("Settings")
    String menuSettings();

}
