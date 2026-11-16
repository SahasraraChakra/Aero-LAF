package aero;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;

public class AeroSpinnerUI extends BasicSpinnerUI {
    public static ComponentUI createUI(JComponent c) { return new AeroSpinnerUI(); }

    @Override protected void installDefaults() {
        super.installDefaults();
        fixEditor();
        spinner.addPropertyChangeListener("editor", e -> fixEditor());
    }

    private void fixEditor() {
        if (spinner.getEditor() instanceof JSpinner.DefaultEditor de) {
            JFormattedTextField f = de.getTextField();
            f.setBorder(new EmptyBorder(2, 3, 2, 3));
            f.addFocusListener(new FocusAdapter() {
                @Override public void focusGained(FocusEvent e) { spinner.repaint(); }
                @Override public void focusLost(FocusEvent e) { spinner.repaint(); }
            });
            MouseAdapter m = new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { spinner.putClientProperty("Aero.hover", true); spinner.repaint(); }
                @Override public void mouseExited(MouseEvent e) { spinner.putClientProperty("Aero.hover", false); spinner.repaint(); }
            };
            f.addMouseListener(m);
            spinner.addMouseListener(m);
        }
    }

    private JButton button(int dir, String name) {
        JButton b = new JButton() {
            @Override public Dimension getPreferredSize() { return new Dimension(17, 10); }
            @Override public void paint(Graphics g) {
                Graphics2D g2 = Aero.g2(g);
                ButtonModel m = getModel();
                Aero.Skin s = Aero.skin(spinner.isEnabled(), m.isRollover(), m.isArmed() && m.isPressed());
                Aero.glossy(g2, 0, dir == SwingConstants.NORTH ? 0 : 0, getWidth(), getHeight(), 2, s, false, false);
                Aero.arrow(g2, getWidth() / 2f, getHeight() / 2f, 2.8f, dir, spinner.isEnabled() ? new Color(0x404B5A) : Aero.TEXT_DISABLED);
                g2.dispose();
            }
        };
        b.setName(name); b.setFocusable(false); b.setBorder(BorderFactory.createEmptyBorder()); b.setRolloverEnabled(true);
        return b;
    }

    @Override protected Component createNextButton() {
        JComponent c = button(SwingConstants.NORTH, "Spinner.nextButton");
        installNextButtonListeners(c);
        return c;
    }
    @Override protected Component createPreviousButton() {
        JComponent c = button(SwingConstants.SOUTH, "Spinner.previousButton");
        installPreviousButtonListeners(c);
        return c;
    }
}
