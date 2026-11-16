package aero;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

/** Win7 tabs: rounded, hover turns blue, selected tab merges into the page with a warm top highlight. */
public class AeroTabbedPaneUI extends BasicTabbedPaneUI {
    public static ComponentUI createUI(JComponent c) { return new AeroTabbedPaneUI(); }
    private static final Color LINE = new Color(0x898C95);
    private int hotTab = -1;

    @Override protected void installListeners() {
        super.installListeners();
        java.awt.event.MouseAdapter m = new java.awt.event.MouseAdapter() {
            @Override public void mouseMoved(java.awt.event.MouseEvent e) { setHot(tabForCoordinate(tabPane, e.getX(), e.getY())); }
            @Override public void mouseExited(java.awt.event.MouseEvent e) { setHot(-1); }
        };
        tabPane.addMouseMotionListener(m); tabPane.addMouseListener(m);
    }

    private void setHot(int i) { if (i != hotTab) { hotTab = i; tabPane.repaint(); } }

    @Override protected void installDefaults() {
        super.installDefaults();
        tabInsets = new Insets(3, 9, 3, 9);
        selectedTabPadInsets = new Insets(2, 2, 2, 2);
        tabAreaInsets = new Insets(3, 2, 0, 2);
        contentBorderInsets = new Insets(1, 1, 1, 1);
        tabPane.setOpaque(false);
    }

    @Override protected int getTabLabelShiftY(int p, int i, boolean sel) { return 0; }
    @Override protected int getTabLabelShiftX(int p, int i, boolean sel) { return 0; }

    private Shape shape(int p, int x, int y, int w, int h, boolean sel) {
        int r = 6;
        Area a;
        switch (p) {
            case BOTTOM -> a = new Area(Aero.rr(x + .5, y - r, w - 1, h + r - .5, r * 2));
            case LEFT -> a = new Area(Aero.rr(x + .5, y + .5, w + r, h - 1, r * 2));
            case RIGHT -> a = new Area(Aero.rr(x - r, y + .5, w + r - .5, h - 1, r * 2));
            default -> a = new Area(Aero.rr(x + .5, y + .5, w - 1, h + r, r * 2));
        }
        Rectangle clip = new Rectangle(x, y, w, h);
        a.intersect(new Area(clip));
        return a;
    }

    @Override protected void paintTabBackground(Graphics g, int p, int i, int x, int y, int w, int h, boolean sel) {
        Graphics2D g2 = Aero.g2(g);
        Shape s = shape(p, x, y, w, h, sel);
        boolean hot = i == hotTab && tabPane.isEnabledAt(i);
        Color t, b;
        if (sel) { t = new Color(0xFFFFFF); b = new Color(0xF6F7F9); }
        else if (hot) { t = new Color(0xEAF6FD); b = new Color(0xBEE6FD); }
        else { t = new Color(0xF3F3F3); b = new Color(0xDADCE0); }
        boolean vertical = p == LEFT || p == RIGHT;
        g2.setPaint(vertical ? new GradientPaint(x, 0, t, x + w, 0, b) : new GradientPaint(0, y, t, 0, y + h, b));
        g2.fill(s);
        g2.dispose();
    }

    @Override protected void paintTabBorder(Graphics g, int p, int i, int x, int y, int w, int h, boolean sel) {
        Graphics2D g2 = Aero.g2(g);
        Shape s = shape(p, x, y, w, h, sel);
        boolean hot = i == hotTab && tabPane.isEnabledAt(i);
        g2.setColor(sel ? LINE : hot ? new Color(0x3C7FB1) : LINE);
        Stroke old = g2.getStroke();
        g2.draw(s);
        if (sel && p == TOP) {   // warm Win7 accent
            g2.setColor(new Color(0xFFB94A));
            g2.drawLine(x + 3, y + 1, x + w - 4, y + 1);
            g2.setColor(new Color(255, 255, 255, 200));
            g2.drawLine(x + 2, y + 2, x + w - 3, y + 2);
        }
        g2.setStroke(old);
        g2.dispose();
    }

    @Override protected void paintFocusIndicator(Graphics g, int p, Rectangle[] rects, int i, Rectangle iconRect, Rectangle textRect, boolean sel) {
        if (tabPane.hasFocus() && sel) {
            Aero.dottedRect((Graphics2D) g, textRect.x - 3, textRect.y - 1, textRect.width + 6, textRect.height + 2);
        }
    }

    @Override protected void paintText(Graphics g, int p, Font font, FontMetrics fm, int i, String title, Rectangle tr, boolean sel) {
        Graphics2D g2 = Aero.text((Graphics2D) g.create());
        g2.setFont(font);
        g2.setColor(tabPane.isEnabledAt(i) ? Aero.TEXT : Aero.TEXT_DISABLED);
        int mn = tabPane.getDisplayedMnemonicIndexAt(i);
        javax.swing.plaf.basic.BasicGraphicsUtils.drawStringUnderlineCharAt(g2, title, mn, tr.x, tr.y + fm.getAscent());
        g2.dispose();
    }

    @Override protected void paintContentBorder(Graphics g, int p, int sel) {
        int w = tabPane.getWidth(), h = tabPane.getHeight();
        Insets in = tabPane.getInsets(), ta = getTabAreaInsets(p);
        int x = in.left, y = in.top, cw = w - in.right - in.left, ch = h - in.top - in.bottom;
        switch (p) {
            case LEFT -> { x += calculateTabAreaWidth(p, runCount, maxTabWidth); cw -= (x - in.left); }
            case RIGHT -> cw -= calculateTabAreaWidth(p, runCount, maxTabWidth);
            case BOTTOM -> ch -= calculateTabAreaHeight(p, runCount, maxTabHeight);
            default -> { y += calculateTabAreaHeight(p, runCount, maxTabHeight); ch -= (y - in.top); }
        }
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(tabPane.getBackground().equals(Aero.FACE) ? Color.WHITE : tabPane.getBackground());
        if (tabPane.isOpaque() || true) { g2.setColor(new Color(0xFCFCFE)); g2.fillRect(x, y, cw, ch); }
        g2.setColor(LINE);
        g2.drawRect(x, y, cw - 1, ch - 1);
        // gap under the selected tab
        if (sel >= 0 && sel < rects.length && p == TOP) {
            Rectangle r = rects[sel];
            g2.setColor(new Color(0xF6F7F9));
            g2.drawLine(r.x + 1, y, r.x + r.width - 2, y);
        } else if (sel >= 0 && sel < rects.length && p == BOTTOM) {
            Rectangle r = rects[sel];
            g2.setColor(new Color(0xF6F7F9));
            g2.drawLine(r.x + 1, y + ch - 1, r.x + r.width - 2, y + ch - 1);
        }
        g2.dispose();
    }

    @Override protected void paintContentBorderTopEdge(Graphics g, int p, int s, int x, int y, int w, int h) { }
    @Override protected void paintContentBorderBottomEdge(Graphics g, int p, int s, int x, int y, int w, int h) { }
    @Override protected void paintContentBorderLeftEdge(Graphics g, int p, int s, int x, int y, int w, int h) { }
    @Override protected void paintContentBorderRightEdge(Graphics g, int p, int s, int x, int y, int w, int h) { }
}
