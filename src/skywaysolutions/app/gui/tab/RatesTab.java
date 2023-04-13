package skywaysolutions.app.gui.tab;

import skywaysolutions.app.gui.Prompt;
import skywaysolutions.app.gui.RateEditor;
import skywaysolutions.app.gui.control.NonEditableDefaultTableModel;
import skywaysolutions.app.gui.control.StatusBar;
import skywaysolutions.app.gui.hoster.HostRunner;
import skywaysolutions.app.gui.hoster.IHostInvokable;
import skywaysolutions.app.staff.StaffRole;
import skywaysolutions.app.utils.AccessorManager;
import skywaysolutions.app.utils.CheckedException;
import skywaysolutions.app.utils.Decimal;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provides the rate management tab.
 *
 * @author Alfred Manville
 */
public class RatesTab extends JPanel implements ITab, IHostInvokable {
    private JPanel Root;
    private JButton buttonCreate;
    private JButton buttonUpdate;
    private JButton buttonDelete;
    private JButton buttonRefresh;
    private JTable tableListed;
    private final DefaultTableModel tableModel;
    private final List<String> tableBacker = new ArrayList<>();
    private Prompt prompt;
    private RateEditor rateEditor;
    private StatusBar statusBar;
    private AccessorManager manager;
    private final Object slock = new Object();
    private boolean setupNotDone = true;
    private final HostRunner runner;

    /**
     * Constructs a new instance of RatesTab.
     */
    public RatesTab() {
        super(true);
        //Create host runner
        runner = new HostRunner(this, statusBar);
        runner.start();
        //Populate table
        tableModel = new NonEditableDefaultTableModel(new Object[]{"Currency Code", "Symbol", "Conversion Rate"}, 0);
        tableListed.getTableHeader().setReorderingAllowed(false);
        tableListed.getTableHeader().setResizingAllowed(true);
        tableListed.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tableListed.setModel(tableModel);
        //Define control events
        buttonCreate.addActionListener(e -> {
            if (setupNotDone) return;
            if (!statusBar.isInHelpMode()) {
                try {
                    rateEditor.setCurrency(null);
                } catch (CheckedException ex) {
                    statusBar.setStatus(ex, 2500);
                }
                rateEditor.showDialog();
                refresh();
            }
        });
        buttonUpdate.addActionListener(e -> {
            if (setupNotDone) return;
            if (!statusBar.isInHelpMode() && tableListed.getSelectedRow() > -1) {
                try {
                    rateEditor.setCurrency(tableBacker.get(tableListed.getSelectedRow()));
                    rateEditor.showDialog();
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
                prompt.setContents("Are you sure you want to delete the rate(s)?\nThis operation may fail.");
                prompt.setButtons(new String[]{"No", "Yes"}, 0);
                prompt.showDialog();
                if (prompt.getLastButton() != null && prompt.getLastButton().equals("Yes"))
                    invDeleteCurrency();
            }
        });
        buttonRefresh.addActionListener(e -> {
            if (setupNotDone) return;
            if (!statusBar.isInHelpMode()) refresh();
        });
        tableListed.getSelectionModel().addListSelectionListener(e -> {
            boolean enb = tableListed.getSelectedRows().length > 0;
            buttonUpdate.setEnabled(enb);
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
            case "deleteRate" -> {
                int[] rows = (int[]) args[0];
                String[] curs = (String[]) args[1];
                for (int i = rows.length - 1; i >= 0; i--) {
                    if (curs[i].equals("USD")) continue;
                    manager.rateAccessor.removeConversionRate(curs[i]);
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
            case "refreshCurrencies" -> {
                manager.rateAccessor.refreshCache("ExchangeRate");
                String[] currencies = manager.rateAccessor.getConvertableCurrencies();
                for (String c : currencies) addRow(c);
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
        rateEditor = new RateEditor(owner, true, manager);
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
        return "Rates Manager";
    }

    private void addRow(String currency) {
        try {
            String symbol = manager.rateAccessor.getCurrencySymbol(currency);
            Decimal rate = manager.rateAccessor.getConversionRate(currency);
            SwingUtilities.invokeLater(() -> {
                synchronized (slock) {
                    tableModel.addRow(new Object[]{currency, symbol, rate.toString()});
                    tableBacker.add(currency);
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
        runner.addEvent("refreshCurrencies", new Object[]{selectionIndex});
        boolean enb = selectionIndex > -1;
        buttonUpdate.setEnabled(enb);
        buttonDelete.setEnabled(enb);
    }

    private void invDeleteCurrency() {
        int[] rows = tableListed.getSelectedRows();
        String[] curs = new String[rows.length];
        for (int i = 0; i < rows.length; i++)
            curs[i] = tableBacker.get(rows[i]);
        runner.addEvent("deleteRate", new Object[]{rows, curs});
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
        buttonCreate = new JButton();
        buttonCreate.setPreferredSize(new Dimension(30, 30));
        buttonCreate.setText("Create");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.25;
        gbc.weighty = 0.15;
        gbc.fill = GridBagConstraints.BOTH;
        Root.add(buttonCreate, gbc);
        buttonUpdate = new JButton();
        buttonUpdate.setPreferredSize(new Dimension(30, 30));
        buttonUpdate.setText("Update");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.25;
        gbc.weighty = 0.15;
        gbc.fill = GridBagConstraints.BOTH;
        Root.add(buttonUpdate, gbc);
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
        final JScrollPane scrollPane1 = new JScrollPane();
        scrollPane1.setVerticalScrollBarPolicy(22);
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
        tableListed.setPreferredScrollableViewportSize(new Dimension(120, 150));
        scrollPane1.setViewportView(tableListed);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return Root;
    }
}
