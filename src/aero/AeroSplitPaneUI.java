package aero;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;

public class AeroSplitPaneUI extends BasicSplitPaneUI {
    public static ComponentUI createUI(JComponent c) { return new AeroSplitPaneUI(); }

    @Override public BasicSplitPaneDivider createDefaultDivider() {
        return new BasicSplitPaneDivider(this) {
            @Override public void paint(Graphics g) {
                Graphics2D g2 = Aero.g2(g);
                boolean hz = splitPane.getOrientation() == JSplitPane.HORIZONTAL_SPLIT;
                int w = getWidth(), h = getHeight();
                g2.setPaint(hz ? new GradientPaint(0, 0, new Color(0xF7F8FA), w, 0, new Color(0xE3E7ED))
                               : new GradientPaint(0, 0, new Color(0xF7F8FA), 0, h, new Color(0xE3E7ED)));
                g2.fillRect(0, 0, w, h);
                g2.setColor(new Color(0xC3CAD4));
                if (hz) { g2.drawLine(0, 0, 0, h); g2.drawLine(w - 1, 0, w - 1, h); }
                else { g2.drawLine(0, 0, w, 0); g2.drawLine(0, h - 1, w, h - 1); }
                for (int i = -2; i <= 2; i++) {
                    int x = hz ? w / 2 - 1 : w / 2 + i * 4 - 1, y = hz ? h / 2 + i * 4 - 1 : h / 2 - 1;
                    g2.setColor(Color.WHITE); g2.fillRect(x + 1, y + 1, 2, 2);
                    g2.setColor(new Color(0x8A97A9)); g2.fillRect(x, y, 2, 2);
                }
                g2.dispose();
                super.paintComponents(g);
            }
        };
    }
}
