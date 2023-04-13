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
        Root.setMinimumSize(new Dimension(300, 386));
        final JLabel label1 = new JLabel();
        label1.setPreferredSize(new Dimension(86, 16));
        label1.setText("Alias:");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        gbc.weighty = 0.1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 2, 0, 0);
        Root.add(label1, gbc);
        textFieldAlias = new JTextField();
        textFieldAlias.setPreferredSize(new Dimension(86, 30));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        gbc.weighty = 0.1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        Root.add(textFieldAlias, gbc);
        final JLabel label2 = new JLabel();
        label2.setPreferredSize(new Dimension(86, 16));
        label2.setText("Type:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.5;
        gbc.weighty = 0.1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 2, 0, 0);
        Root.add(label2, gbc);
        final JLabel label3 = new JLabel();
        label3.setPreferredSize(new Dimension(86, 16));
        label3.setText("Discount Plan:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.5;
        gbc.weighty = 0.1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 2, 0, 0);
        Root.add(label3, gbc);
        final JLabel label4 = new JLabel();
        label4.setPreferredSize(new Dimension(86, 16));
        label4.setText("Local Currency:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.5;
        gbc.weighty = 0.1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 2, 0, 0);
        Root.add(label4, gbc);
        comboBoxType = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("Casual");
        defaultComboBoxModel1.addElement("Regular");
        defaultComboBoxModel1.addElement("Valued");
        comboBoxType.setModel(defaultComboBoxModel1);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.5;
        gbc.weighty = 0.1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        Root.add(comboBoxType, gbc);
        comboBoxPlan = new JComboBox();
        comboBoxPlan.setEditable(true);
        comboBoxPlan.setPreferredSize(new Dimension(86, 30));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 0.5;
        gbc.weighty = 0.1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        Root.add(comboBoxPlan, gbc);
        comboBoxCurrency = new JComboBox();
        comboBoxCurrency.setEditable(true);
        comboBoxCurrency.setPreferredSize(new Dimension(86, 30));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 0.5;
        gbc.weighty = 0.1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        Root.add(comboBoxCurrency, gbc);
        personalInformationEditor = new PersonalInformationEditor();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 0.6;
        gbc.fill = GridBagConstraints.BOTH;
        Root.add(personalInformationEditor.$$$getRootComponent$$$(), gbc);
        buttonCancel = new JButton();
        buttonCancel.setAlignmentX(0.5f);
        buttonCancel.setMaximumSize(new Dimension(65, 30));
        buttonCancel.setMinimumSize(new Dimension(65, 30));
        buttonCancel.setPreferredSize(new Dimension(65, 30));
        buttonCancel.setText("Cancel");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 0.5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        Root.add(buttonCancel, gbc);
        buttonOk = new JButton();
        buttonOk.setAlignmentX(0.5f);
        buttonOk.setMaximumSize(new Dimension(65, 30));
        buttonOk.setMinimumSize(new Dimension(65, 30));
        buttonOk.setPreferredSize(new Dimension(65, 30));
        buttonOk.setText("Ok");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.weightx = 0.5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        Root.add(buttonOk, gbc);
        statusBar = new StatusBar();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
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
