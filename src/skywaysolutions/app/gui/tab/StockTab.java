package skywaysolutions.app.gui.tab;

import skywaysolutions.app.gui.BlankEditor;
import skywaysolutions.app.gui.Prompt;
import skywaysolutions.app.gui.control.NonEditableDefaultTableModel;
import skywaysolutions.app.gui.control.StatusBar;
import skywaysolutions.app.gui.hoster.HostRunner;
import skywaysolutions.app.gui.hoster.IHostInvokable;
import skywaysolutions.app.staff.StaffRole;
import skywaysolutions.app.utils.AccessorManager;
import skywaysolutions.app.utils.CheckedException;
import skywaysolutions.app.utils.Time;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class provides a tab that allows for stock to be managed.
 *
 * @author Alfred Manville
 */
public class StockTab extends JPanel implements ITab, IHostInvokable {
    private JTable tableListed;
    private JButton buttonCreate;
    private JButton buttonModify;
    private JButton buttonReturn;
    private JButton buttonBlacklist;
    private JButton buttonRefresh;
    private JComboBox comboBoxFilter;
    private JPanel Root;
    private final DefaultTableModel tableModel;
    private final List<Long> tableBacker = new ArrayList<>();
    private Prompt prompt;
    private BlankEditor blankEditor;
    private StatusBar statusBar;
    private AccessorManager manager;
    private final Object slock = new Object();
    private boolean setupNotDone = true;
    private StaffRole csRole = null;
    private final HostRunner runner;

    public StockTab() {
        super(true);
        //Create host runner
        runner = new HostRunner(this, statusBar);
        runner.start();
        //Populate table
        tableModel = new NonEditableDefaultTableModel(new Object[] {"ID", "Type", "Assigned Staff", "State"}, 0);
        tableListed.getTableHeader().setReorderingAllowed(false);
        tableListed.getTableHeader().setResizingAllowed(true);
        tableListed.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tableListed.setModel(tableModel);
        //Add control events
        comboBoxFilter.setSelectedIndex(0);
        comboBoxFilter.addActionListener(e -> refresh());
        buttonCreate.addActionListener(e -> {
            if (setupNotDone) return;
            if (!statusBar.isInHelpMode()) {
                try {
                    blankEditor.setBlankID(null);
                    blankEditor.showDialog();
                    invAddStock();
                } catch (CheckedException ex) {
                    statusBar.setStatus(ex, 2500);
                }
            }
        });
        buttonModify.addActionListener(e -> {
            if (setupNotDone) return;
            if (!statusBar.isInHelpMode() && tableListed.getSelectedRow() > -1) {
                try {
                    blankEditor.setBlankID(tableBacker.get(tableListed.getSelectedRow()));
                    blankEditor.showDialog();
                refresh(tableListed.getSelectedRow());
                } catch (CheckedException ex) {
                    statusBar.setStatus(ex, 2500);
                }
            }
        });
        buttonReturn.addActionListener(e -> {
            if (setupNotDone) return;
            if (!statusBar.isInHelpMode() && tableListed.getSelectedRows().length > 0) {
                prompt.setTitle("Are You Sure?");
                prompt.setContents("Are you sure you want to return the blanks(s)?\nThis is permanent and the current date is used.");
                prompt.setButtons(new String[] {"No", "Yes"}, 0);
                prompt.showDialog();
                if (prompt.getLastButton() != null && prompt.getLastButton().equals("Yes")) {
                    invReturnStock();
                    refresh();
                }
            }
        });
        buttonBlacklist.addActionListener(e -> {
            if (setupNotDone) return;
            if (!statusBar.isInHelpMode() && tableListed.getSelectedRows().length > 0) {
                prompt.setTitle("Are You Sure?");
                prompt.setContents("Are you sure you want to blacklist the blanks(s)?\nThis is permanent.");
                prompt.setButtons(new String[] {"No", "Yes"}, 0);
                prompt.showDialog();
                if (prompt.getLastButton() != null && prompt.getLastButton().equals("Yes")) {
                    invBlacklistStock();
                    refresh();
                }
            }
        });
        buttonRefresh.addActionListener(e -> {
            if (setupNotDone) return;
            if (!statusBar.isInHelpMode()) refresh();
        });
        tableListed.getSelectionModel().addListSelectionListener(e -> {
            boolean enb = tableListed.getSelectedRows().length > 0;
            buttonModify.setEnabled(enb);
            buttonReturn.setEnabled(enb && csRole == StaffRole.Administrator);
            buttonBlacklist.setEnabled(enb);
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
            case "addStock" -> {
                long startInd = (long) args[0];
                long endInd = (long) args[1];
                Long assign = (Long) args[2];
                if (assign == null) assign = -1L;
                Date assignD = (Date) args[3];
                Date bCrt = (Date) args[4];
                String desc = (String) args[5];
                for (long i = startInd; i <= endInd; i++) {
                    try {
                        manager.stockAccessor.createBlank(i, assign, desc, bCrt, assignD);
                    } catch (CheckedException ex) {
                        SwingUtilities.invokeLater(() -> {
                            statusBar.setStatus(ex, 2500);
                        });
                    }
                }
            }
            case "returnStock" -> {
                long[] stks = (long[]) args[0];
                Date rd = (Date) args[1];
                for (int i = stks.length - 1; i >= 0; i--)
                    manager.stockAccessor.returnBlank(stks[i], rd);
            }
            case "blacklistStock" -> {
                long[] stks = (long[]) args[0];
                for (int i = stks.length - 1; i >= 0; i--)
                    manager.stockAccessor.blacklistBlank(stks[i]);
            }
            case "refreshClear" -> {
                SwingUtilities.invokeLater(() -> {
                    synchronized (slock) {
                        tableModel.setRowCount(0);
                        tableBacker.clear();
                    }
                });
            }
            case "refreshStock" -> {
                int filter = comboBoxFilter.getSelectedIndex();
                long[] stock = manager.stockAccessor.getBlanks((csRole == StaffRole.Administrator) ? ((filter == 1) ? -2 : -1) : manager.staffAccessor.getLoggedInAccountID());
                for (long c : stock) addRow(c, filter);
                int selectionIndex = (int) args[0];
                if (selectionIndex > -1) SwingUtilities.invokeLater(() -> tableListed.addRowSelectionInterval(selectionIndex, selectionIndex));
            }
        }
    }

    private void addRow(long id, int filter) {
        try {
            boolean bVoid = manager.stockAccessor.isBlankVoided(id);
            boolean bBlacklist = manager.stockAccessor.isBlankBlacklisted(id);
            boolean bReturned = manager.stockAccessor.isBlankReturned(id);
            if ((bVoid || bBlacklist || bReturned) && filter != 0 && filter != 4) return;
            boolean bSold = manager.salesAccessor.blankSold(id);
            if (bSold && filter != 0 && filter != 3) return;
            int bType = manager.stockAccessor.getBlankType(id);
            Long bAID = manager.stockAccessor.getBlankAssignmentID(id);
            if (bAID == null && filter != 0 && filter != 1) return;
            if (bAID != null && filter != 0 && filter != 2) return;
            String bState = (bVoid) ? "Voided" : ((bBlacklist ? "Blacklisted" : ((bReturned) ? "Returned" : ((bSold) ? "Sold" : ((bAID == null) ? "Unassigned" : "Assigned")))));
            String bAStaff = "N/A";
            if (bAID != null) bAStaff = manager.staffAccessor.getAccountEmail(bAID);
            String finalBAStaff = bAStaff;
            SwingUtilities.invokeLater(() -> {
                synchronized (slock) {
                    tableModel.addRow(new Object[] {id, bType, finalBAStaff, bState});
                    tableBacker.add(id);
                }
            });
        } catch (CheckedException e) {
            SwingUtilities.invokeLater(() -> {
                statusBar.setStatus(e, 2500);
            });
        }
    }

    private void invAddStock() {
        if (blankEditor.getStartBlank() == null) return;
        runner.addEvent("addStock", new Object[] {blankEditor.getStartBlank(), blankEditor.getEndBlank(),
                blankEditor.getBlankAssigned(), blankEditor.getBlankAssignedDate(), blankEditor.getBlankCreateDate(), blankEditor.getBlankDescription()});
    }

    private void invReturnStock() {
        int[] rows = tableListed.getSelectedRows();
        long[] stks = new long[rows.length];
        for (int i = 0; i < rows.length; i++)
            stks[i] = tableBacker.get(rows[i]);
        runner.addEvent("returnStock", new Object[] {stks, Time.now()});
    }

    private void invBlacklistStock() {
        int[] rows = tableListed.getSelectedRows();
        long[] stks = new long[rows.length];
        for (int i = 0; i < rows.length; i++)
            stks[i] = tableBacker.get(rows[i]);
        runner.addEvent("blacklistStock", new Object[] {stks});
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
        this.prompt = prompt;
        blankEditor = new BlankEditor(owner, true, manager);
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
        refresh(-1);
    }

    private void refresh(int selectionIndex) {
        if (setupNotDone) return;
        runner.addEvent("refreshClear", null);
        try {
            csRole = manager.staffAccessor.getAccountRole(null);
            runner.addEvent("refreshStock", new Object[] {selectionIndex});
        } catch (CheckedException e) {
            csRole = null;
            statusBar.setStatus(e, 2500);
        }
        buttonCreate.setEnabled(csRole == StaffRole.Administrator);
        boolean enb = selectionIndex > -1;
        buttonModify.setEnabled(enb);
        buttonReturn.setEnabled(enb && csRole == StaffRole.Administrator);
        buttonBlacklist.setEnabled(enb);
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
        return "Stock Manager";
    }
}
