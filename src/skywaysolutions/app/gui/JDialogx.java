package skywaysolutions.app.gui;

import javax.swing.*;
import java.awt.*;

/**
 * This provides a JDialog bas class that supports being reusable.
 *
 * @author Alfred Manville
 */
public abstract class JDialogx extends JDialog {
    protected final boolean reusable;
    protected boolean shown;
    protected Dimension dsize;

    /**
     * Creates a new instance of the base class with the specified owner, title and if reusable.
     *
     * @param owner The owner of the dialog or null.
     * @param title The title of the dialog.
     * @param reusable If the dialog can be shown again after hiding.
     */
    public JDialogx(Window owner, String title, boolean reusable) {
        super(owner, title, Dialog.DEFAULT_MODALITY_TYPE);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.reusable = reusable;
        setResizable(false);
    }

    /**
     * Centers the dialog.
     *
     * @param useParent Whether to center in parent.
     */
    protected void centerDialog(boolean useParent) {
        if (useParent && getOwner() != null && (getOwner() instanceof JFrame || getOwner() instanceof JDialog) && getOwner().isShowing()) {
            int x = (Math.abs(getOwner().getSize().width - getSize().width) / 2) + getParent().getLocationOnScreen().x;
            int y = (Math.abs(getOwner().getSize().height - getSize().height) / 2) + getParent().getLocationOnScreen().y;
            setLocation(x, y);
        } else setLocationRelativeTo(null);
    }

    /**
     * Shows the dialog, will not show again if shown once and not reusable.
     */
    public void showDialog() {
        if (reusable || !shown) {
            setSize(dsize);
            centerDialog(true);
            setVisible(true);
        }
    }

    /**
     * Hides the dialog.
     */
    public void hideDialog() {
        if (reusable) setVisible(false);
        else if (!shown) dispose();
        shown = true;
    }

    /**
     * Was shown and hidden once.
     *
     * @return If the dialog has been shown and hidden at least once.
     */
    public final boolean wasShown() {
        return shown;
    }

    /**
     * Is the dialog reusable.
     *
     * @return The dialog is reusable.
     */
    public final boolean isReusable() {
        return reusable;
    }

    /**
     * Swaps the control in the provided panel with another control.
     *
     * @param panel The panel.
     * @param controlA One of the controls.
     * @param controlB Another of the controls.
     * @param target The target control to insert, however if neither control is in the panel controlA will be placed, null to swap.
     */
    protected void swapControl(JPanel panel, Component controlA, Component controlB, Component target) {
        if (controlA == null || controlB == null) return;
        Component[] components = panel.getComponents();
        Component contained = null;
        for (Component c : components) {
            if (c == controlA) {
                contained = controlA;
                break;
            }
            if (c == controlB) {
                contained = controlB;
                break;
            }
        }
        if (target != null && contained == target) return;
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.weighty = 1;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        if (contained == null || contained == controlB) {
            if (contained == controlB) panel.remove(controlB);
            panel.add(controlA, constraints);
        } else {
            panel.remove(controlA);
            panel.add(controlB, constraints);
        }
    }
}
