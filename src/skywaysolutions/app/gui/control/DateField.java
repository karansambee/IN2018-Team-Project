package skywaysolutions.app.gui.control;

import skywaysolutions.app.utils.CheckedException;
import skywaysolutions.app.utils.Time;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.Calendar;
import java.util.Date;

/**
 * This class provides a date field control.
 *
 * @author Alfred Manville
 */
public class DateField extends JPanel {
    private JPanel Root;
    private VTextField textFieldDate;
    private JButton buttonGetDate;

    private Date lower = new Date(0);
    private Date upper = new Date(Long.MAX_VALUE);
    private Date value;

    /**
     * Constructs a new instance of the DateField control.
     */
    public DateField() {
        super(true);
        buttonGetDate.addActionListener(e -> {
            setValue(Time.now());
            processEntry(e);
        });
        textFieldDate.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                processEntry(new ActionEvent(textFieldDate, ActionEvent.ACTION_PERFORMED, "", Time.now().toInstant().getEpochSecond(), 0));
            }
        });
        textFieldDate.addActionListener(this::processEntry);
    }

    /**
     * Set-up the DateField control to use the specified status bar.
     *
     * @param statusBar The status bar to use.
     * @throws CheckedException Initializing the text field has failed.
     */
    public void setup(StatusBar statusBar) throws CheckedException {
        textFieldDate.setup("([0-9]{4})\\-([0-9]{1,2})\\-([0-9]{1,2})", statusBar, "Invalid Date Entered", true, true);
        //Setup help
        statusBar.registerComponentForHelp(textFieldDate, "Date format is yyyy-mm-dd.\nBut yyyy-m-dd, yyyy-mm-d, yyyy-m-d are also supported.");
        statusBar.registerComponentForHelp(buttonGetDate, "Gets today's date.");
    }

    private void processEntry(ActionEvent event) {
        String[] splt = textFieldDate.getText().split("\\-");
        if (splt.length == 3) {
            try {
                Calendar cal = Calendar.getInstance();
                cal.set(Integer.parseInt(splt[0]), Integer.parseInt(splt[1])-1, Integer.parseInt(splt[2]));
                value = cal.getTime();
                fireActionPerformed(event);
            } catch (NumberFormatException ex) {
            }
        }
    }

    /**
     * Sets the value of the date field.
     *
     * @param date The date to set the field to.
     */
    public void setValue(Date date) {
        if (date.before(lower)) value = lower;
        else if (date.after(upper)) value = upper;
        else value = date;
        Calendar cal = Time.getCalendar(date);
        textFieldDate.setText(cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.DAY_OF_MONTH));
    }

    /**
     * Gets the value of the date field.
     *
     * @return The value of the date field.
     */
    public Date getValue() {
        return value;
    }

    /**
     * Sets the upper bound of the date control.
     *
     * @param date The upper bound of the date control.
     */
    public void setUpper(Date date) {
        if (date.before(lower)) upper = lower;
        else upper = date;
    }

    /**
     * Gets the upper bound of the date control.
     *
     * @return The upper bound of the date control.
     */
    public Date getUpper() {
        return upper;
    }

    /**
     * Sets the lower bound of the date control.
     *
     * @param date The lower bound of the date control.
     */
    public void setLower(Date date) {
        if (date.after(upper)) lower = upper;
        else lower = date;
    }

    /**
     * Gets the lower bound of the date control.
     *
     * @return The lower bound of the date control.
     */
    public Date getLower() {
        return lower;
    }

    /**
     * Adds an ActionListener to the field.
     * Actions get fired when the field is updated by the user.
     *
     * @param l the ActionListener to be added
     */
    public void addActionListener(ActionListener l) {
        listenerList.add(ActionListener.class, l);
    }

    /**
     * Removes an ActionListener from the field.
     *
     * @param l the listener to be removed
     */
    public void removeActionListener(ActionListener l) {
        listenerList.remove(ActionListener.class, l);
    }

    protected void fireActionPerformed(ActionEvent event) {
        Object[] listeners = listenerList.getListenerList();
        ActionEvent e = null;
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==ActionListener.class) {
                if (e == null) e = new ActionEvent(this,
                        ActionEvent.ACTION_PERFORMED,
                        event.getActionCommand(),
                        event.getWhen(),
                        event.getModifiers());
                ((ActionListener)listeners[i+1]).actionPerformed(e);
            }
        }
    }
}
