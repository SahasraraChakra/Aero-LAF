package aero;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;

/** All borders used by the Aero look and feel. */
public final class AeroBorders {
    private AeroBorders() {}

    /** Space around the text/icon of a push button (the outline itself is painted by the UI). */
    public static class ButtonBorder extends AbstractBorder implements UIResource {
        @Override public Insets getBorderInsets(Component c, Insets i) {
            if (c.getParent() instanceof JToolBar || "toolbar".equals(((JComponent) c).getClientProperty("Aero.style"))) {
                i.set(4, 7, 4, 7);
                return i;
            }
            Insets m = (c instanceof AbstractButton b && b.getMargin() != null) ? b.getMargin() : new Insets(3, 12, 3, 12);
            i.set(m.top + 1, m.left + 1, m.bottom + 1, m.right + 1);
            return i;
        }
        @Override public Insets getBorderInsets(Component c) { return getBorderInsets(c, new Insets(0, 0, 0, 0)); }
    }

    /** Win7 text box outline: grey, turns blue on hover and focus. */
    public static class TextBorder extends AbstractBorder implements UIResource {
        private final Insets in;
        public TextBorder() { this(new Insets(3, 5, 3, 5)); }
        public TextBorder(Insets in) { this.in = in; }
        @Override public Insets getBorderInsets(Component c, Insets i) { i.set(in.top, in.left, in.bottom, in.right); return i; }
        @Override public Insets getBorderInsets(Component c) { return (Insets) in.clone(); }
        @Override public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            boolean enabled = c.isEnabled();
            boolean hot = c instanceof JComponent jc && Boolean.TRUE.equals(jc.getClientProperty("Aero.hover"));
            Component fo = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
            boolean focus = fo != null && SwingUtilities.isDescendingFrom(fo, c);
            Color outer = !enabled ? Aero.FIELD_OFF : focus ? Aero.FIELD_FOCUS : hot ? Aero.FIELD_HOT : Aero.FIELD_BORDER;
            g2.setColor(outer);
            g2.drawRect(x, y, w - 1, h - 1);
            if (enabled && (focus || hot)) {
                g2.setColor(new Color(0xB7D5EA));
                g2.drawRect(x + 1, y + 1, w - 3, h - 3);
            } else if (enabled) {
                g2.setColor(new Color(0xE4E9EE));
                g2.drawLine(x + 1, y + 1, x + w - 2, y + 1);
            }
            g2.dispose();
        }
    }

    /** One pixel frame used around scroll panes, list views and tables. */
    public static class ScrollBorder extends AbstractBorder implements UIResource {
        @Override public Insets getBorderInsets(Component c, Insets i) { i.set(1, 1, 1, 1); return i; }
        @Override public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            g.setColor(c.isEnabled() ? new Color(0x828790) : Aero.FIELD_OFF);
            g.drawRect(x, y, w - 1, h - 1);
        }
    }

    /** Frame + white inner line used by popup menus, combo popups and tool-tips. */
    public static class PopupBorder extends AbstractBorder implements UIResource {
        private final Insets in;
        public PopupBorder() { this(new Insets(3, 3, 3, 3)); }
        public PopupBorder(Insets in) { this.in = in; }
        @Override public Insets getBorderInsets(Component c, Insets i) { i.set(in.top, in.left, in.bottom, in.right); return i; }
        @Override public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            g.setColor(new Color(0x979797));
            g.drawRect(x, y, w - 1, h - 1);
            g.setColor(new Color(0xF5F5F5));
            g.drawRect(x + 1, y + 1, w - 3, h - 3);
        }
    }

    /** Rounded pale-blue frame of a group box (TitledBorder). */
    public static class GroupBorder extends AbstractBorder implements UIResource {
        @Override public Insets getBorderInsets(Component c, Insets i) { i.set(5, 5, 5, 5); return i; }
        @Override public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = Aero.g2(g);
            g2.setColor(new Color(0xD5DFE5));
            g2.draw(Aero.rr(x + .5, y + .5, w - 1, h - 1, 6));
            g2.setColor(new Color(255, 255, 255, 200));
            g2.draw(Aero.rr(x + 1.5, y + 1.5, w - 3, h - 3, 4));
            g2.dispose();
        }
    }

    /** Grip dots on the left of a floatable toolbar and a thin bottom rule. */
    public static class ToolBarBorder extends AbstractBorder implements UIResource, SwingConstants {
        @Override public Insets getBorderInsets(Component c, Insets i) {
            boolean floatable = c instanceof JToolBar tb && tb.isFloatable();
            boolean horiz = !(c instanceof JToolBar tb2) || tb2.getOrientation() == HORIZONTAL;
            if (horiz) i.set(2, floatable ? 10 : 3, 3, 3); else i.set(floatable ? 10 : 3, 2, 3, 3);
            return i;
        }
        @Override public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            if (c instanceof JToolBar tb && tb.isFloatable()) {
                boolean horiz = tb.getOrientation() == HORIZONTAL;
                for (int k = 0; k < 4; k++) {
                    int px = horiz ? x + 3 : x + 4 + k * 4 + 3;
                    int py = horiz ? y + 4 + k * 4 : y + 3;
                    if (horiz) { if (py + 2 > y + h - 4) break; } else { if (px + 2 > x + w - 4) break; }
                    g2.setColor(Color.WHITE); g2.fillRect(px + 1, py + 1, 2, 2);
                    g2.setColor(new Color(0x8D9BB0)); g2.fillRect(px, py, 2, 2);
                }
            }
            g2.setColor(new Color(0xCDD6E3));
            g2.drawLine(x, y + h - 1, x + w - 1, y + h - 1);
            g2.dispose();
        }
    }

    /** Glass frame of a JInternalFrame (title pane paints its own top part). */
    public static class InternalFrameBorder extends AbstractBorder implements UIResource {
        @Override public Insets getBorderInsets(Component c, Insets i) { i.set(1, 5, 5, 5); return i; }
        @Override public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = Aero.g2(g);
            boolean active = !(c instanceof JInternalFrame f) || f.isSelected() || Boolean.TRUE.equals(f.getClientProperty("Aero.active"));
            Color a = active ? new Color(0xB2CBE7) : new Color(0xD3DCE7);
            Color b = active ? new Color(0xC6D9EE) : new Color(0xE1E7EF);
            Shape outer = Aero.rr(x, y, w, h, 8);
            g2.setPaint(new GradientPaint(0, y, a, 0, y + h, b));
            g2.fill(outer);
            g2.setColor(new Color(0x34495E).brighter());
            g2.draw(Aero.rr(x + .5, y + .5, w - 1, h - 1, 8));
            g2.setColor(new Color(255, 255, 255, 150));
            g2.draw(Aero.rr(x + 1.5, y + 1.5, w - 3, h - 3, 6));
            g2.setColor(active ? new Color(0x5A6C84) : new Color(0x8D99A8));
            g2.drawRect(x + 4, y + 23, w - 9, h - 28);
            g2.dispose();
        }
    }

    /** Border that paints a dotted focus-style frame (used for list cells). */
    public static Border cellFocus() {
        return new BorderUIResource.LineBorderUIResource(new Color(0x7DA2CE));
    }
}
