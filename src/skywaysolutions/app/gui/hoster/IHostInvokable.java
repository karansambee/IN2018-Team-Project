package skywaysolutions.app.gui.hoster;

import skywaysolutions.app.utils.CheckedException;

/**
 * This interface is used for hosting code that can be called using a {@link HostRunner}.
 *
 * @author Alfred Manville
 */
public interface IHostInvokable {
    /**
     * Invokes using the specified command ID and arguments.
     *
     * @param id The command ID.
     * @param args The arguments.
     * @throws CheckedException An error has occurred.
     */
    void invoke(String id, Object[] args) throws CheckedException;
}
