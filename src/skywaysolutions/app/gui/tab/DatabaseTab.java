package skywaysolutions.app.gui.tab;

import skywaysolutions.app.gui.Prompt;
import skywaysolutions.app.gui.control.StatusBar;
import skywaysolutions.app.staff.StaffRole;
import skywaysolutions.app.utils.AccessorManager;
import skywaysolutions.app.utils.CheckedException;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.nio.file.Paths;

/**
 * This class provides the DatabaseTab.
 *
 * @author Alfred Manville
 */
public class DatabaseTab extends JPanel implements ITab {
    private JPanel Root;
    private JButton buttonForceUnlock;
    private JButton buttonPurge;
    private JList listEntityTables;
    private JButton buttonForceUnlockAll;
    private JButton buttonPurgeAll;
    private JButton buttonBackup;
    private JButton buttonRestore;

    private final DefaultListModel<String> listModel = new DefaultListModel<>();
    private Prompt prompt;
    private StatusBar statusBar;
    private AccessorManager manager;
    private Window parent;
    private final JFileChooser dbStoreChooser;
    private boolean setupNotDone = true;

    public DatabaseTab() {
        super(true);
        listEntityTables.setModel(listModel);
        //Init db file selector
        dbStoreChooser = new JFileChooser();
        dbStoreChooser.setFileFilter(new FileNameExtensionFilter("Binary SQL DB Dump (.bns)", "bns"));
        dbStoreChooser.setMultiSelectionEnabled(false);
        //Add button events
        buttonForceUnlock.addActionListener(e -> {
            if (setupNotDone || statusBar.isInHelpMode()) return;
            if (listEntityTables.getSelectedIndex() > -1) {
                String ctable = listModel.get(listEntityTables.getSelectedIndex());
                prompt.setTitle("Are You Sure?");
                prompt.setContents("Are you sure you want to force unlock " + ctable + "?");
                prompt.setButtons(new String[]{"No", "Yes"}, 0);
                prompt.showDialog();
                if (prompt.getLastButton() == null || prompt.getLastButton().equals("No")) return;
                try {
                    manager.forceUnlock(ctable);
                } catch (CheckedException ex) {
                    statusBar.setStatus(ex, 2500);
                }
            }
        });
        buttonPurge.addActionListener(e -> {
            if (setupNotDone || statusBar.isInHelpMode()) return;
            if (listEntityTables.getSelectedIndex() > -1) {
                try {
                    String ctable = listModel.get(listEntityTables.getSelectedIndex());
                    prompt.setTitle("Are You Sure?");
                    prompt.setContents("Are you sure you want to purge " + ctable + "?\nWARNING: The table will no longer exist, restart the program to re-add the table.\n\nThe operation can fail.");
                    prompt.setButtons(new String[]{"No", "Yes"}, 0);
                    prompt.showDialog();
                    if (prompt.getLastButton() == null || prompt.getLastButton().equals("No")) return;
                    manager.forcePurge(ctable);
                } catch (CheckedException ex) {
                    statusBar.setStatus(ex, 2500);
                }
            }
        });
        buttonForceUnlockAll.addActionListener(e -> {
            if (setupNotDone || statusBar.isInHelpMode()) return;
            prompt.setTitle("Are You Sure?");
            prompt.setContents("Are you sure you want to force unlock all entity tables?");
            prompt.setButtons(new String[]{"No", "Yes"}, 0);
            prompt.showDialog();
            if (prompt.getLastButton() == null || prompt.getLastButton().equals("No")) return;
            try {
                manager.forceUnlock(null);
            } catch (CheckedException ex) {
                statusBar.setStatus(ex, 2500);
            }
        });
        buttonPurgeAll.addActionListener(e -> {
            if (setupNotDone || statusBar.isInHelpMode()) return;
            prompt.setTitle("Are You Sure?");
            prompt.setContents("Are you sure you want to purge all entity tables?\nWARNING: The tables will no longer exist, restart the program to re-add the tables.");
            prompt.setButtons(new String[]{"No", "Yes"}, 0);
            prompt.showDialog();
            if (prompt.getLastButton() == null || prompt.getLastButton().equals("No")) return;
            try {
                manager.forcePurge(null);
            } catch (CheckedException ex) {
                statusBar.setStatus(ex, 2500);
            }
        });
        buttonBackup.addActionListener(e -> {
            if (setupNotDone || statusBar.isInHelpMode()) return;
            dbStoreChooser.setCurrentDirectory(Paths.get(".").toFile());
            if (dbStoreChooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
                if (!dbStoreChooser.getFileFilter().accept(dbStoreChooser.getSelectedFile())) dbStoreChooser.setSelectedFile(new File(dbStoreChooser.getSelectedFile().getAbsolutePath() + ".bns"));
                try {
                    manager.assureTable(null);
                    manager.backup(dbStoreChooser.getSelectedFile());
                } catch (CheckedException ex) {
                    statusBar.setStatus(ex, 2500);
                }
            }
        });
        buttonRestore.addActionListener(e -> {
            if (setupNotDone || statusBar.isInHelpMode()) return;
            dbStoreChooser.setCurrentDirectory(Paths.get(".").toFile());
            if (dbStoreChooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
                prompt.setTitle("Are You Sure?");
                prompt.setContents("Are you sure you want to restore the database, you must purge the database before restoring and force unlock after restoring.");
                prompt.setButtons(new String[]{"No", "Yes"}, 0);
                prompt.showDialog();
                if (prompt.getLastButton() == null || prompt.getLastButton().equals("No")) return;
                try {
                    manager.assureTable(null);
                    manager.restore(dbStoreChooser.getSelectedFile());
                } catch (CheckedException ex) {
                    statusBar.setStatus(ex, 2500);
                }
            }
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
     * @param owner     The parent window the control is contained on.
     * @param prompt    The prompt to use.
     * @param statusBar The status bar to use.
     * @param manager   The accessor manager to use.
     */
    @Override
    public void setup(Window owner, Prompt prompt, StatusBar statusBar, AccessorManager manager) {
        this.parent = owner;
        this.prompt = prompt;
        this.statusBar = statusBar;
        this.manager = manager;
        //Add help information
        statusBar.registerComponentForHelp(buttonPurge, "Drops the selected entity table with its auxiliary table.\nWARNING: The table will no longer exist, restart the program to re-add the table.");
        statusBar.registerComponentForHelp(buttonPurgeAll, "Drops all the entity and auxiliary tables.\nWARNING: The tables will no longer exist, restart the program to re-add the tables.");
        statusBar.registerComponentForHelp(buttonForceUnlock, "Force unlocks an entity table.");
        statusBar.registerComponentForHelp(buttonForceUnlockAll, "Force unlocks all entity tables.");
        statusBar.registerComponentForHelp(buttonBackup, "Backs up all the entity table contents to a binary file.");
        statusBar.registerComponentForHelp(buttonRestore, "Restores all the entity table data from a binary file, please purge all tables before use and unlock all tables after use.");
        setupNotDone = false;
    }

    /**
     * Refreshes the tab's contents.
     */
    @Override
    public void refresh() {
        if (setupNotDone) return;
        listEntityTables.clearSelection();
        listModel.clear();
        for (String c : manager.tables) listModel.addElement(c);
        if (listModel.size() > 0) listEntityTables.setSelectedIndex(0);
    }

    /**
     * If the tab can be accessed by the current, logged in, account.
     *
     * @return If the tab is accessible.
     * @throws CheckedException An error has occurred.
     */
    @Override
    public boolean accessAllowed() throws CheckedException {
        return manager.staffAccessor.getAccountRole(null) == StaffRole.Administrator;
    }

    /**
     * Gets the caption of the tab.
     *
     * @return The caption.
     */
    @Override
    public String getCaption() {
        return "Database Manager";
    }
}
