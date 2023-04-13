package skywaysolutions.app.gui;

import skywaysolutions.app.gui.control.StatusBar;
import skywaysolutions.app.staff.StaffRole;
import skywaysolutions.app.utils.AccessorManager;
import skywaysolutions.app.utils.CheckedException;
import skywaysolutions.app.utils.Decimal;
import skywaysolutions.app.utils.PersonalInformation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * This class provides a way to create and edit blank types.
 *
 * @author Alfred Manville
 */
public class BlankTypeEditor extends JDialogx {
    private JSpinner spinnerTypeCode;
    private JPanel panelTypeCode;
    private JTextArea textAreaDescription;
    private JButton buttonCancel;
    private StatusBar statusBar;
    private JPanel Root;
    private JButton buttonOk;
    private final JTextField textFieldRTypeCode;
    private final AccessorManager manager;
    private Integer id;

    /**
     * Constructs a new instance of AccountEditor with the specified owner, if reusable and the accessor manager instance.
     *
     * @param owner The window owner or null.
     * @param reusable If this dialog is reusable.
     * @param manager The accessor manager instance.
     */
    public BlankTypeEditor(Window owner, boolean reusable, AccessorManager manager) {
        super(owner, "", reusable);
        this.manager = manager;
        //Setup readonly textfields (Used as readonly controls that swap with controls that have to be disabled to be readonly)
        textFieldRTypeCode = new JTextField();
        textFieldRTypeCode.setEditable(false);
        //Setup form contents
        setContentPane(Root);
        //getRootPane().setDefaultButton(buttonOk);
        //Setup form closing events
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                hideDialog();
            }
        });
        //Setup controls and their events
        spinnerTypeCode.setModel(new SpinnerNumberModel(100, 100, 999, 1));
        buttonCancel.addActionListener(e -> {
            if (!statusBar.isInHelpMode()) hideDialog();
        });
        buttonOk.addActionListener(e -> {
            if (!statusBar.isInHelpMode()) {
                try {
                    //Create or update blank type information
                    if (id == null)
                        manager.stockAccessor.createBlankType(((Number) spinnerTypeCode.getValue()).intValue(), textAreaDescription.getText());
                    else
                        manager.stockAccessor.setBlankTypeDescription(id, textAreaDescription.getText());
                    hideDialog();
                } catch (CheckedException ex) {
                    statusBar.setStatus(ex, 2500);
                }
            }
        });
        //Finalize form
        pack();
        setMinimumSize(Root.getMinimumSize());
        dsize = getSize();
        statusBar.createPrompt(this);
    }

    private void updateInterfaceState() throws CheckedException {
        //Readonly blank type Code if not editable
        swapControl(panelTypeCode, spinnerTypeCode, textFieldRTypeCode, (id == null) ? spinnerTypeCode : textFieldRTypeCode);
    }

    /**
     * Sets the blank type ID being edited or null for creation.
     *
     * @param id The blank type ID or null.
     * @throws CheckedException A refresh error occurred.
     */
    public void setBlankTypeID(Integer id) throws CheckedException {
        this.id = id;
        if(id != null) manager.stockAccessor.refreshBlankType(id);
    }

    @Override
    public void showDialog() {
        if (reusable || !shown) {
            try {
                setTitle((id == null) ? "Blank Type Creator" : "Blank Type Editor");
                if (id == null) {
                    spinnerTypeCode.setValue(100);
                    textAreaDescription.setText("");
                } else {
                    spinnerTypeCode.setValue(id);
                    textFieldRTypeCode.setText(String.valueOf(id));
                    textAreaDescription.setText(manager.stockAccessor.getBlankTypeDescription(id));
                }
                updateInterfaceState();
            } catch (CheckedException e) {
                statusBar.setStatus(e, 2500);
            }
        }
        super.showDialog();
    }

    @Override
    public void hideDialog() {
        statusBar.deactivateHelp();
        super.hideDialog();
    }
}
