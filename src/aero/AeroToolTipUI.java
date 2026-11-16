package aero;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;

public class AeroToolTipUI extends BasicToolTipUI {
    public static ComponentUI createUI(JComponent c) { return new AeroToolTipUI(); }
    @Override public void installUI(JComponent c) { super.installUI(c); c.setOpaque(false); }
    @Override public void paint(Graphics g, JComponent c) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setPaint(new GradientPaint(0, 0, Color.WHITE, 0, c.getHeight(), new Color(0xE4E5F0)));
        g2.fillRect(0, 0, c.getWidth(), c.getHeight());
        g2.setColor(new Color(0x767676)); g2.drawRect(0, 0, c.getWidth() - 1, c.getHeight() - 1);
        g2.setColor(new Color(255, 255, 255, 200)); g2.drawRect(1, 1, c.getWidth() - 3, c.getHeight() - 3);
        Graphics2D t = Aero.text((Graphics2D) g.create()); super.paint(t, c); t.dispose();
    }
}
