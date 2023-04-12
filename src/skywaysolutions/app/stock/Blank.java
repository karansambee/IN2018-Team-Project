package skywaysolutions.app.stock;

import skywaysolutions.app.database.DatabaseEntityBase;
import skywaysolutions.app.database.IDB_Connector;
import skywaysolutions.app.utils.CheckedException;
import skywaysolutions.app.utils.ResultSetNullableReturners;
import skywaysolutions.app.utils.Time;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;

public class Blank extends DatabaseEntityBase {
    private long blankID;
    private long assignedStaffID;
    private int typeID;
    private String description;
    private Date returned;
    private boolean blackListed;
    private boolean voided;
    private Date creationDate;
    private Date assignmentDate;

    /**
     * Constructs a new DatabaseEntityBase with the specified connection.
     *
     * @param conn The connection to use.
     */
    public Blank(IDB_Connector conn, long id) {
        super(conn);
        blankID = id;
        typeID = Integer.parseInt(Long.toString(blankID).substring(0,3));
    }

    public Blank(IDB_Connector conn, long id, long assigned, String description, Date creation, Date assignment) {
        super(conn);
        blankID = id;
        typeID = Integer.parseInt(Long.toString(blankID).substring(0,3));
        assignedStaffID = assigned;
        this.description = description;
        creationDate = creation;
        assignmentDate = assignment;
    }

    public Blank(IDB_Connector conn, ResultSet rs, boolean locked) throws SQLException, CheckedException {
        super(conn, locked);
        loadFrom(rs, locked);
    }

    /**
     * Gets the ID of the object that is used for caching.
     *
     * @return The ID of the object.
     */
    @Override
    public Object getPrimaryID() {
        return blankID;
    }

    @Override
    protected String getTableName() {
        return "Blank";
    }


    @Override
    protected boolean deleteAuxRow() throws CheckedException {
        try(PreparedStatement sta = conn.getStatement("DELETE FROM "+getAuxTableName()+" WHERE BlankNumber = ?")) {
            sta.setLong(1, blankID);
            if (sta.executeUpdate() > 0) return true;
        } catch (SQLException throwables) {
            throw new CheckedException(throwables);
        }
        return false;
    }

    @Override
    protected void createAuxRow() throws CheckedException {
        try(PreparedStatement sta = conn.getStatement("INSERT INTO " +getAuxTableName()+" VALUES (?)")){
            sta.setLong(1, blankID);
            sta.executeUpdate();
        } catch (SQLException throwables) {
            throw new CheckedException(throwables);
        }
    }

    @Override
    protected void createRow() throws CheckedException {
        try (PreparedStatement sta = conn.getStatement("INSERT INTO " +getTableName()+" VALUES (?,?,?,?,?,?,?,?,?)")) {
            sta.setLong(1, blankID);
            sta.setLong(2, assignedStaffID);
            sta.setInt(3, typeID);
            sta.setString(4,description);
            sta.setBoolean(5,blackListed);
            sta.setBoolean(6,voided);
            sta.setDate(7, Time.toSQLDate(creationDate));
            if (assignmentDate == null) sta.setNull(8, Types.DATE); else sta.setDate(8, Time.toSQLDate(assignmentDate));
            if (returned == null) sta.setNull(9, Types.DATE); else sta.setDate(9, Time.toSQLDate(returned));
            sta.executeUpdate();
        } catch (SQLException throwables) {
            throw new CheckedException(throwables);
        }
    }

    @Override
    protected void updateRow() throws CheckedException {
        try(PreparedStatement sta = conn.getStatement("UPDATE " +getTableName()+" SET BlankDescription = ?, StaffId = ?, TypeNumber = ?, Blacklisted = ?, Void = ?, ReceivedDate = ?, AssignedDate = ?, ReturnedDate = ? WHERE BlankNumber = ?")){
            sta.setString(1, description);
            sta.setLong(2, assignedStaffID);
            sta.setInt(3, typeID);
            sta.setBoolean(4,blackListed);
            sta.setBoolean(5,voided);
            sta.setDate(6, Time.toSQLDate(creationDate));
            if (assignmentDate == null) sta.setNull(7, Types.DATE); else sta.setDate(7, Time.toSQLDate(assignmentDate));
            if (returned == null) sta.setNull(8, Types.DATE); else sta.setDate(8, Time.toSQLDate(returned));
            sta.setLong(9, blankID);
            sta.executeUpdate();
        } catch (SQLException throwables) {
            throw new CheckedException(throwables);
        }
    }

    @Override
    protected void loadRow() throws CheckedException {
        try(PreparedStatement sta = conn.getStatement("SELECT BlankNumber, StaffID, TypeNumber, BlankDescription, Blacklisted, Void, ReceivedDate, AssignedDate, ReturnedDate FROM " +getTableName()+" WHERE BlankNumber = ?")){
            sta.setLong(1, blankID);
            try (ResultSet rs = sta.executeQuery()) {
                if (!rs.next()) throw new CheckedException("No Row Exists!");
                blankID = rs.getLong("BlankNumber");
                assignedStaffID = rs.getLong("StaffID");
                typeID = rs.getInt("TypeNumber");
                description = rs.getString("BlankDescription");
                blackListed = rs.getBoolean("BlackListed");
                voided = rs.getBoolean("Void");
                creationDate = Time.fromSQLDate(rs.getDate("ReceivedDate"));
                assignmentDate = Time.fromSQLDate(ResultSetNullableReturners.getDateValue(rs, "AssignedDate"));
                returned = Time.fromSQLDate(ResultSetNullableReturners.getDateValue(rs, "ReturnedDate"));
            }
        } catch (SQLException throwables) {
            throw new CheckedException(throwables);
        }
    }

    /**
     * This should load the current object from the passed result set.
     *
     * @param rs     The result set to load from.
     * @param locked If the object is considered locked.
     * @throws SQLException     An SQL error has occurred.
     * @throws CheckedException An error has occurred.
     */
    @Override
    public void loadFrom(ResultSet rs, boolean locked) throws SQLException, CheckedException {
        setLoadedAndExists();
        setLockedState(locked);
        blankID = rs.getLong("BlankNumber");
        assignedStaffID = rs.getLong("StaffID");
        typeID = rs.getInt("TypeNumber");
        description = rs.getString("BlankDescription");
        blackListed = rs.getBoolean("BlackListed");
        voided = rs.getBoolean("Void");
        creationDate = Time.fromSQLDate(rs.getDate("ReceivedDate"));
        assignmentDate = Time.fromSQLDate(ResultSetNullableReturners.getDateValue(rs, "AssignedDate"));
        returned = Time.fromSQLDate(ResultSetNullableReturners.getDateValue(rs, "ReturnedDate"));
    }

    @Override
    protected void deleteRow() throws CheckedException {
        try(PreparedStatement sta = conn.getStatement("DELETE FROM " +getTableName()+" WHERE BlankNumber = ?")){
            sta.setLong(1, blankID);
            sta.executeUpdate();
        } catch (SQLException throwables) {
            throw new CheckedException(throwables);
        }
    }

    @Override
    protected boolean checkRowExistence() throws CheckedException {
        try(PreparedStatement sta = conn.getStatement("SELECT COUNT(*) as rowCount FROM " +getTableName()+" WHERE BlankNumber = ?")){
            sta.setLong(1, blankID);
            try (ResultSet rs = sta.executeQuery()) {
                if (!rs.next()) throw new CheckedException("No Row Exists!");
                return rs.getInt("rowCount") > 0;
            }
        } catch (SQLException throwables) {
            throw new CheckedException(throwables);
        }
    }
    public long getBlankID() {
        return blankID;
    }

    public long getAssignedStaffID() {
        return assignedStaffID;
    }

    public void setAssignedStaffID(long assignedStaffID, Date assignmentDate) {
        this.assignedStaffID = assignedStaffID;
        this.assignmentDate = assignmentDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date isReturned() {
        return returned;
    }

    public void setReturned(Date returned) {
        this.returned = returned;
    }
    public boolean isBlackListed() {
        return blackListed;
    }

    public void setBlackListed(boolean blackListed) {
        this.blackListed = blackListed;
    }

    public boolean isVoided() {
        return voided;
    }

    public void setVoided(boolean voided) {
        this.voided = voided;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getAssignmentDate() {
        return assignmentDate;
    }

    public int getBlankType() {
        return typeID;
    }
}
