package skywaysolutions.app.database;

/**
 * This enum provides the loading modes for multi load support in {@link DatabaseTableBase}.
 *
 * @author Alfred Manville
 */
public enum MultiLoadSyncMode {
    /**
     * Obtains the records ignoring lock state.
     */
    NoLockBeforeLoad,
    /**
     * Unlocks the records once loading is complete.
     */
    UnlockAfterLoad,
    /**
     * Keeps records locked after load.
     */
    KeepLockedAfterLoad,
    /**
     * Does not load the records (Only loads the IDs).
     */
    NoLoad
}
