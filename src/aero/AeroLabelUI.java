package aero;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.BasicLabelUI;

/** JLabel with desktop (ClearType) text anti-aliasing; also used for list / table / tree cell renderers. */
public class AeroLabelUI extends BasicLabelUI {
    private static final AeroLabelUI INSTANCE = new AeroLabelUI();
    public static ComponentUI createUI(JComponent c) { return INSTANCE; }

    @Override public void paint(Graphics g, JComponent c) {
        Graphics2D g2 = Aero.text((Graphics2D) g.create());
        super.paint(g2, c);
        g2.dispose();
    }
}
