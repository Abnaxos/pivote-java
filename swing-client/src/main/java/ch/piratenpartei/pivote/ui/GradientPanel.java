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

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.Rectangle;

import javax.swing.JPanel;

import com.jidesoft.swing.JideSwingUtilities;

import static ch.piratenpartei.pivote.ui.Colors.*;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class GradientPanel extends JPanel {


    public GradientPanel(LayoutManager layout, boolean isDoubleBuffered) {
        super(layout, isDoubleBuffered);
    }

    public GradientPanel(LayoutManager layout) {
        super(layout);
    }

    public GradientPanel(boolean isDoubleBuffered) {
        super(isDoubleBuffered);
    }

    public GradientPanel() {
    }

    @Override
    protected void paintComponent(Graphics g) {
        JideSwingUtilities.fillNormalGradient((Graphics2D)g, new Rectangle(0, 0, getWidth(), getHeight()), colors().ppGradientDark(), colors().ppGradientBright(), false);
        super.paintComponent(g);
    }
}
