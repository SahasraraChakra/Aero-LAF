package aero;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;

public class AeroProgressBarUI extends BasicProgressBarUI {
    public static ComponentUI createUI(JComponent c) { return new AeroProgressBarUI(); }

    @Override public Dimension getPreferredSize(JComponent c) {
        Dimension d = super.getPreferredSize(c);
        if (progressBar.getOrientation() == JProgressBar.HORIZONTAL) d.height = Math.max(d.height, 17);
        else d.width = Math.max(d.width, 17);
        return d;
    }

    private void track(Graphics2D g, int w, int h) {
        g.setPaint(new GradientPaint(0, 0, new Color(0xD9D9D9), 0, h, new Color(0xEFEFEF)));
        g.fill(Aero.rr(0, 0, w, h, 6));
        g.setColor(new Color(0xBCBCBC)); g.draw(Aero.rr(.5, .5, w - 1, h - 1, 6));
    }

    private void bar(Graphics2D g, int x, int y, int w, int h, boolean hz) {
        if (w < 3 || h < 3) return;
        Aero.glossy(g, x, y, w, h, 5, progressBar.isEnabled() ? Aero.GREEN : Aero.DISABLED, !hz);
    }

    @Override protected void paintDeterminate(Graphics g, JComponent c) {
        Graphics2D g2 = Aero.g2(g);
        Insets b = progressBar.getInsets();
        int w = progressBar.getWidth(), h = progressBar.getHeight();
        track(g2, w, h);
        int bw = w - b.left - b.right, bh = h - b.top - b.bottom;
        int full = getAmountFull(b, bw, bh);
        if (progressBar.getOrientation() == JProgressBar.HORIZONTAL) bar(g2, b.left, b.top, full, bh, true);
        else bar(g2, b.left, h - b.bottom - full, bw, full, false);
        g2.dispose();
        if (progressBar.isStringPainted()) { Graphics2D t = Aero.text((Graphics2D) g.create()); paintString(t, b.left, b.top, bw, bh, full, b); t.dispose(); }
    }

    @Override protected void paintIndeterminate(Graphics g, JComponent c) {
        Graphics2D g2 = Aero.g2(g);
        Insets b = progressBar.getInsets();
        int w = progressBar.getWidth(), h = progressBar.getHeight();
        track(g2, w, h);
        Rectangle box = getBox(null);
        if (box != null) bar(g2, box.x, box.y, box.width, box.height, progressBar.getOrientation() == JProgressBar.HORIZONTAL);
        g2.dispose();
        if (progressBar.isStringPainted()) { Graphics2D t = Aero.text((Graphics2D) g.create()); paintString(t, b.left, b.top, w - b.left - b.right, h - b.top - b.bottom, 0, b); t.dispose(); }
    }
}
