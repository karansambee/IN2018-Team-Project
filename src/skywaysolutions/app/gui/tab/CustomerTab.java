package skywaysolutions.app.gui.tab;

import skywaysolutions.app.customers.CustomerType;
import skywaysolutions.app.gui.AccountEditor;
import skywaysolutions.app.gui.CustomerEditor;
import skywaysolutions.app.gui.Prompt;
import skywaysolutions.app.gui.control.NonEditableDefaultTableModel;
import skywaysolutions.app.gui.control.StatusBar;
import skywaysolutions.app.gui.hoster.HostRunner;
import skywaysolutions.app.gui.hoster.IHostInvokable;
import skywaysolutions.app.staff.StaffRole;
import skywaysolutions.app.utils.AccessorManager;
import skywaysolutions.app.utils.CheckedException;
import skywaysolutions.app.utils.Decimal;
import skywaysolutions.app.utils.PersonalInformation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerTab extends JPanel implements ITab, IHostInvokable {
    private JTable tableListed;
    private JButton buttonAdd;
    private JButton buttonEdit;
    private JButton buttonDelete;
    private JButton buttonRefresh;
    private JPanel Root;
    private final DefaultTableModel tableModel;
    private final List<Long> tableBacker = new ArrayList<>();
    private CustomerEditor customerEditor;
    private StatusBar statusBar;
    private AccessorManager manager;
    private final Object slock = new Object();
    private boolean setupNotDone = true;
    private final HostRunner runner;
    private Prompt prompt;

    public CustomerTab() {
        super(true);
        //Create host runner
        runner = new HostRunner(this, statusBar);
        runner.start();
        //Populate table
        tableModel = new NonEditableDefaultTableModel(new Object[] {"Alias", "Email", "Name", "Type", "Currency", "Discount ID"}, 0);
        tableListed.getTableHeader().setReorderingAllowed(false);
        tableListed.getTableHeader().setResizingAllowed(true);
        tableListed.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tableListed.setModel(tableModel);
        buttonAdd.addActionListener(e -> {
            if (setupNotDone) return;
            if (!statusBar.isInHelpMode()) {
                try {
                    customerEditor.setCustomerID(null);
                    customerEditor.showDialog();
                    refresh();
                } catch (CheckedException ex) {
                    statusBar.setStatus(ex, 2500);
                }
            }
        });
        buttonEdit.addActionListener(e -> {
            if (setupNotDone) return;
            if (!statusBar.isInHelpMode() && tableListed.getSelectedRow() > -1) {
                try {
                    customerEditor.setCustomerID(tableBacker.get(tableListed.getSelectedRow()));
                    customerEditor.showDialog();
                    refresh(tableListed.getSelectedRow());
                } catch (CheckedException ex) {
                    statusBar.setStatus(ex, 2500);
                }
            }
        });
        buttonDelete.addActionListener(e -> {
            if (setupNotDone) return;
            if (!statusBar.isInHelpMode() && tableListed.getSelectedRows().length > 0) {
                prompt.setTitle("Are You Sure?");
                prompt.setContents("Are you sure you want to delete the account(s)?\nThis operation may fail.");
                prompt.setButtons(new String[] {"No", "Yes"}, 0);
                prompt.showDialog();
                if (prompt.getLastButton() != null && prompt.getLastButton().equals("Yes"))
                    invDeleteAccount();
            }
        });
        buttonRefresh.addActionListener(e -> {
            if (setupNotDone) return;
            if (!statusBar.isInHelpMode()) refresh();
        });
        tableListed.getSelectionModel().addListSelectionListener(e -> {
            boolean enb = tableListed.getSelectedRows().length > 0;
            buttonEdit.setEnabled(enb);
            buttonDelete.setEnabled(enb);
        });
        //Setup contents (Tab requires explicit adding)
        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.BOTH;
        add(Root, constraints);
    }

    /**
     * Invokes using the specified command ID and arguments.
     *
     * @param id   The command ID.
     * @param args The arguments.
     * @throws CheckedException An error has occurred.
     */
    @Override
    public void invoke(String id, Object[] args) throws CheckedException {
        switch (id) {
            case "deleteAccount" -> {
                int[] rows = (int[]) args[0];
                long[] accs = (long[]) args[1];
                for (int i = rows.length - 1; i >= 0; i--) {
                    manager.customerAccessor.deleteAccount(accs[i]);
                    int finalI = i;
                    SwingUtilities.invokeLater(() -> {
                        synchronized (slock) {
                            tableModel.removeRow(rows[finalI]);
                            tableBacker.remove(rows[finalI]);
                        }
                    });
                }
            }
            case "refreshClear" -> {
                SwingUtilities.invokeLater(() -> {
                    synchronized (slock) {
                        tableModel.setRowCount(0);
                        tableBacker.clear();
                    }
                });
            }
            case "refreshAccounts" -> {
                long[] accounts = manager.customerAccessor.listAccounts(CustomerType.Any);
                for (long c : accounts) addRow(c);
                int selectionIndex = (int) args[0];
                if (selectionIndex > -1) SwingUtilities.invokeLater(() -> tableListed.addRowSelectionInterval(selectionIndex, selectionIndex));
            }
        }
    }

    private void addRow(long id) {
        try {
            String alias = manager.customerAccessor.getAccountAlias(id);
            PersonalInformation cpi =  manager.customerAccessor.getPersonalInformation(id);
            String email = cpi.getEmailAddress();
            String name = cpi.getFirstName() + " " + cpi.getLastName();
            String type = manager.customerAccessor.getCustomerType(id).toString();
            String currency = manager.customerAccessor.getCurrency(id);
            Long discID = manager.customerAccessor.getAccountPlan(id);
            SwingUtilities.invokeLater(() -> {
                synchronized (slock) {
                    tableModel.addRow(new Object[] {alias, email, name,
                            type, currency, (discID == null) ? "N/A" : discID.toString()});
                    tableBacker.add(id);
                }
            });
        } catch (CheckedException e) {
            SwingUtilities.invokeLater(() -> {
                statusBar.setStatus(e, 2500);
            });
        }
    }

    /**
     * Set-ups the tab with the specified owner, prompt, status bar and accessor manager.
     *
     * @param owner     The parent window the control is contained on.
     * @param prompt    The prompt to use.
     * @param statusBar The status bar to use.
     * @param manager   The accessor manager to use.
     */
    @Override
    public void setup(Window owner, Prompt prompt, StatusBar statusBar, AccessorManager manager) {
        customerEditor = new CustomerEditor(owner, true, manager);
        this.statusBar = statusBar;
        this.manager = manager;
        this.prompt = prompt;
        setupNotDone = false;
    }

    /**
     * Refreshes the tab's contents.
     */
    @Override
    public void refresh() {
        if (setupNotDone) return;
        refresh(-1);
    }

    private void refresh(int selectionIndex) {
        if (setupNotDone) return;
        runner.addEvent("refreshClear", null);
        runner.addEvent("refreshAccounts", new Object[] {selectionIndex});
        boolean enb = selectionIndex > -1;
        buttonEdit.setEnabled(enb);
        buttonDelete.setEnabled(enb);
    }

    private void invDeleteAccount() {
        int[] rows = tableListed.getSelectedRows();
        long[] accs = new long[rows.length];
        for (int i = 0; i < rows.length; i++)
            accs[i] = tableBacker.get(rows[i]);
        runner.addEvent("deleteAccount", new Object[] {rows, accs});
    }

    /**
     * If the tab can be accessed by the current, logged in, account.
     *
     * @return If the tab is accessible.
     * @throws CheckedException An error has occurred.
     */
    @Override
    public boolean accessAllowed() throws CheckedException {
        return true;
    }

    /**
     * Gets the caption of the tab.
     *
     * @return The caption.
     */
    @Override
    public String getCaption() {
        return "Customer Manager";
    }
}
