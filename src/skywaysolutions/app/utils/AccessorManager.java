package skywaysolutions.app.utils;

import skywaysolutions.app.customers.CustomerController;
import skywaysolutions.app.customers.ICustomerAccessor;
import skywaysolutions.app.database.DatabaseBackupTable;
import skywaysolutions.app.database.IDB_Connector;
import skywaysolutions.app.rates.IRateAccessor;
import skywaysolutions.app.rates.RateController;
import skywaysolutions.app.reports.IReportAccessor;
import skywaysolutions.app.sales.ISalesAccessor;
import skywaysolutions.app.sales.SaleController;
import skywaysolutions.app.staff.AccountController;
import skywaysolutions.app.staff.IStaffAccessor;
import skywaysolutions.app.stock.BlankController;
import skywaysolutions.app.stock.IStockAccessor;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class provides the ability to manage the accessors.
 *
 * @author Alfred Manville
 */
public final class AccessorManager {
    public final IDB_Connector conn;
    public final IRateAccessor rateAccessor;
    public final IStaffAccessor staffAccessor;
    public final IStockAccessor stockAccessor;
    public final ICustomerAccessor customerAccessor;
    public final ISalesAccessor salesAccessor;
    public final IReportAccessor reportAccessor;
    public final String[] tables;
    public final String[] rTables;

    /**
     * Constructs a new AccessorManager with the specified database connection.
     *
     * @param conn The database connection.
     * @throws CheckedException An initialization error has occured.
     */
    public AccessorManager(IDB_Connector conn) throws CheckedException {
        this.conn = conn;
        conn.getTableList(true);
        this.rateAccessor = new RateController(conn);
        this.staffAccessor = new AccountController(conn);
        this.stockAccessor = new BlankController(conn);
        this.customerAccessor = new CustomerController(conn);
        this.salesAccessor = new SaleController(conn, rateAccessor, stockAccessor);
        this.reportAccessor = null; //TODO: Add report accessor instantiation
        ArrayList<String> tables = new ArrayList<>();
        tables.addAll(List.of(rateAccessor.getTables()));
        tables.addAll(List.of(staffAccessor.getTables()));
        tables.addAll(List.of(stockAccessor.getTables()));
        tables.addAll(List.of(customerAccessor.getTables()));
        tables.addAll(List.of(salesAccessor.getTables()));
        this.tables = tables.toArray(new String[0]);
        Collections.reverse(tables);
        rTables = tables.toArray(new String[0]);
    }

    /**
     * Forces the unlock of a specified table (Or all tables if null is passed).
     *
     * @param table The table name or null.
     * @throws CheckedException An error occurred during unlocking.
     */
    public void forceUnlock(String table) throws CheckedException {
        if (table != null) {
            rateAccessor.forceFullUnlock(table);
            staffAccessor.forceFullUnlock(table);
            stockAccessor.forceFullUnlock(table);
            customerAccessor.forceFullUnlock(table);
            salesAccessor.forceFullUnlock(table);
        } else {
            for (String c : tables) forceUnlock(c);
        }
    }

    /**
     * Forces the purge of a specified table (Or all tables if null is passed).
     *
     * @param table The table name or null.
     * @throws CheckedException An error occurred during purging.
     */
    public void forcePurge(String table) throws CheckedException {
        if (table != null) {
            rateAccessor.forceFullPurge(table);
            staffAccessor.forceFullPurge(table);
            stockAccessor.forceFullPurge(table);
            customerAccessor.forceFullPurge(table);
            salesAccessor.forceFullPurge(table);
        } else {
            for (String c : rTables) forcePurge(c);
        }
    }

    /**
     * Backups the database contents to a file.
     *
     * @param file The file to backup the database too.
     * @throws CheckedException An error occurred during backup.
     */
    public void backup(File file) throws CheckedException {
        try (FileOutputStream os = new FileOutputStream(file)) {
            for (String c : tables) DatabaseBackupTable.backup(c, conn, os);
        } catch (IOException e) {
            throw new CheckedException(e);
        }
    }

    /**
     * Restores the database contents from a file.
     *
     * @param file The file to restore the database from.
     * @throws CheckedException An error occurred during restore.
     */
    public void restore(File file) throws CheckedException {
        try (FileInputStream is = new FileInputStream(file)) {
            for (String c : tables) DatabaseBackupTable.restore(conn, is);
            forceUnlock(null);
        } catch (IOException e) {
            throw new CheckedException(e);
        }
    }
}
