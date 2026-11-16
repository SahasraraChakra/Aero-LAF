package aero;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;

/** Non-editable: glossy button with arrow. Editable: text box + glossy arrow. */
public class AeroComboBoxUI extends BasicComboBoxUI {
    public static ComponentUI createUI(JComponent c) { return new AeroComboBoxUI(); }
    private boolean hot;
    private MouseAdapter hover;

    @Override protected void installDefaults() {
        super.installDefaults();
        comboBox.setOpaque(false);
    }

    @Override protected void installListeners() {
        super.installListeners();
        hover = new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { hot = true; comboBox.repaint(); }
            @Override public void mouseExited(MouseEvent e) { hot = false; comboBox.repaint(); }
        };
        comboBox.addMouseListener(hover);
        comboBox.putClientProperty("Aero.hover", null);
    }

    @Override protected void uninstallListeners() {
        comboBox.removeMouseListener(hover);
        super.uninstallListeners();
    }

    @Override protected void installComponents() {
        super.installComponents();
        if (arrowButton != null && hover != null) arrowButton.addMouseListener(hover);
    }

    @Override protected JButton createArrowButton() {
        JButton b = new JButton() {
            @Override public Dimension getPreferredSize() { return new Dimension(19, 19); }
            @Override public void paint(Graphics g) {
                Graphics2D g2 = Aero.g2(g);
                boolean en = comboBox.isEnabled();
                boolean down = comboBox.isPopupVisible();
                int w = getWidth(), h = getHeight();
                if (comboBox.isEditable()) {
                    Aero.Skin s = Aero.skin(en, hot || getModel().isRollover(), down);
                    Aero.glossy(g2, 0, 1, w - 1, h - 2, 2, s, false);
                } else if (en && (down)) {
                    // whole control already painted pressed
                }
                Aero.arrow(g2, w / 2f - (comboBox.isEditable() ? 0.5f : 1f), h / 2f, 3.6f, SwingConstants.SOUTH,
                        en ? new Color(0x404B5A) : Aero.TEXT_DISABLED);
                g2.dispose();
            }
        };
        b.setFocusable(false); b.setOpaque(false); b.setContentAreaFilled(false); b.setBorderPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder());
        b.setName("ComboBox.arrowButton");
        return b;
    }

    @Override public void paint(Graphics g, JComponent c) {
        Graphics2D g2 = Aero.g2(g);
        int w = c.getWidth(), h = c.getHeight();
        boolean en = c.isEnabled();
        if (comboBox.isEditable()) {
            g2.setColor(en ? Color.WHITE : Aero.FACE);
            g2.fillRect(0, 0, w, h);
            new AeroBorders.TextBorder().paintBorder(c, g2, 0, 0, w, h);
        } else {
            boolean down = comboBox.isPopupVisible();
            Aero.Skin s = Aero.skin(en, hot, down);
            Aero.glossy(g2, 0, 0, w, h, 6, s, false);
            if (comboBox.hasFocus() && !down) Aero.dottedRect(g2, 3, 3, w - 6, h - 6);
        }
        g2.dispose();
        if (!comboBox.isEditable()) {
            Rectangle r = rectangleForCurrentValue();
            paintCurrentValue(g, r, false);
        }
    }

    @Override public void paintCurrentValue(Graphics g, Rectangle bounds, boolean hasFocus) {
        ListCellRenderer<Object> r = comboBox.getRenderer();
        Component c = r.getListCellRendererComponent(listBox, comboBox.getSelectedItem(), -1, false, false);
        c.setFont(comboBox.getFont());
        c.setForeground(comboBox.isEnabled() ? Aero.TEXT : Aero.TEXT_DISABLED);
        if (c instanceof JComponent jc) jc.setOpaque(false);
        Graphics2D tg = Aero.text((Graphics2D) g.create());
        currentValuePane.paintComponent(tg, c, comboBox, bounds.x, bounds.y, bounds.width, bounds.height, c instanceof JPanel);
        tg.dispose();
    }

    @Override public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) { }

    @Override protected ListCellRenderer<Object> createRenderer() { return new AeroListCellRenderer.UI(); }

    @Override protected ComboPopup createPopup() {
        return new BasicComboPopup(comboBox) {
            @Override protected void configurePopup() {
                super.configurePopup();
                setBorder(new AeroBorders.PopupBorder(new Insets(1, 1, 1, 1)));
            }
        };
    }

    @Override protected Insets getInsets() { return new Insets(1, 3, 1, 1); }
}
