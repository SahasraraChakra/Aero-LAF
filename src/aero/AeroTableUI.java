package aero;

import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;

public class AeroTableUI extends BasicTableUI {
    public static ComponentUI createUI(JComponent c) { return new AeroTableUI(); }
    @Override protected void installDefaults() {
        super.installDefaults();
        int rh = UIManager.getInt("Table.rowHeight");
        if (rh > 0 && table.getRowHeight() < rh) table.setRowHeight(rh);
        table.setIntercellSpacing(new java.awt.Dimension(0, 0));
    }
}
