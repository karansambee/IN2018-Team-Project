package skywaysolutions.app.stock;

import skywaysolutions.app.database.IDB_Connector;
import skywaysolutions.app.database.IFilterStatementCreator;
import skywaysolutions.app.database.MultiLoadSyncMode;
import skywaysolutions.app.utils.CheckedException;

import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BlankController implements IStockAccessor {
    private final IDB_Connector conn;
    private final Object slock = new Object();

    //for blanks
    private final BlankAssignmentFilter blankAssignmentFilter = new BlankAssignmentFilter();
    private final BlankTableAccessor blankTableAccessor;
    //for blank types
    private final AllFilter allFilter = new AllFilter();
    private final BlankTypeTableAccessor blankTypeTableAccessor;


    public BlankController(IDB_Connector conn) throws CheckedException {
        this.conn = conn;
        this.blankTableAccessor = new BlankTableAccessor(conn);
        this.blankTypeTableAccessor = new BlankTypeTableAccessor(conn);
        conn.getTableList(true);
        blankTypeTableAccessor.assureTableSchema();
        blankTableAccessor.assureTableSchema();
    }

    /**
     * Refreshes a blank's cache given the ID.
     *
     * @param id The ID of the blank.
     * @throws CheckedException A refresh error has occurred.
     */
    @Override
    public void refreshBlank(long id) throws CheckedException {
        blankTableAccessor.load(id, true);
    }

    /**
     * Refreshes a blank type's cache given the ID.
     *
     * @param id The ID of the blank.
     * @throws CheckedException A refresh error has occurred.
     */
    @Override
    public void refreshBlankType(int id) throws CheckedException {
        blankTypeTableAccessor.load(id, true);
    }

    /**
     * Creates a blank with the specified ID,
     * an ID of a staff member it's assigned to (Not assigned if -1)
     * and a description of the blanks contents.
     *
     * @param id          The ID of the blank.
     * @param assignedID  The staff member ID it is assigned to or -1 if not assigned.
     * @param description The description of the blank's contents.
     * @throws CheckedException The blank creation operation has failed.
     */
    @Override
    public void createBlank(long id, long assignedID, String description, Date creationDate, Date assignmentDate) throws CheckedException {
        synchronized (slock) {
            if (assignedID < -1) assignedID = -1;
            Blank newBlank = new Blank(conn, id, assignedID, description, creationDate, assignmentDate);
            if (newBlank.exists(true)){
                throw new CheckedException("Blank already exists");
            } else {
                newBlank.store();
                blankTableAccessor.cacheOne(newBlank);
            }
        }
    }

    /**
     * Returns a blank with the specified ID.
     *
     * @param id The ID of the blank.
     * @throws CheckedException The blank could not be marked as returned.
     */
    @Override
    public void returnBlank(long id, Date returnedDate) throws CheckedException {
        synchronized (slock) {
            Blank blank = blankTableAccessor.load(id, true);
            try {
                blank.lock();
                blank.setReturned(returnedDate);
                blank.store();
            } finally {
                blank.unlock();
            }
        }
    }

    /**
     * Blacklists a blank with the specified ID.
     *
     * @param id The ID of the blank.
     * @throws CheckedException The blank could not be marked as blacklisted.
     */
    @Override
    public void blacklistBlank(long id) throws CheckedException {
        synchronized (slock) {
            Blank blank = blankTableAccessor.load(id, true);
            try {
                blank.lock();
                blank.setBlackListed(true);
                blank.store();
            } finally {
                blank.unlock();
            }
        }
    }

    /**
     * Voids a blank with the specified ID.
     *
     * @param id The ID of the blank.
     * @throws CheckedException The blank could not be marked as voided.
     */
    @Override
    public void voidBlank(long id) throws CheckedException {
        synchronized (slock) {
            Blank blank = blankTableAccessor.load(id, true);
            try {
                blank.lock();
                blank.setVoided(true);
                blank.store();
            } finally {
                blank.unlock();
            }
        }
    }


    /**
     * Gets the blank type code for the specified blank ID.
     *
     * @param id The blank ID.
     * @return A blank type code.
     */
    @Override
    public int getBlankType(long id) throws CheckedException {
        synchronized (slock) {
            return blankTableAccessor.load(id, false).getBlankType();
        }
    }

    /**
     * Gets a list of blanks that may be filtered by a provided staff member ID.
     *
     * @param assignedID The staff ID to filter by, -1 for no filtering or -2 for un-assigned.
     * @return The list of blank IDs.
     * @throws CheckedException The blanks could not be retrieved.
     */
    @Override
    public long[] getBlanks(long assignedID) throws CheckedException {
        synchronized (slock) {
            blankAssignmentFilter.staffID = assignedID;
            List<Blank> blanks = blankTableAccessor.loadMany(blankAssignmentFilter, MultiLoadSyncMode.NoLoad);
            long[] ids = new long[blanks.size()];
            for (int i = 0; i < ids.length; i++) ids[i] = blanks.get(i).getBlankID();
            return ids;
        }
    }

    /**
     * Gets if a blank has been returned.
     *
     * @param id The blank ID.
     * @return If the blank has been returned.
     * @throws CheckedException The blank could not be retrieved.
     */
    @Override
    public boolean isBlankReturned(long id) throws CheckedException {
        synchronized (slock) {
            return blankTableAccessor.load(id, false).isReturned() != null;
        }
    }

    /**
     * Gets the blank return date or null if it has not been returned.
     *
     * @param id The blank ID.
     * @return The blank return date or null.
     * @throws CheckedException The blank could not be retrieved.
     */
    @Override
    public Date getBlankReturnedDate(long id) throws CheckedException {
        synchronized (slock) {
            return blankTableAccessor.load(id, false).isReturned();
        }
    }

    /**
     * Gets if a blank has been blacklisted.
     *
     * @param id The blank ID.
     * @return If the blank has been blacklisted.
     * @throws CheckedException The blank could not be retrieved.
     */
    @Override
    public boolean isBlankBlacklisted(long id) throws CheckedException {
        synchronized (slock) {
            return blankTableAccessor.load(id, false).isBlackListed();
        }
    }

    /**
     * Gets if a blank has been voided.
     *
     * @param id The blank ID.
     * @return If the blank has been voided.
     * @throws CheckedException The blank could not be retrieved.
     */
    @Override
    public boolean isBlankVoided(long id) throws CheckedException {
        synchronized (slock) {
            return blankTableAccessor.load(id, false).isVoided();
        }
    }

    /**
     * Creates a new blank type with a description.
     *
     * @param typeCode    The 3 digit type code.
     * @param description The description of the type.
     * @throws CheckedException The blank type could not be created.
     */
    @Override
    public void createBlankType(int typeCode, String description) throws CheckedException {
        synchronized (slock) {
            BlankType newBlankType = new BlankType(conn, typeCode, description);
            if (newBlankType.exists(true)){
                throw new CheckedException("Blank Type exists");
            } else {
                newBlankType.store();
                blankTypeTableAccessor.cacheOne(newBlankType);
            }
        }
    }

    /**
     * Gets a blank type description.
     *
     * @param typeCode The 3 digit type code.
     * @return The blank type description.
     * @throws CheckedException The blank type description could not be retrieved.
     */
    @Override
    public String getBlankTypeDescription(int typeCode) throws CheckedException {
        synchronized (slock) {
            return blankTypeTableAccessor.load(typeCode, false).getDescription();
        }
    }

    /**
     * Sets a blank types' description.
     *
     * @param typeCode    The 3 digit type code.
     * @param description The description of the type.
     * @throws CheckedException The description of the type could not be set.
     */
    @Override
    public void setBlankTypeDescription(int typeCode, String description) throws CheckedException {
        synchronized (slock) {
            BlankType blankType = blankTypeTableAccessor.load(typeCode, true);
            try {
                blankType.lock();
                blankType.load();
                blankType.setDescription(description);
                blankType.store();
            } finally {
                blankType.unlock();
            }
        }
    }


    /**
     * Deletes a blank type (Type-Description association).
     *
     * @param typeCode The 3 digit type code.
     */
    @Override
    public void deleteBlankType(int typeCode) throws CheckedException {
        synchronized (slock) {
            BlankType blankType = blankTypeTableAccessor.load(typeCode, false);
            try {
                blankType.lock();
                blankType.delete();
            } finally {
                blankType.unlock();
            }
        }
    }

    /**
     * Gets a list of blank types with descriptions associated with them.
     *
     * @return The list of 3 digit codes of blank types.
     * @throws CheckedException The blank types could not be retrieved.
     */
    @Override
    public int[] listBlankTypes() throws CheckedException {
        synchronized (slock) {
            List<BlankType> blankTypes = blankTypeTableAccessor.loadMany(allFilter, MultiLoadSyncMode.NoLoad);
            int[] ids = new int[blankTypes.size()];
            for (int i = 0; i < ids.length; i++) ids[i] = blankTypes.get(i).getBlankTypeID();
            return ids;
        }
    }

    /**
     * Gets the description of the blank's contents.
     *
     * @param id The blank ID.
     * @return The blank's description.
     * @throws CheckedException The description of the blank could not be retrieved.
     */
    @Override
    public String getBlankDescription(long id) throws CheckedException {
        synchronized (slock) {
            return blankTableAccessor.load(id, false).getDescription();
        }
    }

    /**
     * Sets the description of the blank's contents.
     *
     * @param id          The blank ID.
     * @param description The blank's description.
     * @throws CheckedException The description of the blank could not be stored.
     */
    @Override
    public void setBlankDescription(long id, String description) throws CheckedException {
        synchronized (slock) {
            Blank blank = blankTableAccessor.load(id, true);
            try {
                blank.lock();
                blank.setDescription(description);
                blank.store();
            } finally {
                blank.unlock();
            }
        }
    }

    /**
     * Re-assigns a blank with the specified ID to the
     * staff member with the specified ID.
     *
     * @param id         The ID of the blank.
     * @param assignedID The ID of the staff member.
     * @throws CheckedException Blank re-assigning has failed.
     */
    @Override
    public void reAssignBlank(long id, long assignedID, Date assignmentDate) throws CheckedException {
        synchronized (slock) {
            if (assignedID < -1) assignedID = -1;
            Blank blank = blankTableAccessor.load(id, true);
            try {
                blank.lock();
                blank.setAssignedStaffID(assignedID, assignmentDate);
                blank.store();
            } finally {
                blank.unlock();
            }
        }
    }

    @Override
    public Date getBlankCreationDate(long id) throws CheckedException {
        synchronized (slock) {
            return blankTableAccessor.load(id, false).getAssignmentDate();
        }
    }

    /**
     * Sets the blank creation date.
     *
     * @param id   The blank ID.
     * @param date The creation date.
     * @throws CheckedException The blank could not be retrieved.
     */
    @Override
    public void setBlankCreationDate(long id, Date date) throws CheckedException {
        synchronized (slock) {
            Blank blank = blankTableAccessor.load(id, true);
            try {
                blank.lock();
                blank.setCreationDate(date);
                blank.store();
            } finally {
                blank.unlock();
            }
        }
    }


    @Override
    public Date getBlankAssignmentDate(long id) throws CheckedException {
        synchronized (slock) {
            return blankTableAccessor.load(id, false).getAssignmentDate();
        }
    }

    /**
     * Gets an array of tables that can be backed up.
     *
     * @return The array of tables.
     */
    @Override
    public String[] getTables() {
        return new String[] {"BlankType", "Blank"};
    }

    /**
     * Forces a table to be fully unlocked.
     *
     * @param tableName The table to fully unlock.
     * @throws CheckedException The table could not be unlocked.
     */
    @Override
    public void forceFullUnlock(String tableName) throws CheckedException {
        synchronized (slock) {
            if (tableName.equals("Blank")) blankTableAccessor.unlockAll(true);
            else if (tableName.equals("BlankType")) blankTypeTableAccessor.unlockAll(true);
        }
    }

    /**
     * Forces a table to be deleted (Along with its auxiliary table).
     *
     * @param tableName The table to purge.
     * @throws CheckedException The table could not be purged.
     */
    @Override
    public void forceFullPurge(String tableName) throws CheckedException {
        synchronized (slock) {
            conn.getTableList(true);
            if (tableName.equals("Blank")) blankTableAccessor.purgeTableSchema();
            else if (tableName.equals("BlankType")) blankTypeTableAccessor.purgeTableSchema();
        }
    }

    /**
     * Assures the existence of a table.
     *
     * @param tableName The table to assure the existence of.
     * @throws CheckedException The table could not be assured.
     */
    @Override
    public void assureExistence(String tableName) throws CheckedException {
        synchronized (slock) {
            conn.getTableList(true);
            if (tableName.equals("Blank")) blankTableAccessor.assureTableSchema();
            else if (tableName.equals("BlankType")) blankTypeTableAccessor.assureTableSchema();
        }
    }

    /**
     * Refreshes the cache of a table accessor.
     *
     * @param tableName The name of the table to refresh the cache of.
     * @throws CheckedException The table could not be refreshed.
     */
    @Override
    public void refreshCache(String tableName) throws CheckedException {
        synchronized (slock) {
            conn.getTableList(true);
            if (tableName.equals("Blank")) blankTableAccessor.refreshAll();
            else if (tableName.equals("BlankType")) blankTypeTableAccessor.refreshAll();
        }
    }

    private static class BlankAssignmentFilter implements IFilterStatementCreator {
        public long staffID;

        @Override
        public PreparedStatement createFilteredStatementFor(IDB_Connector conn, String startOfSQLTemplate) throws SQLException, CheckedException {
            PreparedStatement sta = conn.getStatement((staffID == -1) ?
                    startOfSQLTemplate.substring(0, startOfSQLTemplate.length() - 6) + "ORDER BY BlankNumber" :
                    startOfSQLTemplate + "StaffID = ? ORDER BY BlankNumber");
            if (staffID != -1) sta.setLong(1, (staffID == -2) ? -1 : staffID);
            return sta;
        }
    }

    private static class AllFilter implements IFilterStatementCreator {
        @Override
        public PreparedStatement createFilteredStatementFor(IDB_Connector conn, String startOfSQLTemplate) throws SQLException, CheckedException {
            return conn.getStatement(startOfSQLTemplate.substring(0, startOfSQLTemplate.length() - 6) + "ORDER BY TypeNumber");
        }
    }
}
