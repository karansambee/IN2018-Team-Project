package skywaysolutions.app.gui.tab;

import skywaysolutions.app.gui.BlankTypeEditor;
import skywaysolutions.app.gui.Prompt;
import skywaysolutions.app.gui.control.NonEditableDefaultTableModel;
import skywaysolutions.app.gui.control.StatusBar;
import skywaysolutions.app.gui.hoster.HostRunner;
import skywaysolutions.app.gui.hoster.IHostInvokable;
import skywaysolutions.app.staff.StaffRole;
import skywaysolutions.app.utils.AccessorManager;
import skywaysolutions.app.utils.CheckedException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provides the stock types management tab.
 *
 * @author Alfred Manville
 */
public class StockTypesTab extends JPanel implements ITab, IHostInvokable {
    private JPanel Root;
    private JTable tableListed;
    private JButton buttonAdd;
    private JButton buttonEdit;
    private JButton buttonDelete;
    private JButton buttonRefresh;
    private final DefaultTableModel tableModel;
    private final List<Integer> tableBacker = new ArrayList<>();
    private Prompt prompt;
    private BlankTypeEditor blankTypeEditor;
    private StatusBar statusBar;
    private AccessorManager manager;
    private final Object slock = new Object();
    private boolean setupNotDone = true;
    private final HostRunner runner;

    /**
     * Constructs a new instance of the StockTypesTab class.
     */
    public StockTypesTab() {
        super(true);
        //Create host runner
        runner = new HostRunner(this, statusBar);
        runner.start();
        //Populate table
        tableModel = new NonEditableDefaultTableModel(new Object[]{"Type Number", "Type Description"}, 0);
        tableListed.getTableHeader().setReorderingAllowed(false);
        tableListed.getTableHeader().setResizingAllowed(true);
        tableListed.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tableListed.setModel(tableModel);
        //Define control events
        buttonAdd.addActionListener(e -> {
            if (setupNotDone) return;
            if (!statusBar.isInHelpMode()) {
                try {
                    blankTypeEditor.setBlankTypeID(null);
                } catch (CheckedException ex) {
                    statusBar.setStatus(ex, 2500);
                }
                blankTypeEditor.showDialog();
                refresh();
            }
        });
        buttonEdit.addActionListener(e -> {
            if (setupNotDone) return;
            if (!statusBar.isInHelpMode() && tableListed.getSelectedRow() > -1) {
                try {
                    blankTypeEditor.setBlankTypeID(tableBacker.get(tableListed.getSelectedRow()));
                    blankTypeEditor.showDialog();
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
                prompt.setContents("Are you sure you want to delete the stock type(s)?\nThis operation may fail.");
                prompt.setButtons(new String[]{"No", "Yes"}, 0);
                prompt.showDialog();
                if (prompt.getLastButton() != null && prompt.getLastButton().equals("Yes"))
                    invDeleteSType();
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
            case "deleteSType" -> {
                int[] rows = (int[]) args[0];
                int[] stps = (int[]) args[1];
                for (int i = rows.length - 1; i >= 0; i--) {
                    manager.stockAccessor.deleteBlankType(stps[i]);
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
            case "refreshSTypes" -> {
                manager.stockAccessor.refreshCache("BlankType");
                int[] stypes = manager.stockAccessor.listBlankTypes();
                for (int c : stypes) addRow(c);
                int selectionIndex = (int) args[0];
                if (selectionIndex > -1)
                    SwingUtilities.invokeLater(() -> tableListed.addRowSelectionInterval(selectionIndex, selectionIndex));
            }
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
        this.prompt = prompt;
        blankTypeEditor = new BlankTypeEditor(owner, true, manager);
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

    /**
     * If the tab can be accessed by the current, logged in, account.
     *
     * @return If the tab is accessible.
     * @throws CheckedException An error has occurred.
     */
    @Override
    public boolean accessAllowed() throws CheckedException {
        return (manager.staffAccessor.getAccountRole(null) == StaffRole.Administrator);
    }

    /**
     * Gets the caption of the tab.
     *
     * @return The caption.
     */
    @Override
    public String getCaption() {
        return "Stock Type Manager";
    }

    private void addRow(int stype) {
        try {
            String desc = manager.stockAccessor.getBlankTypeDescription(stype);
            if (desc.length() > 32) desc = desc.substring(0, 32);
            String finalDesc = desc;
            SwingUtilities.invokeLater(() -> {
                synchronized (slock) {
                    tableModel.addRow(new Object[]{stype, finalDesc});
                    tableBacker.add(stype);
                }
            });
        } catch (CheckedException e) {
            SwingUtilities.invokeLater(() -> {
                statusBar.setStatus(e, 2500);
            });
        }
    }

    private void refresh(int selectionIndex) {
        if (setupNotDone) return;
        runner.addEvent("refreshClear", null);
        runner.addEvent("refreshSTypes", new Object[]{selectionIndex});
        boolean enb = selectionIndex > -1;
        buttonEdit.setEnabled(enb);
        buttonDelete.setEnabled(enb);
    }

    private void invDeleteSType() {
        int[] rows = tableListed.getSelectedRows();
        int[] curs = new int[rows.length];
        for (int i = 0; i < rows.length; i++)
            curs[i] = tableBacker.get(rows[i]);
        runner.addEvent("deleteSType", new Object[]{rows, curs});
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
        final JScrollPane scrollPane1 = new JScrollPane();
        scrollPane1.setVerticalScrollBarPolicy(22);
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 4;
        gbc.weightx = 1.0;
        gbc.weighty = 0.85;
        gbc.fill = GridBagConstraints.BOTH;
        Root.add(scrollPane1, gbc);
        tableListed = new JTable();
        tableListed.setFillsViewportHeight(true);
        tableListed.setPreferredScrollableViewportSize(new Dimension(200, 150));
        scrollPane1.setViewportView(tableListed);
        buttonAdd = new JButton();
        buttonAdd.setPreferredSize(new Dimension(30, 30));
        buttonAdd.setText("Add");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.25;
        gbc.weighty = 0.15;
        gbc.fill = GridBagConstraints.BOTH;
        Root.add(buttonAdd, gbc);
        buttonEdit = new JButton();
        buttonEdit.setPreferredSize(new Dimension(30, 30));
        buttonEdit.setText("Edit");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.25;
        gbc.weighty = 0.15;
        gbc.fill = GridBagConstraints.BOTH;
        Root.add(buttonEdit, gbc);
        buttonDelete = new JButton();
        buttonDelete.setPreferredSize(new Dimension(30, 30));
        buttonDelete.setText("Delete");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 0.25;
        gbc.weighty = 0.15;
        gbc.fill = GridBagConstraints.BOTH;
        Root.add(buttonDelete, gbc);
        buttonRefresh = new JButton();
        buttonRefresh.setPreferredSize(new Dimension(30, 30));
        buttonRefresh.setText("Refresh");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.weightx = 0.25;
        gbc.weighty = 0.15;
        gbc.fill = GridBagConstraints.BOTH;
        Root.add(buttonRefresh, gbc);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return Root;
    }
}
