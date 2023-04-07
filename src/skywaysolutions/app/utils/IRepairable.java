package skywaysolutions.app.utils;

/**
 * This interface provides the ability to get the list of tables
 * to back up and unlock a specified table.
 *
 * @author Alfred Manville
 */
public interface IRepairable {
    /**
     * Gets an array of tables that can be backed up.
     *
     * @return The array of tables.
     */
    String[] getTables();

    /**
     * Forces a table to be fully unlocked.
     *
     * @param tableName The table to fully unlock.
     * @throws CheckedException The table could not be unlocked.
     */
    void forceFullUnlock(String tableName) throws CheckedException;

    /**
     * Forces a table to be deleted (Along with its auxiliary table).
     *
     * @param tableName The table to purge.
     * @throws CheckedException The table could not be purged.
     */
    void forceFullPurge(String tableName) throws CheckedException;
}
