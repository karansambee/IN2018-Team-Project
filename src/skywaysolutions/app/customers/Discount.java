package skywaysolutions.app.customers;

import skywaysolutions.app.database.DatabaseEntityBase;
import skywaysolutions.app.database.IDB_Connector;
import skywaysolutions.app.utils.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class Discount extends DatabaseEntityBase {
    private Long planID;
    private PlanType planType;
    private Decimal percentage;

    public Discount(IDB_Connector conn, Long id) {
        super(conn);
        planID = id;
    }

    public Discount(IDB_Connector conn, PlanType planType, Decimal percentage) {
        super(conn);
        planID = null;
        this.planType = planType;
        this.percentage = percentage;
    }

    public Discount(IDB_Connector conn, ResultSet rs, boolean locked) throws SQLException, CheckedException {
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
        return planID;
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
            if (planID == null) sta.setNull(1, Types.BIGINT); else sta.setLong(1, planID);
            sta.setInt(2, planType.getValue());
            if (percentage == null) sta.setNull(3, Types.NUMERIC); else sta.setDouble(3, percentage.getValue());
            sta.executeUpdate();
            if (planID == null) {
                try (PreparedStatement stac = conn.getStatement("SELECT MAX(DiscountPlanID) as maxID FROM "+getTableName())) {
                    try (ResultSet rs = stac.executeQuery()) {
                        if (!rs.next()) throw new CheckedException("No Insert Occurred!");
                        planID = rs.getLong("maxID");
                    }
                }
            }
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
        try (PreparedStatement sta = conn.getStatement("UPDATE " + getTableName() + " SET DiscountType  = ?, DiscountPercentage = ? WHERE DiscountPlanID = ?")) {
            sta.setInt(1, planType.getValue());
            if (percentage == null) sta.setNull(2, Types.NUMERIC); else sta.setDouble(2, percentage.getValue());
            sta.setLong(3, planID);
            sta.executeUpdate();
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
            try (ResultSet rs = sta.executeQuery()) {
                if (!rs.next()) throw new CheckedException("No Row Exists!");
                planID = rs.getLong("DiscountPlanID");
                planType = PlanType.getPlanTypeFromValue(rs.getInt("DiscountType"));
                Double dp = ResultSetNullableReturners.getDoubleValue(rs, "DiscountPercentage");
                percentage = (dp == null) ? null : new Decimal(dp, 6);
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
        planID = rs.getLong("DiscountPlanID");
        planType = PlanType.getPlanTypeFromValue(rs.getInt("DiscountType"));
        Double dp = ResultSetNullableReturners.getDoubleValue(rs, "DiscountPercentage");
        percentage = (dp == null) ? null : new Decimal(dp, 6);
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
            try (ResultSet rs = sta.executeQuery()) {
                if (!rs.next()) throw new CheckedException("No Row Exists!");
                return rs.getInt("rowCount") > 0;
            }
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
