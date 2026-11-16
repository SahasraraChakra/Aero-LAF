package aero;

import java.awt.*;
import javax.swing.*;

/** List / combo renderer with the Win7 gradient selection. */
public class AeroListCellRenderer extends DefaultListCellRenderer {
    private boolean sel, hot;

    public static class UI extends AeroListCellRenderer implements javax.swing.plaf.UIResource { }

    public AeroListCellRenderer() { setOpaque(false); setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6)); }

    @Override public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean focus) {
        super.getListCellRendererComponent(list, value, index, false, false);
        sel = isSelected && index >= 0;
        hot = false;
        setOpaque(false);
        setForeground(list.isEnabled() ? Aero.TEXT : Aero.TEXT_DISABLED);
        return this;
    }

    @Override protected void paintComponent(Graphics g) {
        if (sel) {
            Graphics2D g2 = Aero.g2(g);
            Aero.glossy(g2, 1, 0, getWidth() - 2, getHeight(), 5, Aero.SEL_ACTIVE, false);
            g2.dispose();
        }
        super.paintComponent(g);
    }
}
