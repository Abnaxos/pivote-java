package ch.piratenpartei.pivote;

import java.awt.Component;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.prefs.Preferences;

import javax.jnlp.BasicService;
import javax.jnlp.ServiceManager;
import javax.jnlp.UnavailableServiceException;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.slf4j.Logger;

import ch.piratenpartei.pivote.ui.AppPanel;
import com.jidesoft.dialog.JideOptionPane;
import org.jetbrains.annotations.NotNull;

import ch.raffael.util.common.logging.LogUtil;
import ch.raffael.util.i18n.I18N;
import ch.raffael.util.swing.SwingUtil;
import ch.raffael.util.swing.WindowPlacementManager;
import ch.raffael.util.swing.context.Context;
import ch.raffael.util.swing.context.ContextManager;
import ch.raffael.util.swing.error.AbstractErrorDisplayer;
import ch.raffael.util.swing.error.ErrorDisplayer;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public final class PiVote {

    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LogUtil.getLogger();
    private static PiVoteResources res = I18N.getBundle(PiVoteResources.class);

    private final Preferences prefs = Preferences.userNodeForPackage(PiVote.class);

    private File storageDirectory = new File(System.getProperty("user.home"), ".piress");
    private boolean developmentMode = Boolean.getBoolean("ch.piratenpartei.pivote.development");

    private PiVote() {
    }

    public File getStorageDirectory() {
        return storageDirectory;
    }

    public boolean isDevelopmentMode() {
        return developmentMode;
    }

    private void start() {
        try {
            BasicService basic = (BasicService)ServiceManager.lookup(BasicService.class.getName());
            System.out.println(basic.getCodeBase());
        }
        catch ( UnavailableServiceException e ) {
// FIXME: Handle exception
            e.printStackTrace();
        }
        SwingUtil.setupMetalLookAndFeel();
        //I18N.setLenient(true);
        if ( !storageDirectory.isDirectory() ) {
            log.info("Creating storage directory: {}", storageDirectory);
            if ( !storageDirectory.mkdirs() ) {
                log.error("Could not create storage directory: {}", storageDirectory);
            }
        }
        Context rootContext = ContextManager.getInstance().getRoot();
        rootContext.put(PiVote.class, this);
        rootContext.put(ErrorDisplayer.class, new AbstractErrorDisplayer() {
            @Override
            public void displayError(@NotNull Component component, String message, Throwable exception, Object details) {
                JideOptionPane pane = new JideOptionPane(message, JOptionPane.ERROR_MESSAGE, JOptionPane.OK_OPTION);
                if ( details != null ) {
                    pane.setDetails(details);
                }
                if ( exception != null ) {
                    log.error("Exception caught", exception);
                    StringWriter str = new StringWriter();
                    PrintWriter print = new PrintWriter(str);
                    exception.printStackTrace(print);
                    print.flush();
                    if ( details == null ) {
                        pane.setDetails(str.toString());
                    }
                }
            }
        });
        JFrame mainFrame = new JFrame(res.appName());
        rootContext.attach(mainFrame);
        AppPanel appPanel = new AppPanel();
        appPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        mainFrame.setContentPane(appPanel);
        appPanel.updateMessages();
        mainFrame.pack();
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setIconImage(res.piVoteLogo32().getImage());
        rootContext.require(WindowPlacementManager.class).bindToPrefs(mainFrame, prefs, "mainFrame-%s");
        mainFrame.setVisible(true);
    }

    public static void main(String[] args) throws Exception {
        new PiVote().start();
    }

}
