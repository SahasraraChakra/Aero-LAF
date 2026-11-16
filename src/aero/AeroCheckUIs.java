package aero;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;

/** Check box / radio button delegates that always paint their text with desktop anti-aliasing. */
public final class AeroCheckUIs {
    private AeroCheckUIs() {}

    public static class CheckBox extends BasicCheckBoxUI {
        public static ComponentUI createUI(JComponent c) { return new CheckBox(); }
        @Override public void paint(Graphics g, JComponent c) {
            Graphics2D g2 = Aero.text((Graphics2D) g.create());
            super.paint(g2, c);
            g2.dispose();
        }
    }

    public static class RadioButton extends BasicRadioButtonUI {
        public static ComponentUI createUI(JComponent c) { return new RadioButton(); }
        @Override public void paint(Graphics g, JComponent c) {
            Graphics2D g2 = Aero.text((Graphics2D) g.create());
            super.paint(g2, c);
            g2.dispose();
        }
    }
}
