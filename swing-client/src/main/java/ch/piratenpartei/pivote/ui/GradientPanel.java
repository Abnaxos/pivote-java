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
