package skywaysolutions.app.database;

import skywaysolutions.app.utils.CheckedException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provides a base class that is used by all database table managers.
 *
 * @author Alfred Manville
 * @param <T> The DatabaseEntityBase class that is being stored in the table.
 */
public abstract class DatabaseTableBase<T extends DatabaseEntityBase> {
    private final Object slock = new Object();
    private boolean _lock;
    protected final IDB_Connector conn;

    /**
     * Constructs a new DatabaseTableBase with the specified connection.
     *
     * @param conn The connection to use.
     */
    public DatabaseTableBase(IDB_Connector conn) {
        this.conn = conn;
    }

    /**
     * This should return the name of the table.
     *
     * @return The name of the table.
     */
    protected abstract String getTableName();

    /**
     * This returns the auxiliary table name.
     *
     * @return The name of the auxiliary table.
     */
    protected final String getAuxTableName() {
        return "AUX_" + getTableName();
    }

    /**
     * This loads one instance of {@link T} from the current result set.
     * DO NOT call {@link ResultSet#next()}.
     * <p>
     * This means that the class extending {@link DatabaseEntityBase} should have a
     * constructor that takes a {@link IDB_Connector} and {@link java.sql.ResultSet}
     * to allow for a direct load to occur.
     * </p>
     *
     * @param rs The result set to use for loading.
     * @param locked If the object is currently locked.
     * @return An instance of {@link T}.
     * @throws CheckedException An error has occurred.
     */
    protected abstract T loadOneFrom(ResultSet rs, boolean locked) throws CheckedException;

    /**
     * This creates one instance of {@link T} using the ID from the current result set without loading.
     * DO NOT call {@link ResultSet#next()}.
     * <p>
     * This means that the class extending {@link DatabaseEntityBase} should have a
     * constructor that takes a {@link IDB_Connector} and {@link java.sql.ResultSet}
     * to allow for a direct load to occur.
     * </p>
     *
     * @param rs The result set to use for loading.
     * @return An instance of {@link T}.
     * @throws CheckedException An error has occurred.
     */
    protected abstract T noLoadOneFrom(ResultSet rs) throws CheckedException;

    /**
     * Gets the table schema (The bit that's located between the brackets).
     * For: "CREATE TABLE test (id int, test varchar(255))"
     * Becomes: "id int, test varchar(255)"
     *
     * @return The table schema portion between the brackets.
     */
    protected abstract String getTableSchema();

    /**
     * Gets the table auxiliary schema (The bit that's located between the brackets).
     * This is only has the ID column corresponding to the main table.
     * For Main Table: "CREATE TABLE test (id int, test varchar(255))"
     * For Aux Table: "CREATE TABLE test (id int)"
     * Becomes: "id int"
     *
     * @return The aux table schema portion between the brackets.
     */
    protected abstract String getAuxTableSchema();

    /**
     * Makes sure the main and aux tables have been created.
     *
     * @throws CheckedException An error occurs.
     */
    public final void assureTableSchema() throws CheckedException {
        synchronized (slock) {
            List<String> tblList = conn.getTableList(false);
            //If table does not exist, create it
            if (!tblList.contains(getTableName())) {
                try(PreparedStatement sta = conn.getStatement("CREATE TABLE "+getTableName()+" ("+getTableSchema()+")")) {
                    sta.executeUpdate();
                } catch (SQLException e) {
                    throw new CheckedException(e);
                }
            }
            //If aux table does not exist, create it
            if (!tblList.contains(getAuxTableName())) {
                try(PreparedStatement sta = conn.getStatement("CREATE TABLE "+getAuxTableName()+" ("+getAuxTableSchema()+")")) {
                    sta.executeUpdate();
                } catch (SQLException e) {
                    throw new CheckedException(e);
                }
            }
        }
    }

    /**
     * Drops the main and aux tables.
     *
     * @throws CheckedException An error occurs.
     */
    public final void purgeTableSchema() throws CheckedException {
        synchronized (slock) {
            List<String> tblList = conn.getTableList(false);
            //If table exists, drop it
            if (tblList.contains(getTableName())) {
                try(PreparedStatement sta = conn.getStatement("DROP TABLE "+getTableName())) {
                    sta.executeUpdate();
                } catch (SQLException e) {
                    throw new CheckedException(e);
                }
            }
            //If aux table exists, drop it
            if (tblList.contains(getAuxTableName())) {
                try(PreparedStatement sta = conn.getStatement("DROP TABLE "+getAuxTableName())) {
                    sta.executeUpdate();
                } catch (SQLException e) {
                    throw new CheckedException(e);
                }
            }
        }
    }

    /**
     * Loads many {@link T}s from the main table, allowing for a filter provided by {@link IFilterStatementCreator}.
     * Also has a {@link MultiLoadSyncMode} to define the object locking behaviour during this retrieval.
     *
     * @param filter The filter statement builder that extends the statement passed to it and returns a fully parametrized {@link PreparedStatement}.
     * @param syncMode The locking behaviour during retrieval.
     * @return A list of loaded {@link T}.
     * @throws CheckedException An error occurs.
     */
    public final List<T> loadMany(IFilterStatementCreator filter, MultiLoadSyncMode syncMode) throws CheckedException {
        assureTableSchema();
        if (syncMode != MultiLoadSyncMode.NoLockBeforeLoad && syncMode != MultiLoadSyncMode.NoLoad) lockAll();
        List<T> toReturn = new ArrayList<>();
        synchronized (slock) {
            if ((!_lock) && (syncMode == MultiLoadSyncMode.UnlockAfterLoad || syncMode == MultiLoadSyncMode.KeepLockedAfterLoad)) throw new CheckedException("Lock not applied");
            try(PreparedStatement sta = filter.createFilteredStatementFor(conn, "SELECT * FROM "+getTableName()+" WHERE ")) {
                try (ResultSet rs = sta.executeQuery()) {
                    while (rs.next())
                        toReturn.add((syncMode == MultiLoadSyncMode.NoLoad) ? noLoadOneFrom(rs) : loadOneFrom(rs, _lock));
                }
            } catch (SQLException e) {
                throw new CheckedException(e);
            }
        }
        if (syncMode == MultiLoadSyncMode.UnlockAfterLoad) unlockAll(false);
        return toReturn;
    }

    /**
     * Locks all the rows.
     * DO NOT forget to {@link #unlockAll(boolean)} once finished.
     *
     * @throws CheckedException An error occurs.
     */
    public final void lockAll() throws CheckedException {
        assureTableSchema();
        synchronized (slock) {
            if (_lock) return;
            int rc = 0; //Get the number of rows in the table
            try(PreparedStatement sta = conn.getStatement("SELECT COUNT(*) as rowCount FROM "+getTableName())) {
                try (ResultSet rs = sta.executeQuery()) {
                    rc = rs.getInt("rowCount");
                }
            } catch (SQLException e) {
                throw new CheckedException(e);
            }
            int rca = 0; //Get the number of rows in the aux table
            try(PreparedStatement sta = conn.getStatement("SELECT COUNT(*) as rowCount FROM "+getAuxTableName())) {
                try (ResultSet rs = sta.executeQuery()) {
                    rca = rs.getInt("rowCount");
                }
            } catch (SQLException e) {
                throw new CheckedException(e);
            }
            //If the number of rows are not the same, do not attempt to lock (Yes this is not exactly atomic but oh well)
            if (rc != rca) throw new CheckedException("Lock could not be obtained");
            try(PreparedStatement sta = conn.getStatement("DELETE FROM "+getAuxTableName())) {
                sta.executeUpdate();
            } catch (SQLException e) {
                throw new CheckedException("Lock could not be obtained");
            }
            _lock = true;
        }
    }

    /**
     * This should insert all the aux rows.
     * Select all the IDs from the main table to get what to insert.
     * Use {@link IDB_Connector#getStatement(String)} to get a {@link java.sql.PreparedStatement}.
     * ^ Don't forget to use the try([resource]) {}
     * Use {@link #getTableName()} to get the table name for selection.
     * Use {@link #getAuxTableName()} to get the table name for insertion.
     * DO NOT use any public functions provided by {@link DatabaseTableBase} in here otherwise a deadlock could occur.
     *
     * @throws CheckedException An error has occurred.
     */
    protected abstract void createAllAuxRows() throws CheckedException;

    /**
     * Inserts all the aux rows of a specified column name that are longs.
     *
     * @param columnName The name of the ID column.
     * @throws CheckedException An error has occurred.
     */
    protected void createAllAuxRowsLongID(String columnName) throws CheckedException {
        ArrayList<Long> IDs = new ArrayList<>();
        try(PreparedStatement sta = conn.getStatement("SELECT "+columnName+" FROM " + getTableName())) {
            try (ResultSet rs = sta.executeQuery()) {
                while (rs.next()) IDs.add(rs.getLong(columnName));
            }
        } catch (SQLException throwables) {
            throw new CheckedException(throwables);
        }
        try(PreparedStatement sta = conn.getStatement("INSERT INTO " + getAuxTableName() + " VALUES (?)")) {
            for (long c : IDs) {
                sta.setLong(1, c);
                sta.addBatch();
            }
            sta.executeBatch();
        } catch (SQLException throwables) {
            throw new CheckedException(throwables);
        }
    }

    /**
     * Inserts all the aux rows of a specified column name that are strings.
     *
     * @param columnName The name of the ID column.
     * @throws CheckedException An error has occurred.
     */
    protected void createAllAuxRowsStringID(String columnName) throws CheckedException {
        ArrayList<String> IDs = new ArrayList<>();
        try(PreparedStatement sta = conn.getStatement("SELECT "+columnName+" FROM " + getTableName())) {
            try (ResultSet rs = sta.executeQuery()) {
                while (rs.next()) IDs.add(rs.getString(columnName));
            }
        } catch (SQLException throwables) {
            throw new CheckedException(throwables);
        }
        try(PreparedStatement sta = conn.getStatement("INSERT INTO " + getAuxTableName() + " VALUES (?)")) {
            for (String c : IDs) {
                sta.setString(1, c);
                sta.addBatch();
            }
            sta.executeBatch();
        } catch (SQLException throwables) {
            throw new CheckedException(throwables);
        }
    }

    /**
     * Unlocks all the rows allowing them to be used by other connections.
     * See: {@link #lockAll()}
     *
     * @param force Force the unlock.
     * @throws CheckedException An error occurs.
     */
    public final void unlockAll(boolean force) throws CheckedException {
        assureTableSchema();
        synchronized (slock) {
            if (!_lock && !force) throw new CheckedException("Lock not applied");
            try {
                createAllAuxRows();
            } catch (CheckedException e) {
                throw new CheckedException("Lock could not be released");
            }
            _lock = false;
        }
    }

    /**
     * Deletes all the rows.
     * {@link #isAllLocked()} should be true.
     *
     * @throws CheckedException An error occurs.
     */
    public final void deleteAll() throws CheckedException {
        assureTableSchema();
        synchronized (slock) {
            if (!_lock) throw new CheckedException("Lock not applied");
            try(PreparedStatement sta = conn.getStatement("DELETE FROM "+getTableName())) {
                sta.executeUpdate();
            } catch (SQLException e) {
                throw new CheckedException(e);
            }
            try(PreparedStatement sta = conn.getStatement("DELETE FROM "+getAuxTableName())) {
                sta.executeUpdate();
            } catch (SQLException e) {
                throw new CheckedException(e);
            }
            _lock = false;
        }
    }

    /**
     * Returns if all the rows have been locked.
     *
     * @return If all the rows are locked.
     */
    public final boolean isAllLocked() {
        return _lock;
    }
}
