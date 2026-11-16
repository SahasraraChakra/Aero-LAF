package aero;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;

public class AeroSliderUI extends BasicSliderUI {
    public AeroSliderUI(JSlider s) { super(s); }
    public static ComponentUI createUI(JComponent c) { return new AeroSliderUI((JSlider) c); }

    @Override protected Dimension getThumbSize() {
        return slider.getOrientation() == JSlider.HORIZONTAL ? new Dimension(11, 21) : new Dimension(21, 11);
    }

    @Override public void paintFocus(Graphics g) { }

    @Override public void paintTrack(Graphics g) {
        Graphics2D g2 = Aero.g2(g);
        Rectangle t = trackRect;
        boolean hz = slider.getOrientation() == JSlider.HORIZONTAL;
        int th = 4;
        Rectangle r = hz ? new Rectangle(t.x, t.y + (t.height - th) / 2, t.width, th)
                         : new Rectangle(t.x + (t.width - th) / 2, t.y, th, t.height);
        g2.setColor(new Color(0xE7EAEA)); g2.fillRect(r.x, r.y, r.width, r.height);
        g2.setColor(new Color(0xA0A5AA)); g2.drawLine(r.x, r.y, r.x + r.width - 1, r.y); g2.drawLine(r.x, r.y, r.x, r.y + r.height - 1);
        g2.setColor(Color.WHITE); g2.drawLine(r.x + 1, r.y + r.height, r.x + r.width, r.y + r.height); g2.drawLine(r.x + r.width, r.y + 1, r.x + r.width, r.y + r.height);
        g2.dispose();
    }

    @Override public void paintThumb(Graphics g) {
        Graphics2D g2 = Aero.g2(g);
        Rectangle r = thumbRect;
        boolean hz = slider.getOrientation() == JSlider.HORIZONTAL;
        boolean en = slider.isEnabled();
        Aero.Skin s = !en ? Aero.DISABLED : dragging() ? Aero.PRESSED : thumbHot() ? Aero.HOT : Aero.NORMAL;
        Path2D p = new Path2D.Float();
        float x = r.x + .5f, y = r.y + .5f, w = r.width - 1, h = r.height - 1;
        boolean ticks = slider.getPaintTicks();
        if (hz) {
            if (ticks) { p.moveTo(x, y + 1); p.quadTo(x, y, x + 1, y); p.lineTo(x + w - 1, y); p.quadTo(x + w, y, x + w, y + 1);
                         p.lineTo(x + w, y + h - w / 2f - 1); p.lineTo(x + w / 2f, y + h); p.lineTo(x, y + h - w / 2f - 1); p.closePath(); }
            else p.append(new RoundRectangle2D.Float(x, y, w, h, 4, 4), false);
        } else {
            if (ticks) { p.moveTo(x + 1, y); p.lineTo(x + w - h / 2f - 1, y); p.lineTo(x + w, y + h / 2f); p.lineTo(x + w - h / 2f - 1, y + h); p.lineTo(x + 1, y + h);
                         p.quadTo(x, y + h, x, y + h - 1); p.lineTo(x, y + 1); p.quadTo(x, y, x + 1, y); p.closePath(); }
            else p.append(new RoundRectangle2D.Float(x, y, w, h, 4, 4), false);
        }
        g2.setPaint(hz ? new GradientPaint(r.x, 0, s.t1(), r.x + r.width, 0, s.b2()) : new GradientPaint(0, r.y, s.t1(), 0, r.y + r.height, s.b2()));
        g2.fill(p);
        g2.setColor(new Color(255, 255, 255, 160));
        g2.draw(new Rectangle2D.Float(x + 1, y + 1, w - 2, 1));
        g2.setColor(s.border());
        g2.draw(p);
        g2.dispose();
    }

    private boolean thumbHot() { return thumbRect.contains(hotPoint); }
    private Point hotPoint = new Point(-10, -10);

    @Override protected TrackListener createTrackListener(JSlider s) {
        return new TrackListener() {
            @Override public void mouseMoved(java.awt.event.MouseEvent e) { hotPoint = e.getPoint(); slider.repaint(thumbRect); }
            @Override public void mouseExited(java.awt.event.MouseEvent e) { hotPoint = new Point(-10, -10); slider.repaint(thumbRect); }
        };
    }
    private boolean dragging() { return slider.getValueIsAdjusting(); }

    @Override public void paintTicks(Graphics g) {
        g.setColor(new Color(0x8D939B));
        super.paintTicks(g);
    }
}
