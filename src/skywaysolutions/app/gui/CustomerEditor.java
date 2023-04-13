package skywaysolutions.app.gui;

import skywaysolutions.app.customers.CustomerType;
import skywaysolutions.app.customers.PlanType;
import skywaysolutions.app.gui.control.PersonalInformationEditor;
import skywaysolutions.app.gui.control.StatusBar;
import skywaysolutions.app.staff.StaffRole;
import skywaysolutions.app.utils.AccessorManager;
import skywaysolutions.app.utils.CheckedException;
import skywaysolutions.app.utils.Decimal;
import skywaysolutions.app.utils.PersonalInformation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;

public class CustomerEditor extends JDialogx {
    private JPanel Root;
    private JTextField textFieldAlias;
    private JComboBox comboBoxType;
    private JComboBox comboBoxPlan;
    private JComboBox comboBoxCurrency;
    private PersonalInformationEditor personalInformationEditor;
    private JButton buttonCancel;
    private JButton buttonOk;
    private StatusBar statusBar;
    private final AccessorManager manager;
    private Long customerID;
    private String[] currencies;
    private long[] discounts;
    private String[] dispDiscounts;

    public CustomerEditor(Window owner, boolean reusable, AccessorManager manager) {
        super(owner, "", reusable);
        this.manager = manager;
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
        try {
            personalInformationEditor.setup(statusBar, true);
        } catch (CheckedException e) {
            statusBar.setStatus(e, 2500);
        }
        comboBoxCurrency.addActionListener(e -> {
            if (!statusBar.isInHelpMode() && comboBoxCurrency.getSelectedIndex() < 0) {
                Object selectedItem = comboBoxCurrency.getSelectedItem();
                if (selectedItem instanceof String str) {
                    for (String c : currencies)
                        if (c.startsWith(str) || str.startsWith(c)) {
                            comboBoxCurrency.setSelectedItem(c);
                            break;
                        }
                }
                if (comboBoxCurrency.getSelectedIndex() < 0 && comboBoxCurrency.getItemCount() > 0)
                    comboBoxCurrency.setSelectedItem("USD");
            }
        });
        comboBoxPlan.addActionListener(e -> {
            if (!statusBar.isInHelpMode() && comboBoxPlan.getSelectedIndex() < 0) {
                Object selectedItem = comboBoxPlan.getSelectedItem();
                if (selectedItem instanceof String str) {
                    for (String c : currencies)
                        if (c.startsWith(str) || str.startsWith(c)) {
                            comboBoxCurrency.setSelectedItem(c);
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
                try {
                    Long planID = (comboBoxPlan.getSelectedIndex() < 0) ? null : discounts[comboBoxPlan.getSelectedIndex()];
                    if (customerID == null) {
                        manager.customerAccessor.createAccount(personalInformationEditor.getInformation(), planID, false,
                                currencies[comboBoxCurrency.getSelectedIndex()], textFieldAlias.getText(), CustomerType.getCustomerTypeFromValue(comboBoxType.getSelectedIndex()));
                    } else {
                        manager.customerAccessor.setCustomerType(customerID, CustomerType.getCustomerTypeFromValue(comboBoxType.getSelectedIndex()));
                        manager.customerAccessor.setCurrency(customerID, currencies[comboBoxCurrency.getSelectedIndex()]);
                        manager.customerAccessor.setAccountPlan(customerID, planID);
                        manager.customerAccessor.setPersonalInformation(customerID, personalInformationEditor.getInformation());
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

    public void setCustomerID(Long id) throws CheckedException {
        this.customerID = id;
        if (id != null) manager.customerAccessor.refreshCustomer(id);
    }

    public void showDialog() {
        if (reusable || !shown) {
            try {
                setTitle((customerID == null) ? "Customer Creator" : "Customer Editor");
                comboBoxCurrency.removeAllItems();
                currencies = manager.rateAccessor.getConvertableCurrencies();
                for (String c : currencies) comboBoxCurrency.addItem(c);
                comboBoxPlan.removeAllItems();
                discounts = manager.customerAccessor.listPlans(PlanType.Any);
                dispDiscounts = new String[discounts.length];
                for (int i = 0; i < dispDiscounts.length; i++) {
                    PlanType dpt = manager.customerAccessor.getPlanType(discounts[i]);
                    dispDiscounts[i] = discounts[i] + " - ";
                    if (dpt == PlanType.FixedDiscount) {
                        dispDiscounts[i] += dpt + " - " + manager.customerAccessor.getPlanPercentage(discounts[i]).toString();
                    } else if (dpt == PlanType.FlexibleDiscount) {
                        dispDiscounts[i] += dpt + " - " + manager.customerAccessor.getFlexiblePlanRanges(discounts[i]).length;
                    }
                    comboBoxPlan.addItem(dispDiscounts[i]);
                }
                if (customerID == null) {
                    textFieldAlias.setText("");
                    comboBoxType.setSelectedItem(0);
                    comboBoxPlan.setSelectedIndex(-1);
                    comboBoxCurrency.setSelectedItem("USD");
                    personalInformationEditor.setInformation(new PersonalInformation());
                } else {
                    textFieldAlias.setText(manager.customerAccessor.getAccountAlias(customerID));
                    comboBoxType.setSelectedIndex(manager.customerAccessor.getCustomerType(customerID).getValue());
                    Long cPlanT = manager.customerAccessor.getAccountPlan(customerID);
                    if (cPlanT == null) comboBoxPlan.setSelectedItem(-1);
                    else
                        comboBoxPlan.setSelectedIndex(Arrays.binarySearch(discounts, cPlanT));
                    String accCur = manager.customerAccessor.getCurrency(customerID);
                    comboBoxCurrency.setSelectedItem(accCur);
                    personalInformationEditor.setInformation(manager.customerAccessor.getPersonalInformation(customerID));
                }
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
