package skywaysolutions.app.gui;

import skywaysolutions.app.gui.control.DateField;
import skywaysolutions.app.gui.control.StatusBar;
import skywaysolutions.app.gui.control.VTextField;
import skywaysolutions.app.staff.StaffRole;
import skywaysolutions.app.utils.AccessorManager;
import skywaysolutions.app.utils.CheckedException;
import skywaysolutions.app.utils.Time;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class BlankEditor extends JDialogx {
    private JPanel Root;
    private JPanel panelBlankSelection;
    private JPanel panelBlankSelector;
    private VTextField textFieldBlankSelectorA;
    private VTextField textFieldBlankSelectorB;
    private JComboBox comboBoxType;
    private JComboBox comboBoxAssign;
    private DateField dateFieldDate;
    private JTextArea textAreaDescription;
    private JPanel panelType;
    private DateField dateFieldCreated;
    private JPanel panelCreated;
    private DateField dateFieldReturn;
    private JButton buttonBlacklist;
    private JButton buttonVoid;
    private JButton buttonCancel;
    private JButton buttonOk;
    private StatusBar statusBar;
    private JPanel panelReturn;
    private JPanel panelAssign;
    private JLabel labelBlankSelectorB;
    private JLabel labelBlankSelectorA;
    private JPanel panelDate;
    private final JTextField textFieldRBlankSelector;
    private final JTextField textFieldRType;
    private final JTextField textFieldRCreated;
    private final JTextField textFieldRReturn;
    private final JTextField textFieldRAssign;
    private final JTextField textFieldRDate;
    private final AccessorManager manager;
    private Prompt prompt;
    private Long id;
    private int[] blankTypes;
    private final ArrayList<String> dispBlankTypes = new ArrayList<>();
    private String[] staff;
    private final ArrayList<String> dispStaff = new ArrayList<>();
    private Long startIndex;
    private Long endIndex;
    private Long blankAssigned;
    private Date blankAssignedDate;
    private Date blankCreateDate;
    private String blankDesc;

    /**
     * Constructs a new instance of BlankEditor with the specified owner, if reusable and the accessor manager instance.
     *
     * @param owner    The window owner or null.
     * @param reusable If this dialog is reusable.
     * @param manager  The accessor manager instance.
     */
    public BlankEditor(Window owner, boolean reusable, AccessorManager manager) {
        super(owner, "", reusable);
        this.manager = manager;
        prompt = new Prompt(owner, "Are you sure?", "", new String[]{"No", "Yes"}, 0, true);
        //Setup readonly textfields (Used as readonly controls that swap with controls that have to be disabled to be readonly)
        textFieldRBlankSelector = new JTextField();
        textFieldRBlankSelector.setEditable(false);
        textFieldRType = new JTextField();
        textFieldRType.setEditable(false);
        textFieldRCreated = new JTextField();
        textFieldRCreated.setEditable(false);
        textFieldRReturn = new JTextField();
        textFieldRReturn.setEditable(false);
        textFieldRAssign = new JTextField();
        textFieldRAssign.setEditable(false);
        textFieldRDate = new JTextField();
        textFieldRDate.setEditable(false);
        //Setup form contents
        setContentPane(Root);
        //getRootPane().setDefaultButton(buttonOk);
        //Setup form closing events
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                hideDialog();
            }
        });
        //Setup controls and their events
        try {
            textFieldBlankSelectorA.setup("[0-9]{6,8}", statusBar, "Invalid blank extension number!", false, true);
            textFieldBlankSelectorB.setup("[0-9]{6,8}", statusBar, "Invalid blank extension number!", false, true);
            dateFieldDate.setup(statusBar);
            dateFieldCreated.setup(statusBar);
            dateFieldReturn.setup(statusBar);
        } catch (CheckedException e) {
            statusBar.setStatus(e, 2500);
        }
        comboBoxType.addActionListener(e -> {
            if (statusBar.isInHelpMode()) return;
            if (comboBoxType.getSelectedIndex() < 0) {
                Object selectedItem = comboBoxType.getSelectedItem();
                if (selectedItem instanceof String str) {
                    for (String c : dispBlankTypes)
                        if (c.startsWith(str) || c.contains(str)) {
                            comboBoxType.setSelectedItem(c);
                            break;
                        }
                }
                if (comboBoxType.getSelectedIndex() < 0 && comboBoxType.getItemCount() > 0)
                    comboBoxType.setSelectedIndex(0);
            } else {
                String v = String.valueOf(blankTypes[comboBoxType.getSelectedIndex()]);
                labelBlankSelectorA.setText(v);
                labelBlankSelectorB.setText(v);
            }
        });
        comboBoxAssign.addActionListener(e -> {
            if (!statusBar.isInHelpMode() && comboBoxAssign.getSelectedIndex() < 0) {
                Object selectedItem = comboBoxAssign.getSelectedItem();
                if (selectedItem instanceof String str) {
                    for (String c : dispStaff)
                        if (c.startsWith(str) || c.contains(str)) {
                            comboBoxAssign.setSelectedItem(c);
                            break;
                        }
                }
            }
        });
        buttonCancel.addActionListener(e -> {
            if (!statusBar.isInHelpMode()) hideDialog();
        });
        buttonOk.addActionListener(e -> {
            if (!statusBar.isInHelpMode()) {
                blankAssigned = null;
                blankAssignedDate = null;
                startIndex = null;
                endIndex = null;
                blankCreateDate = null;
                blankDesc = null;
                try {
                    if (id == null) {
                        if (textFieldBlankSelectorA.getText().length() != textFieldBlankSelectorB.getText().length()) {
                            statusBar.setStatus("Blank selector numbers need to be the same length.", "", 2500);
                            return;
                        }
                        startIndex = Long.parseLong(blankTypes[comboBoxType.getSelectedIndex()] + textFieldBlankSelectorA.getText());
                        endIndex = Long.parseLong(blankTypes[comboBoxType.getSelectedIndex()] + textFieldBlankSelectorB.getText());
                        if (startIndex > endIndex) {
                            statusBar.setStatus("Blank selector range in invalid.", "", 2500);
                            return;
                        }
                        if (comboBoxAssign.getSelectedIndex() > -1) {
                            if (dateFieldDate.getValue() == null) dateFieldDate.setValue(Time.now());
                            blankAssigned = manager.staffAccessor.getAccountID(staff[comboBoxAssign.getSelectedIndex()]);
                            blankAssignedDate = dateFieldDate.getValue();
                        }
                        blankCreateDate = dateFieldCreated.getValue();
                        blankDesc = textAreaDescription.getText();
                        hideDialog();
                    } else {
                        manager.stockAccessor.setBlankDescription(id, textAreaDescription.getText());
                        manager.stockAccessor.setBlankCreationDate(id, dateFieldCreated.getValue());
                        if (comboBoxAssign.getSelectedIndex() > -1) {
                            if (dateFieldDate.getValue() == null) dateFieldDate.setValue(Time.now());
                            manager.stockAccessor.reAssignBlank(id, manager.staffAccessor.getAccountID(staff[comboBoxAssign.getSelectedIndex()]), dateFieldDate.getValue());
                        }
                        if (dateFieldReturn.getValue() != null)
                            manager.stockAccessor.returnBlank(id, dateFieldReturn.getValue());
                        hideDialog();
                    }
                } catch (CheckedException ex) {
                    statusBar.setStatus(ex, 2500);
                }
            }
        });
        buttonBlacklist.addActionListener(e -> {
            if (!statusBar.isInHelpMode() && id != null) {
                try {
                    prompt.setContents("Are you sure you want to blacklist the blank,\nthis cannot be undone.");
                    prompt.showDialog();
                    if (prompt.getLastButton() != null && !prompt.getLastButton().equals("No")) {
                        manager.stockAccessor.blacklistBlank(id);
                        hideDialog();
                    }
                } catch (CheckedException ex) {
                    statusBar.setStatus(ex, 2500);
                }
            }
        });
        buttonVoid.addActionListener(e -> {
            if (!statusBar.isInHelpMode() && id != null) {
                try {
                    prompt.setContents("Are you sure you want to void the blank,\nthis cannot be undone.");
                    prompt.showDialog();
                    if (prompt.getLastButton() != null && !prompt.getLastButton().equals("No")) {
                        manager.stockAccessor.voidBlank(id);
                        hideDialog();
                    }
                } catch (CheckedException ex) {
                    statusBar.setStatus(ex, 2500);
                }
            }
        });
        dateFieldReturn.addActionListener(e -> {
            if (statusBar.isInHelpMode()) return;
            try {
                if (manager.stockAccessor.getBlankReturnedDate(id) != null) return;
            } catch (CheckedException ex) {
                statusBar.setStatus(ex, 2500);
                dateFieldReturn.clearValue();
                return;
            }
            prompt.setContents("Are you sure you want to return the blank,\nthis will be committed on save and cannot be undone.");
            prompt.showDialog();
            if (prompt.getLastButton() == null || prompt.getLastButton().equals("No"))
                dateFieldReturn.clearValue();
        });
        //Finalize form
        pack();
        setMinimumSize(Root.getMinimumSize());
        dsize = getSize();
        statusBar.createPrompt(this);
    }

    /**
     * Sets the blank ID of the blank type to edit or null to create new blanks.
     *
     * @param id The ID of the blank or null.
     * @throws CheckedException The refresh operation has failed.
     */
    public void setBlankID(Long id) throws CheckedException {
        this.id = id;
        if (id != null) manager.stockAccessor.refreshBlank(id);
    }

    /**
     * Gets the start blank.
     *
     * @return The start blank.
     */
    public Long getStartBlank() {
        return startIndex;
    }

    /**
     * Gets the end blank.
     *
     * @return The end blank.
     */
    public Long getEndBlank() {
        return endIndex;
    }

    /**
     * Gets the blank assignment.
     *
     * @return The blank assignment.
     */
    public Long getBlankAssigned() {
        return blankAssigned;
    }

    /**
     * Gets the blank assignment date.
     *
     * @return The blank assignment date.
     */
    public Date getBlankAssignedDate() {
        return blankAssignedDate;
    }

    /**
     * Gets the blank creation date.
     *
     * @return The blank creation date.
     */
    public Date getBlankCreateDate() {
        return blankCreateDate;
    }

    /**
     * Gets the blank description.
     *
     * @return The blank description.
     */
    public String getBlankDescription() {
        return blankDesc;
    }

    private void updateInterfaceState() throws CheckedException {
        StaffRole sRole = manager.staffAccessor.getAccountRole(null);
        boolean editable = true;
        boolean rEditable = sRole == StaffRole.Administrator;
        if (id != null) {
            editable = !manager.salesAccessor.blankSold(id);
            editable = editable && !manager.stockAccessor.isBlankBlacklisted(id);
            editable = editable && !manager.stockAccessor.isBlankVoided(id);
            rEditable = rEditable && editable;
            editable = editable && !manager.stockAccessor.isBlankReturned(id);
        }
        //Readonly blank IDs if not editable
        swapControl(panelBlankSelection, panelBlankSelector, textFieldRBlankSelector, (id == null) ? panelBlankSelector : textFieldRBlankSelector);
        //Readonly blank type if not editable
        swapControl(panelType, comboBoxType, textFieldRType, (id == null) ? comboBoxType : textFieldRType);
        //Readonly creation date if not editable
        swapControl(panelCreated, dateFieldCreated, textFieldRCreated, (id == null || (sRole == StaffRole.Administrator && editable)) ? dateFieldCreated : textFieldRCreated);
        //Readonly assignment if not editable
        swapControl(panelAssign, comboBoxAssign, textFieldRAssign, (id == null || (sRole != StaffRole.Advisor && editable)) ? comboBoxAssign : textFieldRAssign);
        swapControl(panelDate, dateFieldDate, textFieldRDate, (id == null || (sRole != StaffRole.Advisor && editable)) ? dateFieldDate : textFieldRDate);
        textAreaDescription.setEnabled(editable);
        //Readonly return if not editable
        swapControl(panelReturn, dateFieldReturn, textFieldRReturn, (id != null && rEditable) ? dateFieldReturn : textFieldRReturn);
        buttonVoid.setEnabled(id != null && editable);
        buttonBlacklist.setEnabled(id != null && editable);
    }

    @Override
    public void showDialog() {
        if (reusable || !shown) {
            try {
                blankAssigned = null;
                blankAssignedDate = null;
                startIndex = null;
                endIndex = null;
                blankCreateDate = null;
                blankDesc = null;
                setTitle((id == null) ? "Blank Creator" : "Blank Modifier");
                StaffRole sRole = manager.staffAccessor.getAccountRole(null);
                //Setup blank types combo box
                comboBoxType.removeAllItems();
                dispBlankTypes.clear();
                blankTypes = manager.stockAccessor.listBlankTypes();
                if (blankTypes.length < 1) return; //Cannot do anything if no blank types
                for (int c : blankTypes) {
                    String disp = manager.stockAccessor.getBlankTypeDescription(c);
                    if (disp.length() > 16) disp = disp.substring(0, 16);
                    disp = c + " - " + disp;
                    comboBoxType.addItem(disp);
                    dispBlankTypes.add(disp);
                }
                //Setup staff combo box
                comboBoxAssign.removeAllItems();
                dispStaff.clear();
                staff = manager.staffAccessor.listAccounts(StaffRole.Advisor);
                if (staff.length < 1) return; //Cannot do anything if no staff
                for (String c : staff) {
                    String disp = manager.staffAccessor.getAccountID(c) + " - " + c;
                    comboBoxAssign.addItem(disp);
                    dispStaff.add(disp);
                }
                if (id == null) {
                    textFieldBlankSelectorA.setText("000000");
                    textFieldBlankSelectorB.setText("000001");
                    comboBoxType.setSelectedIndex(0);
                    comboBoxAssign.setSelectedIndex(-1);
                    dateFieldCreated.setValue(Time.now());
                    dateFieldDate.clearValue();
                    textAreaDescription.setText("");
                    textFieldRReturn.setText("");
                    dateFieldReturn.clearValue();
                } else {
                    textFieldRBlankSelector.setText(String.valueOf(id));
                    int bType = manager.stockAccessor.getBlankType(id);
                    comboBoxType.setSelectedIndex(Arrays.binarySearch(blankTypes, bType));
                    textFieldRType.setText(String.valueOf(bType));
                    dateFieldCreated.setValue(manager.stockAccessor.getBlankCreationDate(id));
                    Long bAssignID = manager.stockAccessor.getBlankAssignmentID(id);
                    if (bAssignID == null) {
                        comboBoxAssign.setSelectedIndex(-1);
                        textFieldRAssign.setText("");
                        dateFieldDate.clearValue();
                        textFieldRDate.setText("");
                    } else {
                        String bAssign = manager.staffAccessor.getAccountEmail(bAssignID);
                        comboBoxAssign.setSelectedIndex(Arrays.asList(staff).indexOf(bAssign));
                        textFieldRAssign.setText(bAssignID + "- " + bAssign);
                        Date bAssignDate = manager.stockAccessor.getBlankAssignmentDate(id);
                        dateFieldDate.setValue(bAssignDate);
                        textFieldRDate.setText(bAssignDate.toString());
                    }
                    textAreaDescription.setText(manager.stockAccessor.getBlankDescription(id));
                    dateFieldReturn.clearValue();
                    Date bReturn = manager.stockAccessor.getBlankReturnedDate(id);
                    if (bReturn != null) dateFieldReturn.setValue(bReturn);
                }
                updateInterfaceState();
            } catch (CheckedException e) {
                statusBar.setStatus(e, 2500);
            }
        }
        super.showDialog();
    }

    @Override
    public void hideDialog() {
        statusBar.deactivateHelp();
        super.hideDialog();
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        Root = new JPanel();
        Root.setLayout(new GridBagLayout());
        Root.setMinimumSize(new Dimension(400, 400));
        Root.setPreferredSize(new Dimension(514, 400));
        final JLabel label1 = new JLabel();
        label1.setMaximumSize(new Dimension(98, 16));
        label1.setMinimumSize(new Dimension(98, 16));
        label1.setPreferredSize(new Dimension(98, 16));
        label1.setText("Blank Number(s):");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        gbc.weighty = 0.15;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 2, 0, 0);
        Root.add(label1, gbc);
        panelBlankSelection = new JPanel();
        panelBlankSelection.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        gbc.weighty = 0.15;
        gbc.fill = GridBagConstraints.BOTH;
        Root.add(panelBlankSelection, gbc);
        panelBlankSelector = new JPanel();
        panelBlankSelector.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panelBlankSelection.add(panelBlankSelector, gbc);
        textFieldBlankSelectorA = new VTextField();
        textFieldBlankSelectorA.setMaximumSize(new Dimension(35, 30));
        textFieldBlankSelectorA.setMinimumSize(new Dimension(35, 30));
        textFieldBlankSelectorA.setPreferredSize(new Dimension(35, 30));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.35;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelBlankSelector.add(textFieldBlankSelectorA, gbc);
        final JLabel label2 = new JLabel();
        label2.setHorizontalAlignment(0);
        label2.setHorizontalTextPosition(0);
        label2.setMaximumSize(new Dimension(10, 16));
        label2.setMinimumSize(new Dimension(10, 16));
        label2.setPreferredSize(new Dimension(10, 16));
        label2.setText("-");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panelBlankSelector.add(label2, gbc);
        textFieldBlankSelectorB = new VTextField();
        textFieldBlankSelectorB.setMaximumSize(new Dimension(35, 30));
        textFieldBlankSelectorB.setMinimumSize(new Dimension(35, 30));
        textFieldBlankSelectorB.setPreferredSize(new Dimension(35, 30));
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.weightx = 0.35;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelBlankSelector.add(textFieldBlankSelectorB, gbc);
        labelBlankSelectorB = new JLabel();
        labelBlankSelectorB.setHorizontalAlignment(11);
        labelBlankSelectorB.setMaximumSize(new Dimension(10, 16));
        labelBlankSelectorB.setMinimumSize(new Dimension(10, 16));
        labelBlankSelectorB.setPreferredSize(new Dimension(10, 16));
        labelBlankSelectorB.setText("");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panelBlankSelector.add(labelBlankSelectorB, gbc);
        labelBlankSelectorA = new JLabel();
        labelBlankSelectorA.setHorizontalAlignment(11);
        labelBlankSelectorA.setMaximumSize(new Dimension(10, 16));
        labelBlankSelectorA.setMinimumSize(new Dimension(10, 16));
        labelBlankSelectorA.setPreferredSize(new Dimension(10, 16));
        labelBlankSelectorA.setText("");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panelBlankSelector.add(labelBlankSelectorA, gbc);
        final JLabel label3 = new JLabel();
        label3.setMaximumSize(new Dimension(98, 16));
        label3.setMinimumSize(new Dimension(98, 16));
        label3.setPreferredSize(new Dimension(98, 16));
        label3.setText("Blank Type:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.5;
        gbc.weighty = 0.15;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 2, 0, 0);
        Root.add(label3, gbc);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 0.15;
        gbc.fill = GridBagConstraints.BOTH;
        Root.add(panel1, gbc);
        panel1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-4473925)), "Assignment:", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label4 = new JLabel();
        label4.setMaximumSize(new Dimension(60, 16));
        label4.setMinimumSize(new Dimension(60, 16));
        label4.setPreferredSize(new Dimension(60, 16));
        label4.setText("Date:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.5;
        gbc.weighty = 0.5;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 2, 0, 0);
        panel1.add(label4, gbc);
        panelAssign = new JPanel();
        panelAssign.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(panelAssign, gbc);
        comboBoxAssign = new JComboBox();
        comboBoxAssign.setEditable(true);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelAssign.add(comboBoxAssign, gbc);
        panelDate = new JPanel();
        panelDate.setLayout(new GridBagLayout());
        panelDate.setMinimumSize(new Dimension(200, 30));
        panelDate.setPreferredSize(new Dimension(200, 30));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.5;
        gbc.weighty = 0.5;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(panelDate, gbc);
        dateFieldDate = new DateField();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        gbc.weighty = 0.5;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelDate.add(dateFieldDate.$$$getRootComponent$$$(), gbc);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 0.25;
        gbc.fill = GridBagConstraints.BOTH;
        Root.add(panel2, gbc);
        panel2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-4473925)), "Description:", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JScrollPane scrollPane1 = new JScrollPane();
        scrollPane1.setHorizontalScrollBarPolicy(31);
        scrollPane1.setVerticalScrollBarPolicy(22);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 2, 0, 2);
        panel2.add(scrollPane1, gbc);
        textAreaDescription = new JTextArea();
        textAreaDescription.setLineWrap(true);
        textAreaDescription.setWrapStyleWord(true);
        scrollPane1.setViewportView(textAreaDescription);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 0.15;
        gbc.fill = GridBagConstraints.BOTH;
        Root.add(panel3, gbc);
        panel3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-4473925)), "Return", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label5 = new JLabel();
        label5.setMaximumSize(new Dimension(60, 16));
        label5.setMinimumSize(new Dimension(60, 16));
        label5.setPreferredSize(new Dimension(60, 16));
        label5.setText("Date:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 2, 0, 0);
        panel3.add(label5, gbc);
        panelReturn = new JPanel();
        panelReturn.setLayout(new GridBagLayout());
        panelReturn.setMinimumSize(new Dimension(200, 30));
        panelReturn.setPreferredSize(new Dimension(200, 30));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel3.add(panelReturn, gbc);
        dateFieldReturn = new DateField();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelReturn.add(dateFieldReturn.$$$getRootComponent$$$(), gbc);
        panelType = new JPanel();
        panelType.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.5;
        gbc.weighty = 0.15;
        gbc.fill = GridBagConstraints.BOTH;
        Root.add(panelType, gbc);
        comboBoxType = new JComboBox();
        comboBoxType.setEditable(true);
        comboBoxType.setMinimumSize(new Dimension(100, 30));
        comboBoxType.setPreferredSize(new Dimension(100, 30));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        gbc.weighty = 0.15;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelType.add(comboBoxType, gbc);
        final JLabel label6 = new JLabel();
        label6.setMaximumSize(new Dimension(98, 16));
        label6.setMinimumSize(new Dimension(98, 16));
        label6.setPreferredSize(new Dimension(98, 16));
        label6.setText("Creation Date:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.5;
        gbc.weighty = 0.15;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 2, 0, 0);
        Root.add(label6, gbc);
        panelCreated = new JPanel();
        panelCreated.setLayout(new GridBagLayout());
        panelCreated.setMinimumSize(new Dimension(200, 30));
        panelCreated.setPreferredSize(new Dimension(200, 30));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 0.5;
        gbc.weighty = 0.15;
        gbc.fill = GridBagConstraints.BOTH;
        Root.add(panelCreated, gbc);
        dateFieldCreated = new DateField();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelCreated.add(dateFieldCreated.$$$getRootComponent$$$(), gbc);
        buttonBlacklist = new JButton();
        buttonBlacklist.setAlignmentX(0.5f);
        buttonBlacklist.setMaximumSize(new Dimension(98, 30));
        buttonBlacklist.setMinimumSize(new Dimension(98, 30));
        buttonBlacklist.setPreferredSize(new Dimension(98, 30));
        buttonBlacklist.setText("Blacklist");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.weightx = 0.5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        Root.add(buttonBlacklist, gbc);
        buttonVoid = new JButton();
        buttonVoid.setAlignmentX(0.5f);
        buttonVoid.setMaximumSize(new Dimension(98, 30));
        buttonVoid.setMinimumSize(new Dimension(98, 30));
        buttonVoid.setPreferredSize(new Dimension(98, 30));
        buttonVoid.setText("Void");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.weightx = 0.5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        Root.add(buttonVoid, gbc);
        buttonCancel = new JButton();
        buttonCancel.setAlignmentX(0.5f);
        buttonCancel.setMaximumSize(new Dimension(98, 30));
        buttonCancel.setMinimumSize(new Dimension(98, 30));
        buttonCancel.setPreferredSize(new Dimension(98, 30));
        buttonCancel.setText("Cancel");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.weightx = 0.5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        Root.add(buttonCancel, gbc);
        buttonOk = new JButton();
        buttonOk.setAlignmentX(0.5f);
        buttonOk.setMaximumSize(new Dimension(98, 30));
        buttonOk.setMinimumSize(new Dimension(98, 30));
        buttonOk.setPreferredSize(new Dimension(98, 30));
        buttonOk.setText("Ok");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.weightx = 0.5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        Root.add(buttonOk, gbc);
        statusBar = new StatusBar();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        Root.add(statusBar.$$$getRootComponent$$$(), gbc);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return Root;
    }
}
