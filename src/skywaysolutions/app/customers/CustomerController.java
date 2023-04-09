package skywaysolutions.app.customers;

import skywaysolutions.app.database.IDB_Connector;
import skywaysolutions.app.database.IFilterStatementCreator;
import skywaysolutions.app.database.MultiLoadSyncMode;
import skywaysolutions.app.utils.*;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class CustomerController implements ICustomerAccessor {
    private final IDB_Connector conn;
    private final Object slock = new Object();
    private final AliasFinder finder = new AliasFinder();
    private final FlexiblePlanFilter flexiblePlanFilter = new FlexiblePlanFilter();
    private final CustomerFilter customerFilter = new CustomerFilter();
    private final DiscountFilter discountFilter = new DiscountFilter();
    private final CustomerTableAccessor customerTableAccessor;
    private final DiscountTableAccessor discountTableAccessor;
    private final FlexibleDiscountEntriesTableAccessor flexibleDiscountEntriesTableAccessor;

    public CustomerController(IDB_Connector conn) throws CheckedException {
        this.conn = conn;
        customerTableAccessor = new CustomerTableAccessor(conn);
        discountTableAccessor = new DiscountTableAccessor(conn);
        flexibleDiscountEntriesTableAccessor = new FlexibleDiscountEntriesTableAccessor(conn);
        conn.getTableList(true);
        discountTableAccessor.assureTableSchema();
        flexibleDiscountEntriesTableAccessor.assureTableSchema();
        customerTableAccessor.assureTableSchema();
    }

    /**
     * Allows for an account to be created.
     *
     * @param info The personal information of the account.
     * @param planID The plan ID the account should use (Set to -1 for no plan).
     * @param customerDiscountCredited If the customer is credited by storing the discount.
     * @param currency The local currency of the customer.
     * @param alias The alias of the account.
     * @param type The type of the customer.
     * @return The ID of the created account.
     * @throws CheckedException Account creation fails.
     */
    @Override
    public long createAccount(PersonalInformation info, Long planID, boolean customerDiscountCredited,
                              String currency, String alias, CustomerType type) throws CheckedException {
        synchronized (slock) {
            if (alias == null || alias.equals("")) alias = info.getFirstName() + info.getLastName() + Time.now().getTime();
            Customer aliasAccount = getCustomerFromAlias(alias, true);
            if (aliasAccount == null) throw new CheckedException("Account already exists");
            Customer newAccount = new Customer(this.conn, info, planID, customerDiscountCredited, currency, alias, type);
            newAccount.store();
            return newAccount.getPlanID();
        }
    }

    /**
     * Gets the personal information of the account.
     *
     * @param customer The customer ID of the account.
     * @return The customer's personal information.
     * @throws CheckedException Retrieving personal information fails.
     */
    @Override
    public PersonalInformation getPersonalInformation(long customer) throws CheckedException {
        synchronized (slock) {
            Customer account = new Customer(this.conn, customer);
            try {
                account.lock();
                account.load();
            } finally {
                account.unlock();
            }
            return account.getInfo();
        }
    }


    /**
     * Sets the personal information of the account.
     *
     * @param customer The customer ID of the account.
     * @param info     The customer's new personal information.
     * @throws CheckedException Setting the personal information fails.
     */
    @Override
    public void setPersonalInformation(long customer, PersonalInformation info) throws CheckedException {
        synchronized (slock) {
            Customer account = new Customer(this.conn, customer);
            try {
                account.lock();
                account.load();
                account.setInfo(info);
                account.store();
            } finally {
                account.unlock();
            }
        }
    }

    /**
     * Gets the account alias of the specified account.
     *
     * @param customer The customer ID of the account.
     * @return The customer alias.
     * @throws CheckedException Getting the customer information has failed.
     */
    @Override
    public String getAccountAlias(long customer) throws CheckedException {
        synchronized (slock) {
            Customer account = new Customer(this.conn, customer);
            try {
                account.lock();
                account.load();
            } finally {
                account.unlock();
            }
            return account.getAlias();
        }
    }

    /**
     * Sets the account alias of the specified account.
     *
     * @param customer The customer ID of the account.
     * @param alias    The customer alias.
     * @throws CheckedException Setting the customer information has failed.
     */
    @Override
    public void setAccountAlias(long customer, String alias) throws CheckedException {
        synchronized (slock) {
            Customer account = new Customer(this.conn, customer);
            try {
                account.lock();
                account.load();
                account.setAlias(alias);
                account.store();
            } finally {
                account.unlock();
            }
        }
    }

    private Customer getCustomerFromAlias(String alias, boolean nullOnFail) throws CheckedException {
        finder.alias = alias;
        List<Customer> accounts = customerTableAccessor.loadMany(finder, MultiLoadSyncMode.UnlockAfterLoad);
        if (accounts.size() > 0) return accounts.get(0); else if (nullOnFail) return null; else throw new CheckedException("Account Does Not Exist");
    }

    /**
     * Gets the account ID give the alias.
     *
     * @param alias The alias of the account.
     * @return The customer ID.
     * @throws CheckedException A customer ID did not correspond to the alias.
     */
    @Override
    public long getAccountIDGivenAlias(String alias) throws CheckedException {
        synchronized (slock) {
            return getCustomerFromAlias(alias, false).getCustomerID();
        }
    }

    /**
     * Gets the account plan ID.
     *
     * @param customer The customer ID.
     * @return The plan ID.
     * @throws CheckedException The plan ID could not be obtained.
     */
    @Override
    public long getAccountPlan(long customer) throws CheckedException {
        synchronized (slock) {
            Customer account = new Customer(this.conn, customer);
            try {
                account.lock();
                account.load();
            } finally {
                account.unlock();
            }
            return account.getPlanID();
        }
    }

    /**
     * Sets the account plan of a customer.
     *
     * @param customer The customer ID.
     * @param plan     The plan ID.
     * @throws CheckedException The plan of the customer could not be set.
     */
    @Override
    public void setAccountPlan(long customer, long plan) throws CheckedException {
        synchronized (slock) {
            Customer account = new Customer(this.conn, customer);
            try {
                account.lock();
                account.load();
                account.setPlanID(plan);
                account.store();
            } finally {
                account.unlock();
            }
        }
    }

    /**
     * Deletes a customer account.
     *
     * @param customer The customer ID.
     * @throws CheckedException The deletion of the customer account fails.
     */
    @Override
    public void deleteAccount(long customer) throws CheckedException {
        synchronized (slock) {
            Customer account = new Customer(this.conn, customer);
            try {
                account.lock();
                account.delete();
            } finally {
                account.unlock();
            }
        }
    }

    /**
     * Lists all the customer account's IDs of the specified type.
     *
     * @param type Customer type.
     * @return An array of customer IDs.
     * @throws CheckedException The list of customers could not be retrieved.
     */
    @Override
    public long[] listAccounts(CustomerType type) throws CheckedException {
        synchronized (slock) {
            customerFilter.type = type;
            List<Customer> customers = customerTableAccessor.loadMany(customerFilter, MultiLoadSyncMode.NoLoad);
            long[] ids = new long[customers.size()];
            for (int i = 0; i < ids.length; i++) ids[i] = customers.get(i).getCustomerID();
            return ids;
        }
    }

    /**
     * This checks if the customer is accumulating discount credit.
     * <p>
     * If false the discounts should be applied to each purchase.
     * If true the discounts should be {@link #addCustomerDiscountCredit(long, Decimal)} and the full price paid.
     * </p>
     *
     * @param customer The customer ID.
     * @return If the customer is accumulating discount credit.
     * @throws CheckedException The customer information could not be retrieved.
     */
    @Override
    public boolean isCustomerDiscountCredited(long customer) throws CheckedException {
        synchronized (slock) {
            Customer account = new Customer(this.conn, customer);
            try {
                account.lock();
                account.load();
            } finally {
                account.unlock();
            }
            return account.isCustomerDiscountCredited();
        }
    }

    /**
     * Sets if the customer is to accumulate discount credit.
     * <p>
     * If false the discounts should be applied to each purchase.
     * If true the discounts should be {@link #addCustomerDiscountCredit(long, Decimal)} and the full price paid.
     * </p>
     *
     * @param customer           The customer ID.
     * @param isDiscountCredited If the customer should accumulate discount credit.
     * @throws CheckedException The customer information could not be stored.
     */
    @Override
    public void setIfCustomerIsDiscountCredited(long customer, boolean isDiscountCredited) throws CheckedException {
        synchronized (slock) {
            Customer account = new Customer(this.conn, customer);
            try {
                account.lock();
                account.load();
                account.setCustomerDiscountCredited(isDiscountCredited);
                account.store();
            } finally {
                account.unlock();
            }
        }
    }

    /**
     * Gets the customer discount credit, zeroing it if told to take it.
     *
     * @param customer The customer ID.
     * @param take     If the credit should be considered given.
     * @return The value of the discount credit.
     * @throws CheckedException The customer information could not be retrieved.
     */
    @Override
    public Decimal getCustomerDiscountCredit(long customer, boolean take) throws CheckedException {
        synchronized (slock) {
            Customer account = new Customer(this.conn, customer);
            try {
                account.lock();
                account.load();
                if (account.isCustomerDiscountCredited()) {
                    if (take) {
                        Decimal credit = account.getAccountDiscountCredit();
                        account.setAccountDiscountCredit(new Decimal());
                        account.store();
                        return credit;
                    } else {
                        return account.getAccountDiscountCredit();
                    }
                } else {
                    return new Decimal(0);
                }
            } finally {
                account.unlock();
            }
        }
    }

    /**
     * Adds more customer discount credit.
     *
     * @param customer The customer ID.
     * @param amount   The amount of credit.
     * @throws CheckedException The customer information could not be stored.
     */
    @Override
    public void addCustomerDiscountCredit(long customer, Decimal amount) throws CheckedException {
        synchronized (slock) {
            Customer account = new Customer(this.conn, customer);
            try {
                account.lock();
                account.load();
                if (account.isCustomerDiscountCredited()) {
                    account.setAccountDiscountCredit(account.getAccountDiscountCredit().add(amount));
                    account.store();
                }
            } finally {
                account.unlock();
            }
        }
    }

    /**
     * Gets the monthly purchase accumulation used for flexible discount plans.
     * If the given [Could be current] date is in a different month to the stored amount,
     * the accumulated value should be zeroed and the current month set as the stored month.
     *
     * @param customer The customer ID.
     * @param date     The given [Could be current] date.
     * @return The current monthly purchase accumulation amount.
     * @throws CheckedException The customer information could not be retrieved or updated.
     */
    @Override
    public Decimal getMonthlyPurchaseAccumulation(long customer, Date date) throws CheckedException {
        synchronized (slock) {
            Customer account = new Customer(this.conn, customer);
            try {
                account.lock();
                account.load();
                if (!new MonthPeriod(date).equals(new MonthPeriod(account.getPurchaseMonthStart()))) {
                    account.setPurchaseAccumulation(new Decimal(0));
                    account.setPurchaseMonthStart(new MonthPeriod(Time.now()).getThisMonth());
                    account.store();
                }
            } finally {
                account.unlock();
            }
            return account.getPurchaseAccumulation();
        }
    }


    /**
     * Adds a purchase to the monthly purchase accumulation.
     * If the given [Could be current] date is in a different month to the stored amount,
     * the accumulated value should be zeroed and the current month set as the stored month,
     * then the amount is added.
     *
     * @param customer The customer ID.
     * @param date     The given [Could be current] date.
     * @param amount   The amount to add.
     * @throws CheckedException The customer information could not be updated.
     */
    @Override
    public void addPurchase(long customer, Date date, Decimal amount) throws CheckedException {
        synchronized (slock) {
            Customer account = new Customer(this.conn, customer);
            try {
                account.lock();
                account.load();
                if (new MonthPeriod(date).equals(new MonthPeriod(account.getPurchaseMonthStart())))
                    account.setPurchaseAccumulation(account.getPurchaseAccumulation().add(amount));
                else {
                    account.setPurchaseAccumulation(amount);
                    account.setPurchaseMonthStart(new MonthPeriod(Time.now()).getThisMonth());
                }
                account.store();
            } finally {
                account.unlock();
            }
        }
    }

    /**
     * Lists all the available discount plan IDs given the type of plan.
     *
     * @param type The type of plan.
     * @return An array of plan IDs.
     * @throws CheckedException The list of plans could not be retrieved.
     */
    @Override
    public long[] listPlans(PlanType type) throws CheckedException {
        synchronized (slock) {
            discountFilter.type = type;
            List<Discount> discounts = discountTableAccessor.loadMany(discountFilter, MultiLoadSyncMode.NoLoad);
            long[] ids = new long[discounts.size()];
            for (int i = 0; i < ids.length; i++) ids[i] = discounts.get(i).getPlanID();
            return ids;
        }
    }

    /**
     * Create a plan with a specific type and percentage.
     *
     * @param type       The type of plan.
     * @param percentage The percentage of the discount.
     * @return The plan ID.
     * @throws CheckedException The plan creation operation fails.
     */
    @Override
    public long createPlan(PlanType type, Decimal percentage) throws CheckedException {
        synchronized (slock) {
            Discount newPlan = new Discount(this.conn, type, percentage);
            newPlan.store();
            return newPlan.getPlanID();
        }
    }

    /**
     * Gets the plan type of the specified plan.
     *
     * @param plan The ID of the plan.
     * @return The plan type.
     * @throws CheckedException The plan retrieval fails.
     */
    @Override
    public PlanType getPlanType(long plan) throws CheckedException {
        synchronized (slock) {
            Discount discount = new Discount(this.conn, plan);
            try {
                discount.lock();
                discount.load();
            } finally {
                discount.unlock();
            }
            return discount.getPlanType();
        }
    }

    /**
     * Gets the plan discount percentage.
     *
     * @param plan The plan ID.
     * @return The discount percentage.
     * @throws CheckedException The retrieval of the plan fails.
     */
    @Override
    public Decimal getPlanPercentage(long plan) throws CheckedException {
        synchronized (slock) {
            Discount discount = new Discount(this.conn, plan);
            try {
                discount.lock();
                discount.load();
            } finally {
                discount.unlock();
            }
            return (discount.getPlanType() == PlanType.FixedDiscount) ? discount.getPercentage() : new Decimal();
        }
    }

    /**
     * Sets the plan's discount percentage.
     *
     * @param plan       The plan ID.
     * @param percentage The new discount percentage.
     * @throws CheckedException The storing of the plan fails.
     */
    @Override
    public void setPlanPercentage(long plan, Decimal percentage) throws CheckedException {
        synchronized (slock) {
            Discount discount = new Discount(this.conn, plan);
            try {
                discount.lock();
                discount.load();
                if (discount.getPlanType() == PlanType.FixedDiscount) {
                    discount.setPercentage(percentage);
                    discount.store();
                }
            } finally {
                discount.unlock();
            }
        }
    }

    /**
     * Uses the specified plan on the given amount returning the result.
     *
     * @param plan   The plan ID.
     * @param amount The amount to discount.
     * @return The discounted amount.
     * @throws CheckedException The retrieval of the plan failed.
     */
    @Override
    public Decimal usePlan(long plan, Decimal amount) throws CheckedException {
        synchronized (slock) {
            Discount discount = new Discount(this.conn, plan);
            try {
                discount.lock();
                discount.load();
            } finally {
                discount.unlock();
            }
            if (discount.getPlanType() == PlanType.FixedDiscount) {
                return amount.mul(new Decimal(1, 0).sub(discount.getPercentage().mul(new Decimal(0.01, 2))));
            } else{
                flexiblePlanFilter.PlanID = discount.getPlanID();
                flexiblePlanFilter.range = new FlexiblePlanRange(amount, amount);
                List<FlexibleDiscountEntry> entries = flexibleDiscountEntriesTableAccessor.loadMany(flexiblePlanFilter, MultiLoadSyncMode.NoLoad);
                if (entries.size() > 0) {
                    FlexibleDiscountEntry entry = entries.get(0);
                    try {
                        entry.lock();
                        entry.load();
                    } finally {
                        entry.unlock();
                    }
                    if (entry.getRange().inRange(amount)) return amount.mul(new Decimal(1, 0).sub(entry.getPercentage().mul(new Decimal(0.01, 2))));
                }
            }
            throw new CheckedException("Could not retrieve plan");
        }
    }

    /**
     * Removes the specified plan.
     *
     * @param plan The plan ID.
     * @throws CheckedException The plan removal operation failed.
     */
    @Override
    public void removePlan(long plan) throws CheckedException {
        synchronized (slock) {
            Discount discount = new Discount(this.conn, plan);
            try {
                discount.lock();
                discount.load();
            } finally {
                discount.unlock();
            }
            if (discount.getPlanType() == PlanType.FlexibleDiscount) {
                List<FlexibleDiscountEntry> entries = getFlexiblePlanEntries(plan, null);
                for (FlexibleDiscountEntry c : entries) {
                    try {
                        c.lock();
                        c.delete();
                    } finally {
                        c.unlock();
                    }
                }
            }
            discount.delete();
        }
    }

    private List<FlexibleDiscountEntry> getFlexiblePlanEntries(long plan, FlexiblePlanRange range) throws CheckedException {
        flexiblePlanFilter.PlanID = plan;
        flexiblePlanFilter.range = range;
        return flexibleDiscountEntriesTableAccessor.loadMany(flexiblePlanFilter, MultiLoadSyncMode.UnlockAfterLoad);
    }

    /**
     * Gets the list of flexible plan ranges applied to a flexible plan.
     *
     * @param plan The plan ID.
     * @return The ranges.
     * @throws CheckedException The retrieval of the flexible plans failed.
     */
    @Override
    public FlexiblePlanRange[] getFlexiblePlanRanges(long plan) throws CheckedException {
        synchronized (slock) {
            List<FlexibleDiscountEntry> entries = getFlexiblePlanEntries(plan, null);
            FlexiblePlanRange[] ranges = new FlexiblePlanRange[entries.size()];
            for (int i = 0; i < ranges.length; i++) ranges[i] = entries.get(i).getRange();
            return ranges;
        }
    }

    /**
     * Creates or updates a flexible plan entry with the specified percentage.
     *
     * @param plan       The plan ID.
     * @param range      The flexible plan range.
     * @param percentage The percentage to store.
     * @throws CheckedException Storing the flexible plan entry has failed.
     */
    @Override
    public void createOrUpdateFlexiblePlanEntry(long plan, FlexiblePlanRange range, Decimal percentage) throws CheckedException {
        synchronized (slock) {
            List<FlexibleDiscountEntry> entries = getFlexiblePlanEntries(plan, range);
            if (entries.size() == 0) {
                FlexibleDiscountEntry entry = new FlexibleDiscountEntry(conn, plan, range, percentage);
                entry.store();
            } else {
                boolean stored = false;
                for (FlexibleDiscountEntry c : entries) if (c.getRange().equals(range)) {
                    try {
                        c.lock();
                        c.setPercentage(percentage);
                        c.store();
                    } finally {
                        c.unlock();
                    }
                    stored = true;
                    break;
                }
                if (!stored) throw new CheckedException("Flexible Range Entry does not exist as this range");
            }
        }
    }

    /**
     * Removes a flexible plan entry.
     *
     * @param plan  The plan ID.
     * @param range The flexible plan range.
     * @throws CheckedException Removing the flexible plan entry has failed.
     */
    @Override
    public void removeFlexiblePlanRange(long plan, FlexiblePlanRange range) throws CheckedException {
        synchronized (slock) {
            List<FlexibleDiscountEntry> entries = getFlexiblePlanEntries(plan, range);
            for (FlexibleDiscountEntry c : entries) if (c.getRange().equals(range)) {
                try {
                    c.lock();
                    c.delete();
                } finally {
                    c.unlock();
                }
            }
        }
    }

    /**
     * Gets a flexible plan entry percentage.
     *
     * @param plan  The plan ID.
     * @param range The flexible plan range.
     * @return The discount percentage.
     * @throws CheckedException Retrieving the flexible plan has failed.
     */
    @Override
    public Decimal getFlexiblePlanEntry(long plan, FlexiblePlanRange range) throws CheckedException {
        synchronized (slock) {
            List<FlexibleDiscountEntry> entries = getFlexiblePlanEntries(plan, range);
            if (entries.size() > 0) return entries.get(0).getPercentage(); else throw new CheckedException("Flexible Plan Range Not Found");
        }
    }

    /**
     * Gets the type of customer.
     *
     * @param customer The customer to check.
     * @return The type of customer.
     * @throws CheckedException Retrieving customer information has failed.
     */
    @Override
    public CustomerType getCustomerType(long customer) throws CheckedException {
        synchronized (slock) {
            Customer account = new Customer(this.conn, customer);
            try {
                account.lock();
                account.load();
            } finally {
                account.unlock();
            }
            return account.getCustomerType();
        }
    }

    /**
     * Sets the type of customer.
     *
     * @param customer The customer to switch types.
     * @param type     The new type of customer.
     * @throws CheckedException Storing the customer information has failed.
     */
    @Override
    public void setCustomerType(long customer, CustomerType type) throws CheckedException {
        synchronized (slock) {
            Customer account = new Customer(this.conn, customer);
            try {
                account.lock();
                account.load();
                account.setCustomerType(type);
                account.store();
            } finally {
                account.unlock();
            }
        }
    }

    /**
     * Gets an array of tables that can be backed up.
     *
     * @return The array of tables.
     */
    @Override
    public String[] getTables() {
        return new String[] {"DiscountPlan", "FlexibleDiscountEntries", "Customer"};
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
            switch (tableName) {
                case "Customer" -> customerTableAccessor.unlockAll(true);
                case "DiscountPlan" -> discountTableAccessor.unlockAll(true);
                case "FlexibleDiscountEntries" -> flexibleDiscountEntriesTableAccessor.unlockAll(true);
            }
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
            switch (tableName) {
                case "Customer" -> customerTableAccessor.purgeTableSchema();
                case "DiscountPlan" -> discountTableAccessor.purgeTableSchema();
                case "FlexibleDiscountEntries" -> flexibleDiscountEntriesTableAccessor.purgeTableSchema();
            }
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
            switch (tableName) {
                case "Customer" -> customerTableAccessor.assureTableSchema();
                case "DiscountPlan" -> discountTableAccessor.assureTableSchema();
                case "FlexibleDiscountEntries" -> flexibleDiscountEntriesTableAccessor.assureTableSchema();
            }
        }
    }

    /**
     * Creates a filtered statement to search for customers of a specified type.
     *
     * @author Alfred Manville
     */
    private static class CustomerFilter implements IFilterStatementCreator {
        public CustomerType type;
        @Override
        public PreparedStatement createFilteredStatementFor(IDB_Connector conn, String startOfSQLTemplate) throws SQLException, CheckedException {
            PreparedStatement sta = conn.getStatement((type == CustomerType.Any) ? startOfSQLTemplate.substring(0, startOfSQLTemplate.length() - 6) + "ORDER BY Alias" :
                    startOfSQLTemplate+"CustomerType = ? ORDER BY Alias");
            if (type != CustomerType.Any) sta.setInt(1, type.getValue());
            return sta;
        }
    }

    /**
     * Creates a filtered statement to search for discounts of a specified type.
     *
     * @author Alfred Manville
     */
    private static class DiscountFilter implements IFilterStatementCreator {
        public PlanType type;
        @Override
        public PreparedStatement createFilteredStatementFor(IDB_Connector conn, String startOfSQLTemplate) throws SQLException, CheckedException {
            PreparedStatement sta = conn.getStatement((type == PlanType.Any) ? startOfSQLTemplate.substring(0, startOfSQLTemplate.length() - 7) :
                    startOfSQLTemplate+"DiscountType = ?");
            if (type != PlanType.Any) sta.setInt(1, type.getValue());
            return sta;
        }
    }

    /**
     * Creates a filtered statement to search for alias in Customer table.
     *
     * @author Karan Sambee
     */
    private static class AliasFinder implements IFilterStatementCreator {
        public String alias;
        @Override
        public PreparedStatement createFilteredStatementFor(IDB_Connector conn, String startOfSQLTemplate) throws CheckedException, SQLException {
            PreparedStatement sta = conn.getStatement(startOfSQLTemplate + "Alias = ?");
            sta.setString(1, alias);
            return sta;
        }
    }

    /**
     * This class provides the ability to filter for the specific discount in the specific range.
     *
     * @author Alfred Manville
     */
    private static class FlexiblePlanFilter implements IFilterStatementCreator {
        public long PlanID;
        public FlexiblePlanRange range;

        /**
         * Gets a prepared statement from the specified connection,
         * using the passed string as the beginning of the SQL template.
         * <p>
         * The statement will always begin with "SELECT * FROM [TABLE NAME] WHERE ",
         * EG: "SELECT * FROM test WHERE " where the table here is test.
         * </p>
         * @param conn The database connection.
         * @param startOfSQLTemplate The start of the SQL Template to use for the statement.
         * @return The prepared statement with the filters and their parameters applied.
         * @throws SQLException An SQL error occurred.
         * @throws CheckedException An error occurred.
         */
        @Override
        public PreparedStatement createFilteredStatementFor(IDB_Connector conn, String startOfSQLTemplate) throws SQLException, CheckedException {
            PreparedStatement sta = conn.getStatement(startOfSQLTemplate+"DiscountPlanID = ?" + ((range == null) ? "" :
                    " AND ((AmountLowerBound <= ? AND AmountUpperBound > ?) OR (AmountLowerBound < ? AND AmountUpperBound => ?))") + " ORDER BY AmountLowerBound");
            sta.setLong(1, PlanID);
            if (range != null) {
                sta.setDouble(2, range.getLower().getValue());
                sta.setDouble(3, range.getLower().getValue());
                sta.setDouble(4, range.getUpper().getValue());
                sta.setDouble(5, range.getUpper().getValue());
            }
            return sta;
        }
    }
}







