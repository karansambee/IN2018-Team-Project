package skywaysolutions.app.customers;

import skywaysolutions.app.database.IDB_Connector;
import skywaysolutions.app.database.IFilterStatementCreator;
import skywaysolutions.app.database.MultiLoadSyncMode;
import skywaysolutions.app.utils.CheckedException;
import skywaysolutions.app.utils.Decimal;
import skywaysolutions.app.utils.MonthPeriod;
import skywaysolutions.app.utils.PersonalInformation;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CustomerController implements ICustomerAccessor {
    private IDB_Connector conn;
    private final Object slock = new Object();

    private final AliasFinder finder = new AliasFinder();

    private final CustomerTableAccessor customerTableAccessor;
    private final DiscountTableAccessor discountTableAccessor;
    private final FlexibleDiscountEntriesTableAccessor flexibleDiscountEntriesTableAccessor;


    public CustomerController(IDB_Connector conn) {
        this.conn = conn;
        this.customerTableAccessor = new CustomerTableAccessor(conn);
        this.discountTableAccessor = new DiscountTableAccessor(conn);
        this.flexibleDiscountEntriesTableAccessor = new FlexibleDiscountEntriesTableAccessor(conn);
    }


    /**
     * Allows for an account to be created.
     *
     * @param info   The personal information of the account.
     * @param planID The plan ID the account should use (Set to -1 for no plan).
     * @param alias  The alias of the account.
     * @param type   The type of the customer.
     * @return The ID of the created account.
     * @throws CheckedException Account creation fails.
     */
    @Override
    public long createAccount(PersonalInformation info, Long planID, boolean customerDiscountCredited,
                              String currency, String alias, CustomerType type) throws CheckedException {
        synchronized (slock) {
            Customer newAccount = new Customer(this.conn, info, planID, customerDiscountCredited, currency, alias, type);
            if (newAccount.exists(true)) {
                throw new CheckedException("Account already exists");
            } else {
                newAccount.store();
                newAccount.unlock();
                return newAccount.getPlanID();
            }
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
            account.lock();
            account.load();
            account.unlock();
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
            account.load();
            account.load();
            account.setInfo(info);
            account.store();
            account.unlock();
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
            account.lock();
            account.load();
            account.unlock();
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
            account.lock();
            account.load();
            account.setAlias(alias);
            account.store();
            account.unlock();
        }
    }

    /**
     * Returns Customer object from given alias
     *
     * @param alias
     * @param mode
     * @return
     * @throws CheckedException Account does not exist with given alias
     */
    private Customer getCustomerFromEmailAddress(String alias, MultiLoadSyncMode mode) throws CheckedException {
        finder.alias = alias;
        List<Customer> accounts = customerTableAccessor.loadMany(finder, mode);
        if (accounts.size() > 0) return accounts.get(0); else throw new CheckedException("Account Does Not Exist");
    }

    /**
     * Creates a filtered statement to search for alias in Customer table
     */
    private static class AliasFinder implements IFilterStatementCreator {
        public String alias;
        @Override
        public PreparedStatement createFilteredStatementFor(IDB_Connector conn, String startOfSQLTemplate) throws CheckedException {
            PreparedStatement sta = conn.getStatement(startOfSQLTemplate + "Alias = ?");
            sta.setString(1, alias);

            return sta;
        }
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
        return getCustomerFromEmailAddress(alias, MultiLoadSyncMode.UnlockAfterLoad).getCustomerID();
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
            account.lock();
            account.load();
            account.unlock();
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
            account.lock();
            account.load();
            account.setPlanID(plan);
            account.store();
            account.unlock();
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
            account.lock();
            account.delete();
        }
    }

    /**
     * Lists all the customer account's IDs.
     *
     * @return An array of customer IDs.
     * @throws CheckedException The list of customers could not be retrieved.
     */
    @Override
    public long[] listAccounts() throws CheckedException {
        synchronized (slock) {
            try(PreparedStatement pre = conn.getStatement(
                    "SELECT CustomerID FROM Customer")){
                try (ResultSet rs = pre.executeQuery()) {
                    ArrayList<Long> customerIDs = new ArrayList<>();
                    while (rs.next()) customerIDs.add(rs.getLong("CustomerID"));
                    return customerIDs.stream().mapToLong(Long::longValue).toArray();
                }
            } catch (SQLException | CheckedException throwables){
                throw new CheckedException(throwables);
            }
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
            account.lock();
            account.load();
            account.unlock();
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
            account.lock();
            account.load();
            account.setCustomerDiscountCredited(isDiscountCredited);
            account.store();
            account.unlock();
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
            account.lock();
            account.load();
            account.unlock();
            return account.getAccountDiscountCredit();
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
            account.lock();
            account.load();
            Decimal newAmount = account.getAccountDiscountCredit().add(amount);
            account.setAccountDiscountCredit(newAmount);
            account.store();
            account.unlock();
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
            account.load();
            if (date.getMonth() != account.getPurchaseMonthStart().getMonth() || date.getYear() != account.getPurchaseMonthStart().getYear()) {
                account.setPurchaseAccumulation(new Decimal(0));
                account.setPurchaseMonthStart(new Date());
                account.store();
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
            account.lock();
            account.load();

            if (date.getMonth() != account.getPurchaseMonthStart().getMonth() || date.getYear() != account.getPurchaseMonthStart().getYear()) {
                account.setPurchaseAccumulation(amount);
                account.setPurchaseMonthStart(new Date());
            } else {
                account.setPurchaseAccumulation(account.getPurchaseAccumulation().add(amount));
            }

            account.store();
            account.unlock();
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
            if (newPlan.exists(true)){
                throw new CheckedException("Discount Plan already exists");
            } else {
                newPlan.store();

                newPlan.lock();
                newPlan.load();
                newPlan.unlock();
                return newPlan.getPlanID();
            }
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
            discount.lock();
            discount.load();
            discount.unlock();
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
            discount.lock();
            discount.load();
            discount.unlock();
            return discount.getPercentage();
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
            discount.lock();
            discount.load();
            discount.setPercentage(percentage);
            discount.store();
            discount.unlock();
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
            discount.lock();
            discount.load();
            discount.unlock();
            return discount.getPercentage().mul(amount);
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
            discount.lock();
            discount.delete();
        }
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
        return null;
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
            synchronized (slock) {
                Customer account = new Customer(this.conn, customer);
                account.lock();
                account.load();
                account.unlock();
                return account.getCustomerType();
            }
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
            account.lock();
            account.load();
            account.setCustomerType(type);
            account.store();
            account.unlock();
        }
    }

    /**
     * Gets an array of tables that can be backed up.
     *
     * @return The array of tables.
     */
    @Override
    public String[] getTables() {
        return new String[] {"Customer", "DiscountPlan", "FlexibleDiscountEntries"};
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
            if (tableName.equals("Customer")) customerTableAccessor.unlockAll();
            else if (tableName.equals("DiscountPlan")) discountTableAccessor.unlockAll();
            else if (tableName.equals("FlexibleDiscountEntries")) flexibleDiscountEntriesTableAccessor.unlockAll();
        }
    }


}







