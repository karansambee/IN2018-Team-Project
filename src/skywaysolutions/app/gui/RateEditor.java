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
     * @param owner The window owner or null.
     * @param reusable If this dialog is reusable.
     * @param manager The accessor manager instance.
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
}
