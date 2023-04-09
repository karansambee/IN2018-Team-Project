package skywaysolutions.app.gui.tab;

import skywaysolutions.app.gui.Prompt;
import skywaysolutions.app.gui.control.StatusBar;
import skywaysolutions.app.utils.AccessorManager;
import skywaysolutions.app.utils.CheckedException;

import java.awt.*;

/**
 * Provides a standard tab interface for Main.
 *
 * @author Alfred Manville
 */
public interface ITab {
    /**
     * Set-ups the tab with the specified owner, prompt, status bar and accessor manager.
     *
     * @param owner The parent window the control is contained on.
     * @param prompt The prompt to use.
     * @param statusBar The status bar to use.
     * @param manager The accessor manager to use.
     */
    void setup(Window owner, Prompt prompt, StatusBar statusBar, AccessorManager manager);

    /**
     * Refreshes the tab's contents.
     */
    void refresh();

    /**
     * If the tab can be accessed by the current, logged in, account.
     *
     * @return If the tab is accessible.
     * @throws CheckedException An error has occurred.
     */
    boolean accessAllowed() throws CheckedException;

    /**
     * Gets the caption of the tab.
     *
     * @return The caption.
     */
    String getCaption();
}
