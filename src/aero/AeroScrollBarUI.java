package aero;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;

public class AeroScrollBarUI extends BasicScrollBarUI {
    public static ComponentUI createUI(JComponent c) { return new AeroScrollBarUI(); }

    @Override protected void configureScrollBarColors() { super.configureScrollBarColors(); }

    private JButton arrow(int dir) {
        return new BasicArrowButton(dir) {
            @Override public Dimension getPreferredSize() { return new Dimension(17, 17); }
            @Override public void paint(Graphics g) {
                Graphics2D g2 = Aero.g2(g);
                ButtonModel m = getModel();
                boolean en = isEnabled();
                Aero.Skin s = Aero.skin(en, m.isRollover(), m.isArmed() && m.isPressed());
                boolean vertical = scrollbar.getOrientation() == VERTICAL;
                Aero.glossy(g2, 0, 0, getWidth(), getHeight(), 0, s, vertical, false);
                Aero.arrow(g2, getWidth() / 2f, getHeight() / 2f, 3.3f, getDirection(), en ? new Color(0x4D5B6E) : new Color(0xB0B0B0));
                g2.dispose();
            }
        };
    }
    @Override protected JButton createDecreaseButton(int o) { JButton b = arrow(o); b.setRolloverEnabled(true); return b; }
    @Override protected JButton createIncreaseButton(int o) { JButton b = arrow(o); b.setRolloverEnabled(true); return b; }

    @Override protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
        Graphics2D g2 = Aero.g2(g);
        boolean v = scrollbar.getOrientation() == VERTICAL;
        g2.setPaint(v ? new GradientPaint(r.x, 0, new Color(0xE6E6E6), r.x + r.width, 0, new Color(0xF4F4F4))
                      : new GradientPaint(0, r.y, new Color(0xE6E6E6), 0, r.y + r.height, new Color(0xF4F4F4)));
        g2.fillRect(r.x, r.y, r.width, r.height);
        g2.dispose();
    }

    @Override protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
        if (r.isEmpty() || !scrollbar.isEnabled()) return;
        Graphics2D g2 = Aero.g2(g);
        boolean v = scrollbar.getOrientation() == VERTICAL;
        Aero.Skin s = isDragging ? Aero.PRESSED : isThumbRollover() ? Aero.HOT
                : new Aero.Skin(new Color(0xF6F6F6), new Color(0xEDEDED), new Color(0xDCDCDC), new Color(0xD0D0D0), new Color(0x979797));
        Aero.glossy(g2, r.x + (v ? 0 : 0), r.y, r.width, r.height, 2, s, v);
        // grip
        g2.setStroke(new BasicStroke(1f));
        int cx = r.x + r.width / 2, cy = r.y + r.height / 2;
        for (int i = -1; i <= 1; i++) {
            if (v) { if (r.height < 22) break; g2.setColor(new Color(0x9AA3AE)); g2.drawLine(cx - 4, cy + i * 3, cx + 3, cy + i * 3);
                     g2.setColor(Color.WHITE); g2.drawLine(cx - 4, cy + i * 3 + 1, cx + 3, cy + i * 3 + 1); }
            else { if (r.width < 22) break; g2.setColor(new Color(0x9AA3AE)); g2.drawLine(cx + i * 3, cy - 4, cx + i * 3, cy + 3);
                   g2.setColor(Color.WHITE); g2.drawLine(cx + i * 3 + 1, cy - 4, cx + i * 3 + 1, cy + 3); }
        }
        g2.dispose();
    }

    @Override protected Dimension getMinimumThumbSize() { return new Dimension(17, 17); }
}
