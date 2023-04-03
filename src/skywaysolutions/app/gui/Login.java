package skywaysolutions.app.gui;

import skywaysolutions.app.utils.AccessorManager;
import skywaysolutions.app.utils.CheckedException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Login extends JDialog {
    private JPanel Root;
    private JPanel StatusPanel;
    private JLabel statusLabel;
    private JButton helpButton;
    private JPanel contentPanel;
    private JPanel headerPanel;
    private JTextField loginTextField;
    private JLabel loginLabel;
    private JLabel passwordLabel;
    private JPasswordField passwordPasswordField;
    private JButton buttonExit;
    private JButton buttonLogin;
    private final boolean reusable;
    private final Object shslock = new Object();
    private Thread statusHider;

    public Login(Frame owner, boolean reusable, AccessorManager manager) {
        super(owner, "Login", true);
        setContentPane(Root);
        setResizable(false);
        getRootPane().setDefaultButton(buttonLogin);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.reusable = reusable;
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (reusable) setVisible(false); else dispose();
            }
        });
        buttonExit.addActionListener(e -> nshow());
        buttonLogin.addActionListener(e -> {
            try {
                if (manager.staffAccessor.authenticateAccount(loginTextField.getText(), String.valueOf(passwordPasswordField.getPassword()))) nshow(); else {
                    statusLabel.setText("Login Attempt Failed");
                    executeStatusHider();
                }
            } catch (CheckedException ex) {
                statusLabel.setText("Exception: "+ex.getClass().getName());
                executeStatusHider();
            }
        });
        pack();
    }

    public void nshow() {
        if (reusable) setVisible(false); else dispose();
    }

    private void executeStatusHider() {
        synchronized (shslock) {
            if (statusHider == null) {
                statusHider = new Thread(new StatusHideRunner());
                statusHider.setDaemon(true);
                statusHider.start();
            }
        }
    }

    private class StatusHideRunner implements Runnable {
        @Override
        public void run() {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
            }
            SwingUtilities.invokeLater(() -> statusLabel.setText("..."));
        }
    }
}
