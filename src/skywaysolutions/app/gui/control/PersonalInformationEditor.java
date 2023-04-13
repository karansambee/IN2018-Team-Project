package skywaysolutions.app.gui.control;

import skywaysolutions.app.utils.CheckedException;
import skywaysolutions.app.utils.PersonalInformation;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;

/**
 * This class provides a PersonalInformation editor control.
 *
 * @author Alfred Manville
 */
public class PersonalInformationEditor extends JPanel {
    private JPanel Root;
    private VTextField textFieldEmailAddress;
    private JTextField textFieldFirstName;
    private JTextField textFieldLastName;
    private VTextField textFieldPhoneNumber;
    private VTextField textFieldHouseNumber;
    private JTextField textFieldStreetName;
    private JLabel labelPostcode;
    private VTextField textFieldPostcode;
    private JLabel labelHouseNumber;
    private DateField dateFieldDateOfBirth;
    private JLabel labelPhoneNumber;
    private JLabel labelDateOfBirth;
    private PersonalInformation information;

    /**
     * Constructs a new instance of the PersonalInformationEditor control.
     */
    public PersonalInformationEditor() {
        super(true);
        //Setup events
        PIUpdater handler = new PIUpdater();
        textFieldEmailAddress.addFocusListener(handler);
        textFieldEmailAddress.addActionListener(handler);
        textFieldFirstName.addFocusListener(handler);
        textFieldFirstName.addActionListener(handler);
        textFieldLastName.addFocusListener(handler);
        textFieldLastName.addActionListener(handler);
        textFieldPhoneNumber.addFocusListener(handler);
        textFieldPhoneNumber.addActionListener(handler);
        textFieldHouseNumber.addFocusListener(handler);
        textFieldHouseNumber.addActionListener(handler);
        textFieldStreetName.addFocusListener(handler);
        textFieldStreetName.addActionListener(handler);
        textFieldPostcode.addFocusListener(handler);
        textFieldPostcode.addActionListener(handler);
        dateFieldDateOfBirth.addActionListener(handler);
    }

    /**
     * Set-ups the editor with the specified status bar and if the email field is editable.
     *
     * @param statusBar     The status bar.
     * @param emailEditable If the email field is editable.
     * @throws CheckedException Initializing the fields has failed.
     */
    public void setup(StatusBar statusBar, boolean emailEditable) throws CheckedException {
        textFieldEmailAddress.setEditable(emailEditable);
        textFieldEmailAddress.setup("[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)*[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", statusBar, "Invalid email address!", false, true);
        textFieldPhoneNumber.setup("(((\\+)?[0-9]{1,3}|0)(\s)?[0-9]([0-9]|\s)*|\\A\\z)", statusBar, "Invalid Phone Number!", false, true);
        textFieldHouseNumber.setup("[0-9]*", statusBar, "Invalid House Number Warning!", false, false);
        textFieldPostcode.setup("(([A-Z]{1,2}|[a-z]{1,2})[0-9]([A-Z0-9]|[a-z0-9])?\s?[0-9]([A-Z]|[a-z]){2}|\\A\\z)", statusBar, "Invalid Postcode Warning!", false, false);
        dateFieldDateOfBirth.setup(statusBar);
        //Setup help
        statusBar.registerComponentForHelp(labelDateOfBirth, "Date format is yyyy-mm-dd.\nBut yyyy-m-dd, yyyy-mm-d, yyyy-m-d are also supported.");
        statusBar.registerComponentForHelp(labelPhoneNumber, "The phone number format is the country code (That may include the +)\nFollowed by rest of the phone number.");
        statusBar.registerComponentForHelp(textFieldPhoneNumber, "The phone number format is the country code (That may include the +)\nFollowed by rest of the phone number.");
        statusBar.registerComponentForHelp(labelHouseNumber, "The house number is numeric but you could also enter a house name here and disregard the warning.");
        statusBar.registerComponentForHelp(textFieldHouseNumber, "The house number is numeric but you could also enter a house name here and disregard the warning.");
        statusBar.registerComponentForHelp(labelPostcode, "The post code uses UK validation but you could also enter another country format's postcode and disregard the warning.");
        statusBar.registerComponentForHelp(textFieldPostcode, "The post code uses UK validation but you could also enter another country format's postcode and disregard the warning.");
    }

    /**
     * Sets if the email field is editable.
     *
     * @param editable If the email field is editable.
     */
    public void setEmailEditable(boolean editable) {
        textFieldEmailAddress.setEditable(editable);
    }

    /**
     * Gets the held PersonalInformation.
     *
     * @return The held personal information.
     */
    public PersonalInformation getInformation() {
        return information;
    }

    /**
     * Sets the held PersonalInformation.
     *
     * @param information The new held personal information.
     */
    public void setInformation(PersonalInformation information) {
        this.information = information;
        textFieldEmailAddress.setText(information.getEmailAddress());
        textFieldFirstName.setText(information.getFirstName());
        textFieldLastName.setText(information.getLastName());
        textFieldPhoneNumber.setText(information.getPhoneNumber());
        dateFieldDateOfBirth.setValue(information.getDateOfBirth());
        textFieldHouseNumber.setText(information.getHouseNumber());
        textFieldStreetName.setText(information.getStreetName());
        textFieldPostcode.setText(information.getPostcode());
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
        Root.setPreferredSize(new Dimension(300, 200));
        Root.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-4473925)), "Personal Information", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.TOP, null, null));
        final JLabel label1 = new JLabel();
        label1.setPreferredSize(new Dimension(64, 16));
        label1.setText("Email Address:");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.4;
        gbc.weighty = 0.125;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        Root.add(label1, gbc);
        textFieldEmailAddress = new VTextField();
        textFieldEmailAddress.setPreferredSize(new Dimension(96, 30));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.6;
        gbc.weighty = 0.125;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        Root.add(textFieldEmailAddress, gbc);
        final JLabel label2 = new JLabel();
        label2.setPreferredSize(new Dimension(64, 16));
        label2.setText("First Name:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.4;
        gbc.weighty = 0.125;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        Root.add(label2, gbc);
        textFieldFirstName = new JTextField();
        textFieldFirstName.setPreferredSize(new Dimension(96, 30));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.6;
        gbc.weighty = 0.125;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        Root.add(textFieldFirstName, gbc);
        final JLabel label3 = new JLabel();
        label3.setPreferredSize(new Dimension(64, 16));
        label3.setText("Last Name:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.4;
        gbc.weighty = 0.125;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        Root.add(label3, gbc);
        textFieldLastName = new JTextField();
        textFieldLastName.setPreferredSize(new Dimension(96, 30));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 0.6;
        gbc.weighty = 0.125;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        Root.add(textFieldLastName, gbc);
        labelDateOfBirth = new JLabel();
        labelDateOfBirth.setPreferredSize(new Dimension(64, 16));
        labelDateOfBirth.setText("Date of Birth:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.4;
        gbc.weighty = 0.125;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        Root.add(labelDateOfBirth, gbc);
        labelPhoneNumber = new JLabel();
        labelPhoneNumber.setPreferredSize(new Dimension(64, 16));
        labelPhoneNumber.setText("Phone Number:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.4;
        gbc.weighty = 0.125;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        Root.add(labelPhoneNumber, gbc);
        textFieldPhoneNumber = new VTextField();
        textFieldPhoneNumber.setPreferredSize(new Dimension(96, 30));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weightx = 0.6;
        gbc.weighty = 0.125;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        Root.add(textFieldPhoneNumber, gbc);
        labelHouseNumber = new JLabel();
        labelHouseNumber.setPreferredSize(new Dimension(64, 16));
        labelHouseNumber.setText("House Number:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 0.4;
        gbc.weighty = 0.125;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        Root.add(labelHouseNumber, gbc);
        textFieldHouseNumber = new VTextField();
        textFieldHouseNumber.setPreferredSize(new Dimension(96, 30));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.weightx = 0.6;
        gbc.weighty = 0.125;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        Root.add(textFieldHouseNumber, gbc);
        final JLabel label4 = new JLabel();
        label4.setPreferredSize(new Dimension(64, 16));
        label4.setText("Street Name:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.weightx = 0.4;
        gbc.weighty = 0.125;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        Root.add(label4, gbc);
        textFieldStreetName = new JTextField();
        textFieldStreetName.setPreferredSize(new Dimension(96, 30));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.weightx = 0.6;
        gbc.weighty = 0.125;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        Root.add(textFieldStreetName, gbc);
        labelPostcode = new JLabel();
        labelPostcode.setPreferredSize(new Dimension(64, 16));
        labelPostcode.setText("Postcode:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.weightx = 0.4;
        gbc.weighty = 0.125;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        Root.add(labelPostcode, gbc);
        textFieldPostcode = new VTextField();
        textFieldPostcode.setPreferredSize(new Dimension(96, 30));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.weightx = 0.6;
        gbc.weighty = 0.125;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        Root.add(textFieldPostcode, gbc);
        dateFieldDateOfBirth = new DateField();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 0.6;
        gbc.weighty = 0.125;
        gbc.fill = GridBagConstraints.BOTH;
        Root.add(dateFieldDateOfBirth.$$$getRootComponent$$$(), gbc);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return Root;
    }

    /**
     * This class provides listeners for settings the values of the contained PersonalInformation class.
     *
     * @author Alfred Manville
     */
    private class PIUpdater implements FocusListener, ActionListener {

        /**
         * Invoked when an action occurs.
         *
         * @param e the event to be processed
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == textFieldEmailAddress) setEmailAddress();
            else if (e.getSource() == textFieldFirstName) setFirstName();
            else if (e.getSource() == textFieldLastName) setLastName();
            else if (e.getSource() == textFieldPhoneNumber) setPhoneNumber();
            else if (e.getSource() == dateFieldDateOfBirth) setDateOfBirth();
            else if (e.getSource() == textFieldHouseNumber) setHouseNumber();
            else if (e.getSource() == textFieldStreetName) setStreetName();
            else if (e.getSource() == textFieldPostcode) setPostcode();
        }

        /**
         * Invoked when a component gains the keyboard focus.
         *
         * @param e the event to be processed
         */
        @Override
        public void focusGained(FocusEvent e) {
        }

        /**
         * Invoked when a component loses the keyboard focus.
         *
         * @param e the event to be processed
         */
        @Override
        public void focusLost(FocusEvent e) {
            if (e.getSource() == textFieldEmailAddress) setEmailAddress();
            else if (e.getSource() == textFieldFirstName) setFirstName();
            else if (e.getSource() == textFieldLastName) setLastName();
            else if (e.getSource() == textFieldPhoneNumber) setPhoneNumber();
            else if (e.getSource() == textFieldHouseNumber) setHouseNumber();
            else if (e.getSource() == textFieldStreetName) setStreetName();
            else if (e.getSource() == textFieldPostcode) setPostcode();
        }

        private void setEmailAddress() {
            if (textFieldEmailAddress.matches(null)) information.setEmailAddress(textFieldEmailAddress.getText());
        }

        private void setFirstName() {
            information.setFirstName(textFieldFirstName.getText());
        }

        private void setLastName() {
            information.setLastName(textFieldLastName.getText());
        }

        private void setPhoneNumber() {
            if (textFieldPhoneNumber.matches(null)) information.setPhoneNumber(textFieldPhoneNumber.getText());
        }

        private void setDateOfBirth() {
            information.setDateOfBirth(dateFieldDateOfBirth.getValue());
        }

        private void setHouseNumber() {
            information.setHouseNumber(textFieldHouseNumber.getText());
        }

        private void setStreetName() {
            information.setStreetName(textFieldStreetName.getText());
        }

        private void setPostcode() {
            information.setPostcode(textFieldPostcode.getText());
        }
    }
}
