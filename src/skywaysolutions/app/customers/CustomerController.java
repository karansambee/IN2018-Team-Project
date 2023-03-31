package skywaysolutions.app.customers;

import skywaysolutions.app.utils.CheckedException;
import skywaysolutions.app.utils.Decimal;
import skywaysolutions.app.utils.PersonalInformation;

import java.util.Date;

public class CustomerController implements ICustomerAccessor {

    /**
     * Allows for an account to be created.
     *
     * @param info  The personal information of the account.
     * @param plan  The plan ID the account should use (Set to -1 for no plan).
     * @param alias The alias of the account.
     * @param type  The type of the customer.
     * @return The ID of the created account.
     * @throws CheckedException Account creation fails.
     */
    @Override
    public long createAccount(PersonalInformation info, long plan, String alias, CustomerType type) throws CheckedException {
        return 0;
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
        return null;
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
        return null;
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
        return 0;
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
        return 0;
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

    }

    /**
     * Deletes a customer account.
     *
     * @param customer The customer ID.
     * @throws CheckedException The deletion of the customer account fails.
     */
    @Override
    public void deleteAccount(long customer) throws CheckedException {

    }

    /**
     * Lists all the customer account's IDs.
     *
     * @return An array of customer IDs.
     * @throws CheckedException The list of customers could not be retrieved.
     */
    @Override
    public long[] listAccounts() throws CheckedException {
        return new long[0];
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
        return false;
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
        return null;
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
        return null;
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
        return 0;
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
        return null;
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
        return null;
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
        return null;
    }

    /**
     * Removes the specified plan.
     *
     * @param plan The plan ID.
     * @throws CheckedException The plan removal operation failed.
     */
    @Override
    public void removePlan(long plan) throws CheckedException {

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
        return new FlexiblePlanRange[0];
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
        return null;
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

    }

    /**
     * Gets an array of tables that can be backed up.
     *
     * @return The array of tables.
     */
    @Override
    public String[] getTables() {
        return new String[0];
    }

    /**
     * Forces a table to be fully unlocked.
     *
     * @param tableName The table to fully unlock.
     * @throws CheckedException The table could not be unlocked.
     */
    @Override
    public void forceFullUnlock(String tableName) throws CheckedException {

    }
}
