package aero;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import javax.swing.text.JTextComponent;

/** Text fields repaint their border on hover / focus; other text components use the stock UI. */
public final class AeroTextUI {
    private AeroTextUI() {}

    private static final String KEY = "Aero.textHandler";

    private static void hook(JTextComponent c) {
        MouseAdapter h = new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { c.putClientProperty("Aero.hover", Boolean.TRUE); c.repaint(); }
            @Override public void mouseExited(MouseEvent e) { c.putClientProperty("Aero.hover", Boolean.FALSE); c.repaint(); }
        };
        FocusAdapter f = new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { c.repaint(); }
            @Override public void focusLost(FocusEvent e) { c.repaint(); }
        };
        c.addMouseListener(h); c.addFocusListener(f);
        c.putClientProperty(KEY, new Object[]{h, f});
    }

    private static void unhook(JTextComponent c) {
        Object[] o = (Object[]) c.getClientProperty(KEY);
        if (o != null) { c.removeMouseListener((MouseListener) o[0]); c.removeFocusListener((FocusListener) o[1]); }
    }

    public static class TextField extends BasicTextFieldUI {
        public static ComponentUI createUI(JComponent c) { return new TextField(); }
        @Override protected void installListeners() { super.installListeners(); hook(getComponent()); }
        @Override protected void uninstallListeners() { unhook(getComponent()); super.uninstallListeners(); }
        @Override protected void paintSafely(java.awt.Graphics g) {
            java.awt.Graphics2D g2 = Aero.text((java.awt.Graphics2D) g.create()); super.paintSafely(g2); g2.dispose();
        }
    }

    public static class PasswordField extends BasicPasswordFieldUI {
        public static ComponentUI createUI(JComponent c) { return new PasswordField(); }
        @Override protected void installListeners() { super.installListeners(); hook(getComponent()); }
        @Override protected void uninstallListeners() { unhook(getComponent()); super.uninstallListeners(); }
        @Override protected void paintSafely(java.awt.Graphics g) {
            java.awt.Graphics2D g2 = Aero.text((java.awt.Graphics2D) g.create()); super.paintSafely(g2); g2.dispose();
        }
    }

    public static class FormattedTextField extends BasicFormattedTextFieldUI {
        public static ComponentUI createUI(JComponent c) { return new FormattedTextField(); }
        @Override protected void installListeners() { super.installListeners(); hook(getComponent()); }
        @Override protected void uninstallListeners() { unhook(getComponent()); super.uninstallListeners(); }
        @Override protected void paintSafely(java.awt.Graphics g) {
            java.awt.Graphics2D g2 = Aero.text((java.awt.Graphics2D) g.create()); super.paintSafely(g2); g2.dispose();
        }
    }
}
