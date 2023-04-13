package skywaysolutions.app.gui;

import skywaysolutions.app.gui.control.StatusBar;
import skywaysolutions.app.gui.control.VTextField;
import skywaysolutions.app.staff.StaffRole;
import skywaysolutions.app.utils.AccessorManager;
import skywaysolutions.app.utils.CheckedException;
import skywaysolutions.app.utils.Decimal;
import skywaysolutions.app.utils.PersonalInformation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * This class provides a rate creator / updater.
 *
 * @author Alfred Manville
 */
public class RateEditor extends JDialogx {
    private JPanel Root;
    private VTextField textFieldCurrency;
    private VTextField textFieldSymbol;
    private JButton buttonCancel;
    private JButton buttonOk;
    private StatusBar statusBar;
    private VTextField textFieldRate;
    private final AccessorManager manager;
    private String currency;

    /**
     * Constructs a new instance of RateEditor with the specified owner, if reusable and the accessor manager instance.
     *
     * @param owner    The window owner or null.
     * @param reusable If this dialog is reusable.
     * @param manager  The accessor manager instance.
     */
    public RateEditor(Window owner, boolean reusable, AccessorManager manager) {
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
        //Setup controls and their events
        try {
            textFieldCurrency.setup("[A-Z]{3,4}", statusBar, "Invalid Currency Code Length!", false, true);
            textFieldSymbol.setup("(.{1,2})", statusBar, "Invalid Currency Symbol Length!", false, true);
            textFieldRate.setup("([0-9]*|0)(\\.([0-9]+))?", statusBar, "Conversion Rate not Positive Numeric!", false, true);
        } catch (CheckedException e) {
            statusBar.setStatus(e, 2500);
        }
        buttonCancel.addActionListener(e -> {
            if (!statusBar.isInHelpMode()) hideDialog();
        });
        buttonOk.addActionListener(e -> {
            if (!statusBar.isInHelpMode()) {
                //Check the currency
                if (!textFieldCurrency.matches(null)) {
                    statusBar.setStatus("Invalid Currency Code Length!", "", 2500);
                    return;
                }
                //Check the symbol
                if (!textFieldSymbol.matches(null)) {
                    statusBar.setStatus("Invalid Currency Symbol Length!", "", 2500);
                    return;
                }
                //Check the rate
                if (textFieldRate.matches(null)) {
                    try {
                        //Create or update a rate
                        if (currency == null) {
                            manager.rateAccessor.setConversionRate(textFieldCurrency.getText(), new Decimal(Double.parseDouble(textFieldRate.getText()), 6));
                            manager.rateAccessor.setCurrencySymbol(textFieldCurrency.getText(), textFieldSymbol.getText());
                        } else {
                            manager.rateAccessor.setConversionRate(currency, new Decimal(Double.parseDouble(textFieldRate.getText()), 6));
                            manager.rateAccessor.setCurrencySymbol(currency, textFieldSymbol.getText());
                        }
                        hideDialog();
                        return;
                    } catch (CheckedException ex) {
                        statusBar.setStatus(ex, 2500);
                        return;
                    }
                }
                statusBar.setStatus("Conversion Rate not Positive Numeric!", "", 2500);
            }
        });
        //Finalize form
        pack();
        setMinimumSize(Root.getMinimumSize());
        dsize = getSize();
        statusBar.createPrompt(this);
    }

    private void updateInterfaceState() throws CheckedException {
        textFieldCurrency.setEditable(currency == null);
    }

    /**
     * Set the currency code the rate editor will update or null to create a new rate.
     *
     * @param currency The currency code or null.
     * @throws CheckedException A refresh error has occurred.
     */
    public void setCurrency(String currency) throws CheckedException {
        this.currency = currency;
        if (currency != null) manager.rateAccessor.refreshConversionRate(currency);
    }

    public void showDialog() {
        if (reusable || !shown) {
            try {
                setTitle((currency == null) ? "Rate Creator" : "Rate Updater");
                if (currency == null) {
                    textFieldCurrency.setText("");
                    textFieldRate.setText("0.0");
                    textFieldSymbol.setText("");
                } else {
                    textFieldCurrency.setText(currency);
                    textFieldRate.setText(manager.rateAccessor.getConversionRate(currency).toString());
                    textFieldSymbol.setText(manager.rateAccessor.getCurrencySymbol(currency));
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
        Root.setPreferredSize(new Dimension(360, 200));
        final JLabel label1 = new JLabel();
        label1.setMinimumSize(new Dimension(60, 16));
        label1.setPreferredSize(new Dimension(60, 16));
        label1.setText("Currency Code:");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        gbc.weighty = 0.33;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 2, 0, 0);
        Root.add(label1, gbc);
        textFieldCurrency = new VTextField();
        textFieldCurrency.setMinimumSize(new Dimension(60, 30));
        textFieldCurrency.setPreferredSize(new Dimension(60, 30));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        gbc.weighty = 0.33;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        Root.add(textFieldCurrency, gbc);
        final JLabel label2 = new JLabel();
        label2.setMinimumSize(new Dimension(60, 16));
        label2.setPreferredSize(new Dimension(60, 16));
        label2.setText("Currency Symbol:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.5;
        gbc.weighty = 0.33;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 2, 0, 0);
        Root.add(label2, gbc);
        textFieldSymbol = new VTextField();
        textFieldSymbol.setMinimumSize(new Dimension(60, 30));
        textFieldSymbol.setPreferredSize(new Dimension(60, 30));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.5;
        gbc.weighty = 0.33;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        Root.add(textFieldSymbol, gbc);
        final JLabel label3 = new JLabel();
        label3.setMinimumSize(new Dimension(60, 16));
        label3.setPreferredSize(new Dimension(60, 16));
        label3.setText("Conversion Rate From USD:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.5;
        gbc.weighty = 0.33;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 2, 0, 0);
        Root.add(label3, gbc);
        textFieldRate = new VTextField();
        textFieldRate.setMinimumSize(new Dimension(60, 30));
        textFieldRate.setPreferredSize(new Dimension(60, 30));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 0.5;
        gbc.weighty = 0.33;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        Root.add(textFieldRate, gbc);
        buttonCancel = new JButton();
        buttonCancel.setAlignmentX(0.5f);
        buttonCancel.setMaximumSize(new Dimension(65, 30));
        buttonCancel.setMinimumSize(new Dimension(65, 30));
        buttonCancel.setPreferredSize(new Dimension(65, 30));
        buttonCancel.setText("Cancel");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
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
        gbc.gridy = 3;
        gbc.weightx = 0.5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        Root.add(buttonOk, gbc);
        statusBar = new StatusBar();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
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
