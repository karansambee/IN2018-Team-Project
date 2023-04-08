package skywaysolutions.app.gui;

import skywaysolutions.app.database.IDB_Connector;
import skywaysolutions.app.gui.control.StatusBar;
import skywaysolutions.app.utils.AccessorManager;
import skywaysolutions.app.utils.CheckedException;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.CountDownLatch;

/**
 * This class provides the main form.
 *
 * @author Alfred Manville
 */
public class Main extends JFrame {
    private JPanel Root;
    private JPanel HeaderPanel;
    private JPanel TabContainerPanel;
    private JPanel FooterPanel;
    private JButton aboutButton;
    private JPanel titlePanel;
    private JButton logoutButton;
    private JButton exitButton;
    private JTabbedPane tabbedPaneMain;
    private StatusBar statusBar;
    private final AccessorManager manager;
    private final Prompt prompt;
    private final Login login;
    private final CountDownLatch shutDownLatch;

    public Main(String title, IDB_Connector conn, CountDownLatch shutdownLatch) throws CheckedException {
        super(title);
        //Initialize form
        setContentPane(Root);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        //Store shutdown latch for decrementing when form terminates.
        this.shutDownLatch = shutdownLatch;
        //Define the manager that holds the accessor
        manager = new AccessorManager(conn);
        this.manager.rateAccessor.assureUSDCurrency();
        this.manager.staffAccessor.assureDefaultAdministratorAccount();
        //TODO: Remove test prompt?
        prompt = new Prompt(this, "", "", null, 0, true);
        //Add window listener
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                newLogin();
            }
            @Override
            public void windowClosing(WindowEvent e) {
                manager.staffAccessor.logoutAccount();
                hideFrame();
            }
        });
        //Add button events
        exitButton.addActionListener(e -> hideFrame());
        logoutButton.addActionListener(e -> newLogin());
        pack();
        //Create the login form
        login = new Login(this, true, this.manager);
        setVisible(true);
        statusBar.createPrompt(this);
    }

    private void newLogin() {
        tabbedPaneMain.removeAll();
        manager.staffAccessor.logoutAccount();
        login.showDialog();
        if (manager.staffAccessor.getLoggedInAccountEmail() == null) hideFrame();
        else {
            prompt.setContents("Welcome " + manager.staffAccessor.getLoggedInAccountEmail());
            prompt.setButtons(new String[] {"Ok"}, 0);
            prompt.setTitle("Welcome");
            prompt.showDialog();
        }
    }

    private void hideFrame() {
        setVisible(false);
        dispose();
        shutDownLatch.countDown();
    }
}
