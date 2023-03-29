package skywaysolutions.app.stock;

import skywaysolutions.app.database.DatabaseEntityBase;
import skywaysolutions.app.database.IDB_Connector;
import skywaysolutions.app.utils.CheckedException;
import skywaysolutions.app.utils.Time;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class Blank extends DatabaseEntityBase {
    private long blankID;
    private long assignedStaffID;
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
    }

    public Blank(IDB_Connector conn, long id, long assigned, String description, Date creation, Date assignment) {
        super(conn);
        blankID = id;
        assignedStaffID = assigned;
        this.description = description;
        creationDate = creation;
        assignmentDate = assignment;
    }

    public Blank(IDB_Connector conn, ResultSet rs) throws SQLException {
        super(conn);
        blankID = rs.getInt("BlankNumber");
        assignedStaffID = rs.getLong("StaffID");
        description = rs.getString("BlankDescription");
        blackListed = rs.getBoolean("BlackListed");
        voided = rs.getBoolean("Void");
        creationDate = rs.getDate("ReceivedDate");
        assignmentDate = rs.getDate("AssignedDate");
        returned = rs.getDate("ReturnedDate");
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
            sta.setInt(3, Integer.parseInt(Long.toString(blankID).substring(0, 3)));
            sta.setString(4,description);
            sta.setBoolean(5,blackListed);
            sta.setBoolean(6,voided);
            sta.setDate(7, Time.toSQLDate(creationDate));
            sta.setDate(8, (assignmentDate == null) ? null : Time.toSQLDate(assignmentDate));
            sta.setDate(9, (returned == null) ? null : Time.toSQLDate(returned));
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
            sta.setInt(3, Integer.parseInt(Long.toString(blankID).substring(0, 3)));
            sta.setBoolean(4,blackListed);
            sta.setBoolean(5,voided);
            sta.setDate(6, Time.toSQLDate(creationDate));
            sta.setDate(7, (assignmentDate == null) ? null : Time.toSQLDate(assignmentDate));
            sta.setDate(8, (returned == null) ? null : Time.toSQLDate(returned));
            sta.setLong(9, blankID);
            sta.executeUpdate();
        } catch (SQLException throwables) {
            throw new CheckedException(throwables);
        }

    }

    @Override
    protected void loadRow() throws CheckedException {
        try(PreparedStatement sta = conn.getStatement("SELECT BlankNumber, StaffID, BlankDescription, Blacklisted, Void, ReceivedDate, AssignedDate, ReturnedDate FROM " +getTableName()+" WHERE BlankNumber = ?"  )){
            sta.setLong(1, blankID);
            ResultSet rs = sta.executeQuery();
            rs.next();
            blankID = rs.getInt("BlankNumber");
            assignedStaffID = rs.getLong("StaffID");
            description = rs.getString("BlankDescription");
            blackListed = rs.getBoolean("BlackListed");
            voided = rs.getBoolean("Void");
            creationDate = rs.getDate("ReceivedDate");
            assignmentDate = rs.getDate("AssignedDate");
            returned = rs.getDate("ReturnedDate");
            rs.close();
        } catch (SQLException throwables) {
            throw new CheckedException(throwables);
        }
    }

    @Override
    protected void deleteRow() throws CheckedException {
        try(PreparedStatement sta = conn.getStatement("DELETE FROM " +getTableName()+" WHERE BlankNumber = ?"  )){
            sta.setLong(1, blankID);
            sta.executeUpdate();
        } catch (SQLException throwables) {
            throw new CheckedException(throwables);
        }

    }

    @Override
    protected boolean checkRowExistence() throws CheckedException {
        try(PreparedStatement sta = conn.getStatement("SELECT COUNT(*) as rowCount FROM " +getTableName()+" WHERE BlankNumber = ?"  )){
            sta.setLong(1, blankID);
            ResultSet rs = sta.executeQuery();
            rs.next();
            int rc = rs.getInt("rowCount");
            rs.close();
            if (rc > 0) return true; else return false;
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

}
