package skywaysolutions.app.gui;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * This provides the about dialog.
 *
 * @author Alfred Manville
 */
public class About extends JDialogx {
    private JPanel Root;
    private JPanel panelHeader;
    private JButton buttonClose;
    private JTextArea textAreaDescription;
    private JTextArea textAreaLicense;

    /**
     * Constructs a new instance of About with the specified parent, if the control can be reshown, description and license.
     *
     * @param owner       The parent window of this dialog.
     * @param reusable    If the dialog can be reshown.
     * @param description The description of the program.
     * @param license     The license of the program.
     */
    public About(Window owner, boolean reusable, String description, String license) {
        super(owner, "About", reusable);
        //Setup form contents
        setContentPane(Root);
        getRootPane().setDefaultButton(buttonClose);
        //Setup form closing events
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                hideDialog();
            }
        });
        //Setup textbox contents
        textAreaDescription.setText(description);
        textAreaLicense.setText(license);
        //Setup button events
        buttonClose.addActionListener(e -> hideDialog());
        //Finalize form
        pack();
        dsize = getSize();
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
        Root.setMinimumSize(new Dimension(400, 300));
        Root.setPreferredSize(new Dimension(400, 300));
        panelHeader = new JPanel();
        panelHeader.setLayout(new GridBagLayout());
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 0.3;
        gbc.fill = GridBagConstraints.BOTH;
        Root.add(panelHeader, gbc);
        final JScrollPane scrollPane1 = new JScrollPane();
        scrollPane1.setHorizontalScrollBarPolicy(31);
        scrollPane1.setVerticalScrollBarPolicy(22);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.5;
        gbc.weighty = 0.6;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 2, 0, 2);
        Root.add(scrollPane1, gbc);
        scrollPane1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-4473925)), "Description:", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        textAreaDescription = new JTextArea();
        textAreaDescription.setEditable(false);
        textAreaDescription.setLineWrap(true);
        textAreaDescription.setWrapStyleWord(true);
        scrollPane1.setViewportView(textAreaDescription);
        final JScrollPane scrollPane2 = new JScrollPane();
        scrollPane2.setHorizontalScrollBarPolicy(31);
        scrollPane2.setVerticalScrollBarPolicy(22);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.5;
        gbc.weighty = 0.6;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 2, 0, 2);
        Root.add(scrollPane2, gbc);
        scrollPane2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "License:", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        textAreaLicense = new JTextArea();
        textAreaLicense.setEditable(false);
        textAreaLicense.setLineWrap(true);
        textAreaLicense.setWrapStyleWord(true);
        scrollPane2.setViewportView(textAreaLicense);
        buttonClose = new JButton();
        buttonClose.setText("Close");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 0.1;
        gbc.fill = GridBagConstraints.BOTH;
        Root.add(buttonClose, gbc);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return Root;
    }
}
