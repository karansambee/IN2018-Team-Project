package skywaysolutions.app.gui.tab;

import skywaysolutions.app.gui.AccountEditor;
import skywaysolutions.app.gui.Prompt;
import skywaysolutions.app.gui.control.StatusBar;
import skywaysolutions.app.sales.PaymentType;
import skywaysolutions.app.sales.Sale;
import skywaysolutions.app.staff.StaffRole;
import skywaysolutions.app.utils.*;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.ArrayList;

/**
 * This class provides a DashboardTab control.
 *
 * @author Alfred Manville
 */
public class DashboardTab extends JPanel implements ITab {
    private JPanel Root;
    private JLabel labelEmailAddress;
    private JLabel labelRole;
    private JButton buttonModifyAccount;
    private JList listNotifications;
    private JButton buttonViewNotification;
    private JButton buttonRemoveNotification;
    private JLabel labelName;
    private JLabel labelCommission;
    private Prompt prompt;
    private AccountEditor accountEditor;
    private StatusBar statusBar;
    private AccessorManager manager;
    private boolean setupNotDone = true;
    private final ArrayList<String> extendedNotifications = new ArrayList<>();
    private final DefaultListModel<String> listModel = new DefaultListModel<>();

    /**
     * Creates a new instance of the DashboardTab.
     */
    public DashboardTab() {
        super(true);
        listNotifications.setModel(listModel);
        //Init button events
        buttonModifyAccount.addActionListener(e -> {
            if (setupNotDone) return;
            accountEditor.showDialog();
        });
        buttonViewNotification.addActionListener(e -> {
            int indx = listNotifications.getSelectedIndex();
            if (indx > -1) {
                prompt.setTitle("Notification");
                prompt.setContents(extendedNotifications.get(indx));
                prompt.setButtons(new String[] {"Close", "Dismiss"}, 0);
                prompt.showDialog();
                if (prompt.getLastButton() != null && prompt.getLastButton().equals("Dismiss")) {
                    listModel.remove(indx);
                    extendedNotifications.remove(indx);
                }
            }
        });
        buttonRemoveNotification.addActionListener(e -> {
            int indx = listNotifications.getSelectedIndex();
            if (indx > -1) {
                listModel.remove(indx);
                extendedNotifications.remove(indx);
            }
        });
        listNotifications.addListSelectionListener(e -> updateNotificationButtons());
        //Setup contents (Tab requires explicit adding)
        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.BOTH;
        add(Root, constraints);
    }

    /**
     * Set-ups the tab with the specified owner, prompt, status bar and accessor manager.
     *
     * @param owner The parent window the control is contained on.
     * @param prompt The prompt to use.
     * @param statusBar The status bar to use.
     * @param manager The accessor manager to use.
     */
    @Override
    public void setup(Window owner, Prompt prompt, StatusBar statusBar, AccessorManager manager) {
        this.prompt = prompt;
        accountEditor = new AccountEditor(owner, true, manager);
        this.statusBar = statusBar;
        this.manager = manager;
        setupNotDone = false;
    }

    /**
     * Refreshes the tab's contents.
     */
    @Override
    public void refresh() {
        if (setupNotDone) return;
        try {
            //Setup labels and prompt
            labelEmailAddress.setText("Email: " + manager.staffAccessor.getLoggedInAccountEmail());
            accountEditor.setAccountName(manager.staffAccessor.getLoggedInAccountEmail());
            StaffRole accRole = manager.staffAccessor.getAccountRole(null);
            labelRole.setText("Role: " +accRole.toString());
            PersonalInformation pi = manager.staffAccessor.getPersonalInformation(null);
            long sID = manager.staffAccessor.getAccountID(null);
            labelName.setText("Name ["+ sID + "]: " + pi.getFirstName() + " " + pi.getLastName());
            labelCommission.setText("Commission: " + ((accRole == StaffRole.Advisor) ? manager.staffAccessor.getCommission(null).toString() + "%" : "N/A"));
            //Gather notifications
            extendedNotifications.clear();
            listModel.clear();
            long[] lateSales = (accRole == StaffRole.Advisor) ? manager.salesAccessor.getSalesByStaff(null, PaymentType.Any, manager.staffAccessor.getCurrency(null), sID) :
                    manager.salesAccessor.getSales(null, PaymentType.Any, manager.staffAccessor.getCurrency(null));
            //Output notifications to list
            for (long c : lateSales) if (manager.salesAccessor.late(c, Time.now())) {
                Sale sale = manager.salesAccessor.getSale(c);
                listModel.addElement("Overdue Sale: " + c + " ; " + manager.customerAccessor.getAccountAlias(sale.getCustomerID()));
                PersonalInformation info = manager.customerAccessor.getPersonalInformation(sale.getCustomerID());
                extendedNotifications.add("The Sale: " + c +"\nBy Customer: " + info.getFirstName() + " " + info.getLastName()+"\nIs Late.\n\n"
                + "Payments Were Due: " + sale.getDueDate().toString()+"\nThe customer owes:\n" + manager.rateAccessor.getCurrencySymbol(sale.getCurrency())
                + getOwed(sale).toString());
            }
            //If there are notifications, select the first one
            if (listModel.size() > 0) listNotifications.setSelectedIndex(0);
            updateNotificationButtons();
        } catch (CheckedException ex) {
            statusBar.setStatus(ex, 2500);
        }
    }

    /**
     * If the tab can be accessed by the current, logged in, account.
     *
     * @return If the tab is accessible.
     */
    @Override
    public boolean accessAllowed() {
        return true;
    }

    /**
     * Gets the caption of the tab.
     *
     * @return The caption.
     */
    @Override
    public String getCaption() {
        return "Dashboard";
    }

    private Decimal getOwed(Sale sale) throws CheckedException {
        //Work out what's owed by adding all the refunds and subtracting all the transactions from the cost of sale
        Decimal owed = sale.getCost();
        long[] transactions = manager.salesAccessor.getTransactions(sale.getBlankNumber());
        for (long c : transactions) owed = owed.sub(manager.salesAccessor.getTransaction(c).getPayment().getAmount());
        long[] refunds = manager.salesAccessor.refund(sale.getBlankNumber(), null);
        for (long c : refunds) owed = owed.add(manager.salesAccessor.getRefund(c).getRefundAmount());
        return owed;
    }

    private void updateNotificationButtons() {
        buttonViewNotification.setEnabled(listNotifications.getSelectedIndex() > -1);
        buttonRemoveNotification.setEnabled(listNotifications.getSelectedIndex() > -1);
    }
}
