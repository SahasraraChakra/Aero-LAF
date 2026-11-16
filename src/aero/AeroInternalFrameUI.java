package aero;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;

/** JInternalFrame with a glass title bar and small caption buttons. */
public class AeroInternalFrameUI extends BasicInternalFrameUI {
    public AeroInternalFrameUI(JInternalFrame f) { super(f); }
    public static ComponentUI createUI(JComponent c) { return new AeroInternalFrameUI((JInternalFrame) c); }

    @Override protected JComponent createNorthPane(JInternalFrame w) {
        titlePane = new Title(w);
        return titlePane;
    }

    public static final class Title extends BasicInternalFrameTitlePane {
        public Title(JInternalFrame f) { super(f); }

        @Override protected void createButtons() {
            super.createButtons();
            style(iconButton, "caption"); style(maxButton, "caption"); style(closeButton, "captionClose");
        }
        private void style(JButton b, String s) {
            b.putClientProperty("Aero.style", s);
            b.setOpaque(false);
            b.setPreferredSize(new Dimension(s.equals("captionClose") ? 26 : 22, 16));
        }
        @Override protected void installDefaults() {
            super.installDefaults();
            selectedTextColor = Aero.TEXT; notSelectedTextColor = Aero.TEXT_DISABLED;
        }
        @Override protected LayoutManager createLayout() { return new Lay(); }

        final class Lay implements LayoutManager {
            public void addLayoutComponent(String n, Component c) { }
            public void removeLayoutComponent(Component c) { }
            public Dimension preferredLayoutSize(Container c) { return new Dimension(100, 22); }
            public Dimension minimumLayoutSize(Container c) { return preferredLayoutSize(c); }
            public void layoutContainer(Container c) {
                int w = c.getWidth(), x = w - 4;
                for (JButton b : new JButton[]{closeButton, maxButton, iconButton}) {
                    if (b == null || !b.isVisible()) continue;
                    int bw = b.getPreferredSize().width;
                    x -= bw;
                    b.setBounds(x, 2, bw, 16);
                }
            }
        }

        @Override protected void paintTitleBackground(Graphics g) { }

        @Override public void paintComponent(Graphics g) {
            Graphics2D g2 = Aero.g2(g);
            boolean sel = frame.isSelected() || Boolean.TRUE.equals(frame.getClientProperty("Aero.active"));
            int w = getWidth(), h = getHeight();
            g2.setPaint(new GradientPaint(0, 0, sel ? new Color(0xDCE9F7) : new Color(0xE8EEF5), 0, h,
                    sel ? new Color(0xB2CBE7) : new Color(0xD3DCE7)));
            g2.fillRect(0, 0, w, h);
            g2.setColor(new Color(255, 255, 255, 110)); g2.fillRect(0, 0, w, h / 2);
            Aero.text(g2);
            g2.setFont(frame.getFont() != null ? frame.getFont() : getFont());
            FontMetrics fm = g2.getFontMetrics();
            int x = 4, y = (h + fm.getAscent() - fm.getDescent()) / 2;
            Icon ic = frame.getFrameIcon();
            if (ic != null) { ic.paintIcon(this, g2, x, (h - ic.getIconHeight()) / 2); x += ic.getIconWidth() + 4; }
            String t = frame.getTitle();
            if (t != null) {
                for (int dx = -1; dx <= 1; dx++) for (int dy = -1; dy <= 1; dy++) {
                    g2.setColor(new Color(255, 255, 255, 40)); g2.drawString(t, x + dx, y + dy);
                }
                g2.setColor(sel ? Aero.TEXT : Aero.TEXT_DISABLED);
                g2.drawString(t, x, y);
            }
            g2.dispose();
        }
    }
}
