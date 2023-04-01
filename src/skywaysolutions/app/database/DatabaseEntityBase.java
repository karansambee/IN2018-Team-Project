package skywaysolutions.app.database;

import skywaysolutions.app.utils.CheckedException;

import java.sql.ResultSet;

/**
 * This class provides a base class that is used by all database stored entities.
 * Any extending classes should also have a constructor that takes a {@link IDB_Connector} and
 * {@link java.sql.ResultSet} as this should be used by the corresponding extender of {@link DatabaseTableBase#loadOneFrom(ResultSet, boolean)} and
 * {@link DatabaseTableBase#noLoadOneFrom(ResultSet)}. This constructor should also contain a {@link #setLoadedAndExists()} call and use the
 * {@link #DatabaseEntityBase(IDB_Connector, boolean)} should be used for the super constructor.
 *
 * @author Alfred Manville
 */
public abstract class DatabaseEntityBase {
    private final Object slock = new Object();
    private boolean _lock;
    private boolean _exists;
    private boolean _loaded;
    protected final IDB_Connector conn;

    /**
     * Constructs a new DatabaseEntityBase with the specified connection.
     *
     * @param conn The connection to use.
     */
    public DatabaseEntityBase(IDB_Connector conn) {
        this.conn = conn;
    }

    /**
     * Constructs a new DatabaseEntityBase with the specified connection and if it is already locked.
     *
     * @param conn The connection to use.
     * @param locked If the object is already locked.
     */
    public DatabaseEntityBase(IDB_Connector conn, boolean locked) {
        this(conn);
        _lock = locked;
    }

    /**
     * Sets _loaded and _exists to true.
     * To be used in the case of the constructor with {@link IDB_Connector} and {@link ResultSet}.
     */
    protected void setLoadedAndExists() {
        _loaded = true;
        _exists = true;
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
     * This should delete the auxiliary row corresponding to the current object.
     * Use {@link IDB_Connector#getStatement(String)} to get a {@link java.sql.PreparedStatement}.
     * ^ Don't forget to use the try([resource]) {}
     * Use {@link #getAuxTableName()} to get the auxiliary table name.
     * DO NOT use any public functions provided by {@link DatabaseEntityBase} in here otherwise a deadlock could occur.
     *
     * @return If the row deletion occurred.
     * @throws CheckedException An error has occurred.
     */
    protected abstract boolean deleteAuxRow() throws CheckedException;

    /**
     * This should insert the auxiliary row corresponding to the current object.
     * Use {@link IDB_Connector#getStatement(String)} to get a {@link java.sql.PreparedStatement}.
     * ^ Don't forget to use the try([resource]) {}
     * Use {@link #getAuxTableName()} to get the auxiliary table name.
     * DO NOT use any public functions provided by {@link DatabaseEntityBase} in here otherwise a deadlock could occur.
     *
     * @throws CheckedException An error has occurred.
     */
    protected abstract void createAuxRow() throws CheckedException;

    /**
     * This should insert the row corresponding to the current object.
     * Use {@link IDB_Connector#getStatement(String)} to get a {@link java.sql.PreparedStatement}.
     * ^ Don't forget to use the try([resource]) {}
     * Use {@link #getTableName()} to get the table name.
     * DO NOT use any public functions provided by {@link DatabaseEntityBase} in here otherwise a deadlock could occur.
     *
     * @throws CheckedException An error has occurred.
     */
    protected abstract void createRow() throws CheckedException;

    /**
     * This should update the row corresponding to the current object.
     * Use {@link IDB_Connector#getStatement(String)} to get a {@link java.sql.PreparedStatement}.
     * ^ Don't forget to use the try([resource]) {}
     * Use {@link #getTableName()} to get the table name.
     * DO NOT use any public functions provided by {@link DatabaseEntityBase} in here otherwise a deadlock could occur.
     *
     * @throws CheckedException An error has occurred.
     */
    protected abstract void updateRow() throws CheckedException;

    /**
     * This should select the row corresponding to the current object.
     * Use {@link IDB_Connector#getStatement(String)} to get a {@link java.sql.PreparedStatement}.
     * ^ Don't forget to use the try([resource]) {}
     * Use {@link #getTableName()} to get the table name.
     * DO NOT use any public functions provided by {@link DatabaseEntityBase} in here otherwise a deadlock could occur.
     *
     * @throws CheckedException An error has occurred (EG: Row does not exist).
     */
    protected abstract void loadRow() throws CheckedException;

    /**
     * This should delete the row corresponding to the current object.
     * Use {@link IDB_Connector#getStatement(String)} to get a {@link java.sql.PreparedStatement}.
     * ^ Don't forget to use the try([resource]) {}
     * Use {@link #getTableName()} to get the table name.
     * DO NOT use any public functions provided by {@link DatabaseEntityBase} in here otherwise a deadlock could occur.
     *
     * @throws CheckedException An error has occurred.
     */
    protected abstract void deleteRow() throws CheckedException;

    /**
     * This locks the object allowing it to have {@link #load()} and {@link #store()} operate.
     * DO NOT forget to {@link #unlock()} once finished.
     *
     * @throws CheckedException An error has occurred.
     */
    public final void lock() throws CheckedException {
        synchronized (slock) {
            //Allow for lock to be used on existing rows without using exists(true) before a lock
            if (!_exists) _exists = checkRowExistence();
            //Skip locking of the object does not exist
            if (_lock || !_exists) return;
            if (deleteAuxRow()) _lock = true; else throw new CheckedException("Lock could not be obtained");
        }
    }

    /**
     * This unlocks the object allowing it to be used by other connections.
     * See: {@link #lock()}
     *
     * @throws CheckedException An error has occurred.
     */
    public final void unlock() throws CheckedException {
        synchronized (slock) {
            if (!_lock) return;
            try {
                createAuxRow();
            } catch (CheckedException e) {
                throw new CheckedException("Lock could not be released");
            }
            _lock = false;
        }
    }

    /**
     * Returns if this object is in use by this connection and can be loaded and stored.
     *
     * @return If the object is locked.
     */
    public final boolean isLocked() {
        return _lock;
    }

    /**
     * This should check if the row corresponding to the current object exists.
     * ( COUNT(*) and WHERE is your friend)
     * Use {@link IDB_Connector#getStatement(String)} to get a {@link java.sql.PreparedStatement}.
     * ^ Don't forget to use the try([resource]) {}
     * Use {@link #getTableName()} to get the table name.
     * DO NOT use any public functions provided by {@link DatabaseEntityBase} in here otherwise a deadlock could occur.
     *
     * @throws CheckedException An error has occurred.
     */
    protected abstract boolean checkRowExistence() throws CheckedException;

    /**
     * Gets if the object exists in its table.
     *
     * @param update Whether to recheck the table.
     * @return If the object exists.
     * @throws CheckedException An error has occurred.
     */
    public final boolean exists(boolean update) throws CheckedException {
        if (update) _exists = checkRowExistence();
        return _exists;
    }

    /**
     * Loads the object's contents.
     * {@link #isLocked()} should be true.
     *
     * @throws CheckedException An error has occurred.
     */
    public final void load() throws CheckedException {
        synchronized (slock) {
            if (!_lock) throw new CheckedException("Lock not applied");
            loadRow();
            _exists = true;
            _loaded = true;
        }
    }

    /**
     * Stores the object's contents.
     * {@link #isLocked()} should be true.
     * However, if {@link #exists(boolean)} is false, then the object is created
     * and {@link #isLocked()} should be false.
     *
     * @throws CheckedException An error has occurred.
     */
    public final void store() throws CheckedException {
        synchronized (slock) {
            //Allow for non-existent objects to be stored, even if they are not locked
            if ((!_lock) && _exists) throw new CheckedException("Lock not applied");
            if (!_loaded && _exists) throw new CheckedException("Entity not loaded");
            if (_exists) updateRow(); else {
                createRow();
                _exists = true;
            }
        }
    }

    /**
     * Deletes an object.
     * {@link #isLocked()} should be true.
     * The object is implicitly unlocked once deleted.
     *
     * @throws CheckedException An error has occurred.
     */
    public final void delete() throws CheckedException {
        synchronized (slock) {
            if (!_lock) throw new CheckedException("Lock not applied");
            if (_exists) {
                deleteRow();
                _exists = false;
                _loaded = false;
                //Mark lock as disabled once deleted
                _lock = false;
            } else throw new CheckedException("Row does not exist");
        }
    }

    /**
     * Gets if the object is loaded.
     *
     * @return If the object is loaded.
     */
    public final boolean isLoaded() {
        return _loaded;
    }
}
