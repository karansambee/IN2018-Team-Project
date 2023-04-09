package skywaysolutions.app.gui.control;

import skywaysolutions.app.gui.Prompt;
import skywaysolutions.app.utils.CheckedException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;

/**
 * Provides a status bar control that can do help.
 *
 * @author Alfred Manville
 */
public class StatusBar extends JPanel {
    private JPanel Root;
    private JLabel statusLabel;
    private JButton helpButton;
    private Prompt prompt;
    private boolean activeHelp;
    private boolean activeExtended;
    private int statusTimeout;
    private Thread statusThread;
    private String currentStatus = "";
    private final Object slock = new Object();
    private final HelpActionHandler helpActionLsnr = new HelpActionHandler();
    private final HashMap<Object, String> helpMap = new HashMap<>();

    /**
     * Constructs a new status bar.
     */
    public StatusBar() {
        super(true);
        //Define help button action listener
        helpButton.addActionListener(e -> {
            if (prompt == null) return;
            synchronized (slock) {
                if (activeHelp) {
                    if (e.getSource() == helpButton) {
                        //Restore help button to ?
                        activeHelp = false;
                        helpButton.setText("?");
                        statusLabel.setText(currentStatus);
                    }
                } else if (activeExtended) {
                    if (e.getSource() == helpButton) prompt.showDialog();
                } else {
                    if (e.getSource() == helpButton) {
                        //Set help button to x
                        helpButton.setText("x");
                        statusLabel.setText("Click on an object to get help...");
                        activeHelp = true;
                    }
                }
            }
        });
    }

    /**
     * Creates the internal prompt dialog object.
     * This function allows the prompt to be generated once there is a parent window.
     *
     * @param parent The parent window or null of the created prompt.
     */
    public void createPrompt(Window parent) {
        //Prompt for extended status or help's construction
        prompt = new Prompt(parent, true);
        prompt.setButtons(new String[]{"Ok"}, 0);
    }

    /**
     * Registers a component with the specified help text.
     *
     * @param component The component.
     * @param helpInformation The help text attached to the component.
     */
    public void registerComponentForHelp(Component component, String helpInformation) {
        synchronized (slock) {
            helpMap.put(component, helpInformation);
            component.addMouseListener(helpActionLsnr);
        }
    }

    /**
     * Un-registers a component from help.
     *
     * @param component The component.
     */
    public void unRegisterComponentForHelp(Component component) {
        synchronized (slock) {
            helpMap.remove(component);
            component.removeMouseListener(helpActionLsnr);
        }
    }

    /**
     * Gets if the status bar is in help mode.
     *
     * @return If the status bar is in control selection mode for help.
     */
    public boolean isInHelpMode() {
        return activeHelp;
    }

    /**
     * Set the status using an exception.
     *
     * @param ex The exception to use.
     * @param statusTimeout The timeout of the status message (0 To Disable).
     */
    public void setStatus(CheckedException ex, int statusTimeout) {
        setStatus("Exception: " + ex.getMessage(), "Exception: " + ex.getClass().getName() + "\n\n" + ex.getMessage() +
                "\n\nStack Trace:\n\n" + ex.getStackTraceAsString(), statusTimeout);
    }

    /**
     * Set the status using a string and optional extended status.
     *
     * @param status The status to display.
     * @param extendedStatus The extended status to display in the prompt (null to not have any).
     * @param statusTimeout The timeout of the status message (0 To Disable).
     */
    public void setStatus(String status, String extendedStatus, int statusTimeout) {
        synchronized (slock) {
            currentStatus = status;
            if (statusTimeout > 0 && (statusThread == null || !statusThread.isAlive())) {
                this.statusTimeout = statusTimeout;
                statusThread = new Thread(new StatusClearRunner());
                statusThread.setDaemon(true);
                statusThread.start();
            }
            if (extendedStatus == null || extendedStatus.equals("")) {
                activeExtended = false;
                helpButton.setText("?");
            }
            if (!activeHelp) { //Do not overwrite when help active
                statusLabel.setText(status);
                if (extendedStatus != null && !extendedStatus.equals("")) {
                    activeExtended = true;
                    //Setup extended status prompt
                    prompt.setTitle("Extended Status");
                    prompt.setContents(extendedStatus);
                    //Set help button to ^
                    helpButton.setText("^");
                }
            }
        }
    }

    /**
     * Clears the status.
     */
    public void clearStatus() {
        setStatus("", "", 0);
    }

    /**
     * Clears the status if it is the same as the passed status text.
     *
     * @param status The status text.
     */
    public void clearStatus(String status) {
        boolean isStatus = false;
        synchronized (slock) {
            isStatus = currentStatus != null && currentStatus.equals(status);
        }
        if (isStatus) clearStatus();
    }

    /**
     * Deactivates the help system if active.
     */
    public void deactivateHelp() {
        if (activeHelp) {
            //Restore help button to ?
            activeHelp = false;
            helpButton.setText("?");
            statusLabel.setText(currentStatus);
        }
    }

    /**
     * This class provides the action for when a mouse action occurs on a control with help in help mode.
     *
     * @author Alfred Manville
     */
    private class HelpActionHandler extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            synchronized (slock) {
                if (activeHelp && !(e.getSource() == helpButton)) {
                    String helpText = helpMap.get(e.getSource());
                    if (helpText != null) {
                        //Setup help prompt
                        prompt.setTitle("Help");
                        prompt.setContents(helpText);
                        prompt.showDialog();
                    }
                    //Restore help button to ?
                    activeHelp = false;
                    helpButton.setText("?");
                    statusLabel.setText(currentStatus);
                }
            }
        }
    }

    /**
     * This class provides the StatusClearRunner which clears the status after a certain amount of time has elapsed.
     *
     * @author Alfred Manville
     */
    private class StatusClearRunner implements Runnable {
        @Override
        public void run() {
            try {
                Thread.sleep(statusTimeout);
            } catch (InterruptedException e) {
            }
            currentStatus = "";
            SwingUtilities.invokeLater(() -> {
                if (activeExtended) {
                    activeExtended = false;
                    helpButton.setText("?");
                }
                if (!activeHelp) statusLabel.setText("");
            });
        }
    }
}
