package skywaysolutions.app.gui;

import skywaysolutions.app.gui.control.DateField;
import skywaysolutions.app.gui.control.StatusBar;
import skywaysolutions.app.staff.StaffRole;
import skywaysolutions.app.utils.AccessorManager;
import skywaysolutions.app.utils.CheckedException;
import skywaysolutions.app.utils.Time;

import javax.swing.*;
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
    private skywaysolutions.app.gui.control.VTextField textFieldBlankSelectorA;
    private skywaysolutions.app.gui.control.VTextField textFieldBlankSelectorB;
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
     * @param owner The window owner or null.
     * @param reusable If this dialog is reusable.
     * @param manager The accessor manager instance.
     */
    public BlankEditor(Window owner, boolean reusable, AccessorManager manager) {
        super(owner, "", reusable);
        this.manager = manager;
        prompt = new Prompt(owner, "Are you sure?", "", new String[] {"No", "Yes"}, 0, true);
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
        getRootPane().setDefaultButton(buttonOk);
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
        } catch (CheckedException e) {
            statusBar.setStatus(e, 2500);
        }
        comboBoxType.addActionListener(e -> {
            if (statusBar.isInHelpMode()) return;
            if (comboBoxType.getSelectedIndex() < 0) {
                Object selectedItem = comboBoxType.getSelectedItem();
                if (selectedItem instanceof String str) {
                    for (String c : dispBlankTypes) if (c.startsWith(str) || c.contains(str)) {
                        comboBoxType.setSelectedItem(c);
                        break;
                    }
                }
                if (comboBoxType.getSelectedIndex() < 0 && comboBoxType.getItemCount() > 0) comboBoxType.setSelectedIndex(0);
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
                    for (String c : dispStaff) if (c.startsWith(str) || c.contains(str)) {
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
                        if (dateFieldReturn.getValue() != null) manager.stockAccessor.returnBlank(id, dateFieldReturn.getValue());
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
                    comboBoxType.setSelectedIndex(Arrays.binarySearch(blankTypes , bType));
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
}
