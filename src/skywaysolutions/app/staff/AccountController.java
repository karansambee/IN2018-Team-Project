package skywaysolutions.app.staff;

import skywaysolutions.app.database.*;
import skywaysolutions.app.staff.IStaffAccessor;
import skywaysolutions.app.staff.StaffRole;
import skywaysolutions.app.utils.CheckedException;
import skywaysolutions.app.utils.Decimal;
import skywaysolutions.app.utils.PersonalInformation;
import skywaysolutions.app.utils.Time;

import java.net.Authenticator;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AccountController implements IStaffAccessor {
    private final IDB_Connector conn;
    private Account currentAccount;
    private final StaffTableAccessor accessor;
    private final EmailFinder finder = new EmailFinder();
    private final Object slock = new Object();

    public AccountController(IDB_Connector conn) throws CheckedException {
        this.conn = conn;
        this.accessor = new StaffTableAccessor(conn);
        accessor.assureTableSchema();
    }

    private Account getAccountFromEmailAddress(String emailAddress, MultiLoadSyncMode mode) throws CheckedException {
        finder.email = emailAddress;
        List<Account> accounts = accessor.loadMany(finder, mode);
        if (accounts.size() > 0) return accounts.get(0); else throw new CheckedException("Account Does Not Exist");
    }

    /**
     * Assures the default administrator account of ID 0 exists.
     *
     * @throws CheckedException An assurance error has occurred.
     */
    @Override
    public void assureDefaultAdministratorAccount() throws CheckedException {
        synchronized (slock) {
            Account dAdmin = new Account(conn, 1L);
            if (dAdmin.exists(true)) {
                dAdmin.lock();
                dAdmin.load();
                dAdmin.setRole(StaffRole.Administrator);
                dAdmin.store();
                dAdmin.unlock();
            } else {
                dAdmin = new Account(conn, new PersonalInformation("Administrator", "Administrator", "", "admin@localhost", Time.now(), "", "", ""),
                        StaffRole.Administrator, null, "USD", new PasswordString("Administrator", PasswordString.getRandomSalt()), 1L);
                dAdmin.store();
            }
        }
    }

    /**
     * Gets the logged in account ID, null if no account logged in.
     *
     * @return The logged in account ID or null.
     */
    @Override
    public Long getLoggedInAccountID() {
        return (currentAccount == null) ? null : currentAccount.getAccountID();
    }

    /**
     * Gets the logged in account email, null if no account is logged in.
     *
     * @return The logged in account email or null.
     */
    @Override
    public String getLoggedInAccountEmail() {
        return (currentAccount == null) ? null : currentAccount.getEmail();
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
        synchronized (slock) {
            Account newAccount = new Account(this.conn, info, role, commission,currency, new PasswordString(password, PasswordString.getRandomSalt()), id);
            if (newAccount.exists(true)){
                throw new CheckedException("Account already exists");
            } else {
                newAccount.store();
                return newAccount.getAccountID();
            }
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
        synchronized (slock) {
            Account newAccount = getAccountFromEmailAddress(emailAddress, MultiLoadSyncMode.UnlockAfterLoad);
            if (newAccount.getPassword().checkPassword(password)){
               currentAccount = newAccount;
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * Logs out the current logged in account.
     *
     * @return Whether the current account was logged out.
     */
    @Override
    public boolean logoutAccount() {
        synchronized (slock) {
            if (currentAccount == null) return false;
            currentAccount = null;
            return true;
        }
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
        if (currentAccount == null) throw new CheckedException("No Logged in Account");
        synchronized (slock) {
            if (emailAddress == null){
              currentAccount.lock();
              currentAccount.load();
              currentAccount.setPassword(new PasswordString(password, PasswordString.getRandomSalt()));
              currentAccount.store();
              currentAccount.unlock();
            } else if (currentAccount.getRole() == StaffRole.Administrator){
               Account account = getAccountFromEmailAddress(emailAddress, MultiLoadSyncMode.KeepLockedAfterLoad);
               account.setPassword(new PasswordString(password, PasswordString.getRandomSalt()));
               account.store();
               accessor.unlockAll(false);
            } else {
                throw new CheckedException("Cannot change the password of another account unless you are System Administrator");
            }
        }
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
        if (currentAccount == null) throw new CheckedException("No Logged in Account");
        synchronized (slock) {
            if (emailAddress == null) {
                return currentAccount.getInfo();
            } else if (currentAccount.getRole() == StaffRole.Administrator) {
                return getAccountFromEmailAddress(emailAddress, MultiLoadSyncMode.UnlockAfterLoad).getInfo();
            } else {
                throw new CheckedException("Cannot retrieve info of another account unless you are System Administrator");
            }
        }
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
        if (currentAccount == null) throw new CheckedException("No Logged in Account");
        synchronized (slock) {
            if (emailAddress == null) {
                currentAccount.lock();
                currentAccount.load();
                currentAccount.setInfo(info);
                currentAccount.store();
                currentAccount.unlock();
            } else if (currentAccount.getRole() == StaffRole.Administrator) {
                Account account = getAccountFromEmailAddress(emailAddress, MultiLoadSyncMode.KeepLockedAfterLoad);
                account.setInfo(info);
                account.store();
                accessor.unlockAll(false);
            } else {
                throw new CheckedException("Cannot change the info of another account unless you are System Administrator");
            }
        }
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
        if (currentAccount == null) throw new CheckedException("No Logged in Account");
        synchronized (slock) {
            if (emailAddress == null) {
                return currentAccount.getAccountID();
            } else {
                return getAccountFromEmailAddress(emailAddress, MultiLoadSyncMode.UnlockAfterLoad).getAccountID();
            }
        }
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
        if (currentAccount == null) throw new CheckedException("No Logged in Account");
        synchronized (slock) {
            if (emailAddress == null) {
                return currentAccount.getRole();
            } else if (currentAccount.getRole() == StaffRole.Administrator || currentAccount.getRole() == StaffRole.Manager) {
                return getAccountFromEmailAddress(emailAddress, MultiLoadSyncMode.UnlockAfterLoad).getRole();
            } else {
                throw new CheckedException("Cannot get the role of another account unless you are System Administrator or Manager");
            }
        }
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
        if (currentAccount == null) throw new CheckedException("No Logged in Account");
        synchronized (slock) {
            if (currentAccount.getRole() == StaffRole.Administrator) {
                if (emailAddress == null) {
                    currentAccount.lock();
                    currentAccount.load();
                    currentAccount.setRole(role);
                    currentAccount.store();
                    currentAccount.unlock();
                } else {
                    Account account = getAccountFromEmailAddress(emailAddress, MultiLoadSyncMode.KeepLockedAfterLoad);
                    account.setRole(role);
                    account.store();
                    accessor.unlockAll(false);
                }
            } else {
                throw new CheckedException("Cannot change the role of an account unless you are System Administrator");
            }
        }
    }

    /**
     * Gets an array of all account email addresses for the specific role.
     *
     * @param role The role to filter by.
     * @return An array of account email addresses.
     */
    @Override
    public String[] listAccounts(StaffRole role) throws CheckedException {
        synchronized (slock) {
            try(PreparedStatement pre = conn.getStatement(
                    "SELECT EmailAddress FROM Staff" + ((role == StaffRole.Any) ? "" : " WHERE StaffRole = ?"))){
                if (role != StaffRole.Any) pre.setInt(1, role.getValue());
                try (ResultSet rs = pre.executeQuery()) {
                    ArrayList<String> addresses = new ArrayList<>();
                    while (rs.next()) addresses.add(rs.getString("EmailAddress"));
                    return addresses.toArray(new String[0]);
                }
            } catch (SQLException | CheckedException throwables){
                throw new CheckedException(throwables);
            }
        }
    }

    /**
     * Deletes an account given the email address.
     *
     * @param emailAddress The email address of the account to delete.
     * @throws CheckedException The account deletion operation failed.
     */
    @Override
    public void deleteAccount(String emailAddress) throws CheckedException {
        if (currentAccount == null) throw new CheckedException("No Logged in Account");
        if (emailAddress.equals(currentAccount.getEmail())) throw new CheckedException("Cannot delete own account");
        synchronized (slock) {
            if (currentAccount.getRole() == StaffRole.Administrator) {
                getAccountFromEmailAddress(emailAddress, MultiLoadSyncMode.KeepLockedAfterLoad).delete();
                accessor.unlockAll(false);
            } else {
                throw new CheckedException("Cannot delete an account unless you are System Administrator");
            }
        }
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
        if (currentAccount == null) throw new CheckedException("No Logged in Account");
        synchronized (slock) {
            if (emailAddress == null) {
                return currentAccount.getCommission();
            } else if (currentAccount.getRole() == StaffRole.Manager || currentAccount.getRole() == StaffRole.Administrator) {
                return getAccountFromEmailAddress(emailAddress, MultiLoadSyncMode.UnlockAfterLoad).getCommission();
            } else {
                throw new CheckedException("Cannot retrieve commission rates of another account unless you are System Administrator or Manager");
            }
        }
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
        if (currentAccount == null) throw new CheckedException("No Logged in Account");
        synchronized (slock) {
            if (currentAccount.getRole() == StaffRole.Manager || currentAccount.getRole() == StaffRole.Administrator) {
                Account account = getAccountFromEmailAddress(emailAddress, MultiLoadSyncMode.KeepLockedAfterLoad);
                account.setCommission(commission);
                account.store();
                accessor.unlockAll(false);
            } else {
                throw new CheckedException("Cannot change commission rate unless you are System Administrator or Manager");
            }
        }
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
        if (currentAccount == null) throw new CheckedException("No Logged in Account");
        synchronized (slock) {
            if (emailAddress == null) {
                return currentAccount.getCurrency();
            } else {
                return getAccountFromEmailAddress(emailAddress, MultiLoadSyncMode.UnlockAfterLoad).getCurrency();
            }
        }
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
        if (currentAccount == null) throw new CheckedException("No Logged in Account");
        synchronized (slock) {
            if (currentAccount.getRole() == StaffRole.Administrator || currentAccount.getRole() == StaffRole.Manager) {
                if (emailAddress == null) {
                    currentAccount.setCurrency(currency);
                } else {
                    Account account = getAccountFromEmailAddress(emailAddress, MultiLoadSyncMode.KeepLockedAfterLoad);
                    account.setCurrency(currency);
                    account.store();
                    accessor.unlockAll(false);
                }
            } else {
                throw new CheckedException("Cannot change currency unless you are System Administrator or Manager");
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
        return new String[] {"Staff"};
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
          if (tableName.equals("Staff")) accessor.unlockAll(true);
      }
    }

    private static class EmailFinder implements IFilterStatementCreator {
        public String email;
        @Override
        public PreparedStatement createFilteredStatementFor(IDB_Connector conn, String startOfSQLTemplate) throws SQLException, CheckedException {
            PreparedStatement sta = conn.getStatement(startOfSQLTemplate + "EmailAddress = ?");
            sta.setString(1, email);
            return sta;
        }
    }
}