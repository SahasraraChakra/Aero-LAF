package aero;

import java.awt.*;
import java.util.Enumeration;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.BasicLookAndFeel;

/**
 * Windows 7 "Aero" look and feel for Swing. Usage:
 * <pre>AeroLookAndFeel.install();   // before creating any window</pre>
 * Default font is Inter 11 (bundled). Override the size with -Daero.fontSize=12.
 */
public class AeroLookAndFeel extends BasicLookAndFeel {

    /** Installs the L&F and switches on custom (glass) window decorations. */
    public static void install() {
        System.setProperty("swing.aatext", "true");
        System.setProperty("awt.useSystemAAFontSettings", "on");
        try { UIManager.setLookAndFeel(new AeroLookAndFeel()); } catch (UnsupportedLookAndFeelException e) { throw new RuntimeException(e); }
        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);
    }

    @Override public String getName() { return "Aero"; }
    @Override public String getID() { return "Aero"; }
    @Override public String getDescription() { return "Windows 7 Aero look and feel"; }
    @Override public boolean isNativeLookAndFeel() { return false; }
    @Override public boolean isSupportedLookAndFeel() { return true; }
    @Override public boolean getSupportsWindowDecorations() { return true; }

    /** Also borrow Swing's own text anti-aliasing setting (needs a real desktop; harmless elsewhere). */
    @Override public UIDefaults getDefaults() {
        UIDefaults d = super.getDefaults();
        try {
            UIDefaults m = new javax.swing.plaf.metal.MetalLookAndFeel().getDefaults();
            for (Object k : m.keySet()) {
                if ("AATextInfoPropertyKey".equals(String.valueOf(k)) && m.get(k) != null) { d.put(k, m.get(k)); break; }
            }
        } catch (Throwable ignored) { }
        return d;
    }

    @Override protected void initClassDefaults(UIDefaults t) {
        super.initClassDefaults(t);
        String p = "aero.";
        Object[] d = {
            "LabelUI", p + "AeroLabelUI",
            "PanelUI", p + "AeroPanelUI",
            "CheckBoxUI", p + "AeroCheckUIs$CheckBox",
            "RadioButtonUI", p + "AeroCheckUIs$RadioButton",
            "TextAreaUI", p + "AeroTextAreaUIs$TextArea",
            "EditorPaneUI", p + "AeroTextAreaUIs$EditorPane",
            "TextPaneUI", p + "AeroTextAreaUIs$TextPane",
            "ButtonUI", p + "AeroButtonUI",
            "ToggleButtonUI", p + "AeroButtonUI",
            "ComboBoxUI", p + "AeroComboBoxUI",
            "TabbedPaneUI", p + "AeroTabbedPaneUI",
            "TextFieldUI", p + "AeroTextUI$TextField",
            "PasswordFieldUI", p + "AeroTextUI$PasswordField",
            "FormattedTextFieldUI", p + "AeroTextUI$FormattedTextField",
            "MenuBarUI", p + "AeroMenus$MenuBar",
            "MenuUI", p + "AeroMenus$Menu",
            "MenuItemUI", p + "AeroMenus$MenuItem",
            "CheckBoxMenuItemUI", p + "AeroMenus$CheckBoxMenuItem",
            "RadioButtonMenuItemUI", p + "AeroMenus$RadioButtonMenuItem",
            "PopupMenuUI", p + "AeroMenus$PopupMenu",
            "PopupMenuSeparatorUI", p + "AeroMenus$PopupSeparator",
            "ToolBarUI", p + "AeroToolBarUI",
            "ToolBarSeparatorUI", p + "AeroToolBarUI$Separator",
            "ScrollBarUI", p + "AeroScrollBarUI",
            "SliderUI", p + "AeroSliderUI",
            "ProgressBarUI", p + "AeroProgressBarUI",
            "SpinnerUI", p + "AeroSpinnerUI",
            "SplitPaneUI", p + "AeroSplitPaneUI",
            "TableHeaderUI", p + "AeroTableHeaderUI",
            "TableUI", p + "AeroTableUI",
            "ToolTipUI", p + "AeroToolTipUI",
            "InternalFrameUI", p + "AeroInternalFrameUI",
            "DesktopIconUI", p + "AeroDesktopIconUI",
            "RootPaneUI", p + "AeroRootPaneUI",
        };
        t.putDefaults(d);
        // CheckBox/RadioButton: stock delegates + our icons
    }

    @Override protected void initSystemColorDefaults(UIDefaults t) {
        String[] c = {
            "desktop", "#AFC2DA", "activeCaption", "#B7CEE9", "activeCaptionText", "#000000", "activeCaptionBorder", "#2F4560",
            "inactiveCaption", "#DAE2EC", "inactiveCaptionText", "#6A7686", "inactiveCaptionBorder", "#7B8798",
            "window", "#FFFFFF", "windowBorder", "#828790", "windowText", "#1A1A1A",
            "menu", "#F1F1F1", "menuText", "#1A1A1A", "text", "#FFFFFF", "textText", "#1A1A1A",
            "textHighlight", "#3399FF", "textHighlightText", "#FFFFFF", "textInactiveText", "#838383",
            "control", "#F0F0F0", "controlText", "#1A1A1A", "controlHighlight", "#FFFFFF", "controlLtHighlight", "#FFFFFF",
            "controlShadow", "#A0A0A0", "controlDkShadow", "#696969", "scrollbar", "#E6E6E6",
            "info", "#F0F1F8", "infoText", "#575757",
        };
        loadSystemColors(t, c, false);
    }

    private static ColorUIResource col(int v) { return new ColorUIResource(v); }

    @Override protected void initComponentDefaults(UIDefaults t) {
        super.initComponentDefaults(t);
        Font base = Aero.baseFont();
        FontUIResource plain = new FontUIResource(base);
        FontUIResource bold = new FontUIResource(base.deriveFont(Font.BOLD));

        // 1. every font key becomes Inter (keys are inspected without resolving lazy values)
        java.util.List<Object> keys = new java.util.ArrayList<>(t.keySet());
        for (Object k : keys) {
            if (k instanceof String ks && (ks.endsWith(".font") || ks.endsWith("Font"))) t.put(ks, plain);
        }

        ColorUIResource text = col(0x1A1A1A), off = col(0x838383), white = col(0xFFFFFF), face = col(0xF0F0F0);
        ColorUIResource selBg = col(0xCCE4FA), selFg = col(0x000000);
        Border field = new BorderUIResource(new AeroBorders.TextBorder());
        Border scroll = new BorderUIResource(new AeroBorders.ScrollBorder());
        Border popup = new BorderUIResource(new AeroBorders.PopupBorder());
        Insets none = new InsetsUIResource(0, 0, 0, 0);

        Object[] d = {
            // ---- general
            "Panel.background", face, "Viewport.background", white, "OptionPane.background", face,
            "Label.foreground", text, "Label.disabledForeground", off, "Label.disabledShadow", white,
            "TitledBorder.border", new BorderUIResource(new AeroBorders.GroupBorder()), "TitledBorder.titleColor", col(0x1E395B),
            "ScrollPane.border", scroll, "ScrollPane.background", white, "ScrollPane.viewportBorder", null,
            "Separator.foreground", col(0xD5D5D5), "Separator.background", white,
            "ToolTip.background", col(0xF0F1F8), "ToolTip.foreground", col(0x575757),
            "ToolTip.border", new BorderUIResource(new EmptyBorder(4, 7, 4, 7)),
            // ---- buttons
            "Button.border", new BorderUIResource(new AeroBorders.ButtonBorder()), "Button.margin", new InsetsUIResource(3, 12, 3, 12),
            "Button.foreground", text, "Button.background", face, "Button.disabledText", off, "Button.textShiftOffset", 0,
            "Button.focus", col(0x404040), "Button.select", face,
            "ToggleButton.border", new BorderUIResource(new AeroBorders.ButtonBorder()), "ToggleButton.margin", new InsetsUIResource(3, 12, 3, 12),
            "ToggleButton.foreground", text, "ToggleButton.disabledText", off, "ToggleButton.textShiftOffset", 0,
            "CheckBox.icon", AeroIcons.checkBox(), "CheckBox.foreground", text, "CheckBox.disabledText", off, "CheckBox.focus", col(0x404040),
            "CheckBox.textIconGap", 6, "CheckBox.background", face,
            "RadioButton.icon", AeroIcons.radio(), "RadioButton.foreground", text, "RadioButton.disabledText", off, "RadioButton.focus", col(0x404040),
            "RadioButton.textIconGap", 6, "RadioButton.background", face,
            // ---- text
            "TextField.border", field, "PasswordField.border", field, "FormattedTextField.border", field, "Spinner.border", field,
            "TextField.background", white, "PasswordField.background", white, "FormattedTextField.background", white,
            "TextField.inactiveBackground", face, "PasswordField.inactiveBackground", face, "FormattedTextField.inactiveBackground", face,
            "TextArea.background", white, "TextPane.background", white, "EditorPane.background", white,
            "TextField.margin", none, "PasswordField.margin", none, "FormattedTextField.margin", none,
            "TextArea.margin", new InsetsUIResource(2, 4, 2, 4), "TextPane.margin", new InsetsUIResource(2, 4, 2, 4), "EditorPane.margin", new InsetsUIResource(2, 4, 2, 4),
            "TextField.inactiveForeground", off, "TextArea.inactiveForeground", off,
            "TextField.selectionBackground", col(0x3399FF), "TextField.selectionForeground", white,
            "TextArea.selectionBackground", col(0x3399FF), "TextArea.selectionForeground", white,
            "TextPane.selectionBackground", col(0x3399FF), "EditorPane.selectionBackground", col(0x3399FF),
            "PasswordField.selectionBackground", col(0x3399FF), "FormattedTextField.selectionBackground", col(0x3399FF),
            "TextField.caretForeground", text, "TextArea.caretForeground", text,
            // ---- combo / list
            "ComboBox.background", white, "ComboBox.foreground", text, "ComboBox.disabledForeground", off,
            "ComboBox.border", new BorderUIResource(new EmptyBorder(1, 1, 1, 1)), "ComboBox.squareButton", Boolean.FALSE,
            "ComboBox.selectionBackground", selBg, "ComboBox.selectionForeground", selFg,
            "List.background", white, "List.foreground", text, "List.selectionBackground", selBg, "List.selectionForeground", selFg,
            "List.focusCellHighlightBorder", new BorderUIResource(new EmptyBorder(1, 1, 1, 1)),
            "List.cellRenderer", new UIDefaults.ProxyLazyValue("aero.AeroListCellRenderer$UI"),
            // ---- tabs
            "TabbedPane.background", face, "TabbedPane.foreground", text, "TabbedPane.contentOpaque", Boolean.TRUE,
            "TabbedPane.tabInsets", new InsetsUIResource(3, 9, 3, 9), "TabbedPane.tabAreaInsets", new InsetsUIResource(3, 2, 0, 2),
            "TabbedPane.contentBorderInsets", new InsetsUIResource(1, 1, 1, 1), "TabbedPane.selectedTabPadInsets", new InsetsUIResource(2, 2, 2, 2),
            "TabbedPane.tabsOverlapBorder", Boolean.TRUE, "TabbedPane.textIconGap", 4, "TabbedPane.tabRunOverlay", 0,
            // ---- menus
            "MenuBar.border", new BorderUIResource(new EmptyBorder(1, 2, 2, 2)), "MenuBar.background", face,
            "Menu.border", new BorderUIResource(new EmptyBorder(3, 7, 3, 7)), "Menu.foreground", text, "Menu.selectionForeground", selFg, "Menu.selectionBackground", selBg,
            "Menu.arrowIcon", AeroIcons.menuArrow(), "Menu.checkIcon", AeroIcons.nothing(1, 1), "Menu.margin", new InsetsUIResource(2, 2, 2, 2),
            "Menu.disabledForeground", off, "Menu.acceleratorForeground", col(0x6D6D6D),
            "MenuItem.border", new BorderUIResource(new EmptyBorder(3, 4, 3, 4)), "MenuItem.foreground", text, "MenuItem.selectionForeground", selFg, "MenuItem.selectionBackground", selBg,
            "MenuItem.acceleratorForeground", col(0x6D6D6D), "MenuItem.acceleratorSelectionForeground", col(0x6D6D6D), "MenuItem.disabledForeground", off,
            "MenuItem.checkIcon", AeroIcons.nothing(1, 1), "MenuItem.arrowIcon", AeroIcons.nothing(1, 1), "MenuItem.margin", new InsetsUIResource(1, 24, 1, 2),
            "MenuItem.minimumTextOffset", 0, "MenuItem.afterCheckIconGap", 8, "MenuItem.textIconGap", 8,
            "CheckBoxMenuItem.checkIcon", AeroIcons.menuCheck(), "CheckBoxMenuItem.border", new BorderUIResource(new EmptyBorder(3, 4, 3, 4)),
            "CheckBoxMenuItem.margin", new InsetsUIResource(1, 24, 1, 2), "CheckBoxMenuItem.selectionBackground", selBg, "CheckBoxMenuItem.selectionForeground", selFg,
            "CheckBoxMenuItem.acceleratorForeground", col(0x6D6D6D), "CheckBoxMenuItem.foreground", text, "CheckBoxMenuItem.arrowIcon", AeroIcons.nothing(1, 1),
            "RadioButtonMenuItem.checkIcon", AeroIcons.menuRadio(), "RadioButtonMenuItem.border", new BorderUIResource(new EmptyBorder(3, 4, 3, 4)),
            "RadioButtonMenuItem.margin", new InsetsUIResource(1, 24, 1, 2), "RadioButtonMenuItem.selectionBackground", selBg, "RadioButtonMenuItem.selectionForeground", selFg,
            "RadioButtonMenuItem.acceleratorForeground", col(0x6D6D6D), "RadioButtonMenuItem.foreground", text, "RadioButtonMenuItem.arrowIcon", AeroIcons.nothing(1, 1),
            "PopupMenu.border", popup, "PopupMenu.background", col(0xF1F1F1), "PopupMenu.foreground", text,
            // ---- toolbar
            "ToolBar.border", new BorderUIResource(new AeroBorders.ToolBarBorder()), "ToolBar.background", face, "ToolBar.isRollover", Boolean.TRUE,
            "ToolBar.rolloverBorder", new BorderUIResource(new AeroBorders.ButtonBorder()), "ToolBar.nonrolloverBorder", new BorderUIResource(new AeroBorders.ButtonBorder()),
            "ToolBar.separatorSize", new DimensionUIResource(8, 8),
            // ---- scroll / slider / progress / spinner / split
            "ScrollBar.width", 17, "ScrollBar.background", col(0xE6E6E6),
            "Slider.background", face, "Slider.focus", face, "Slider.tickColor", col(0x8D939B),
            "ProgressBar.border", new BorderUIResource(new EmptyBorder(1, 1, 1, 1)), "ProgressBar.foreground", col(0x1A1A1A), "ProgressBar.background", col(0xE6E6E6),
            "ProgressBar.selectionForeground", col(0x1A1A1A), "ProgressBar.selectionBackground", col(0x1A1A1A),
            "ProgressBar.horizontalSize", new DimensionUIResource(146, 17), "ProgressBar.verticalSize", new DimensionUIResource(17, 146),
            "ProgressBar.repaintInterval", 30, "ProgressBar.cycleTime", 1600,
            "Spinner.background", white, "Spinner.foreground", text,
            "SplitPane.border", null, "SplitPane.dividerSize", 7, "SplitPaneDivider.border", null, "SplitPane.background", face,
            // ---- table / tree
            "Table.background", white, "Table.foreground", text, "Table.selectionBackground", selBg, "Table.selectionForeground", selFg,
            "Table.gridColor", col(0xF0F0F0), "Table.rowHeight", 22, "Table.scrollPaneBorder", scroll,
            "Table.focusCellHighlightBorder", new BorderUIResource.LineBorderUIResource(col(0x7DA2CE)),
            "Table.focusSelectedCellHighlightBorder", new BorderUIResource.LineBorderUIResource(col(0x7DA2CE)),
            "Table.cellNoFocusBorder", new BorderUIResource(new EmptyBorder(1, 6, 1, 6)),
            "TableHeader.foreground", col(0x4C607A), "TableHeader.background", white,
            "Tree.background", white, "Tree.foreground", text, "Tree.textBackground", white, "Tree.textForeground", text,
            "Tree.selectionBackground", selBg, "Tree.selectionForeground", selFg, "Tree.selectionBorderColor", col(0x7DA2CE),
            "Tree.expandedIcon", AeroIcons.treeNode(true), "Tree.collapsedIcon", AeroIcons.treeNode(false),
            "Tree.openIcon", AeroIcons.folder(true), "Tree.closedIcon", AeroIcons.folder(false), "Tree.leafIcon", AeroIcons.file(),
            "Tree.paintLines", Boolean.FALSE, "Tree.rowHeight", 22, "Tree.leftChildIndent", 8, "Tree.rightChildIndent", 10,
            // ---- dialogs
            "OptionPane.errorIcon", AeroIcons.message(AeroIcons.Message.ERROR), "OptionPane.informationIcon", AeroIcons.message(AeroIcons.Message.INFO),
            "OptionPane.warningIcon", AeroIcons.message(AeroIcons.Message.WARNING), "OptionPane.questionIcon", AeroIcons.message(AeroIcons.Message.QUESTION),
            "OptionPane.messageForeground", col(0x003399), "OptionPane.buttonAreaBorder", new BorderUIResource(new EmptyBorder(8, 8, 8, 8)),
            "FileChooser.upFolderIcon", AeroIcons.upFolder(), "FileChooser.newFolderIcon", AeroIcons.newFolder(), "FileChooser.homeFolderIcon", AeroIcons.home(),
            "FileChooser.listViewIcon", AeroIcons.listView(), "FileChooser.detailsViewIcon", AeroIcons.detailsView(),
            "FileChooser.computerIcon", AeroIcons.computer(), "FileChooser.directoryIcon", AeroIcons.folder(false), "FileChooser.fileIcon", AeroIcons.file(),
            "FileView.directoryIcon", AeroIcons.folder(false), "FileView.fileIcon", AeroIcons.file(), "FileView.computerIcon", AeroIcons.computer(),
            "FileView.hardDriveIcon", AeroIcons.drive(false), "FileView.floppyDriveIcon", AeroIcons.drive(true),
            // ---- internal frames / desktop
            "DesktopPane.background", col(0xAFC2DA), "InternalFrame.border", new BorderUIResource(new AeroBorders.InternalFrameBorder()),
            "InternalFrame.iconifyIcon", AeroIcons.caption(AeroIcons.Caption.MIN), "InternalFrame.maximizeIcon", AeroIcons.caption(AeroIcons.Caption.MAX),
            "InternalFrame.minimizeIcon", AeroIcons.caption(AeroIcons.Caption.RESTORE), "InternalFrame.closeIcon", AeroIcons.caption(AeroIcons.Caption.CLOSE),
            "InternalFrame.activeTitleBackground", col(0xB7CEE9), "InternalFrame.inactiveTitleBackground", col(0xDAE2EC),
            "InternalFrame.activeTitleForeground", text, "InternalFrame.inactiveTitleForeground", off,
            "InternalFrame.titleFont", plain,
            "Desktop.background", col(0xAFC2DA),
            "DesktopIcon.border", new BorderUIResource(new AeroBorders.InternalFrameBorder()),
        };
        t.putDefaults(d);
    }
}
