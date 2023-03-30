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
    private IDB_Connector conn;
    private final Object slock = new Object();

    //for blanks
    private IDFinder blankFinder = new IDFinder();
    private BlankTableAccessor blankTableAccessor;

    //for blank types
    private TypeFinder blankTypeFinder = new TypeFinder();
    private BlankTypeTableAccessor blankTypeTableAccessor;


    public BlankController(IDB_Connector conn) {
        this.conn = conn;
        this.blankTableAccessor = new BlankTableAccessor(conn);
        this.blankTypeTableAccessor = new BlankTypeTableAccessor(conn);
    }

    //for blanks
    private class IDFinder implements IFilterStatementCreator {
        public long blankID;
        @Override
        public PreparedStatement createFilteredStatementFor(IDB_Connector conn, String startOfSQLTemplate) {
            try {
                PreparedStatement sta = conn.getStatement(startOfSQLTemplate + "BlankNumber = ?");
                sta.setLong(1, blankID);
                return sta;
            } catch (SQLException | CheckedException e) {
                return null;
            }
        }
    }

    private Blank getBlankFromID(long id, MultiLoadSyncMode mode) throws CheckedException {
        blankFinder.blankID = id;
        List<Blank> blanks = blankTableAccessor.loadMany(blankFinder, mode);
        if (blanks.size() > 0) return blanks.get(0); else throw new CheckedException("Blank Does Not Exist");
    }


    //for blank types
    private class TypeFinder implements IFilterStatementCreator {
        public long blankTypeID;
        @Override
        public PreparedStatement createFilteredStatementFor(IDB_Connector conn, String startOfSQLTemplate) {
            try {
                PreparedStatement sta = conn.getStatement(startOfSQLTemplate + "TypeNumber = ?");
                sta.setLong(1, blankTypeID);
                return sta;
            } catch (SQLException | CheckedException e) {
                return null;
            }
        }
    }

    private BlankType getBlankTypeFromID(long id, MultiLoadSyncMode mode) throws CheckedException {
        blankTypeFinder.blankTypeID = id;
        List<BlankType> blankTypes = blankTypeTableAccessor.loadMany(blankTypeFinder, mode);
        if (blankTypes.size() > 0) return blankTypes.get(0); else throw new CheckedException("BlankType Does Not Exist");
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
            Blank newBlank = new Blank(conn, id, assignedID, description, creationDate, assignmentDate);
            if (newBlank.exists(true)){
                throw new CheckedException("Blank already exists");
            } else {
                newBlank.store();
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
            Blank blank = getBlankFromID(id, MultiLoadSyncMode.KeepLockedAfterLoad);
            blank.setReturned(returnedDate);
            blank.store();
            blank.unlock();
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
            Blank blank = getBlankFromID(id, MultiLoadSyncMode.KeepLockedAfterLoad);
            blank.setBlackListed(true);
            blank.store();
            blank.unlock();
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
            Blank blank = getBlankFromID(id, MultiLoadSyncMode.KeepLockedAfterLoad);
            blank.setVoided(true);
            blank.store();
            blank.unlock();
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
            Blank blank = getBlankFromID(id, MultiLoadSyncMode.KeepLockedAfterLoad);
            return Integer.parseInt(String.valueOf(blank.getBlankID()).substring(0,3));
        }
    }

    /**
     * Gets a list of blanks that may be filtered by a provided staff member ID.
     *
     * @param assignedID The staff ID to filter by or -1 for no filtering.
     * @return The list of blank IDs.
     * @throws CheckedException The blanks could not be retrieved.
     */
    @Override
    public long[] getBlanks(long assignedID) throws CheckedException {
        synchronized (slock) {
            if (assignedID == -1) {
                try(PreparedStatement pre = conn.getStatement(
                        "SELECT BlankNumber FROM Blank")){
                    ResultSet rs = pre.executeQuery();
                    ArrayList<Integer> blankNumbers = new ArrayList<>();
                    while (rs.next()) blankNumbers.add(rs.getInt("BlankNumber"));
                    rs.close();
                    return blankNumbers.stream().mapToLong(Integer::longValue).toArray();
                } catch (SQLException | CheckedException throwables){
                    throw new CheckedException(throwables);
                }
            } else {
                try(PreparedStatement pre = conn.getStatement(
                        "SELECT BlankNumber FROM Blank WHERE StaffID = ?")){
                    pre.setLong(1, assignedID);
                    ResultSet rs = pre.executeQuery();
                    ArrayList<Integer> blankNumbers = new ArrayList<>();
                    while (rs.next()) blankNumbers.add(rs.getInt("BlankNumber"));
                    rs.close();
                    return blankNumbers.stream().mapToLong(Integer::longValue).toArray();
                } catch (SQLException | CheckedException throwables){
                    throw new CheckedException(throwables);
                }
            }
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
            Blank blank = getBlankFromID(id, MultiLoadSyncMode.KeepLockedAfterLoad);
            return blank.isReturned() != null;
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
            Blank blank = getBlankFromID(id, MultiLoadSyncMode.KeepLockedAfterLoad);
            return blank.isReturned();
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
            Blank blank = getBlankFromID(id, MultiLoadSyncMode.KeepLockedAfterLoad);
            return blank.isBlackListed();
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
            Blank blank = getBlankFromID(id, MultiLoadSyncMode.KeepLockedAfterLoad);
            return blank.isVoided();
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
            BlankType blankType = getBlankTypeFromID(typeCode, MultiLoadSyncMode.KeepLockedAfterLoad);
            return blankType.getDescription();
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
            BlankType blankType = getBlankTypeFromID(typeCode, MultiLoadSyncMode.KeepLockedAfterLoad);
            blankType.setDescription(description);
            blankType.store();
            blankType.unlock();
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
            getBlankTypeFromID(typeCode, MultiLoadSyncMode.KeepLockedAfterLoad).delete();
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
            try(PreparedStatement pre = conn.getStatement(
                    "SELECT TypeNumber FROM BlankType")){
                ResultSet rs = pre.executeQuery();
                ArrayList<Integer> typeNumbers = new ArrayList<>();
                while (rs.next()) typeNumbers.add(rs.getInt("BlankType"));
                rs.close();
                return typeNumbers.stream().mapToInt(Integer::intValue).toArray();
            } catch (SQLException | CheckedException throwables){
                throw new CheckedException(throwables);
            }
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
            Blank blank = getBlankFromID(id, MultiLoadSyncMode.KeepLockedAfterLoad);
            return blank.getDescription();
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
            Blank blank = getBlankFromID(id, MultiLoadSyncMode.KeepLockedAfterLoad);
            blank.setDescription(description);
            blank.store();
            blank.unlock();
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
            Blank blank = getBlankFromID(id, MultiLoadSyncMode.KeepLockedAfterLoad);
            blank.setAssignedStaffID(assignedID, assignmentDate);
            blank.store();
            blank.unlock();
        }
    }

    @Override
    public Date getBlankCreationDate(long id) throws CheckedException {
        synchronized (slock) {
            Blank blank = getBlankFromID(id, MultiLoadSyncMode.KeepLockedAfterLoad);
            return blank.getAssignmentDate();
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
            Blank blank = getBlankFromID(id, MultiLoadSyncMode.KeepLockedAfterLoad);
            blank.setCreationDate(date);
            blank.store();
            blank.unlock();
        }
    }


    @Override
    public Date getBlankAssignmentDate(long id) throws CheckedException {
        synchronized (slock) {
            Blank blank = getBlankFromID(id, MultiLoadSyncMode.KeepLockedAfterLoad);
            return blank.getAssignmentDate();
        }
    }

    /**
     * Gets an array of tables that can be backed up.
     *
     * @return The array of tables.
     */
    @Override
    public String[] getTables() {
        return new String[] {"Blank", "BlankType"};
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
            if (tableName.equals("Blank")) blankTableAccessor.unlockAll();
            else if (tableName.equals("BlankType")) blankTypeTableAccessor.unlockAll();
        }
    }

}
