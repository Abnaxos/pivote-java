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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.Action;

import com.google.common.base.Preconditions;

import ch.raffael.util.i18n.ResourceBundle;
import ch.raffael.util.swing.context.Context;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public abstract class AbstractActivity<T extends Component> implements Activity {

    private final String title;
    private final String description;
    private final ResourceBundle.Resource<URL> help;

    private Navigator navigator;
    private Context context;
    private T component;

    private List<Action> actions = Collections.emptyList();

    protected AbstractActivity(String title, String description, ResourceBundle.Resource<URL> help) {
        this.title = title;
        this.description = description;
        this.help = help;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public ResourceBundle.Resource<URL> getHelp() {
        return help;
    }

    @Override
    public Component getComponent() {
        Preconditions.checkState(component != null, "Activity not opened");
        return component;
    }

    @Override
    public List<Action> getActions() {
        return Collections.unmodifiableList(actions);
    }

    @Override
    public void openActivity(Context context, Navigator navigator) {
        this.navigator = navigator;
        this.context = context;
        if ( component == null ) {
            component = buildUi();
        }
    }

    @Override
    public boolean activityClosing() {
        return true;
    }

    @Override
    public void activityClosed() {
    }

    protected T component() {
        return component;
    }

    protected Navigator navigator() {
        return navigator;
    }

    protected Context context() {
        return context;
    }

    protected void actions(List<Action> actions) {
        this.actions = actions;
    }

    protected void actions(Action... actions) {
        this.actions = Arrays.asList(actions);
    }

    protected abstract T buildUi();

    protected void destroyUi() {
        component = null;
    }

}
