package skywaysolutions.app.gui.tab;

import skywaysolutions.app.gui.AccountEditor;
import skywaysolutions.app.gui.Prompt;
import skywaysolutions.app.gui.control.NonEditableDefaultTableModel;
import skywaysolutions.app.gui.control.StatusBar;
import skywaysolutions.app.gui.hoster.HostRunner;
import skywaysolutions.app.gui.hoster.IHostInvokable;
import skywaysolutions.app.staff.Account;
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

/**
 * This class provides the AccountsTab control.
 *
 * @author Alfred Manville
 */
public class AccountsTab extends JPanel implements ITab, IHostInvokable {
    private JPanel Root;
    private JButton buttonAdd;
    private JButton buttonEdit;
    private JButton buttonDisable;
    private JButton buttonDelete;
    private JButton buttonRefresh;
    private JComboBox comboBoxFilter;
    private JTable tableListed;
    private final DefaultTableModel tableModel;
    private final List<String> tableBacker = new ArrayList<>();
    private Prompt prompt;
    private AccountEditor accountEditor;
    private StatusBar statusBar;
    private AccessorManager manager;
    private final Object slock = new Object();
    private boolean setupNotDone = true;
    private boolean isAdmin = false;
    private final HostRunner runner;

    public AccountsTab() {
        super(true);
        //Create host runner
        runner = new HostRunner(this, statusBar);
        runner.start();
        //Populate table
        tableModel = new NonEditableDefaultTableModel(new Object[] {"ID", "Email", "Name", "Role", "Currency", "Commission %"}, 0);
        tableListed.getTableHeader().setReorderingAllowed(false);
        tableListed.getTableHeader().setResizingAllowed(true);
        tableListed.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tableListed.setModel(tableModel);
        //Add control events
        comboBoxFilter.setSelectedIndex(0);
        comboBoxFilter.addActionListener(e -> refresh());
        buttonAdd.addActionListener(e -> {
            if (setupNotDone) return;
            if (!statusBar.isInHelpMode()) {
                accountEditor.setAccountName(null);
                accountEditor.showDialog();
                refresh();
            }
        });
        buttonEdit.addActionListener(e -> {
            if (setupNotDone) return;
            if (!statusBar.isInHelpMode() && tableListed.getSelectedRow() > -1) {
                accountEditor.setAccountName(tableBacker.get(tableListed.getSelectedRow()));
                accountEditor.showDialog();
                refresh(tableListed.getSelectedRow());
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
        buttonDisable.addActionListener(e -> {
            if (setupNotDone) return;
            if (!statusBar.isInHelpMode() && tableListed.getSelectedRows().length > 0) {
                prompt.setTitle("Are You Sure?");
                prompt.setContents("Are you sure you want to disable the account(s)?");
                prompt.setButtons(new String[]{"No", "Yes"}, 0);
                prompt.showDialog();
                if (prompt.getLastButton() != null && prompt.getLastButton().equals("Yes"))
                    invDisableAccount();
            }
        });
        buttonRefresh.addActionListener(e -> {
            if (setupNotDone) return;
            if (!statusBar.isInHelpMode()) refresh();
        });
        tableListed.getSelectionModel().addListSelectionListener(e -> {
            boolean enb = tableListed.getSelectedRows().length > 0;
            buttonEdit.setEnabled(enb);
            buttonDelete.setEnabled(enb && isAdmin);
            buttonDisable.setEnabled(enb && isAdmin);
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
            if (comboBoxFilter.getSelectedIndex() > 1 && manager.staffAccessor.getAccountRole(null) != StaffRole.Administrator)
                comboBoxFilter.setSelectedIndex(1);
        } catch (CheckedException e) {
            statusBar.setStatus(e, 2500);
        }
        refresh(-1);
    }

    /**
     * If the tab can be accessed by the current, logged in, account.
     *
     * @return If the tab is accessible.
     * @throws CheckedException An error has occurred.
     */
    @Override
    public boolean accessAllowed() throws CheckedException {
        return (manager.staffAccessor.getAccountRole(null) != StaffRole.Advisor);
    }

    /**
     * Gets the caption of the tab.
     *
     * @return The caption.
     */
    @Override
    public String getCaption() {
        return "Staff Manager";
    }

    private void refresh(int selectionIndex) {
        if (setupNotDone) return;
        runner.addEvent("refreshClear", null);
        try {
            isAdmin = manager.staffAccessor.getAccountRole(null) == StaffRole.Administrator;
            runner.addEvent("refreshAccounts", new Object[] {selectionIndex});
        } catch (CheckedException e) {
            isAdmin = false;
            statusBar.setStatus(e, 2500);
        }
        buttonAdd.setEnabled(isAdmin);
        buttonDisable.setEnabled(isAdmin);
        boolean enb = selectionIndex > -1;
        buttonEdit.setEnabled(enb);
        buttonDelete.setEnabled(enb && isAdmin);
        buttonDisable.setEnabled(enb && isAdmin);
    }

    private void invDeleteAccount() {
        int[] rows = tableListed.getSelectedRows();
        String[] accs = new String[rows.length];
        for (int i = 0; i < rows.length; i++)
            accs[i] = tableBacker.get(rows[i]);
        runner.addEvent("deleteAccount", new Object[] {rows, accs});
    }

    private void invDisableAccount() {
        int[] rows = tableListed.getSelectedRows();
        String[] accs = new String[rows.length];
        for (int i = 0; i < rows.length; i++)
            accs[i] = tableBacker.get(rows[i]);
        runner.addEvent("disableAccount", new Object[] {accs});
    }

    /**
     * Invokes using the specified command ID and arguments.
     *
     * @param id The command ID.
     * @param args The arguments.
     * @throws CheckedException An error has occurred.
     */
    @Override
    public void invoke(String id, Object[] args) throws CheckedException {
        switch (id) {
            case "deleteAccount" -> {
                int[] rows = (int[]) args[0];
                String[] accs = (String[]) args[1];
                for (int i = rows.length - 1; i >= 0; i--) {
                    manager.staffAccessor.deleteAccount(accs[i]);
                    int finalI = i;
                    SwingUtilities.invokeLater(() -> {
                        synchronized (slock) {
                            tableModel.removeRow(rows[finalI]);
                            tableBacker.remove(rows[finalI]);
                        }
                    });
                }
            }
            case "disableAccount" -> {
                String[] accs = (String[]) args[0];
                for (int i = accs.length - 1; i >= 0; i--)
                    manager.staffAccessor.clearPassword(accs[i]);
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
                String[] accounts = manager.staffAccessor.listAccounts((isAdmin) ?
                        StaffRole.getStaffRoleFromValue(comboBoxFilter.getSelectedIndex() - 1) : StaffRole.Advisor);
                for (String c : accounts) addRow(c);
                int selectionIndex = (int) args[0];
                if (selectionIndex > -1) SwingUtilities.invokeLater(() -> tableListed.addRowSelectionInterval(selectionIndex, selectionIndex));
            }
        }
    }

    private void addRow(String email) {
        try {
            long id = manager.staffAccessor.getAccountID(email);
            PersonalInformation pi = manager.staffAccessor.getPersonalInformation(email);
            Decimal comp = manager.staffAccessor.getCommission(email);
            StaffRole sr = manager.staffAccessor.getAccountRole(email);
            String cr = manager.staffAccessor.getCurrency(email);
            SwingUtilities.invokeLater(() -> {
                synchronized (slock) {
                    tableModel.addRow(new Object[] {id, email, pi.getFirstName() + " " + pi.getLastName(),
                            sr.toString(), cr, (comp == null) ? "N/A" : comp.toString()});
                    tableBacker.add(email);
                }
            });
        } catch (CheckedException e) {
            SwingUtilities.invokeLater(() -> {
                statusBar.setStatus(e, 2500);
            });
        }
    }
}
