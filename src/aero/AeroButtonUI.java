package aero;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicGraphicsUtils;

/**
 * JButton and JToggleButton. Three looks, chosen automatically:
 * <ul>
 * <li>push button: glossy grey that turns blue on hover / press (default button gets a blue glow)</li>
 * <li>toolbar button (parent is a JToolBar): flat until hovered</li>
 * <li>caption button (client property Aero.style = "caption" / "captionClose"): glass buttons of internal frames</li>
 * </ul>
 */
public class AeroButtonUI extends BasicButtonUI {
    public static ComponentUI createUI(JComponent c) { return new AeroButtonUI(); }

    @Override protected void installDefaults(AbstractButton b) {
        super.installDefaults(b);
        b.setOpaque(false);
        b.setRolloverEnabled(true);
    }

    @Override public void update(Graphics g, JComponent c) { paint(g, c); }

    @Override public void paint(Graphics g, JComponent c) {
        AbstractButton b = (AbstractButton) c;
        if (b.isContentAreaFilled() || b.isBorderPainted()) paintBackground(g, b);
        super.paint(g, c);
    }

    private static boolean isToolbar(AbstractButton b) {
        return b.getParent() instanceof JToolBar || "toolbar".equals(b.getClientProperty("Aero.style"));
    }

    protected void paintBackground(Graphics g, AbstractButton b) {
        ButtonModel m = b.getModel();
        int w = b.getWidth(), h = b.getHeight();
        boolean enabled = b.isEnabled();
        boolean hot = enabled && m.isRollover();
        boolean pressed = enabled && m.isArmed() && m.isPressed();
        boolean selected = m.isSelected();
        Object style = b.getClientProperty("Aero.style");
        Graphics2D g2 = Aero.g2(g);
        try {
            if ("caption".equals(style) || "captionClose".equals(style)) {
                paintCaption(g2, w, h, hot, pressed, "captionClose".equals(style));
            } else if (isToolbar(b)) {
                if (!(hot || pressed || selected)) return;
                Aero.Skin s = (pressed || selected) ? Aero.TOOL_PRESSED : Aero.TOOL_HOT;
                Aero.glossy(g2, 0, 0, w, h, 6, s, false);
            } else {
                Aero.Skin s = Aero.skin(enabled, hot, pressed || (selected && b instanceof JToggleButton));
                Aero.glossy(g2, 0, 0, w, h, 6, s, false);
                if (enabled && !hot && !pressed && b instanceof JButton jb && jb.isDefaultButton()) {
                    g2.setColor(new Color(0x3DBCF0));
                    g2.draw(Aero.rr(1.5, 1.5, w - 3, h - 3, 4));
                    g2.setColor(Aero.alpha(new Color(0x3DBCF0), 90));
                    g2.draw(Aero.rr(2.5, 2.5, w - 5, h - 5, 3));
                } else if (selected && enabled && b instanceof JToggleButton) {
                    g2.setColor(new Color(0, 0, 0, 28));
                    g2.draw(Aero.rr(1.5, 1.5, w - 3, h - 3, 4));
                }
            }
        } finally {
            g2.dispose();
        }
    }

    private void paintCaption(Graphics2D g, int w, int h, boolean hot, boolean pressed, boolean close) {
        Aero.Skin s;
        if (close) {
            s = pressed ? new Aero.Skin(new Color(0xD26C58), new Color(0xBF402B), new Color(0x9E2713), new Color(0xC0533C), new Color(0x5A1A10))
                    : hot ? new Aero.Skin(new Color(0xF5C2B3), new Color(0xEC8A74), new Color(0xE0472B), new Color(0xF08C70), new Color(0x7A2A1C))
                    : new Aero.Skin(new Color(0xE9B3A8), new Color(0xD7796A), new Color(0xC44530), new Color(0xDE7E69), new Color(0x7A3A2E));
        } else {
            s = pressed ? new Aero.Skin(new Color(0xB1D4EA), new Color(0x8EC1E6), new Color(0x3F95CF), new Color(0x6EB9E8), new Color(0x2F5B7E))
                    : hot ? new Aero.Skin(new Color(0xE9F6FF), new Color(0xC8E9FB), new Color(0x7FC1EA), new Color(0xA6DBF6), new Color(0x3C6F96))
                    : new Aero.Skin(new Color(0xDCE7F4), new Color(0xBFD2E8), new Color(0xA5BDDA), new Color(0xB9CFE8), new Color(0x4A5B73));
        }
        Aero.glossy(g, 0, 0, w, h, 6, s, false);
    }

    @Override protected void paintText(Graphics g, AbstractButton b, Rectangle textRect, String text) {
        Graphics2D g2 = Aero.text((Graphics2D) g.create());
        FontMetrics fm = g2.getFontMetrics();
        g2.setColor(b.isEnabled() ? b.getForeground() : Aero.TEXT_DISABLED);
        BasicGraphicsUtils.drawStringUnderlineCharAt(g2, text, b.getDisplayedMnemonicIndex(),
                textRect.x, textRect.y + fm.getAscent());
        g2.dispose();
    }

    @Override protected void paintFocus(Graphics g, AbstractButton b, Rectangle viewRect, Rectangle textRect, Rectangle iconRect) {
        if (b.getParent() instanceof JToolBar || b.getClientProperty("Aero.style") != null) return;
        Graphics2D g2 = (Graphics2D) g;
        Aero.dottedRect(g2, 3, 3, b.getWidth() - 6, b.getHeight() - 6);
    }

    @Override protected void paintButtonPressed(Graphics g, AbstractButton b) { /* handled in paintBackground */ }
}
