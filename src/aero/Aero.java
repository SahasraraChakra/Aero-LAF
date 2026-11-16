package aero;

import java.awt.*;
import java.awt.geom.*;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import javax.swing.SwingConstants;

/** Palette, gradients and small painting helpers shared by every Aero UI delegate. */
public final class Aero {
    private Aero() {}

    /** A four-stop "glossy" fill (top half t1→t2, bottom half b1→b2) plus an outline. */
    public record Skin(Color t1, Color t2, Color b1, Color b2, Color border) {}

    private static Color c(int v) { return new Color(v); }
    public static Color alpha(Color c, int a) { return new Color(c.getRed(), c.getGreen(), c.getBlue(), a); }

    // ---------------------------------------------------------------- basic colours
    public static final Color FACE          = c(0xF0F0F0);
    public static final Color WINDOW        = Color.WHITE;
    public static final Color TEXT          = c(0x1A1A1A);
    public static final Color TEXT_DISABLED = c(0x838383);
    public static final Color TEXT_GROUP    = c(0x1E395B);
    public static final Color TEXT_HEADER   = c(0x4C607A);
    public static final Color FIELD_BORDER  = c(0xABADB3);
    public static final Color FIELD_HOT     = c(0x5794BF);
    public static final Color FIELD_FOCUS   = c(0x3D7BAD);
    public static final Color FIELD_OFF     = c(0xC9CACB);
    public static final Color SELECTION     = c(0x3399FF);
    public static final Color SEPARATOR     = c(0xD5D5D5);
    public static final Color ACCENT_DARK   = c(0x1E395B);

    // ---------------------------------------------------------------- button skins
    public static final Skin NORMAL   = new Skin(c(0xF2F2F2), c(0xEBEBEB), c(0xDDDDDD), c(0xCFCFCF), c(0x707070));
    public static final Skin HOT      = new Skin(c(0xEAF6FD), c(0xD9F0FC), c(0xBEE6FD), c(0xA7D9F5), c(0x3C7FB1));
    public static final Skin PRESSED  = new Skin(c(0xE5F4FC), c(0xC4E5F6), c(0x98D1EF), c(0x68B3DB), c(0x2C628B));
    public static final Skin DISABLED = new Skin(c(0xF4F4F4), c(0xF4F4F4), c(0xF4F4F4), c(0xF4F4F4), c(0xADB2B5));
    // flat toolbar / menu-bar items
    public static final Skin TOOL_HOT     = new Skin(c(0xFDFEFF), c(0xF4F8FD), c(0xE8F0FA), c(0xDCE8F7), c(0xB6CEEE));
    public static final Skin TOOL_PRESSED = new Skin(c(0xE4EFFB), c(0xD6E6F8), c(0xC5DBF4), c(0xB6D1F0), c(0x8CAEDB));
    // list / tree / menu selection
    public static final Skin SEL_ACTIVE   = new Skin(c(0xEBF4FE), c(0xDCEBFC), c(0xD0E3FB), c(0xC1DBFC), c(0x7DA2CE));
    public static final Skin SEL_HOT      = new Skin(c(0xFAFBFD), c(0xF3F7FD), c(0xEBF3FD), c(0xDDEBFD), c(0xB8D6FB));
    public static final Skin SEL_INACTIVE = new Skin(c(0xFAFAFB), c(0xF1F1F2), c(0xE8E8E9), c(0xD9D9DA), c(0xD9D9D9));
    // progress
    public static final Skin GREEN = new Skin(c(0x9BF0A8), c(0x4FDC68), c(0x0FB531), c(0x35D555), c(0x2A9B42));

    public static Skin skin(boolean enabled, boolean hot, boolean pressed) {
        if (!enabled) return DISABLED;
        if (pressed) return PRESSED;
        return hot ? HOT : NORMAL;
    }

    // ---------------------------------------------------------------- graphics helpers
    public static Graphics2D g2(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        return g2;
    }

    /** Applies the desktop's text anti-aliasing (ClearType on Windows) or plain AA. */
    public static Graphics2D text(Graphics2D g) {
        Object hints = Toolkit.getDefaultToolkit().getDesktopProperty("awt.font.desktophints");
        if (hints instanceof Map<?, ?> m) {
            for (Map.Entry<?, ?> e : m.entrySet())
                g.setRenderingHint((RenderingHints.Key) e.getKey(), e.getValue());
        } else {
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        }
        return g;
    }

    public static RoundRectangle2D rr(double x, double y, double w, double h, double arc) {
        return new RoundRectangle2D.Double(x, y, w, h, arc, arc);
    }

    /** Fills a rounded shape with a two-tone glossy gradient, then strokes the outline. */
    public static void glossy(Graphics2D g, float x, float y, float w, float h, float arc, Skin s, boolean leftToRight) {
        glossy(g, x, y, w, h, arc, s, leftToRight, true);
    }

    public static void glossy(Graphics2D g, float x, float y, float w, float h, float arc, Skin s,
                              boolean leftToRight, boolean innerHighlight) {
        RoundRectangle2D shape = new RoundRectangle2D.Float(x + .5f, y + .5f, w - 1, h - 1, arc, arc);
        Area a1 = new Area(shape), a2 = new Area(shape);
        if (!leftToRight) {
            float mid = y + h / 2f;
            a1.intersect(new Area(new Rectangle2D.Float(x, y, w, mid - y)));
            a2.intersect(new Area(new Rectangle2D.Float(x, mid, w, y + h - mid)));
            g.setPaint(new GradientPaint(0, y, s.t1, 0, mid, s.t2)); g.fill(a1);
            g.setPaint(new GradientPaint(0, mid, s.b1, 0, y + h, s.b2)); g.fill(a2);
        } else {
            float mid = x + w / 2f;
            a1.intersect(new Area(new Rectangle2D.Float(x, y, mid - x, h)));
            a2.intersect(new Area(new Rectangle2D.Float(mid, y, x + w - mid, h)));
            g.setPaint(new GradientPaint(x, 0, s.t1, mid, 0, s.t2)); g.fill(a1);
            g.setPaint(new GradientPaint(mid, 0, s.b1, x + w, 0, s.b2)); g.fill(a2);
        }
        if (innerHighlight && w > 4 && h > 4) {
            g.setColor(new Color(255, 255, 255, 170));
            g.draw(new RoundRectangle2D.Float(x + 1.5f, y + 1.5f, w - 3, h - 3, Math.max(arc - 2, 0), Math.max(arc - 2, 0)));
        }
        if (s.border != null) { g.setColor(s.border); g.draw(shape); }
    }

    public static void vgradient(Graphics2D g, Shape s, float y, float h, Color top, Color bottom) {
        g.setPaint(new GradientPaint(0, y, top, 0, y + h, bottom));
        g.fill(s);
    }

    /** Small solid triangle. size = half of the base width. */
    public static void arrow(Graphics2D g, float cx, float cy, float size, int dir, Color col) {
        Path2D p = new Path2D.Float();
        float h = size;
        switch (dir) {
            case SwingConstants.NORTH -> { p.moveTo(cx - size, cy + h / 2); p.lineTo(cx + size, cy + h / 2); p.lineTo(cx, cy - h / 2); }
            case SwingConstants.SOUTH -> { p.moveTo(cx - size, cy - h / 2); p.lineTo(cx + size, cy - h / 2); p.lineTo(cx, cy + h / 2); }
            case SwingConstants.WEST  -> { p.moveTo(cx + h / 2, cy - size); p.lineTo(cx + h / 2, cy + size); p.lineTo(cx - h / 2, cy); }
            default                   -> { p.moveTo(cx - h / 2, cy - size); p.lineTo(cx - h / 2, cy + size); p.lineTo(cx + h / 2, cy); }
        }
        p.closePath();
        g.setColor(col);
        g.fill(p);
    }

    public static void dottedRect(Graphics2D g, int x, int y, int w, int h) {
        Graphics2D d = (Graphics2D) g.create();
        d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        d.setColor(new Color(0x404040));
        d.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1f, new float[]{1f, 1f}, 0f));
        d.drawRect(x, y, w - 1, h - 1);
        d.dispose();
    }

    // ---------------------------------------------------------------- fonts
    private static Font base;

    public static int fontSize() { return Integer.getInteger("aero.fontSize", 11); }

    /** Default UI font: Inter 11 (bundled copy is registered if Inter isn't installed). */
    public static synchronized Font baseFont() {
        if (base == null) {
            registerBundledFonts();
            Set<String> have = new HashSet<>(java.util.List.of(
                    GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));
            String family = "SansSerif";
            for (String f : new String[]{"Inter", "Segoe UI", "SF Pro Text", "Helvetica Neue", "Arial"}) {
                if (have.contains(f)) { family = f; break; }
            }
            base = new Font(family, Font.PLAIN, fontSize());
        }
        return base;
    }

    private static void registerBundledFonts() {
        for (String n : new String[]{"Inter-Regular", "Inter-Bold", "Inter-Italic", "Inter-BoldItalic"}) {
            try (InputStream in = Aero.class.getResourceAsStream("/aero/fonts/" + n + ".otf")) {
                if (in == null) continue;
                Font f = Font.createFont(Font.TRUETYPE_FONT, in);
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(f);
            } catch (Exception ignored) { }
        }
    }
}
