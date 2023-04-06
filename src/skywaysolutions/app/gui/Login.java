package skywaysolutions.app.gui;

import skywaysolutions.app.gui.control.StatusBar;
import skywaysolutions.app.utils.AccessorManager;
import skywaysolutions.app.utils.CheckedException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * This class provides the login interface.
 *
 * @author Alfred Manville
 */
public class Login extends JDialogx {
    private JPanel Root;
    private JPanel contentPanel;
    private JPanel headerPanel;
    private skywaysolutions.app.gui.control.VTextField loginTextField;
    private JLabel passwordLabel;
    private JPasswordField passwordPasswordField;
    private JButton buttonExit;
    private JButton buttonLogin;
    private StatusBar statusBar;

    /**
     * This constructs a new instance of the login dialog.
     *
     * @param owner The owner of the dialog or null for no owner.
     * @param reusable If the dialog can be shown again after being hidden.
     * @param manager The accessor manager containing all the interfaces.
     */
    public Login(Frame owner, boolean reusable, AccessorManager manager) {
        super(owner, "Login", reusable);
        //Setup form contents
        setContentPane(Root);
        getRootPane().setDefaultButton(buttonLogin);
        //Setup form closing events
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                hideDialog();
            }
        });
        //Setup login text field
        try {
            loginTextField.setup("[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)*[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", statusBar, "Invalid email address!", false, true);
        } catch (CheckedException e) {
            statusBar.setStatus(e, 2500);
        }
        //Setup button events
        buttonExit.addActionListener(e -> {
            if (!statusBar.isInHelpMode()) hideDialog();
        });
        buttonLogin.addActionListener(e -> {
            if (!statusBar.isInHelpMode()) {
                try {
                    if (manager.staffAccessor.authenticateAccount(loginTextField.getText(), String.valueOf(passwordPasswordField.getPassword())))
                        hideDialog();
                    else statusBar.setStatus("Password Incorrect", null, 2500);
                    passwordPasswordField.setText("");
                } catch (CheckedException ex) {
                    statusBar.setStatus(ex, 2500);
                }
            }
        });
        //Setup Help
        statusBar.registerComponentForHelp(loginTextField, "Enter the email address of you user.");
        statusBar.registerComponentForHelp(passwordPasswordField, "Enter the password of your user.\n" +
                "If you've forgotten your password, please contact a system administrator to change it.");
        statusBar.registerComponentForHelp(passwordLabel, "If you've forgotten your password, please contact a system administrator to change it.");
        //Finalize form
        pack();
        dsize = getSize();
        statusBar.createPrompt(this);
    }

    public void hideDialog() {
        loginTextField.setText("");
        passwordPasswordField.setText("");
        super.hideDialog();
    }
}
