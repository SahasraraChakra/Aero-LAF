package aero;

import java.awt.*;
import java.beans.PropertyChangeListener;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.BasicPanelUI;

/**
 * Panels: stock painting, but a TitledBorder (group box) is swapped for one that anti-aliases its title.
 * Without this the title turns crisp whenever a child repaints and the panel is redrawn on its own.
 */
public class AeroPanelUI extends BasicPanelUI {
    public static ComponentUI createUI(JComponent c) { return new AeroPanelUI(); }
    private PropertyChangeListener l;

    /** TitledBorder that draws its caption with text anti-aliasing. */
    public static final class Titled extends TitledBorder implements UIResource {
        Titled(TitledBorder t) {
            super(t.getBorder(), t.getTitle(), t.getTitleJustification(), t.getTitlePosition(), t.getTitleFont(), t.getTitleColor());
        }
        @Override public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = Aero.text((Graphics2D) g.create());
            super.paintBorder(c, g2, x, y, w, h);
            g2.dispose();
        }
    }

    private static void swap(JComponent c) {
        Border b = c.getBorder();
        if (b instanceof TitledBorder tb && !(b instanceof Titled)) c.setBorder(new Titled(tb));
    }

    @Override public void installUI(JComponent c) {
        super.installUI(c);
        l = e -> swap((JComponent) e.getSource());
        c.addPropertyChangeListener("border", l);
        swap(c);
    }

    @Override public void uninstallUI(JComponent c) {
        c.removePropertyChangeListener("border", l);
        super.uninstallUI(c);
    }
}
