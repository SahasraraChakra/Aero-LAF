package aero;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.plaf.UIResource;

/** Every icon of the look and feel is drawn with Java2D, so nothing needs image files. */
public final class AeroIcons {
    private AeroIcons() {}

    private abstract static class VIcon implements Icon, UIResource {
        final int w, h;
        VIcon(int w, int h) { this.w = w; this.h = h; }
        public int getIconWidth() { return w; }
        public int getIconHeight() { return h; }
        public final void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = Aero.g2(g);
            g2.translate(x, y);
            draw(c, g2);
            g2.dispose();
        }
        abstract void draw(Component c, Graphics2D g);
    }

    private static boolean[] state(Component c) {   // enabled, hot, pressed, selected
        if (c instanceof AbstractButton b) {
            ButtonModel m = b.getModel();
            return new boolean[]{b.isEnabled(), m.isRollover(), m.isArmed() && m.isPressed(), m.isSelected()};
        }
        return new boolean[]{c == null || c.isEnabled(), false, false, false};
    }

    // ------------------------------------------------------------------ check box / radio
    public static Icon checkBox() {
        return new VIcon(15, 15) {
            void draw(Component c, Graphics2D g) {
                boolean[] s = state(c);
                Aero.Skin sk = !s[0] ? new Aero.Skin(Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, new Color(0xBCBCBC))
                        : s[2] ? new Aero.Skin(new Color(0xC4E5F6), new Color(0xB5DDF3), new Color(0xA2D4EF), new Color(0xC8E8F8), new Color(0x2C628B))
                        : s[1] ? new Aero.Skin(new Color(0xEAF6FD), new Color(0xD9F0FC), new Color(0xD3EEFB), new Color(0xEAF8FE), new Color(0x3C7FB1))
                        : new Aero.Skin(new Color(0xE9E9EA), new Color(0xF3F3F3), new Color(0xFAFAFA), new Color(0xFFFFFF), new Color(0x8E8F8F));
                Aero.glossy(g, 1, 1, 13, 13, 3, sk, false, false);
                if (s[3]) {
                    Path2D p = new Path2D.Float();
                    p.moveTo(4, 7.6); p.lineTo(6.3, 10.2); p.lineTo(11, 4);
                    g.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g.setColor(s[0] ? new Color(0x1E395B) : new Color(0xA0A0A0));
                    g.draw(p);
                }
            }
        };
    }

    public static Icon radio() {
        return new VIcon(15, 15) {
            void draw(Component c, Graphics2D g) {
                boolean[] s = state(c);
                Color o = !s[0] ? new Color(0xBCBCBC) : s[2] ? new Color(0x2C628B) : s[1] ? new Color(0x3C7FB1) : new Color(0x8E8F8F);
                Color t = !s[0] ? Color.WHITE : s[2] ? new Color(0xC4E5F6) : s[1] ? new Color(0xEAF6FD) : new Color(0xE9E9EA);
                Color b = !s[0] ? Color.WHITE : s[2] ? new Color(0xA2D4EF) : s[1] ? new Color(0xD3EEFB) : Color.WHITE;
                Ellipse2D e = new Ellipse2D.Float(1.5f, 1.5f, 12, 12);
                g.setPaint(new GradientPaint(0, 1, t, 0, 14, b));
                g.fill(e);
                g.setColor(o);
                g.draw(e);
                if (s[3]) {
                    Ellipse2D d = new Ellipse2D.Float(4.5f, 4.5f, 6, 6);
                    g.setPaint(new GradientPaint(0, 4.5f, s[0] ? new Color(0x5B8BCB) : new Color(0xC4C4C4),
                            0, 10.5f, s[0] ? new Color(0x1B3A6B) : new Color(0x9A9A9A)));
                    g.fill(d);
                }
            }
        };
    }

    // ------------------------------------------------------------------ menus
    public static Icon menuCheck() {
        return new VIcon(18, 18) {
            void draw(Component c, Graphics2D g) {
                if (!(c instanceof AbstractButton b) || !b.isSelected()) return;
                Aero.glossy(g, 0, 0, 18, 18, 4, new Aero.Skin(new Color(0xF9FCFF), new Color(0xE9F2FC),
                        new Color(0xDCEAF9), new Color(0xE8F2FC), new Color(0xA7C3E8)), false, true);
                Path2D p = new Path2D.Float();
                p.moveTo(4.5, 9.3); p.lineTo(7.6, 12.4); p.lineTo(13.2, 5.2);
                g.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.setColor(b.isEnabled() ? new Color(0x1E395B) : new Color(0xA0A0A0));
                g.draw(p);
            }
        };
    }

    public static Icon menuRadio() {
        return new VIcon(18, 18) {
            void draw(Component c, Graphics2D g) {
                if (!(c instanceof AbstractButton b) || !b.isSelected()) return;
                Aero.glossy(g, 0, 0, 18, 18, 4, new Aero.Skin(new Color(0xF9FCFF), new Color(0xE9F2FC),
                        new Color(0xDCEAF9), new Color(0xE8F2FC), new Color(0xA7C3E8)), false, true);
                g.setColor(b.isEnabled() ? new Color(0x1E395B) : new Color(0xA0A0A0));
                g.fill(new Ellipse2D.Float(5.5f, 5.5f, 7, 7));
            }
        };
    }

    public static Icon menuArrow() {
        return new VIcon(8, 10) {
            void draw(Component c, Graphics2D g) {
                boolean on = !(c instanceof AbstractButton b) || b.isEnabled();
                Aero.arrow(g, 4, 5, 3.5f, SwingConstants.EAST, on ? new Color(0x404040) : Aero.TEXT_DISABLED);
            }
        };
    }

    public static Icon nothing(int w, int h) {
        return new VIcon(w, h) { void draw(Component c, Graphics2D g) { } };
    }

    // ------------------------------------------------------------------ tree
    public static Icon treeNode(boolean expanded) {
        return new VIcon(16, 16) {
            void draw(Component c, Graphics2D g) {
                Path2D p = new Path2D.Float();
                if (expanded) {
                    p.moveTo(11.5, 4.5); p.lineTo(11.5, 11.5); p.lineTo(4.5, 11.5); p.closePath();
                    g.setPaint(new GradientPaint(0, 4, new Color(0x6F7F94), 0, 12, new Color(0x2F3E52)));
                    g.fill(p);
                } else {
                    p.moveTo(5.5, 3.5); p.lineTo(10.5, 8); p.lineTo(5.5, 12.5); p.closePath();
                    g.setColor(Color.WHITE);
                    g.fill(p);
                    g.setColor(new Color(0x8E98A6));
                    g.setStroke(new BasicStroke(1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g.draw(p);
                }
            }
        };
    }

    public static Icon folder(boolean open) {
        return new VIcon(16, 16) {
            void draw(Component c, Graphics2D g) {
                Path2D back = new Path2D.Float();
                back.moveTo(1.5, 3.5); back.lineTo(6, 3.5); back.lineTo(7.5, 5); back.lineTo(14, 5);
                back.lineTo(14, 13); back.lineTo(1.5, 13); back.closePath();
                g.setPaint(new GradientPaint(0, 3, new Color(0xFFE8A0), 0, 13, new Color(0xE9B64A)));
                g.fill(back);
                g.setColor(new Color(0xBE9333));
                g.draw(back);
                Path2D front = new Path2D.Float();
                if (open) { front.moveTo(3, 7); front.lineTo(15.5, 7); front.lineTo(13, 13); front.lineTo(1.5, 13); }
                else { front.moveTo(1.5, 6.5); front.lineTo(14, 6.5); front.lineTo(14, 13); front.lineTo(1.5, 13); }
                front.closePath();
                g.setPaint(new GradientPaint(0, 6, new Color(0xFFF2C2), 0, 13, new Color(0xF4C95A)));
                g.fill(front);
                g.setColor(new Color(0xC79A38));
                g.draw(front);
            }
        };
    }

    public static Icon file() {
        return new VIcon(16, 16) {
            void draw(Component c, Graphics2D g) {
                Path2D p = new Path2D.Float();
                p.moveTo(3.5, 1.5); p.lineTo(10, 1.5); p.lineTo(13.5, 5); p.lineTo(13.5, 14.5); p.lineTo(3.5, 14.5); p.closePath();
                g.setPaint(new GradientPaint(0, 1, Color.WHITE, 0, 15, new Color(0xE2E8EF)));
                g.fill(p);
                g.setColor(new Color(0x8895A8));
                g.draw(p);
                g.setColor(new Color(0xC1CAD6));
                g.drawLine(10, 1, 10, 5); g.drawLine(10, 5, 13, 5);
                for (int i = 0; i < 3; i++) g.drawLine(5, 7 + i * 2, 11, 7 + i * 2);
            }
        };
    }

    public static Icon computer() {
        return new VIcon(16, 16) {
            void draw(Component c, Graphics2D g) {
                g.setPaint(new GradientPaint(0, 2, new Color(0xDFE6EE), 0, 11, new Color(0xA9B6C6)));
                g.fill(Aero.rr(1.5, 2.5, 13, 9, 2));
                g.setColor(new Color(0x5B6B80)); g.draw(Aero.rr(1.5, 2.5, 13, 9, 2));
                g.setPaint(new GradientPaint(0, 3.5f, new Color(0x7EC4F4), 0, 10, new Color(0x2F7FC9)));
                g.fillRect(3, 4, 10, 6);
                g.setColor(new Color(0x7D8A9B)); g.fillRect(6, 12, 4, 1); g.fillRect(4, 13, 8, 1);
            }
        };
    }

    public static Icon drive(boolean floppy) {
        return new VIcon(16, 16) {
            void draw(Component c, Graphics2D g) {
                if (floppy) {
                    g.setColor(new Color(0x2E4E8C)); g.fill(Aero.rr(2.5, 1.5, 11, 13, 2));
                    g.setColor(new Color(0xE9EEF5)); g.fillRect(5, 2, 6, 4);
                    g.setColor(new Color(0xC9D3E2)); g.fillRect(4, 9, 8, 5);
                } else {
                    g.setPaint(new GradientPaint(0, 4, new Color(0xE9EEF4), 0, 13, new Color(0xA2AFC0)));
                    g.fill(Aero.rr(1.5, 4.5, 13, 8, 2));
                    g.setColor(new Color(0x5F6F84)); g.draw(Aero.rr(1.5, 4.5, 13, 8, 2));
                    g.setColor(new Color(0x39D36B)); g.fillRect(10, 9, 3, 2);
                }
            }
        };
    }

    public static Icon upFolder() {
        return new VIcon(18, 16) {
            void draw(Component c, Graphics2D g) {
                folder(false).paintIcon(c, g, 1, 1);
                Path2D p = new Path2D.Float();
                p.moveTo(11, 11); p.lineTo(11, 7); p.lineTo(8.5, 7); p.lineTo(12, 2.5); p.lineTo(15.5, 7); p.lineTo(13, 7); p.lineTo(13, 11); p.closePath();
                g.setPaint(new GradientPaint(0, 2, new Color(0x6FC66A), 0, 11, new Color(0x2E9A3C)));
                g.fill(p); g.setColor(new Color(0x226F2E)); g.draw(p);
            }
        };
    }

    public static Icon newFolder() {
        return new VIcon(18, 16) {
            void draw(Component c, Graphics2D g) {
                folder(false).paintIcon(c, g, 0, 1);
                g.setColor(new Color(0xF2B400)); g.fillOval(11, 1, 7, 7);
                g.setColor(Color.WHITE); g.fillRect(14, 2, 1, 5); g.fillRect(12, 4, 5, 1);
            }
        };
    }

    public static Icon home() {
        return new VIcon(18, 16) {
            void draw(Component c, Graphics2D g) {
                Path2D roof = new Path2D.Float();
                roof.moveTo(1, 8); roof.lineTo(9, 1.5); roof.lineTo(17, 8); roof.closePath();
                g.setColor(new Color(0xC8503A)); g.fill(roof);
                g.setPaint(new GradientPaint(0, 8, new Color(0xF8EBC7), 0, 15, new Color(0xE2C27F)));
                g.fillRect(3, 8, 12, 7);
                g.setColor(new Color(0x7A5A2A)); g.drawRect(3, 8, 12, 7);
                g.setColor(new Color(0x7A5A2A)); g.fillRect(8, 10, 3, 5);
            }
        };
    }

    public static Icon listView() {
        return new VIcon(18, 16) {
            void draw(Component c, Graphics2D g) {
                for (int col = 0; col < 2; col++) for (int r = 0; r < 4; r++) {
                    g.setColor(new Color(0x3F7FBF)); g.fillRect(2 + col * 8, 2 + r * 3, 2, 2);
                    g.setColor(new Color(0x7D8A9B)); g.fillRect(5 + col * 8, 2 + r * 3, 4, 2);
                }
            }
        };
    }

    public static Icon detailsView() {
        return new VIcon(18, 16) {
            void draw(Component c, Graphics2D g) {
                g.setColor(new Color(0xB3C4DA)); g.fillRect(2, 2, 14, 3);
                for (int r = 0; r < 3; r++) {
                    g.setColor(new Color(0x3F7FBF)); g.fillRect(2, 7 + r * 3, 2, 2);
                    g.setColor(new Color(0x7D8A9B)); g.fillRect(5, 7 + r * 3, 11, 2);
                }
            }
        };
    }

    // ------------------------------------------------------------------ option pane
    public enum Message { INFO, WARNING, ERROR, QUESTION }

    public static Icon message(Message kind) {
        return new VIcon(32, 32) {
            void draw(Component c, Graphics2D g) {
                Font f = Aero.baseFont().deriveFont(Font.BOLD, 20f);
                g.setFont(f);
                Aero.text(g);
                switch (kind) {
                    case WARNING -> {
                        Path2D t = new Path2D.Float();
                        t.moveTo(16, 2.5); t.lineTo(30, 28.5); t.lineTo(2, 28.5); t.closePath();
                        g.setPaint(new GradientPaint(0, 3, new Color(0xFFE675), 0, 29, new Color(0xF0A800)));
                        g.fill(t);
                        g.setColor(new Color(0xA77800)); g.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)); g.draw(t);
                        g.setColor(new Color(0x4A3600)); g.drawString("!", 16 - g.getFontMetrics().stringWidth("!") / 2f, 26);
                    }
                    default -> {
                        Color a = switch (kind) { case ERROR -> new Color(0xF5A08D); case QUESTION -> new Color(0x8CC4F4); default -> new Color(0x8CC4F4); };
                        Color b = switch (kind) { case ERROR -> new Color(0xCB2B14); default -> new Color(0x1C6BCB); };
                        Ellipse2D e = new Ellipse2D.Float(2, 2, 28, 28);
                        g.setPaint(new GradientPaint(0, 2, a, 0, 30, b)); g.fill(e);
                        g.setColor(Aero.alpha(Color.WHITE, 90)); g.fill(new Ellipse2D.Float(5, 3.5f, 22, 12));
                        g.setColor(b.darker()); g.setStroke(new BasicStroke(1.4f)); g.draw(e);
                        g.setColor(Color.WHITE);
                        String s = switch (kind) { case ERROR -> "\u2715"; case QUESTION -> "?"; default -> "i"; };
                        if (kind == Message.ERROR) {
                            g.setStroke(new BasicStroke(3.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                            g.drawLine(11, 11, 21, 21); g.drawLine(21, 11, 11, 21);
                        } else {
                            g.drawString(s, 16 - g.getFontMetrics().stringWidth(s) / 2f, 23.5f);
                        }
                    }
                }
            }
        };
    }

    // ------------------------------------------------------------------ small glyphs
    public enum Caption { MIN, MAX, RESTORE, CLOSE }

    /** Glyph for small caption buttons (internal frames). */
    public static Icon caption(Caption kind) {
        return new VIcon(12, 12) {
            void draw(Component c, Graphics2D g) {
                paintCaptionGlyph(g, kind, 6, 6, 1f, true);
            }
        };
    }

    public static void paintCaptionGlyph(Graphics2D g, Caption k, float cx, float cy, float scale, boolean shadow) {
        Shape s = switch (k) {
            case MIN -> new Line2D.Float(cx - 3.5f * scale, cy + 2.5f * scale, cx + 3.5f * scale, cy + 2.5f * scale);
            case MAX -> new Rectangle2D.Float(cx - 4f * scale, cy - 3f * scale, 8f * scale, 7f * scale);
            case RESTORE -> {
                Path2D p = new Path2D.Float();
                p.append(new Rectangle2D.Float(cx - 2f * scale, cy - 4f * scale, 6f * scale, 5f * scale), false);
                p.append(new Rectangle2D.Float(cx - 4.5f * scale, cy - 1.5f * scale, 6f * scale, 5f * scale), false);
                yield p;
            }
            case CLOSE -> {
                Path2D p = new Path2D.Float();
                p.moveTo(cx - 3.5f * scale, cy - 3.2f * scale); p.lineTo(cx + 3.5f * scale, cy + 3.2f * scale);
                p.moveTo(cx + 3.5f * scale, cy - 3.2f * scale); p.lineTo(cx - 3.5f * scale, cy + 3.2f * scale);
                yield p;
            }
        };
        float w = switch (k) { case MIN -> 3f; case MAX -> 2.2f; case RESTORE -> 1.6f; case CLOSE -> 2.4f; };
        if (shadow) {
            g.setStroke(new BasicStroke(w + 1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.setColor(new Color(0x14, 0x24, 0x3C, 150));
            g.draw(s);
        }
        g.setStroke(new BasicStroke(w, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(Color.WHITE);
        g.draw(s);
    }

    public static Icon sortArrow(boolean ascending) {
        return new VIcon(9, 6) {
            void draw(Component c, Graphics2D g) {
                Aero.arrow(g, 4.5f, 3, 3.5f, ascending ? SwingConstants.NORTH : SwingConstants.SOUTH, new Color(0x6B7C93));
            }
        };
    }

    /** Spin / scroll arrows share this: a chevron. */
    public static Icon arrow(int dir, int size, Color color) {
        return new VIcon(size, size) {
            void draw(Component c, Graphics2D g) {
                Aero.arrow(g, size / 2f, size / 2f, size / 2.6f, dir, color);
            }
        };
    }
}
