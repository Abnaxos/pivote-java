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

import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import ch.raffael.util.i18n.Default;
import ch.raffael.util.i18n.Forward;
import ch.raffael.util.i18n.ResourceBundle;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public interface HelpResources extends ResourceBundle {

    @Default("{0} Help")
    String help(String appName);

    @Default("Forward")
    String forward();
    @Forward(bundle = PiVoteResources.class, method = "forwardIcon")
    ImageIcon forwardIcon();
    @Default("control alt RIGHT")
    KeyStroke forwardKey();

    @Default("Back")
    String back();
    @Forward(bundle = PiVoteResources.class, method = "backwardIcon")
    ImageIcon backIcon();
    @Default("control alt LEFT")
    KeyStroke backKey();

    @Default("Home")
    String home();
    @Forward(bundle = PiVoteResources.class, method = "homeIcon")
    ImageIcon homeIcon();
    @Default("alt HOME")
    KeyStroke homeKey();

    @Default("help/toc.html")
    URL tableOfContents();

    @Default("help/someHelp.html")
    URL someHelp();

}
