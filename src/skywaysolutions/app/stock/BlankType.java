package skywaysolutions.app.stock;

import skywaysolutions.app.database.DatabaseEntityBase;
import skywaysolutions.app.database.IDB_Connector;
import skywaysolutions.app.utils.CheckedException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BlankType extends DatabaseEntityBase {
    private int blankTypeID;
    private String description;

    /**
     *
     *
     * @param conn The connection to use.
     */
    public BlankType(IDB_Connector conn, int id) {
        super(conn);
        blankTypeID = id;
    }

    public BlankType(IDB_Connector conn, int id, String description) {
        super(conn);
        blankTypeID = id;
        this.description = description;
    }
    public BlankType(IDB_Connector conn, ResultSet rs, boolean locked) throws SQLException {
        super(conn, locked);
        setLoadedAndExists();
        blankTypeID = rs.getInt("TypeNumber");
        description = rs.getString("TypeDescription");
    }

    @Override
    protected String getTableName() {
        return "BlankType";
    }

    @Override
    protected boolean deleteAuxRow() throws CheckedException {
        try(PreparedStatement sta = conn.getStatement("DELETE FROM "+getAuxTableName()+" WHERE TypeNumber = ?")) {
            sta.setInt(1, blankTypeID);
            if (sta.executeUpdate() > 0) return true;
        } catch (SQLException throwables) {
            throw new CheckedException(throwables);
        }
        return false;
    }

    @Override
    protected void createAuxRow() throws CheckedException {
        try(PreparedStatement sta = conn.getStatement("INSERT INTO " +getAuxTableName()+" VALUES (?)")){
            sta.setInt(1, blankTypeID);
            sta.executeUpdate();
        } catch (SQLException throwables) {
            throw new CheckedException(throwables);
        }
    }

    @Override
    protected void createRow() throws CheckedException {
        try (PreparedStatement sta = conn.getStatement("INSERT INTO " +getTableName()+" VALUES (?,?)")) {
            sta.setInt(1,blankTypeID);
            sta.setString(2,description);
            sta.executeUpdate();
        } catch (SQLException throwables) {
            throw new CheckedException(throwables);
        }

    }

    @Override
    protected void updateRow() throws CheckedException {
        try(PreparedStatement sta = conn.getStatement("UPDATE " +getTableName()+" SET TypeDescription = ? WHERE TypeNumber = ?"  )){
            sta.setString(1, description);
            sta.setInt(2, blankTypeID);
            sta.executeUpdate();
        } catch (SQLException throwables) {
            throw new CheckedException(throwables);
        }

    }

    @Override
    protected void loadRow() throws CheckedException {
        try(PreparedStatement sta = conn.getStatement("SELECT TypeNumber, TypeDescription FROM " +getTableName()+" WHERE TypeNumber = ?")){
            sta.setInt(1, blankTypeID);
            try (ResultSet rs = sta.executeQuery()) {
                if (!rs.next()) throw new CheckedException("No Row Exists!");
                blankTypeID = rs.getInt("TypeNumber");
                description = rs.getString("TypeDescription");
            }
        } catch (SQLException throwables) {
            throw new CheckedException(throwables);
        }
    }

    @Override
    protected void deleteRow() throws CheckedException {
        try(PreparedStatement sta = conn.getStatement("DELETE FROM " +getTableName()+" WHERE TypeNumber = ?")){
            sta.setInt(1, blankTypeID);
            sta.executeUpdate();
        } catch (SQLException throwables) {
            throw new CheckedException(throwables);
        }

    }

    @Override
    protected boolean checkRowExistence() throws CheckedException {
        try(PreparedStatement sta = conn.getStatement("SELECT COUNT(*) as rowCount FROM " +getTableName()+" WHERE TypeNumber = ?")){
            sta.setInt(1, blankTypeID);
            try (ResultSet rs = sta.executeQuery()) {
                if (!rs.next()) throw new CheckedException("No Row Exists!");
                return rs.getInt("rowCount") > 0;
            }
        } catch (SQLException throwables) {
            throw new CheckedException(throwables);
        }
    }

    public int getBlankTypeID() {
        return blankTypeID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
