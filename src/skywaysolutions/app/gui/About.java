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

}
