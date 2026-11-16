package aero;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import javax.swing.table.*;

public class AeroTableHeaderUI extends BasicTableHeaderUI {
    public static ComponentUI createUI(JComponent c) { return new AeroTableHeaderUI(); }

    @Override protected void installDefaults() {
        super.installDefaults();
        header.setDefaultRenderer(new HeaderRenderer());
        header.setOpaque(false);
    }

    @Override public void paint(Graphics g, JComponent c) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setPaint(new GradientPaint(0, 0, Color.WHITE, 0, c.getHeight(), new Color(0xF0F2F5)));
        g2.fillRect(0, 0, c.getWidth(), c.getHeight());
        super.paint(g, c);
    }

    static final class HeaderRenderer extends DefaultTableCellRenderer implements UIResource {
        HeaderRenderer() { setOpaque(false); setHorizontalAlignment(LEADING); setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 6)); }
        @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int col) {
            super.getTableCellRendererComponent(t, v, false, false, r, col);
            setForeground(Aero.TEXT_HEADER);
            setFont(t != null ? t.getTableHeader().getFont() : getFont());
            Icon icon = null;
            if (t != null && t.getRowSorter() != null && !t.getRowSorter().getSortKeys().isEmpty()) {
                RowSorter.SortKey k = t.getRowSorter().getSortKeys().get(0);
                if (k.getColumn() == t.convertColumnIndexToModel(col) && k.getSortOrder() != SortOrder.UNSORTED)
                    icon = AeroIcons.sortArrow(k.getSortOrder() == SortOrder.ASCENDING);
            }
            setIcon(icon); setHorizontalTextPosition(LEADING);
            return this;
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setPaint(new GradientPaint(0, 0, Color.WHITE, 0, getHeight(), new Color(0xF0F2F5)));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setColor(new Color(0xD5D9DF)); g2.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
            g2.setColor(new Color(0xE0E3E8)); g2.drawLine(getWidth() - 1, 3, getWidth() - 1, getHeight() - 5);
            super.paintComponent(g);
        }
        @Override public Dimension getPreferredSize() { Dimension d = super.getPreferredSize(); d.height = Math.max(d.height, 24); return d; }
    }
}
