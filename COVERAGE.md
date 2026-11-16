# Component coverage (java_swing_components.md)

Custom Aero painting: JButton, JToggleButton, JCheckBox, JRadioButton, JMenu, JMenuItem, JCheckBoxMenuItem,
JRadioButtonMenuItem, JPopupMenu, JMenuBar, popup separator, JTextField, JPasswordField, JFormattedTextField,
JComboBox (+popup, editable), JSlider, JSpinner, JProgressBar, JScrollBar, JSplitPane, JTabbedPane, JToolBar
(+separator), JTable (+JTableHeader, sort arrows), JTree, JList, JToolTip, JInternalFrame, JDesktopIcon,
JRootPane (glass frame + title bar for JFrame / JDialog / JOptionPane dialogs), TitledBorder (group box).

Skinned through colours, fonts, borders and icons (stock layout, Aero styling): JLabel, JPanel, JScrollPane,
JViewport, JSeparator, JTextArea, JEditorPane, JTextPane, JDesktopPane, JLayeredPane, JColorChooser,
JFileChooser, JOptionPane (vector info/warning/error/question icons), Box / Box.Filler, JLayer.

No UI of their own (nothing to skin): JWindow (undecorated by design), JApplet (removed from modern Java),
ButtonGroup, Action, Timer, SwingWorker, BorderFactory, UIManager, and the layout managers.
Every demo component is in `AeroDemo`.
