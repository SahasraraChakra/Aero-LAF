package aero;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.beans.PropertyChangeEvent;
import java.util.List;
import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.BasicRootPaneUI;

/**
 * Window decorations: custom Aero glass frame, title bar and caption buttons for JFrame, JDialog and
 * JOptionPane dialogs. Works through JFrame.setDefaultLookAndFeelDecorated(true) (done by AeroLookAndFeel.install()).
 */
public class AeroRootPaneUI extends BasicRootPaneUI {
    static final int SIDE = 6, TITLE = 30, RESIZE = 6, CORNER = 14;

    public static ComponentUI createUI(JComponent c) { return new AeroRootPaneUI(); }

    private JRootPane root;
    private TitlePane title;
    private LayoutManager oldLayout;
    private MouseInputAdapter resizer;
    private HierarchyListener hier;

    @Override public void installUI(JComponent c) {
        super.installUI(c);
        root = (JRootPane) c;
        if (root.getWindowDecorationStyle() != JRootPane.NONE) installDecorations();
    }

    @Override public void uninstallUI(JComponent c) {
        uninstallDecorations();
        super.uninstallUI(c);
    }

    @Override public void propertyChange(PropertyChangeEvent e) {
        super.propertyChange(e);
        if ("windowDecorationStyle".equals(e.getPropertyName())) {
            uninstallDecorations();
            if (root.getWindowDecorationStyle() != JRootPane.NONE) installDecorations();
        }
    }

    private boolean resizable() {
        Window w = SwingUtilities.getWindowAncestor(root);
        if (w instanceof Frame f) return f.isResizable();
        if (w instanceof Dialog d) return d.isResizable();
        return true;
    }

    private boolean maximized() {
        Window w = SwingUtilities.getWindowAncestor(root);
        return w instanceof Frame f && (f.getExtendedState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH;
    }

    private boolean active() {
        Window w = SwingUtilities.getWindowAncestor(root);
        return w == null || w.isActive() || w.getFocusOwner() != null;
    }

    private static boolean undecorated(Window w) {
        return w instanceof Frame f ? f.isUndecorated() : w instanceof Dialog d ? d.isUndecorated() : true;
    }

    private boolean translucent() {
        try {
            return !GraphicsEnvironment.isHeadless() && GraphicsEnvironment.getLocalGraphicsEnvironment()
                    .getDefaultScreenDevice().isWindowTranslucencySupported(GraphicsDevice.WindowTranslucency.PERPIXEL_TRANSLUCENT);
        } catch (Throwable t) { return false; }
    }

    /** Layered pane that switches on desktop text anti-aliasing for everything inside the window. */
    private static final class AALayeredPane extends JLayeredPane {
        @Override public void paint(Graphics g) {
            Graphics2D g2 = Aero.text((Graphics2D) g.create());
            super.paint(g2);
            g2.dispose();
        }
    }

    private void installDecorations() {
        if (!(root.getLayeredPane() instanceof AALayeredPane)) {
            JLayeredPane old = root.getLayeredPane();
            AALayeredPane np = new AALayeredPane();
            for (Component c : old.getComponents()) {
                int layer = old.getLayer(c);
                old.remove(c);
                np.add(c, Integer.valueOf(layer));
            }
            root.setLayeredPane(np);
        }
        title = new TitlePane();
        root.add(title);
        oldLayout = root.getLayout();
        root.setLayout(new RootLayout());
        root.setOpaque(false);
        resizer = new Resizer();
        root.addMouseListener(resizer);
        root.addMouseMotionListener(resizer);
        hier = e -> {
            Window w = SwingUtilities.getWindowAncestor(root);
            if (w != null && undecorated(w) && translucent()) {
                try { w.setBackground(new Color(0, 0, 0, 0)); } catch (Exception ignored) { }
                if (w instanceof JFrame jf) { jf.getContentPane().setBackground(Aero.FACE); }
            }
            if (w != null) title.attach(w);
        };
        root.addHierarchyListener(hier);
        hier.hierarchyChanged(null);
        root.revalidate();
        root.repaint();
    }

    private void uninstallDecorations() {
        if (title == null) return;
        root.removeHierarchyListener(hier);
        root.removeMouseListener(resizer);
        root.removeMouseMotionListener(resizer);
        root.remove(title);
        root.setOpaque(true);
        if (oldLayout != null) root.setLayout(oldLayout);
        title = null;
        root.revalidate();
    }

    // ------------------------------------------------------------------- layout
    private final class RootLayout implements LayoutManager2 {
        public void addLayoutComponent(Component c, Object o) { }
        public void addLayoutComponent(String n, Component c) { }
        public void removeLayoutComponent(Component c) { }
        public float getLayoutAlignmentX(Container t) { return .5f; }
        public float getLayoutAlignmentY(Container t) { return .5f; }
        public void invalidateLayout(Container t) { }
        public Dimension maximumLayoutSize(Container t) { return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE); }

        private int side() { return maximized() ? 0 : SIDE; }
        private int th() { return maximized() ? 26 : TITLE; }

        public Dimension preferredLayoutSize(Container p) {
            Dimension cp = root.getContentPane() != null ? root.getContentPane().getPreferredSize() : new Dimension(100, 100);
            Dimension mb = root.getJMenuBar() != null && root.getJMenuBar().isVisible() ? root.getJMenuBar().getPreferredSize() : new Dimension();
            return new Dimension(Math.max(cp.width, mb.width) + 2 * side(), cp.height + mb.height + th() + side());
        }
        public Dimension minimumLayoutSize(Container p) {
            Dimension cp = root.getContentPane() != null ? root.getContentPane().getMinimumSize() : new Dimension(100, 100);
            Dimension mb = root.getJMenuBar() != null && root.getJMenuBar().isVisible() ? root.getJMenuBar().getMinimumSize() : new Dimension();
            return new Dimension(Math.max(cp.width, mb.width) + 2 * side() + 120, cp.height + mb.height + th() + side());
        }
        public void layoutContainer(Container p) {
            int w = p.getWidth(), h = p.getHeight(), s = side(), t = th();
            if (root.getGlassPane() != null) root.getGlassPane().setBounds(0, 0, w, h);
            int lw = Math.max(0, w - 2 * s), lh = Math.max(0, h - t - s);
            root.getLayeredPane().setBounds(s, t, lw, lh);
            int mh = 0;
            JMenuBar mb = root.getJMenuBar();
            if (mb != null && mb.isVisible()) { mh = mb.getPreferredSize().height; mb.setBounds(0, 0, lw, mh); }
            if (root.getContentPane() != null) root.getContentPane().setBounds(0, mh, lw, Math.max(0, lh - mh));
            if (title != null) title.setBounds(0, 0, w, t);
        }
    }

    // ------------------------------------------------------------------- frame painting
    @Override public void update(Graphics g, JComponent c) { paint(g, c); }

    @Override public void paint(Graphics g, JComponent c) {
        if (title == null) { if (c.isOpaque()) { g.setColor(c.getBackground()); g.fillRect(0, 0, c.getWidth(), c.getHeight()); } return; }
        Graphics2D g2 = Aero.g2(g);
        int w = c.getWidth(), h = c.getHeight();
        boolean act = active(), max = maximized();
        Window win = SwingUtilities.getWindowAncestor(c);
        boolean round = !max && (Boolean.TRUE.equals(c.getClientProperty("Aero.forceRounded"))
                || (win != null && win.getBackground() != null && win.getBackground().getAlpha() < 255));
        int arc = round ? 14 : 0;
        int s = max ? 0 : SIDE, t = max ? 26 : TITLE;
        Shape outer = new RoundRectangle2D.Float(0, 0, w, h, arc, arc);
        Color c0 = act ? new Color(0xD6E5F6) : new Color(0xE9EEF4);
        Color c1 = act ? new Color(0xB7CEE9) : new Color(0xDAE2EC);
        Color c2 = act ? new Color(0xA9C3E2) : new Color(0xD0DAE6);
        Color c3 = act ? new Color(0xC3D6EC) : new Color(0xDDE4ED);
        g2.setPaint(new LinearGradientPaint(0, 0, 0, h, new float[]{0f, Math.min(.45f, (t * .5f) / h), Math.min(.9f, (float) t / h), 1f},
                new Color[]{c0, c1, c2, c3}));
        g2.fill(outer);
        // glass sheen, intersected with the rounded outline so no square tip shows at the corners
        Path2D sheen = new Path2D.Float();
        sheen.moveTo(0, 0); sheen.lineTo(w * .58f, 0); sheen.lineTo(w * .22f, t + 4); sheen.lineTo(0, t + 4); sheen.closePath();
        Area sh = new Area(sheen);
        sh.intersect(new Area(outer));
        g2.setPaint(new GradientPaint(0, 0, new Color(255, 255, 255, act ? 120 : 70), 0, t, new Color(255, 255, 255, 0)));
        g2.fill(sh);
        // frame outline + inner highlight
        if (!max) {
            g2.setColor(act ? new Color(0x2F4560) : new Color(0x7B8798));
            g2.draw(new RoundRectangle2D.Float(.5f, .5f, w - 1, h - 1, arc, arc));
            g2.setColor(new Color(255, 255, 255, act ? 170 : 130));
            g2.draw(new RoundRectangle2D.Float(1.5f, 1.5f, w - 3, h - 3, Math.max(arc - 2, 0), Math.max(arc - 2, 0)));
        }
        // client edge
        g2.setColor(act ? new Color(0x5A6C84) : new Color(0x8D99A8));
        g2.drawRect(s - 1, t - 1, w - 2 * s + 1, h - t - s + 1);
        g2.setColor(new Color(255, 255, 255, 110));
        g2.drawRect(s - 2, t - 2, w - 2 * s + 3, h - t - s + 3);
        g2.dispose();
    }

    // ------------------------------------------------------------------- resize / move
    private int hit(int x, int y) {
        if (!resizable() || maximized()) return 0;
        int w = root.getWidth(), h = root.getHeight();
        int dir = 0;
        boolean nearX = x < CORNER || x >= w - CORNER, nearY = y < CORNER || y >= h - CORNER;
        if (y < RESIZE || (x < RESIZE || x >= w - RESIZE) && nearY && y < CORNER) dir |= 1;               // N
        if (y >= h - RESIZE || (x < RESIZE || x >= w - RESIZE) && nearY && y >= h - CORNER) dir |= 2;   // S
        if (x < RESIZE || (y < RESIZE || y >= h - RESIZE) && nearX && x < CORNER) dir |= 4;             // W
        if (x >= w - RESIZE || (y < RESIZE || y >= h - RESIZE) && nearX && x >= w - CORNER) dir |= 8;   // E
        return dir;
    }

    private static Cursor cursor(int d) {
        return Cursor.getPredefinedCursor(switch (d) {
            case 1 -> Cursor.N_RESIZE_CURSOR; case 2 -> Cursor.S_RESIZE_CURSOR;
            case 4 -> Cursor.W_RESIZE_CURSOR; case 8 -> Cursor.E_RESIZE_CURSOR;
            case 5 -> Cursor.NW_RESIZE_CURSOR; case 9 -> Cursor.NE_RESIZE_CURSOR;
            case 6 -> Cursor.SW_RESIZE_CURSOR; case 10 -> Cursor.SE_RESIZE_CURSOR;
            default -> Cursor.DEFAULT_CURSOR; });
    }

    private final class Resizer extends MouseInputAdapter {
        private int dir; private Point start; private Rectangle bounds; private Window win;
        @Override public void mouseMoved(MouseEvent e) {
            Point p = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), root);
            root.setCursor(cursor(hit(p.x, p.y)));
        }
        @Override public void mouseExited(MouseEvent e) { if (dir == 0) root.setCursor(Cursor.getDefaultCursor()); }
        @Override public void mousePressed(MouseEvent e) {
            Point p = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), root);
            dir = hit(p.x, p.y);
            win = SwingUtilities.getWindowAncestor(root);
            if (dir != 0 && win != null) { start = e.getLocationOnScreen(); bounds = win.getBounds(); }
        }
        @Override public void mouseDragged(MouseEvent e) {
            if (dir == 0 || win == null) return;
            Point p = e.getLocationOnScreen();
            int dx = p.x - start.x, dy = p.y - start.y;
            Rectangle r = new Rectangle(bounds);
            Dimension min = new Dimension(Math.max(win.getMinimumSize().width, 240), Math.max(win.getMinimumSize().height, 120));
            if ((dir & 4) != 0) { int nw = Math.max(min.width, bounds.width - dx); r.x = bounds.x + bounds.width - nw; r.width = nw; }
            if ((dir & 8) != 0) r.width = Math.max(min.width, bounds.width + dx);
            if ((dir & 1) != 0) { int nh = Math.max(min.height, bounds.height - dy); r.y = bounds.y + bounds.height - nh; r.height = nh; }
            if ((dir & 2) != 0) r.height = Math.max(min.height, bounds.height + dy);
            win.setBounds(r);
            win.validate();
        }
        @Override public void mouseReleased(MouseEvent e) { dir = 0; }
    }

    // ------------------------------------------------------------------- title bar
    final class TitlePane extends JComponent {
        private int hover = -1, pressed = -1;       // 0 min, 1 max, 2 close
        private Window win;
        private Point dragStart; private Rectangle dragBounds; private boolean dragging;
        private final Rectangle[] btn = {new Rectangle(), new Rectangle(), new Rectangle()};

        TitlePane() {
            setOpaque(false);
            MouseInputAdapter m = new MouseInputAdapter() {
                @Override public void mouseMoved(MouseEvent e) {
                    int h = which(e.getPoint());
                    if (h != hover) { hover = h; repaint(); }
                    Point p = SwingUtilities.convertPoint(TitlePane.this, e.getPoint(), root);
                    setCursor(cursor(hit(p.x, p.y)));
                }
                @Override public void mouseExited(MouseEvent e) { hover = -1; repaint(); }
                @Override public void mousePressed(MouseEvent e) {
                    Point p = SwingUtilities.convertPoint(TitlePane.this, e.getPoint(), root);
                    int d = hit(p.x, p.y);
                    if (d != 0) { redirect(e); return; }
                    if (SwingUtilities.isRightMouseButton(e)) { systemMenu(e); return; }
                    pressed = which(e.getPoint());
                    if (pressed >= 0) { repaint(); return; }
                    if (e.getClickCount() == 2 && win instanceof Frame f && f.isResizable()) { toggleMax(); return; }
                    dragStart = e.getLocationOnScreen();
                    dragBounds = win != null ? win.getBounds() : null;
                    dragging = true;
                }
                @Override public void mouseDragged(MouseEvent e) {
                    if (redirectActive) { redirect(e); return; }
                    if (!dragging || win == null || dragBounds == null) return;
                    if (maximized()) {
                        Frame f = (Frame) win;
                        double rel = e.getX() / (double) Math.max(1, getWidth());
                        f.setExtendedState(Frame.NORMAL);
                        dragBounds = f.getBounds();
                        dragStart = e.getLocationOnScreen();
                        dragBounds.x = dragStart.x - (int) (rel * dragBounds.width);
                        dragBounds.y = dragStart.y - 12;
                        f.setLocation(dragBounds.x, dragBounds.y);
                        return;
                    }
                    Point p = e.getLocationOnScreen();
                    win.setLocation(dragBounds.x + p.x - dragStart.x, dragBounds.y + p.y - dragStart.y);
                }
                @Override public void mouseReleased(MouseEvent e) {
                    if (redirectActive) { redirect(e); redirectActive = false; return; }
                    int r = which(e.getPoint());
                    if (pressed >= 0 && pressed == r) act(r);
                    pressed = -1; dragging = false; repaint();
                }
            };
            addMouseListener(m);
            addMouseMotionListener(m);
        }

        private boolean redirectActive;
        private void redirect(MouseEvent e) {
            redirectActive = true;
            MouseEvent c = SwingUtilities.convertMouseEvent(this, e, root);
            for (MouseListener l : root.getMouseListeners()) {
                if (e.getID() == MouseEvent.MOUSE_PRESSED) l.mousePressed(c); else if (e.getID() == MouseEvent.MOUSE_RELEASED) l.mouseReleased(c);
            }
            if (e.getID() == MouseEvent.MOUSE_DRAGGED) for (MouseMotionListener l : root.getMouseMotionListeners()) l.mouseDragged(c);
        }

        void attach(Window w) {
            if (win == w) return;
            win = w;
            WindowAdapter wa = new WindowAdapter() {
                @Override public void windowActivated(WindowEvent e) { root.repaint(); }
                @Override public void windowDeactivated(WindowEvent e) { root.repaint(); }
                @Override public void windowStateChanged(WindowEvent e) { root.revalidate(); root.repaint(); }
            };
            w.addWindowListener(wa); w.addWindowStateListener(wa);
            w.addPropertyChangeListener(e -> { if (e.getPropertyName().startsWith("title") || e.getPropertyName().contains("con")) repaint(); });
        }

        private String titleText() {
            if (win instanceof Frame f) return f.getTitle();
            if (win instanceof Dialog d) return d.getTitle();
            Object o = root.getClientProperty("Aero.title");
            return o == null ? "" : o.toString();
        }

        private boolean full() { return root.getWindowDecorationStyle() == JRootPane.FRAME; }

        private int which(Point p) {
            for (int i = 2; i >= 0; i--) if (btn[i].width > 0 && btn[i].contains(p)) return i;
            return -1;
        }

        private void act(int i) {
            if (win == null) return;
            switch (i) {
                case 0 -> { if (win instanceof Frame f) f.setExtendedState(f.getExtendedState() | Frame.ICONIFIED); }
                case 1 -> toggleMax();
                case 2 -> win.dispatchEvent(new WindowEvent(win, WindowEvent.WINDOW_CLOSING));
            }
        }

        private void toggleMax() {
            if (!(win instanceof Frame f) || !f.isResizable()) return;
            int st = f.getExtendedState();
            if ((st & Frame.MAXIMIZED_BOTH) != 0) f.setExtendedState(st & ~Frame.MAXIMIZED_BOTH);
            else {
                f.setMaximizedBounds(GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds());
                f.setExtendedState(st | Frame.MAXIMIZED_BOTH);
            }
        }

        private void systemMenu(MouseEvent e) {
            JPopupMenu pm = new JPopupMenu();
            boolean fr = win instanceof Frame;
            boolean max = maximized();
            JMenuItem restore = new JMenuItem("Restore"); restore.setEnabled(fr && max); restore.addActionListener(a -> toggleMax());
            JMenuItem min = new JMenuItem("Minimize"); min.setEnabled(fr && full()); min.addActionListener(a -> act(0));
            JMenuItem mx = new JMenuItem("Maximize"); mx.setEnabled(fr && !max && resizable() && full()); mx.addActionListener(a -> toggleMax());
            JMenuItem close = new JMenuItem("Close"); close.addActionListener(a -> act(2));
            pm.add(restore); pm.add(min); pm.add(mx); pm.addSeparator(); pm.add(close);
            pm.show(this, e.getX(), e.getY());
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = Aero.g2(g);
            boolean act = active(), max = maximized();
            int w = getWidth();
            // ---- caption buttons
            boolean fr = full();
            int cw = 47, mw = 27, nw = 29, bh = max ? 19 : 20, top = max ? 0 : 1;
            int right = w - (max ? 6 : SIDE + 2);
            btn[0].setBounds(0, 0, 0, 0); btn[1].setBounds(0, 0, 0, 0);
            btn[2].setBounds(right - cw, top, cw, bh);
            int gx = right - cw, gw = cw;
            if (fr) {
                btn[1].setBounds(right - cw - mw, top, mw, bh);
                btn[0].setBounds(right - cw - mw - nw, top, nw, bh);
                gx = right - cw - mw - nw; gw = cw + mw + nw;
            }
            Shape group = new RoundRectangle2D.Float(gx + .5f, top - 8, gw - 1, bh + 8 - .5f, 10, 10);
            Shape oldClip = g2.getClip();
            for (int i = 0; i < 3; i++) {
                Rectangle r = btn[i];
                if (r.width == 0) continue;
                boolean hot = hover == i, down = pressed == i && hover == i;
                Aero.Skin s;
                if (i == 2) {
                    s = down ? new Aero.Skin(new Color(0xD26C58), new Color(0xBF402B), new Color(0x9E2713), new Color(0xC0533C), null)
                      : hot ? new Aero.Skin(new Color(0xF5C2B3), new Color(0xEC8A74), new Color(0xE0472B), new Color(0xF08C70), null)
                      : act ? new Aero.Skin(new Color(0xE9B3A8), new Color(0xD7796A), new Color(0xC44530), new Color(0xDE7E69), null)
                            : new Aero.Skin(new Color(0xE6D6D3), new Color(0xD9C0BB), new Color(0xCDA59E), new Color(0xD7B4AD), null);
                } else {
                    s = down ? new Aero.Skin(new Color(0xB1D4EA), new Color(0x8EC1E6), new Color(0x3F95CF), new Color(0x6EB9E8), null)
                      : hot ? new Aero.Skin(new Color(0xE9F6FF), new Color(0xC8E9FB), new Color(0x7FC1EA), new Color(0xA6DBF6), null)
                      : act ? new Aero.Skin(new Color(0xDCE7F4), new Color(0xBFD2E8), new Color(0xA5BDDA), new Color(0xB9CFE8), null)
                            : new Aero.Skin(new Color(0xE9EEF4), new Color(0xDCE4EE), new Color(0xD0DAE6), new Color(0xD8E0EA), null);
                }
                g2.clip(group);
                g2.setPaint(new GradientPaint(0, r.y, s.t1(), 0, r.y + r.height / 2f, s.t2()));
                g2.fillRect(r.x, r.y - 8, r.width, r.height / 2 + 8);
                g2.setPaint(new GradientPaint(0, r.y + r.height / 2f, s.b1(), 0, r.y + r.height, s.b2()));
                g2.fillRect(r.x, r.y + r.height / 2, r.width, r.height - r.height / 2 + 1);
                g2.setClip(oldClip);
                if (i > 0 && btn[i - 1].width > 0) {          // divider
                    g2.setColor(new Color(0x3A4C66, false)); g2.drawLine(r.x, r.y, r.x, r.y + r.height - 1);
                }
                float cx = r.x + r.width / 2f, cy = r.y + r.height / 2f + 1;
                AeroIcons.Caption k = i == 0 ? AeroIcons.Caption.MIN : i == 2 ? AeroIcons.Caption.CLOSE
                        : maximized() ? AeroIcons.Caption.RESTORE : AeroIcons.Caption.MAX;
                boolean disabled = i == 1 && !(win == null || (win instanceof Frame f && f.isResizable()));
                if (!disabled) AeroIcons.paintCaptionGlyph(g2, k, cx, cy, 1f, true);
            }
            g2.setColor(act ? new Color(0x2F4560) : new Color(0x7B8798));
            g2.draw(group);
            g2.setColor(new Color(255, 255, 255, 140));
            g2.draw(new RoundRectangle2D.Float(gx + 1.5f, top - 8, gw - 3, bh + 8 - 1.5f, 8, 8));
            // ---- icon + title
            int x = max ? 6 : SIDE + 3;
            Image img = null;
            if (win != null) { List<Image> imgs = win.getIconImages(); if (!imgs.isEmpty()) img = imgs.get(0); }
            if (img != null) { g2.drawImage(img.getScaledInstance(16, 16, Image.SCALE_SMOOTH), x, (getHeight() - 16) / 2 - 1, null); x += 22; }
            String t = titleText();
            if (t != null && !t.isEmpty()) {
                Aero.text(g2);
                Font f = Aero.baseFont().deriveFont(Font.PLAIN, (float) Aero.fontSize());
                g2.setFont(f);
                FontMetrics fm = g2.getFontMetrics();
                int avail = (fr ? btn[0].x : btn[2].x) - x - 8;
                String s = t;
                while (s.length() > 1 && fm.stringWidth(s) > avail) s = s.substring(0, s.length() - 1);
                if (!s.equals(t)) s = s.substring(0, Math.max(0, s.length() - 1)) + "\u2026";
                int y = (getHeight() - 4 + fm.getAscent() - fm.getDescent()) / 2 + (max ? -1 : 1);
                for (int dx = -2; dx <= 2; dx++) for (int dy = -2; dy <= 2; dy++) {
                    if (dx * dx + dy * dy > 5) continue;
                    g2.setColor(new Color(255, 255, 255, act ? 26 : 16)); g2.drawString(s, x + dx, y + dy);
                }
                g2.setColor(act ? Color.BLACK : new Color(0x6A7686));
                g2.drawString(s, x, y);
            }
            g2.dispose();
        }
    }
}
