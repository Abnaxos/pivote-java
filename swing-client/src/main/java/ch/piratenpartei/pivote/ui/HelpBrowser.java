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
package ch.piratenpartei.pivote.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.Document;

import ch.piratenpartei.pivote.HelpResources;
import ch.piratenpartei.pivote.PiVoteResources;
import com.google.common.base.Throwables;
import com.jidesoft.dialog.BannerPanel;

import ch.raffael.util.beans.Observable;
import ch.raffael.util.i18n.I18N;
import ch.raffael.util.i18n.ResourceBundle;
import ch.raffael.util.swing.WindowPlacementManager;
import ch.raffael.util.swing.actions.ActionGroupBuilder;
import ch.raffael.util.swing.actions.ActionPresenter;
import ch.raffael.util.swing.actions.CommonAction;
import ch.raffael.util.swing.actions.CommonActionGroup;
import ch.raffael.util.swing.context.Context;
import ch.raffael.util.swing.layout.VerticalFlowLayout;

import static ch.piratenpartei.pivote.ui.Colors.*;
import static ch.raffael.util.swing.context.ContextManager.*;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class HelpBrowser extends JPanel implements Observable {

    private static final HelpResources res = I18N.getBundle(HelpResources.class);
    private static final Preferences prefs = Preferences.userRoot();

    private final JEditorPane display = new JEditorPane();
    private final BannerPanel title = new BannerPanel("Title");

    private final LinkedList<URL> history = new LinkedList<URL>();
    private int historyPosition = -1;
    private final URL homeUrl;
    private JPanel topPanel;

    public HelpBrowser(URL homeUrl) {
        super(new BorderLayout(0, 0));
        this.homeUrl = homeUrl;
        topPanel = new JPanel(new VerticalFlowLayout(VerticalFlowLayout.Alignment.FILL, 0));
        title.setGradientPaint(colors().ppGradientDark(), colors().ppGradientBright(), false);
        topPanel.add(title);
        add(BorderLayout.NORTH, topPanel);
        display.setEditable(false);
        display.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent evt) {
                if ( evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED ) {
                    open(evt.getURL());
                }
            }
        });
        //display.setEditorKitForContentType("text/html", new HTMLEditorKit() {
        //    @Override
        //    public Document createDefaultDocument() {
        //        Document doc = super.createDefaultDocument();
        //        ((HTMLDocument)doc).setAsynchronousLoadPriority(-1);
        //        return doc;
        //    }
        //});
        add(BorderLayout.CENTER, new JScrollPane(display));
        open(homeUrl);
    }

    public void setupToolbar() {
        ActionGroupBuilder builder = new ActionGroupBuilder(new CommonActionGroup("Toolbar"));
        builder.add(new HistoryAction(res, "back") {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if ( historyPosition > 0 ) {
                    setHistoryPosition(historyPosition - 1);
                }
            }
            @Override
            protected void updateState() {
                setEnabled(historyPosition > 0);
            }
        });
        builder.add(new HistoryAction(res, "forward") {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if ( historyPosition < history.size() - 1 ) {
                    setHistoryPosition(historyPosition + 1);
                }
            }
            @Override
            protected void updateState() {
                setEnabled(historyPosition < history.size() - 1);
            }
        });
        builder.add(new CommonAction(res, "home") {
            @Override
            public void actionPerformed(ActionEvent evt) {
                open(homeUrl);
            }
        });
        JToolBar toolbar = context(this).require(ActionPresenter.class).builder(JToolBar.class).addFlat(builder.get()).getTarget();
        //toolbar.add(Box.createHorizontalStrut(10));
        //toolbar.add(title);
        topPanel.add(toolbar);
    }

    public void open(URL url) {
        while ( historyPosition >= 0 && history.size() > historyPosition + 1 ) {
            history.removeLast();
        }
        history.add(url);
        setHistoryPosition(historyPosition + 1);
    }

    public int getHistoryPosition() {
        return historyPosition;
    }

    public void setHistoryPosition(int historyPosition) {
        if ( this.historyPosition != historyPosition ) {
            int oldValue = this.historyPosition;
            this.historyPosition = historyPosition;
            firePropertyChange("historyPosition", oldValue, historyPosition);
            try {
                final URL page = history.get(historyPosition);
                display.setPage(page);
                final Document document = display.getDocument();
                document.addDocumentListener(new DocumentListener() {
                    @Override
                    public void insertUpdate(DocumentEvent e) {
                        update();
                    }
                    @Override
                    public void removeUpdate(DocumentEvent e) {
                        update();
                    }
                    @Override
                    public void changedUpdate(DocumentEvent e) {
                        update();
                    }
                    private void update() {
                        if ( updateTitle(page) ) {
                            document.removeDocumentListener(this);
                        }
                    }
                });
                updateTitle(page);
            }
            catch ( IOException e ) {
                title.setTitle("Error loading help document");
                display.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
                display.setText(Throwables.getStackTraceAsString(e));
            }
        }
    }

    private boolean updateTitle(URL page) {
        Object t = display.getDocument().getProperty("title");
        if ( t == null ) {
            title.setTitle(page.toString());
            return false;
        }
        else {
            title.setTitle(String.valueOf(t));
            return true;
        }
    }

    private abstract class HistoryAction extends CommonAction implements PropertyChangeListener {

        protected HistoryAction(String name) {
            super(name);
        }

        protected HistoryAction(String name, KeyStroke accelerator) {
            super(name, accelerator);
        }

        protected HistoryAction(String name, Icon icon) {
            super(name, icon);
        }

        protected HistoryAction(String name, Icon icon, KeyStroke accelerator) {
            super(name, icon, accelerator);
        }

        protected HistoryAction(String name, boolean enabled) {
            super(name, enabled);
        }

        protected HistoryAction(String name, KeyStroke accelerator, boolean enabled) {
            super(name, accelerator, enabled);
        }

        protected HistoryAction(String name, Icon icon, boolean enabled) {
            super(name, icon, enabled);
        }

        protected HistoryAction(String name, Icon icon, KeyStroke accelerator, boolean enabled) {
            super(name, icon, accelerator, enabled);
        }

        protected HistoryAction(ResourceBundle resources, String baseName) {
            super(resources, baseName);
        }

        protected HistoryAction(ResourceBundle resources, String baseName, boolean enabled) {
            super(resources, baseName, enabled);
        }

        @Override
        protected void init() {
            super.init();
            HelpBrowser.this.addPropertyChangeListener(this);
            updateState();
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if ( "historyPosition".equals(evt.getPropertyName()) ) {
                updateState();
            }
        }

        protected abstract void updateState();

    }

    public static class Dialog extends JDialog {
        private final HelpBrowser helpBrowser;
        public Dialog(Window owner, URL homeUrl) {
            super(owner, res.help(I18N.getBundle(PiVoteResources.class).appName()));
            setModalityType(ModalityType.MODELESS);
            setDefaultCloseOperation(HIDE_ON_CLOSE);
            helpBrowser = new HelpBrowser(homeUrl);
            helpBrowser.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            setContentPane(helpBrowser);
            pack();
            Context context = context(owner).create(helpBrowser);
            context.require(WindowPlacementManager.class).placeDialog(owner, this);
            context.require(WindowPlacementManager.class).bindToPrefs(this, prefs, "helpDialog-%s");
            validate();
        }

        public HelpBrowser getHelpBrowser() {
            return helpBrowser;
        }
    }

    public static void showDialog(Component component, URL url) {
        Dialog dialog = context(component).get(Dialog.class);
        if ( dialog == null ) {
            dialog = new Dialog(SwingUtilities.getWindowAncestor(component), res.tableOfContents());
            context(component).put(Dialog.class, dialog);
            dialog.getHelpBrowser().setupToolbar();
        }
        if ( url != null ) {
            dialog.getHelpBrowser().open(url);
        }
        dialog.setVisible(true);
        dialog.toFront();
    }

}
