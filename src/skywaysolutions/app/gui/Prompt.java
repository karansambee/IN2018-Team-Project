package skywaysolutions.app.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Prompt extends JDialog {
    private JPanel Root;
    private JTextArea contentsTextArea;
    private JPanel buttonPanel;
    private JButton[] buttons;
    private String lastButton;
    private final boolean reusable;

    public Prompt(Frame owner, String title, String contents, String[] buttons, int defaultButtonIndex, boolean reusable) {
        super(owner, title, true);
        setContentPane(Root);
        setResizable(true);
        setButtons(buttons, defaultButtonIndex);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.reusable = reusable;
        setContents(contents);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                lastButton = null;
                nshow();
            }
        });
        pack();
    }

    public void setContents(String contents) {
        contentsTextArea.setText(contents);
    }

    public void setButtons(String[] buttons, int defaultButtonIndex) {
        lastButton = null;
        buttonPanel.removeAll();
        if (buttons == null) return;
        GridBagConstraints g = new GridBagConstraints();
        this.buttons = new JButton[buttons.length];
        int x = 0;
        for (String c : buttons) {
            g.gridy = 0;
            g.gridx = x++;
            g.fill = GridBagConstraints.BOTH;
            g.weightx = 1.0F / buttons.length;
            g.weighty = 1.0F;
            this.buttons[x-1] = new JButton(c);
            this.buttons[x-1].addActionListener(e -> {
                if (e.getSource() instanceof JButton d) lastButton = d.getText();
                nshow();
            });
            buttonPanel.add(this.buttons[x-1], g);
        };
        if (defaultButtonIndex > -1 && defaultButtonIndex < buttons.length) getRootPane().setDefaultButton(this.buttons[defaultButtonIndex]);
    }

    public String getLastButton() {
        return lastButton;
    }

    public void nshow() {
        if (reusable) setVisible(false); else dispose();
    }
}
