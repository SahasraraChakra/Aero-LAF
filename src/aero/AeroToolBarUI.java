package aero;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;

public class AeroToolBarUI extends BasicToolBarUI {
    public static ComponentUI createUI(JComponent c) { return new AeroToolBarUI(); }
    @Override public void update(Graphics g, JComponent c) {
        Graphics2D g2 = Aero.g2(g);
        boolean h = ((JToolBar) c).getOrientation() == SwingConstants.HORIZONTAL;
        g2.setPaint(h ? new GradientPaint(0, 0, new Color(0xFAFBFD), 0, c.getHeight(), new Color(0xE4EAF3))
                      : new GradientPaint(0, 0, new Color(0xFAFBFD), c.getWidth(), 0, new Color(0xE4EAF3)));
        g2.fillRect(0, 0, c.getWidth(), c.getHeight());
        g2.dispose();
        paint(g, c);
    }

    public static class Separator extends BasicToolBarSeparatorUI {
        public static ComponentUI createUI(JComponent c) { return new Separator(); }
        @Override public void paint(Graphics g, JComponent c) {
            Dimension s = c.getSize();
            boolean vertical = !(c.getParent() instanceof JToolBar tb) || tb.getOrientation() == SwingConstants.HORIZONTAL;
            if (vertical) {
                int x = s.width / 2;
                g.setColor(new Color(0xCDD6E3)); g.drawLine(x, 2, x, s.height - 3);
                g.setColor(Color.WHITE); g.drawLine(x + 1, 2, x + 1, s.height - 3);
            } else {
                int y = s.height / 2;
                g.setColor(new Color(0xCDD6E3)); g.drawLine(2, y, s.width - 3, y);
                g.setColor(Color.WHITE); g.drawLine(2, y + 1, s.width - 3, y + 1);
            }
        }
    }
}
