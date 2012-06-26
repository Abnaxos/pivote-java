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
package ch.piratenpartei.pivote;

import java.awt.Component;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.LogManager;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.bridge.SLF4JBridgeHandler;

import ch.piratenpartei.pivote.serialize.SerializationContext;
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

    private boolean developmentMode = Boolean.getBoolean("ch.piratenpartei.pivote.development");

    private PiVote() {
    }

    public boolean isDevelopmentMode() {
        return developmentMode;
    }

    private void start() throws Exception {
        //try {
        //    BasicService basic = (BasicService)ServiceManager.lookup(BasicService.class.getName());
        //    System.out.println(basic.getCodeBase());
        //}
        //catch ( UnavailableServiceException e ) {
        //    log.info("Not running in JNLP mode", e);
        //}
        SwingUtil.setupMetalLookAndFeel();
        //I18N.setLenient(true);
        Context rootContext = ContextManager.getInstance().getRoot();
        rootContext.put(PiVote.class, this);
        rootContext.put(SerializationContext.class, new SerializationContext());
        rootContext.put(Storage.class, FileSystemStorage.newInstance(rootContext.get(SerializationContext.class)));
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
        LogManager.getLogManager().reset();
        SLF4JBridgeHandler.install();
        new PiVote().start();
    }

}
