package skywaysolutions.app.gui;

import skywaysolutions.app.database.IDB_Connector;
import skywaysolutions.app.utils.AccessorManager;
import skywaysolutions.app.utils.CheckedException;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Main extends JFrame {
    private JPanel Root;
    private JPanel StatusPanel;
    private JPanel HeaderPanel;
    private JPanel TabContainerPanel;
    private JPanel FooterPanel;
    private JLabel statusLabel;
    private JButton helpButton;
    private JButton aboutButton;
    private JPanel titlePanel;
    private JButton logoutButton;
    private JButton exitButton;
    private JTabbedPane tabbedPaneMain;
    private AccessorManager manager;
    private final Prompt prompt;
    private final Login login;

    public Main(String title, IDB_Connector conn) {
        super(title);
        setContentPane(Root);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        prompt = new Prompt(this, "", "", null, 0, true);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if(manager != null) manager.staffAccessor.logoutAccount();
            }
        });
        exitButton.addActionListener(e -> {
            setVisible(false);
            System.exit(0);
        });
        logoutButton.addActionListener(e -> newLogin());
        pack();
        setVisible(true);
        try {
            manager = new AccessorManager(conn);
            manager.staffAccessor.assureDefaultAdministratorAccount();
        } catch (CheckedException e) {
            prompt.setContents(e.getClass().getName() + ":\n\n"+e.getMessage());
            prompt.setButtons(new String[] {"Exit"}, 0);
            prompt.setTitle("Exception!");
            prompt.setVisible(true);
            System.exit(-1);
        }
        login = new Login(this, true, manager);
        newLogin();
    }

    private void newLogin() {
        tabbedPaneMain.removeAll();
        manager.staffAccessor.logoutAccount();
        login.setVisible(true);
        if (manager.staffAccessor.getLoggedInAccountEmail() == null) {
            setVisible(false);
            System.exit(0);
        } else {
            prompt.setContents("Welcome " + manager.staffAccessor.getLoggedInAccountEmail());
            prompt.setButtons(new String[] {"Ok"}, 0);
            prompt.setTitle("Welcome");
            prompt.setVisible(true);
        }
    }
}
