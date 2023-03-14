package skywaysolutions.app.customers;

import skywaysolutions.app.utils.CheckedException;
import skywaysolutions.app.utils.PersonalInformation;

/**
 * Provides the customer accessor interface for the package.
 *
 * @author Alfred Manville
 */
public interface ICustomerAccessor {
    /**
     * Allows for an account to be created.
     *
     * @param info The personal information of the account.
     * @param plan The plan ID the account should use (Set to -1 for no plan).
     * @return The ID of the created account.
     * @throws CheckedException Account creation fails.
     */
    long createAccount(PersonalInformation info, long plan) throws CheckedException;

    /**
     * Gets the personal information of the account.
     *
     * @param customer The customer ID of the account.
     * @return The customer's personal information.
     * @throws CheckedException Retrieving personal information fails.
     */
    PersonalInformation getPersonalInformation(long customer) throws CheckedException;

    /**
     * Sets the personal information of the account.
     *
     * @param customer The customer ID of the account.
     * @param info The customer's new personal information.
     * @throws CheckedException Setting the personal information fails.
     */
    void setPersonalInformation(long customer, PersonalInformation info) throws CheckedException;

    /**
     * Gets the account plan ID.
     *
     * @param customer The customer ID.
     * @return The plan ID.
     * @throws CheckedException The plan ID could not be obtained.
     */
    long getAccountPlan(long customer) throws CheckedException;

    /**
     * Sets the account plan of a customer.
     *
     * @param customer The customer ID.
     * @param plan The plan ID.
     * @throws CheckedException The plan of the customer could not be set.
     */
    void setAccountPlan(long customer, long plan) throws CheckedException;

    /**
     * Deletes a customer account.
     *
     * @param customer The customer ID.
     * @throws CheckedException The deletion of the customer account fails.
     */
    void deleteAccount(long customer) throws CheckedException;

    /**
     * Lists all the customer account's IDs.
     *
     * @return An array of customer IDs.
     * @throws CheckedException The list of customers could not be retrieved.
     */
    long[] listAccounts() throws CheckedException;

    /**
     * Create a plan with a specific type and percentage.
     *
     * @param type The type of plan.
     * @param percentage The percentage of the discount.
     * @return The plan ID.
     * @throws CheckedException The plan creation operation fails.
     */
    long createPlan(PlanType type, double percentage) throws CheckedException;

    /**
     * Gets the plan type of the specified plan.
     *
     * @param plan The ID of the plan.
     * @return The plan type.
     * @throws CheckedException The plan retrieval fails.
     */
    PlanType getPlanType(long plan) throws CheckedException;

    /**
     * Gets the plan discount percentage.
     *
     * @param plan The plan ID.
     * @return The discount percentage.
     * @throws CheckedException
     */
    double getPlanPercentage(long plan) throws CheckedException;

    /**
     * Uses the specified plan on the given amount returning the result.
     *
     * @param plan The plan ID.
     * @param amount The amount to discount.
     * @return The discounted amount.
     * @throws CheckedException The retrieval of the plan failed.
     */
    double usePlan(long plan, double amount) throws CheckedException;

    /**
     * Removes the specified plan.
     *
     * @param plan The plan ID.
     * @throws CheckedException The plan removal operation failed.
     */
    void removePlan(long plan) throws CheckedException;
}
