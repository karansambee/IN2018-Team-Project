package skywaysolutions.app.gui;

import skywaysolutions.app.database.IDB_Connector;
import skywaysolutions.app.gui.control.StatusBar;
import skywaysolutions.app.gui.tab.DashboardTab;
import skywaysolutions.app.utils.AccessorManager;
import skywaysolutions.app.utils.CheckedException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
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
    private final DashboardTab dashboardTab;
    private StatusBar statusBar;
    private final Login login;
    private final About about;
    private final AccessorManager manager;
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
        aboutButton.addActionListener(e -> showAbout());
        pack();
        //Create the login form
        login = new Login(this, true, this.manager);
        //Create the about form
        about = new About(this, true, "AirVia ATS", "(C) Skyway Solutions 2023\nAll rights reserved, licensed to AirVia LTD.");
        //Open form and setup sub-controls
        setVisible(true);
        statusBar.createPrompt(this);
        //Create tabs
        dashboardTab = new DashboardTab();
        dashboardTab.setup(this, statusBar, manager);
    }

    private void showAbout() {
        about.showDialog();
    }

    private void newLogin() {
        tabbedPaneMain.removeAll();
        //Begin login sequence
        manager.staffAccessor.logoutAccount();
        login.showDialog();
        if (manager.staffAccessor.getLoggedInAccountEmail() == null) hideFrame();
        else {
            //Add tabs
            dashboardTab.refresh();
            tabbedPaneMain.addTab("Dashboard", dashboardTab);
        }
    }

    private void hideFrame() {
        setVisible(false);
        dispose();
        shutDownLatch.countDown();
    }
}
