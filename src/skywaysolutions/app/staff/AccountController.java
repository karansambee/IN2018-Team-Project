package skywaysolutions.app.staff;

import skywaysolutions.app.database.IDB_Connector;
import skywaysolutions.app.staff.IStaffAccessor;
import skywaysolutions.app.staff.StaffRole;
import skywaysolutions.app.utils.CheckedException;
import skywaysolutions.app.utils.Decimal;
import skywaysolutions.app.utils.PersonalInformation;

import java.net.Authenticator;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountController implements IStaffAccessor {
    private IDB_Connector conn;
    private Account currentAccount;
    private Object slock;

    public AccountController(IDB_Connector conn){
        this.conn = conn;
    }

    /**
     * Creates an account with the specified personal information, role, commission rate,
     * local currency and password. An ID can also be specified for creation.
     *
     * @param info       The personal information of the user.
     * @param role       The role of the user.
     * @param commission The commission rate percentage of the user.
     * @param currency   The local currency of the user.
     * @param password   The user's password.
     * @param id         The user's ID, null to generate a new one.
     * @throws CheckedException Account creation fails.
     */
    @Override
    public long createAccount(PersonalInformation info, StaffRole role, Decimal commission, String currency, String password, Long id) throws CheckedException {
        Account newAccount = new Account(this.conn, info, role, commission,currency, new PasswordString(password, PasswordString.getRandomSalt()), id);
        if (newAccount.exists(true)){
            throw new CheckedException("Account already exists");
        } else {
            newAccount.store();
            return newAccount.getAccountID();
        }
    }

    /**
     * Authenticates an account with the specified emailAddress and password.
     *
     * @param emailAddress The email address to authenticate as.
     * @param password     The password to authenticate with.
     * @return If the authentication was successful.
     * @throws CheckedException Accessing account information failed.
     */
    @Override
    public boolean authenticateAccount(String emailAddress, String password) throws CheckedException {
        Long ID = Account.getID(conn, emailAddress);
        Account newAccount = new Account(conn, ID);
        newAccount.load();
        if (newAccount.getPassword().checkPassword(password)){
            currentAccount = newAccount;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Logs out the current logged in account.
     *
     * @return Whether the current account was logged out.
     */
    @Override
    public boolean logoutAccount() {
        if (currentAccount == null) return false;
        currentAccount = null;
        return true;
    }

    /**
     * Changes the password of an account.
     *
     * @param emailAddress The email address of the account (Null for the current logged-in account).
     * @param password     The new password for the account.
     * @throws CheckedException Changing the account password has failed.
     */
    @Override
    public void changePassword(String emailAddress, String password) throws CheckedException {

    }

    /**
     * Gets the personal information of an account.
     *
     * @param emailAddress The email address of the account (Null for the current logged-in account).
     * @return The PersonalInformation of the account.
     * @throws CheckedException Retrieving the personal information has failed.
     */
    @Override
    public PersonalInformation getPersonalInformation(String emailAddress) throws CheckedException {
        return null;
    }

    /**
     * Sets the personal information of an account,
     * the email address within the {@link PersonalInformation} class is ignored.
     *
     * @param emailAddress The email address of the account (Null for the current logged-in account).
     * @param info         The new set of PersonalInformation.
     * @throws CheckedException Storing the personal information has failed.
     */
    @Override
    public void setPersonalInformation(String emailAddress, PersonalInformation info) throws CheckedException {

    }

    /**
     * Gets the account ID.
     *
     * @param emailAddress The email address of the account (Null for the current logged-in account).
     * @return The account ID.
     * @throws CheckedException The account could not be retrieved.
     */
    @Override
    public long getAccountID(String emailAddress) throws CheckedException {
        return 0;
    }

    /**
     * Gets the account staff role.
     *
     * @param emailAddress The email address of the account (Null for the current logged-in account).
     * @return The staff role.
     * @throws CheckedException The account could not be retrieved.
     */
    @Override
    public StaffRole getAccountRole(String emailAddress) throws CheckedException {
        return null;
    }

    /**
     * Sets the account staff role.
     *
     * @param emailAddress The email address of the account (Null for the current logged-in account).
     * @param role         The staff role.
     * @throws CheckedException The account could not be stored.
     */
    @Override
    public void setAccountRole(String emailAddress, StaffRole role) throws CheckedException {

    }

    /**
     * Gets an array of all account email addresses for the specific role.
     *
     * @param role The role to filter by.
     * @return An array of account email addresses.
     */
    @Override
    public String[] listAccounts(StaffRole role) {
        return new String[0];
    }

    /**
     * Deletes an account given the email address.
     *
     * @param emailAddress The email address of the account to delete.
     * @throws CheckedException The account deletion operation failed.
     */
    @Override
    public void deleteAccount(String emailAddress) throws CheckedException {

    }

    /**
     * Gets the commission rate of an account.
     *
     * @param emailAddress The email address of the account.
     * @return The commission rate percentage.
     * @throws CheckedException The commission rate could not be retrieved.
     */
    @Override
    public Decimal getCommission(String emailAddress) throws CheckedException {
        return null;
    }

    /**
     * Sets the commission rate of an account.
     *
     * @param emailAddress The email address of the account.
     * @param commission   The new commission rate percentage of the account.
     * @throws CheckedException The commission rate storage operation failed.
     */
    @Override
    public void setCommission(String emailAddress, Decimal commission) throws CheckedException {

    }

    /**
     * Gets the local currency of an account.
     *
     * @param emailAddress The email address of the account (Null for the current logged-in account).
     * @return The local currency name.
     * @throws CheckedException The account currency could not be retrieved.
     */
    @Override
    public String getCurrency(String emailAddress) throws CheckedException {
        return null;
    }

    /**
     * Sets the local currency of an account.
     *
     * @param emailAddress The email address of the account (Null for the current logged-in account).
     * @param currency     The new local currency of the account.
     * @throws CheckedException The local currency update operation failed.
     */
    @Override
    public void setCurrency(String emailAddress, String currency) throws CheckedException {

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