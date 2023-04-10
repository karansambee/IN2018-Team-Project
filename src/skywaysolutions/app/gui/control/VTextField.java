package skywaysolutions.app.gui.control;

import skywaysolutions.app.utils.CheckedException;
import skywaysolutions.app.utils.Time;

import javax.swing.*;
import java.awt.event.*;
import java.time.temporal.TemporalUnit;
import java.util.Date;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Provides a text field with regex validation support.
 *
 * @author Alfred Manville
 */
public class VTextField extends JTextField {
    protected Pattern pattern;
    protected FocusAdapter focusAdapter;
    protected ActionListener actionListener;
    protected Date lastInvokeTime;
    private final Object slock = new Object();

    /**
     * Set-ups the text field for the specified pattern using the specified status bar, invalid message,
     * if it should be cleared and if it should be focused.
     *
     * @param regex The regex to use.
     * @param statusBar The status bar to show the invalid message on, null to not show one.
     * @param invalidMessage The invalid message to show, null to not show one.
     * @param clear Whether to clear the field after an invalid entry.
     * @param focus Whether to focus the field after an invalid entry.
     * @throws CheckedException A {@link PatternSyntaxException} has occurred and was wrapped.
     */
    public void setup(String regex, StatusBar statusBar, String invalidMessage, boolean clear, boolean focus) throws CheckedException {
        synchronized (slock) {
            if (focusAdapter == null) {
                focusAdapter = new FocusAdapter() {
                    @Override
                    public void focusLost(FocusEvent e) {
                        invokeInvalidProcessing(statusBar, invalidMessage, clear, focus);
                    }
                };
                addFocusListener(focusAdapter);
            }
            if (actionListener == null) {
                actionListener = e -> invokeInvalidProcessing(statusBar, invalidMessage, clear, focus);
                addActionListener(actionListener);
            }
        }
        try {
            pattern = Pattern.compile(regex);
        } catch (PatternSyntaxException e) {
            throw new CheckedException(e);
        }
    }

    protected void invokeInvalidProcessing(StatusBar statusBar, String invalidMessage, boolean clear, boolean focus) {
        if (pattern != null && !pattern.matcher(getText()).matches()) {
            if (statusBar != null && invalidMessage != null) statusBar.setStatus(invalidMessage, "", 2500);
            if (clear) setText("");
            if (focus && (lastInvokeTime == null || lastInvokeTime.toInstant().plusSeconds(1).getEpochSecond() < Time.now().toInstant().getEpochSecond())) {
                lastInvokeTime = Time.now();
                requestFocusInWindow();
            }
        }
    }

    /**
     * Gets if the passed input or current value of the field matches the regex of the text field.
     *
     * @param input The input to check or null to check the current value of the field.
     * @return If the input matches the regex.
     */
    public boolean matches(String input) {
        return pattern != null && pattern.matcher((input == null) ? getText() : input).matches();
    }
}
