package skywaysolutions.app.customers;

import skywaysolutions.app.database.DatabaseEntityBase;
import skywaysolutions.app.database.IDB_Connector;
import skywaysolutions.app.utils.CheckedException;
import skywaysolutions.app.utils.Decimal;
import skywaysolutions.app.utils.PersonalInformation;
import skywaysolutions.app.utils.Time;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Discount extends DatabaseEntityBase {

    private long planID;
    private PlanType planType;
    private Decimal percentage;

    /**
     * Constructs a new DatabaseEntityBase with the specified connection.
     *
     * @param conn The connection to use.
     */
    public Discount(IDB_Connector conn, Long id) {
        super(conn);
        planID = id;
    }

    public Discount(IDB_Connector conn, PlanType planType, Decimal percentage) {
        super(conn);
        this.planType = planType;
        this.percentage = percentage;
    }

    public Discount(IDB_Connector conn, ResultSet rs) throws SQLException {
        super(conn);
        planID = rs.getLong("DiscountPlanID");
        planType = PlanType.getPlanTypeFromValue((int) rs.getLong("DiscountType"));
        percentage = new Decimal(rs.getDouble("DiscountPercentage"), 2);
        rs.close();
        rs.close();
    }

    /**
     * This should return the name of the table.
     *
     * @return The name of the table.
     */
    @Override
    protected String getTableName() {
        return "DiscountPlan";
    }

    /**
     * This should delete the auxiliary row corresponding to the current object.
     * Use {@link IDB_Connector#getStatement(String)} to get a {@link PreparedStatement}.
     * ^ Don't forget to use the try([resource]) {}
     * Use {@link #getAuxTableName()} to get the auxiliary table name.
     * DO NOT use any public functions provided by {@link DatabaseEntityBase} in here otherwise a deadlock could occur.
     *
     * @return If the row deletion occurred.
     * @throws CheckedException An error has occurred.
     */
    @Override
    protected boolean deleteAuxRow() throws CheckedException {
        try (PreparedStatement sta = conn.getStatement("DELETE FROM " + getAuxTableName() + " WHERE DiscountPlanID = ?")) {
            sta.setLong(1, planID);
            if (sta.executeUpdate() > 0) return true;
        } catch (SQLException throwables) {
            throw new CheckedException(throwables);
        }
        return false;
    }


    /**
     * This should insert the auxiliary row corresponding to the current object.
     * Use {@link IDB_Connector#getStatement(String)} to get a {@link PreparedStatement}.
     * ^ Don't forget to use the try([resource]) {}
     * Use {@link #getAuxTableName()} to get the auxiliary table name.
     * DO NOT use any public functions provided by {@link DatabaseEntityBase} in here otherwise a deadlock could occur.
     *
     * @throws CheckedException An error has occurred.
     */
    @Override
    protected void createAuxRow() throws CheckedException {
        try (PreparedStatement sta = conn.getStatement("INSERT INTO " + getAuxTableName() + " VALUES (?)")) {
            sta.setLong(1, planID);
            sta.executeUpdate();
        } catch (SQLException throwables) {
            throw new CheckedException(throwables);
        }
    }

    /**
     * This should insert the row corresponding to the current object.
     * Use {@link IDB_Connector#getStatement(String)} to get a {@link PreparedStatement}.
     * ^ Don't forget to use the try([resource]) {}
     * Use {@link #getTableName()} to get the table name.
     * DO NOT use any public functions provided by {@link DatabaseEntityBase} in here otherwise a deadlock could occur.
     *
     * @throws CheckedException An error has occurred.
     */
    @Override
    protected void createRow() throws CheckedException {
        try (PreparedStatement sta = conn.getStatement("INSERT INTO " + getTableName() + " VALUES (?,?,?)")) {
            sta.setLong(1, planID);
            sta.setInt(2, planType.getValue());
            sta.setDouble(3, percentage.getValue());
            sta.executeUpdate();
        } catch (SQLException throwables) {
            throw new CheckedException(throwables);
        }

    }

    /**
     * This should update the row corresponding to the current object.
     * Use {@link IDB_Connector#getStatement(String)} to get a {@link PreparedStatement}.
     * ^ Don't forget to use the try([resource]) {}
     * Use {@link #getTableName()} to get the table name.
     * DO NOT use any public functions provided by {@link DatabaseEntityBase} in here otherwise a deadlock could occur.
     *
     * @throws CheckedException An error has occurred.
     */
    @Override
    protected void updateRow() throws CheckedException {
        try (PreparedStatement sta = conn.getStatement("UPDATE " + getTableName() + " SET DiscountPlanID = ?, DiscountType  = ?, DiscountPercentage = ? WHERE DiscountPlanID = ?")) {
            sta.setLong(1, planID);
            sta.setInt(2, planType.getValue());
            sta.setDouble(3, percentage.getValue());
            sta.setLong(4, planID);

        } catch (SQLException throwables) {
            throw new CheckedException(throwables);
        }

    }

    /**
     * This should select the row corresponding to the current object.
     * Use {@link IDB_Connector#getStatement(String)} to get a {@link PreparedStatement}.
     * ^ Don't forget to use the try([resource]) {}
     * Use {@link #getTableName()} to get the table name.
     * DO NOT use any public functions provided by {@link DatabaseEntityBase} in here otherwise a deadlock could occur.
     *
     * @throws CheckedException An error has occurred (EG: Row does not exist).
     */
    @Override
    protected void loadRow() throws CheckedException {
        try (PreparedStatement sta = conn.getStatement("SELECT DiscountPlanID, DiscountType, DiscountPercentage FROM " + getTableName() + " WHERE DiscountPlanID = ?")) {
            sta.setLong(1, planID);
            ResultSet rs = sta.executeQuery();
            rs.next();
            planID = rs.getLong("DiscountPlanID");
            planType = PlanType.getPlanTypeFromValue((int) rs.getLong("DiscountType"));
            percentage = new Decimal(rs.getDouble("DiscountPercentage"), 2);
            rs.close();
        } catch (SQLException throwables) {
            throw new CheckedException(throwables);
        }
    }


    /**
     * This should delete the row corresponding to the current object.
     * Use {@link IDB_Connector#getStatement(String)} to get a {@link PreparedStatement}.
     * ^ Don't forget to use the try([resource]) {}
     * Use {@link #getTableName()} to get the table name.
     * DO NOT use any public functions provided by {@link DatabaseEntityBase} in here otherwise a deadlock could occur.
     *
     * @throws CheckedException An error has occurred.
     */
    @Override
    protected void deleteRow() throws CheckedException {
        try (PreparedStatement sta = conn.getStatement("DELETE FROM " + getTableName() + " WHERE DiscountPlanID = ?")) {
            sta.setLong(1, planID);
            sta.executeUpdate();
        } catch (SQLException throwables) {
            throw new CheckedException(throwables);
        }

    }

    /**
     * This should check if the row corresponding to the current object exists.
     * ( COUNT(*) and WHERE is your friend)
     * Use {@link IDB_Connector#getStatement(String)} to get a {@link PreparedStatement}.
     * ^ Don't forget to use the try([resource]) {}
     * Use {@link #getTableName()} to get the table name.
     * DO NOT use any public functions provided by {@link DatabaseEntityBase} in here otherwise a deadlock could occur.
     *
     * @throws CheckedException An error has occurred.
     */
    @Override
    protected boolean checkRowExistence() throws CheckedException {
        try (PreparedStatement sta = conn.getStatement("SELECT COUNT(*) as rowCount FROM " + getTableName() + " WHERE DiscountPlanID = ?")) {
            sta.setLong(1, planID);
            ResultSet rs = sta.executeQuery();
            rs.next();
            int rc = rs.getInt("rowCount");
            rs.close();
            if (rc > 0) return true;
            else return false;
        } catch (SQLException throwables) {
            throw new CheckedException(throwables);
        }
    }

    public long getPlanID() {
        return planID;
    }

    public PlanType getPlanType() {
        return planType;
    }

    public void setPlanType(PlanType planType) {
        this.planType = planType;
    }

    public Decimal getPercentage() {
        return percentage;
    }

    public void setPercentage(Decimal percentage) {
        this.percentage = percentage;
    }
}
