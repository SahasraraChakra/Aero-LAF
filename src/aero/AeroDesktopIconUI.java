package aero;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.BasicDesktopIconUI;

/** Minimised JInternalFrame (JDesktopIcon): a small glass title bar. */
public class AeroDesktopIconUI extends BasicDesktopIconUI {
    public static ComponentUI createUI(JComponent c) { return new AeroDesktopIconUI(); }

    @Override protected void installComponents() {
        frame = desktopIcon.getInternalFrame();
        iconPane = new AeroInternalFrameUI.Title(frame);
        desktopIcon.setLayout(new BorderLayout());
        desktopIcon.add(iconPane, BorderLayout.CENTER);
    }
    @Override public Dimension getPreferredSize(JComponent c) { return new Dimension(170, 24); }
    @Override public Dimension getMinimumSize(JComponent c) { return getPreferredSize(c); }
}
