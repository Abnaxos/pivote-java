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

import java.awt.Component;
import java.net.URL;
import java.util.List;

import javax.swing.Action;

import ch.raffael.util.i18n.ResourceBundle;
import ch.raffael.util.swing.context.Context;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public interface Activity {

    String getTitle();

    String getDescription();

    ResourceBundle.Resource<URL> getHelp();

    Component getComponent();

    List<Action> getActions();

    void openActivity(Context context, Navigator navigator);

    boolean activityClosing();
    void activityClosed();

    interface Navigator {
        void open(Activity activity);
        void close();
    }

}
