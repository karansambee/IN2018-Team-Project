package skywaysolutions.app.gui;

import skywaysolutions.app.gui.control.PersonalInformationEditor;
import skywaysolutions.app.gui.control.StatusBar;
import skywaysolutions.app.staff.StaffRole;
import skywaysolutions.app.utils.AccessorManager;
import skywaysolutions.app.utils.CheckedException;
import skywaysolutions.app.utils.Decimal;
import skywaysolutions.app.utils.PersonalInformation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.Collections;

/**
 * This class provides an AccountEditor that can create and edit staff accounts.
 *
 * @author Alfred Manville
 */
public class AccountEditor extends JDialogx {
    private JPanel Root;
    private StatusBar statusBar;
    private JButton buttonCancel;
    private JButton buttonOk;
    private JSpinner spinnerStaffID;
    private skywaysolutions.app.gui.control.VTextField textFieldCommissionRate;
    private JComboBox comboBoxCurrency;
    private JComboBox comboBoxRole;
    private PersonalInformationEditor personalInformationEditor;
    private JPasswordField passwordFieldPassword;
    private JPasswordField passwordFieldCPassword;
    private JLabel labelCommissionRate;
    private JLabel labelPassword;
    private JLabel labelCPassword;
    private JPanel panelCurrency;
    private JPanel panelRole;
    private JPanel panelStaffID;
    private final JTextField textFieldRCurrency;
    private final JTextField textFieldRRole;
    private final JTextField textFieldRStaffID;
    private final AccessorManager manager;
    private String accountName;
    private String[] currencies;

    /**
     * Constructs a new instance of AccountEditor with the specified owner, if reusable and the accessor manager instance.
     *
     * @param owner The window owner or null.
     * @param reusable If this dialog is reusable.
     * @param manager The accessor manager instance.
     */
    public AccountEditor(Window owner, boolean reusable, AccessorManager manager) {
        super(owner, "", reusable);
        this.manager = manager;
        //Setup readonly textfields (Used as readonly controls that swap with controls that have to be disabled to be readonly)
        textFieldRRole = new JTextField();
        textFieldRRole.setEditable(false);
        textFieldRCurrency = new JTextField();
        textFieldRCurrency.setEditable(false);
        textFieldRStaffID = new JTextField();
        textFieldRStaffID.setEditable(false);
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
        spinnerStaffID.setModel(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
        comboBoxRole.addActionListener(e -> {
            try {
                updateInterfaceState();
            } catch (CheckedException ex) {
                statusBar.setStatus(ex, 2500);
            }
        });
        try {
            textFieldCommissionRate.setup("([0-9]*|0)(\\.([0-9]+))?", statusBar, "Conversion Rate not Positive Numeric!", false, true);
            personalInformationEditor.setup(statusBar, false);
            currencies = manager.rateAccessor.getConvertableCurrencies();
            for (String c : currencies) comboBoxCurrency.addItem(c);
        } catch (CheckedException e) {
            statusBar.setStatus(e, 2500);
        }
        comboBoxCurrency.addActionListener(e -> {
            if (!statusBar.isInHelpMode() && comboBoxCurrency.getSelectedIndex() < 0) {
                Object selectedItem = comboBoxCurrency.getSelectedItem();
                if (selectedItem instanceof String str) {
                    for (String c : currencies) if (c.startsWith(str) || str.startsWith(c)) {
                        comboBoxCurrency.setSelectedItem(c);
                        break;
                    }
                }
                if (comboBoxCurrency.getSelectedIndex() < 0 && comboBoxCurrency.getItemCount() > 0) comboBoxCurrency.setSelectedItem("USD");
            }
        });
        buttonCancel.addActionListener(e -> {
            if (!statusBar.isInHelpMode()) hideDialog();
        });
        buttonOk.addActionListener(e -> {
            if (!statusBar.isInHelpMode()) {
                //Check email address state
                if (personalInformationEditor.getInformation().getEmailAddress() == null || personalInformationEditor.getInformation().getEmailAddress().equals("")) {
                    statusBar.setStatus("Email address is empty!", "", 2500);
                    return;
                }
                //Check password state, allow blank password for no change when editing an account
                if (accountName == null || passwordFieldPassword.getPassword().length > 0 || passwordFieldCPassword.getPassword().length > 0) {
                    boolean exitOut = false;
                    if (passwordFieldPassword.getPassword().length == 0 || passwordFieldCPassword.getPassword().length == 0) {
                        statusBar.setStatus("Passwords should not be empty!", "", 2500);
                        exitOut = true;
                    } else if (passwordFieldPassword.getPassword().length != passwordFieldCPassword.getPassword().length) {
                        statusBar.setStatus("Passwords are not the same length!", "", 2500);
                        exitOut = true;
                    } else {
                        for (int i = 0; i < passwordFieldPassword.getPassword().length; i++)
                            if (passwordFieldPassword.getPassword()[i] != passwordFieldCPassword.getPassword()[i]) {
                                statusBar.setStatus("Passwords do not match!", "", 2500);
                                exitOut = true;
                                break;
                            }
                    }
                    if (exitOut) return;
                }
                //Create or Update account information
                try {
                    if (accountName == null) {
                        manager.staffAccessor.createAccount(personalInformationEditor.getInformation(), StaffRole.getStaffRoleFromValue(comboBoxRole.getSelectedIndex()),
                                (StaffRole.getStaffRoleFromValue(comboBoxRole.getSelectedIndex()) == StaffRole.Advisor) ? new Decimal(Double.parseDouble(textFieldCommissionRate.getText()), 6) : null,
                                comboBoxCurrency.getItemAt(comboBoxCurrency.getSelectedIndex()).toString(), String.valueOf(passwordFieldCPassword.getPassword()), ((Integer) spinnerStaffID.getValue() > 0) ? ((Number) spinnerStaffID.getValue()).longValue() : null);
                    } else {
                        String emailAddr = personalInformationEditor.getInformation().getEmailAddress();
                        StaffRole sRole = manager.staffAccessor.getAccountRole(null);
                        if (sRole == StaffRole.Administrator || sRole == StaffRole.Manager) {
                            manager.staffAccessor.setCommission(emailAddr, (StaffRole.getStaffRoleFromValue(comboBoxRole.getSelectedIndex()) == StaffRole.Advisor) ? new Decimal(Double.parseDouble(textFieldCommissionRate.getText()), 6) : null);
                            if (sRole == StaffRole.Administrator) {
                                manager.staffAccessor.setAccountRole(emailAddr, StaffRole.getStaffRoleFromValue(comboBoxRole.getSelectedIndex()));
                                manager.staffAccessor.setCurrency(emailAddr, comboBoxCurrency.getItemAt(comboBoxCurrency.getSelectedIndex()).toString());
                                if (passwordFieldCPassword.getPassword().length > 0) manager.staffAccessor.changePassword(emailAddr, String.valueOf(passwordFieldCPassword.getPassword()));
                            }
                        }
                        manager.staffAccessor.setPersonalInformation(emailAddr, personalInformationEditor.getInformation());
                    }
                    hideDialog();
                } catch (CheckedException ex) {
                    statusBar.setStatus(ex, 2500);
                }
            }
        });
        //Finalize form
        pack();
        setMinimumSize(Root.getMinimumSize());
        dsize = getSize();
        statusBar.createPrompt(this);
    }

    private void updateInterfaceState() throws CheckedException {
        //Readonly staff ID if not editable
        swapControl(panelStaffID, spinnerStaffID, textFieldRStaffID, (accountName == null) ? spinnerStaffID : textFieldRStaffID);
        //Readonly role selection if not editable
        swapControl(panelRole, comboBoxRole, textFieldRRole, (accountName == null ||
                (manager.staffAccessor.getAccountRole(null) == StaffRole.Administrator &&
                        !manager.staffAccessor.getLoggedInAccountEmail().equals(accountName))) ? comboBoxRole : textFieldRRole);

        personalInformationEditor.setEmailEditable(accountName == null);

        boolean canChangePassword = accountName == null || accountName.equals(manager.staffAccessor.getLoggedInAccountEmail()) ||
                manager.staffAccessor.getAccountRole(null) == StaffRole.Administrator;

        labelPassword.setEnabled(canChangePassword);
        passwordFieldPassword.setEnabled(canChangePassword);
        labelCPassword.setEnabled(canChangePassword);
        passwordFieldCPassword.setEnabled(canChangePassword);
        //Readonly currency selection if not editable
        swapControl(panelCurrency, comboBoxCurrency, textFieldRCurrency, (manager.staffAccessor.getAccountRole(null) == StaffRole.Administrator) ?
                comboBoxCurrency : textFieldRCurrency);

        switch (StaffRole.getStaffRoleFromValue(comboBoxRole.getSelectedIndex())) {
            case Advisor -> {
                boolean canChangeCommissionRate = manager.staffAccessor.getAccountRole(null) == StaffRole.Manager ||
                        manager.staffAccessor.getAccountRole(null) == StaffRole.Administrator;

                labelCommissionRate.setEnabled(canChangeCommissionRate);
                textFieldCommissionRate.setEnabled(canChangeCommissionRate);
            }
            case Manager, Administrator -> {
                labelCommissionRate.setEnabled(false);
                textFieldCommissionRate.setEnabled(false);
            }
        }
    }

    /**
     * Sets the account currently being edited, null to create an account.
     *
     * @param name The account name or null.
     */
    public void setAccountName(String name) {
        accountName = name;
    }

    public void showDialog() {
        if (reusable || !shown) {
            try {
                setTitle((accountName == null) ? "Account Creator" : "Account Editor");
                comboBoxCurrency.removeAllItems();
                currencies = manager.rateAccessor.getConvertableCurrencies();
                for (String c : currencies) comboBoxCurrency.addItem(c);
                passwordFieldPassword.setText("");
                passwordFieldCPassword.setText("");
                if (accountName == null) {
                    spinnerStaffID.setValue(0);
                    comboBoxRole.setSelectedIndex(0);
                    textFieldCommissionRate.setText("0.0");
                    comboBoxCurrency.setSelectedItem("USD");
                    personalInformationEditor.setInformation(new PersonalInformation());
                } else {
                    long accID = manager.staffAccessor.getAccountID(accountName);
                    spinnerStaffID.setValue(accID);
                    textFieldRStaffID.setText(String.valueOf(accID));
                    StaffRole accRl = manager.staffAccessor.getAccountRole(accountName);
                    comboBoxRole.setSelectedIndex(accRl.getValue());
                    textFieldRRole.setText(accRl.toString());
                    Decimal commissionRate = manager.staffAccessor.getCommission(accountName);
                    textFieldCommissionRate.setText((commissionRate == null) ? "0.0" : commissionRate.toString());
                    String accCur = manager.staffAccessor.getCurrency(accountName);
                    comboBoxCurrency.setSelectedItem(accCur);
                    textFieldRCurrency.setText(accCur);
                    personalInformationEditor.setInformation(manager.staffAccessor.getPersonalInformation(accountName));
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
