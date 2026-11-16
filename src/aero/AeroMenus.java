package aero;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;

/** JMenuBar, JMenu, JMenuItem, JCheckBoxMenuItem, JRadioButtonMenuItem, JPopupMenu and its separator. */
public final class AeroMenus {
    private AeroMenus() {}
    static final int GUTTER = 30;   // x of the icon-gutter divider inside a popup (from item left)

    static void paintItem(Graphics g, JMenuItem mi) {
        ButtonModel m = mi.getModel();
        int w = mi.getWidth(), h = mi.getHeight();
        boolean top = mi.getParent() instanceof JMenuBar;
        boolean open = mi instanceof JMenu && m.isSelected();
        boolean armed = m.isArmed() || open;
        Aero.text((Graphics2D) g);
        Graphics2D g2 = Aero.g2(g);
        if (top) {
            if (open) Aero.glossy(g2, 0, 0, w, h, 6, Aero.TOOL_PRESSED, false);
            else if (armed || m.isRollover()) Aero.glossy(g2, 0, 0, w, h, 6, Aero.TOOL_HOT, false);
        } else {
            if (armed && mi.isEnabled()) Aero.glossy(g2, 1, 0, w - 2, h, 5, Aero.SEL_ACTIVE, false);
        }
        g2.dispose();
    }

    public static class MenuBar extends BasicMenuBarUI {
        public static ComponentUI createUI(JComponent c) { return new MenuBar(); }
        @Override public void update(Graphics g, JComponent c) {
            Graphics2D g2 = Aero.g2(g);
            g2.setPaint(new GradientPaint(0, 0, new Color(0xFBFCFE), 0, c.getHeight(), new Color(0xE6EBF4)));
            g2.fillRect(0, 0, c.getWidth(), c.getHeight());
            g2.setColor(new Color(0xCDD6E3));
            g2.drawLine(0, c.getHeight() - 1, c.getWidth(), c.getHeight() - 1);
            g2.dispose();
        }
    }

    public static class Menu extends BasicMenuUI {
        public static ComponentUI createUI(JComponent c) { return new Menu(); }
        @Override protected void installDefaults() { super.installDefaults(); menuItem.setOpaque(false); }
        @Override protected void paintBackground(Graphics g, JMenuItem mi, Color bg) { paintItem(g, mi); }
    }

    public static class MenuItem extends BasicMenuItemUI {
        public static ComponentUI createUI(JComponent c) { return new MenuItem(); }
        @Override protected void installDefaults() { super.installDefaults(); menuItem.setOpaque(false); }
        @Override protected void paintBackground(Graphics g, JMenuItem mi, Color bg) { paintItem(g, mi); }
    }

    public static class CheckBoxMenuItem extends BasicCheckBoxMenuItemUI {
        public static ComponentUI createUI(JComponent c) { return new CheckBoxMenuItem(); }
        @Override protected void installDefaults() { super.installDefaults(); menuItem.setOpaque(false); }
        @Override protected void paintBackground(Graphics g, JMenuItem mi, Color bg) { paintItem(g, mi); }
    }

    public static class RadioButtonMenuItem extends BasicRadioButtonMenuItemUI {
        public static ComponentUI createUI(JComponent c) { return new RadioButtonMenuItem(); }
        @Override protected void installDefaults() { super.installDefaults(); menuItem.setOpaque(false); }
        @Override protected void paintBackground(Graphics g, JMenuItem mi, Color bg) { paintItem(g, mi); }
    }

    public static class PopupMenu extends BasicPopupMenuUI {
        public static ComponentUI createUI(JComponent c) { return new PopupMenu(); }
        @Override public void update(Graphics g, JComponent c) {
            Graphics2D g2 = Aero.g2(g);
            int w = c.getWidth(), h = c.getHeight();
            g2.setColor(new Color(0xF1F1F1)); g2.fillRect(0, 0, w, h);
            Insets in = c.getInsets();
            int gx = in.left + GUTTER;
            g2.setColor(new Color(0xE2E3E3)); g2.drawLine(gx, in.top, gx, h - in.bottom - 1);
            g2.setColor(Color.WHITE); g2.drawLine(gx + 1, in.top, gx + 1, h - in.bottom - 1);
            g2.dispose();
            paint(g, c);
        }
    }

    public static class PopupSeparator extends BasicPopupMenuSeparatorUI {
        public static ComponentUI createUI(JComponent c) { return new PopupSeparator(); }
        @Override public void paint(Graphics g, JComponent c) {
            g.setColor(new Color(0xE2E3E3)); g.drawLine(GUTTER + 4, 1, c.getWidth() - 2, 1);
            g.setColor(Color.WHITE); g.drawLine(GUTTER + 4, 2, c.getWidth() - 2, 2);
        }
        @Override public Dimension getPreferredSize(JComponent c) { return new Dimension(0, 4); }
    }
}
