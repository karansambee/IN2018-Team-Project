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
                } else if (comboBoxCurrency.getItemCount() > 0) comboBoxCurrency.setSelectedItem("USD");
            }
        });
        buttonCancel.addActionListener(e -> {
            if (!statusBar.isInHelpMode()) hideDialog();
        });
        buttonOk.addActionListener(e -> {
            if (accountName == null || passwordFieldPassword.getPassword().length > 0 || passwordFieldCPassword.getPassword().length > 0) {
                boolean exitOut = false;
                if (passwordFieldPassword.getPassword().length == 0 || passwordFieldCPassword.getPassword().length == 0) {
                    statusBar.setStatus("Password should not be empty!", "", 2500);
                    exitOut = true;
                } else if (passwordFieldPassword.getPassword().length != passwordFieldCPassword.getPassword().length) {
                    statusBar.setStatus("Passwords are not the same length!", "", 2500);
                    exitOut = true;
                } else {
                    for (int i = 0; i < passwordFieldPassword.getPassword().length; i++) if (passwordFieldPassword.getPassword()[i] != passwordFieldCPassword.getPassword()[i]) {
                        statusBar.setStatus("Passwords do not match!", "", 2500);
                        exitOut = true;
                        break;
                    }
                }
                if (exitOut) return;
            }
            try {
                if (accountName == null) {
                    manager.staffAccessor.createAccount(personalInformationEditor.getInformation(), StaffRole.getStaffRoleFromValue(comboBoxRole.getSelectedIndex()),
                            (StaffRole.getStaffRoleFromValue(comboBoxRole.getSelectedIndex()) == StaffRole.Advisor) ? new Decimal(Double.parseDouble(textFieldCommissionRate.getText()), 6) : null,
                            comboBoxCurrency.getItemAt(comboBoxCurrency.getSelectedIndex()).toString(), String.valueOf(passwordFieldCPassword.getPassword()), ((long) spinnerStaffID.getValue() > 0) ? (long) spinnerStaffID.getValue() : null);
                } else {
                    String emailAddr = personalInformationEditor.getInformation().getEmailAddress();
                    StaffRole sRole = manager.staffAccessor.getAccountRole(null);
                    if (sRole == StaffRole.Administrator || sRole == StaffRole.Manager) {
                        manager.staffAccessor.setCommission(emailAddr, (StaffRole.getStaffRoleFromValue(comboBoxRole.getSelectedIndex()) == StaffRole.Advisor) ? new Decimal(Double.parseDouble(textFieldCommissionRate.getText()), 6) : null);
                        if (sRole == StaffRole.Administrator) {
                            manager.staffAccessor.setAccountRole(emailAddr, StaffRole.getStaffRoleFromValue(comboBoxRole.getSelectedIndex()));
                            manager.staffAccessor.setCurrency(emailAddr, comboBoxCurrency.getItemAt(comboBoxCurrency.getSelectedIndex()).toString());
                        }
                    }
                    manager.staffAccessor.setPersonalInformation(emailAddr, personalInformationEditor.getInformation());
                }
            } catch (CheckedException ex) {
                statusBar.setStatus(ex, 2500);
            }
        });
    }

    private void updateInterfaceState() throws CheckedException {
        spinnerStaffID.setEnabled(accountName == null);
        comboBoxRole.setEnabled(accountName == null);
        personalInformationEditor.setEmailEditable(accountName == null);
        boolean canChangePassword = accountName == null || accountName.equals(manager.staffAccessor.getLoggedInAccountEmail()) ||
                manager.staffAccessor.getAccountRole(null) == StaffRole.Administrator;
        labelPassword.setEnabled(canChangePassword);
        passwordFieldPassword.setEnabled(canChangePassword);
        labelCPassword.setEnabled(canChangePassword);
        passwordFieldCPassword.setEnabled(canChangePassword);
        comboBoxCurrency.setEnabled(manager.staffAccessor.getAccountRole(null) == StaffRole.Administrator);
        switch (StaffRole.getStaffRoleFromValue(comboBoxRole.getSelectedIndex())) {
            case Advisor -> {
                boolean canChangeCommissionRate = manager.staffAccessor.getAccountRole(accountName) == StaffRole.Manager ||
                        manager.staffAccessor.getAccountRole(accountName) == StaffRole.Administrator;
                labelCommissionRate.setEnabled(canChangeCommissionRate);
                textFieldCommissionRate.setEditable(canChangeCommissionRate);
            }
            case Manager, Administrator -> {
                labelCommissionRate.setEnabled(false);
                textFieldCommissionRate.setEditable(false);
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
                comboBoxCurrency.removeAllItems();
                currencies = manager.rateAccessor.getConvertableCurrencies();
                for (String c : currencies) comboBoxCurrency.addItem(c);
                if (accountName == null) {
                    spinnerStaffID.setValue(0);
                    comboBoxRole.setSelectedIndex(0);
                    textFieldCommissionRate.setText("0.0");
                    comboBoxCurrency.setSelectedItem("USD");
                    personalInformationEditor.setInformation(new PersonalInformation());
                } else {
                    spinnerStaffID.setValue(manager.staffAccessor.getAccountID(accountName));
                    comboBoxRole.setSelectedIndex(manager.staffAccessor.getAccountRole(accountName).getValue());
                    textFieldCommissionRate.setText(manager.staffAccessor.getCommission(accountName).toString());
                    comboBoxCurrency.setSelectedItem(manager.staffAccessor.getCurrency(accountName));
                    personalInformationEditor.setInformation(manager.staffAccessor.getPersonalInformation(accountName));
                }
                updateInterfaceState();
            } catch (CheckedException e) {
                statusBar.setStatus(e, 2500);
            }
        }
        super.showDialog();
    }
}
