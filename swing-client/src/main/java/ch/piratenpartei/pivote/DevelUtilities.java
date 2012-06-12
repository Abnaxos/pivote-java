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

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.net.URL;

import javax.swing.JOptionPane;

import ch.piratenpartei.pivote.ui.AbstractActivity;
import ch.piratenpartei.pivote.ui.Activity;
import com.jidesoft.swing.StyledLabel;
import com.jidesoft.swing.StyledLabelBuilder;

import ch.raffael.util.i18n.I18N;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public final class DevelUtilities {

    private DevelUtilities() {
    }

    public static void actionPlaceholder(Component component, String action) {
        JOptionPane.showMessageDialog(component, action, "Action Placeholder", JOptionPane.INFORMATION_MESSAGE);
    }

    public static Component componentPlaceholder(String description) {
        StyledLabel label = new StyledLabelBuilder().add("Component Placeholder: ", Font.BOLD).add(description).createLabel();
        label.setBackground(Color.ORANGE);
        label.setOpaque(true);
        label.setHorizontalAlignment(StyledLabel.CENTER);
        return label;
    }

    public static Activity activityPlaceholder(final String title, String description) {
        return new AbstractActivity<Component>(title, description, I18N.getBundle(HelpResources.class).meta().resource(URL.class, "someHelp")) {
            @Override
            protected Component buildUi() {
                return componentPlaceholder(title);
            }
        };
    }

}
