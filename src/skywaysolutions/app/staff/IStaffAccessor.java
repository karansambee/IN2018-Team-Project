package skywaysolutions.app.staff;

import skywaysolutions.app.utils.CheckedException;
import skywaysolutions.app.utils.Decimal;
import skywaysolutions.app.utils.IRepairable;
import skywaysolutions.app.utils.PersonalInformation;

/**
 * Provides a staff accessor interface to manage and authenticate staff accounts.
 *
 * @author Alfred Manville
 */
public interface IStaffAccessor extends IRepairable {

    /**
     * Assures the default administrator account of ID 1 exists.
     *
     * @throws CheckedException An assurance error has occurred.
     */
    void assureDefaultAdministratorAccount() throws CheckedException;

    /**
     * Gets the logged in account ID, null if no account logged in.
     *
     * @return The logged in account ID or null.
     */
    Long getLoggedInAccountID();

    /**
     * Gets the logged in account email, null if no account is logged in.
     *
     * @return The logged in account email or null.
     */
    String getLoggedInAccountEmail();

    /**
     * Creates an account with the specified personal information, role, commission rate,
     * local currency and password. An ID can also be specified for creation.
     *
     * @param info The personal information of the user.
     * @param role The role of the user.
     * @param commission The commission rate percentage of the user.
     * @param currency The local currency of the user.
     * @param password The user's password.
     * @param id The user's ID, null to generate a new one.
     * @throws CheckedException Account creation fails.
     */
    long createAccount(PersonalInformation info, StaffRole role, Decimal commission, String currency, String password, Long id) throws CheckedException;

    /**
     * Authenticates an account with the specified emailAddress and password.
     *
     * @param emailAddress The email address to authenticate as.
     * @param password The password to authenticate with.
     * @return If the authentication was successful.
     * @throws CheckedException Accessing account information failed.
     */
    boolean authenticateAccount(String emailAddress, String password) throws CheckedException;

    /**
     * Logs out the current logged in account.
     *
     * @return Whether the current account was logged out.
     */
    boolean logoutAccount();

    /**
     * Changes the password of an account.
     *
     * @param emailAddress The email address of the account (Null for the current logged-in account).
     * @param password The new password for the account.
     * @throws CheckedException Changing the account password has failed.
     */
    void changePassword(String emailAddress, String password) throws CheckedException;

    /**
     * Clears the password of an account.
     *
     * @param emailAddress The email address of the account.
     * @throws CheckedException Changing the account password has failed.
     */
    void clearPassword(String emailAddress) throws CheckedException;

    /**
     * Gets the personal information of an account.
     *
     * @param emailAddress The email address of the account (Null for the current logged-in account).
     * @return The PersonalInformation of the account.
     * @throws CheckedException Retrieving the personal information has failed.
     */
    PersonalInformation getPersonalInformation(String emailAddress) throws CheckedException;

    /**
     * Sets the personal information of an account,
     * the email address within the {@link PersonalInformation} class is ignored.
     *
     * @param emailAddress The email address of the account (Null for the current logged-in account).
     * @param info The new set of PersonalInformation.
     * @throws CheckedException Storing the personal information has failed.
     */
    void setPersonalInformation(String emailAddress, PersonalInformation info) throws CheckedException;

    /**
     * Gets the account ID.
     *
     * @param emailAddress The email address of the account (Null for the current logged-in account).
     * @return The account ID.
     * @throws CheckedException The account could not be retrieved.
     */
    long getAccountID(String emailAddress) throws CheckedException;

    /**
     * Gets the account staff role.
     *
     * @param emailAddress The email address of the account (Null for the current logged-in account).
     * @return The staff role.
     * @throws CheckedException The account could not be retrieved.
     */
    StaffRole getAccountRole(String emailAddress) throws CheckedException;

    /**
     * Sets the account staff role.
     *
     * @param emailAddress The email address of the account (Null for the current logged-in account).
     * @param role The staff role.
     * @throws CheckedException The account could not be stored.
     */
    void setAccountRole(String emailAddress, StaffRole role) throws CheckedException;

    /**
     * Gets an array of all account email addresses for the specific role.
     *
     * @param role The role to filter by.
     * @return An array of account email addresses.
     */
    String[] listAccounts(StaffRole role) throws CheckedException;

    /**
     * Deletes an account given the email address.
     *
     * @param emailAddress The email address of the account to delete.
     * @throws CheckedException The account deletion operation failed.
     */
    void deleteAccount(String emailAddress) throws CheckedException;

    /**
     * Gets the commission rate of an account.
     *
     * @param emailAddress The email address of the account.
     * @return The commission rate percentage.
     * @throws CheckedException The commission rate could not be retrieved.
     */
    Decimal getCommission(String emailAddress) throws CheckedException;

    /**
     * Sets the commission rate of an account.
     *
     * @param emailAddress The email address of the account.
     * @param commission The new commission rate percentage of the account.
     * @throws CheckedException The commission rate storage operation failed.
     */
    void setCommission(String emailAddress, Decimal commission) throws CheckedException;

    /**
     * Gets the local currency of an account.
     *
     * @param emailAddress The email address of the account (Null for the current logged-in account).
     * @return The local currency name.
     * @throws CheckedException The account currency could not be retrieved.
     */
    String getCurrency(String emailAddress) throws CheckedException;

    /**
     * Sets the local currency of an account.
     *
     * @param emailAddress The email address of the account (Null for the current logged-in account).
     * @param currency The new local currency of the account.
     * @throws CheckedException The local currency update operation failed.
     */
    void setCurrency(String emailAddress, String currency) throws CheckedException;
}
