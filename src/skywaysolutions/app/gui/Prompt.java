package skywaysolutions.app.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * This class provides a prompt that can have an arbitrarily defined set of buttons.
 *
 * @author Alfred Manville
 */
public class Prompt extends JDialogx {
    private JPanel Root;
    private JTextArea contentsTextArea;
    private JPanel buttonPanel;
    private String lastButton;

    /**
     * Creates a new Prompt with the specified owner and if reusable.
     *
     * @param owner  The owner of the dialog or null.
     * @param reusable If the form can be relaunched after being hidden.
     */
    public Prompt(Window owner, boolean reusable) {
        this(owner, "", "", null, -1, reusable);
    }

    /**
     * Creates a new Prompt with the specified owner, title, contents, button labels, default button index and if reusable.
     *
     * @param owner The owner of the dialog or null.
     * @param title The title of the prompt.
     * @param contents The contents of the prompt.
     * @param buttons The labels of the buttons the prompt has.
     * @param defaultButtonIndex The index of the default button (Based on the array of the provided labels) or -1 for no default button.
     * @param reusable If the form can be relaunched after being hidden.
     */
    public Prompt(Window owner, String title, String contents, String[] buttons, int defaultButtonIndex, boolean reusable) {
        super(owner, title, reusable);
        //Setup form
        setContentPane(Root);
        setResizable(true);
        setButtons(buttons, defaultButtonIndex);
        setContents(contents);
        //Setup window events
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                lastButton = null;
                hideDialog();
            }
        });
        //Finalize form
        pack();
        setMinimumSize(Root.getMinimumSize());
        dsize = getSize();
    }

    /**
     * Sets the contents of the prompt.
     *
     * @param contents The contents of the prompt.
     */
    public void setContents(String contents) {
        contentsTextArea.setText(contents);
        contentsTextArea.setCaretPosition(0);
    }

    /**
     * Sets the button labels available on buttons in the prompt.
     *
     * @param buttons The labels of the buttons the prompt has.
     * @param defaultButtonIndex The index of the default button (Based on the array of the provided labels) or -1 for no default button.
     */
    public void setButtons(String[] buttons, int defaultButtonIndex) {
        //Clear previous buttons
        lastButton = null;
        buttonPanel.removeAll();
        if (buttons == null) return;
        //Create new buttons based on labels with each button proportional in size to all others in its row
        GridBagConstraints g = new GridBagConstraints();
        JButton[] jbuttons = new JButton[buttons.length];
        int x = 0;
        for (String c : buttons) {
            g.gridy = 0;
            g.gridx = x++;
            g.fill = GridBagConstraints.BOTH;
            g.weightx = 1.0F / buttons.length;
            g.weighty = 1.0F;
            jbuttons[x-1] = new JButton(c);
            jbuttons[x-1].addActionListener(e -> {
                if (e.getSource() instanceof JButton d) lastButton = d.getText();
                hideDialog();
            });
            buttonPanel.add(jbuttons[x-1], g);
        }
        //Sets the default index of the button
        if (defaultButtonIndex > -1 && defaultButtonIndex < buttons.length) getRootPane().setDefaultButton(jbuttons[defaultButtonIndex]); else getRootPane().setDefaultButton(null);
    }

    /**
     * Gets the label of the last clicked button or null.
     *
     * @return The label of the last clicked button or null for no button pressed.
     */
    public String getLastButton() {
        return lastButton;
    }
}
