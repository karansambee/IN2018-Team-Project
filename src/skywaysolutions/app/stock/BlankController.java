package skywaysolutions.app.stock;

import skywaysolutions.app.utils.CheckedException;

public class BlankController implements IStockAccessor{
    /**
     * Creates a blank with the specified ID,
     * an ID of a staff member it's assigned to (Not assigned if -1)
     * and a description of the blanks contents.
     *
     * @param id          The ID of the blank.
     * @param assignedID  The staff member ID it is assigned to or -1 if not assigned.
     * @param description The description of the blank's contents.
     * @throws CheckedException The blank creation operation has failed.
     */
    @Override
    public void createBlank(long id, long assignedID, String description) throws CheckedException {

    }

    /**
     * Returns a blank with the specified ID.
     *
     * @param id The ID of the blank.
     * @throws CheckedException The blank could not be marked as returned.
     */
    @Override
    public void returnBlank(long id) throws CheckedException {
        return;

    }

    /**
     * Blacklists a blank with the specified ID.
     *
     * @param id The ID of the blank.
     * @throws CheckedException The blank could not be marked as blacklisted.
     */
    @Override
    public void blacklistBlank(long id) throws CheckedException {

    }

    /**
     * Voids a blank with the specified ID.
     *
     * @param id The ID of the blank.
     * @throws CheckedException The blank could not be marked as voided.
     */
    @Override
    public void voidBlank(long id) throws CheckedException {

    }

    /**
     * Re-assigns a blank with the specified ID to the
     * staff member with the specified ID.
     *
     * @param id         The ID of the blank.
     * @param assignedID The ID of the staff member.
     * @throws CheckedException Blank re-assigning has failed.
     */
    @Override
    public void reAssignBlank(long id, long assignedID) throws CheckedException {

    }

    /**
     * Gets the blank type code for the specified blank ID.
     *
     * @param id The blank ID.
     * @return A blank type code.
     */
    @Override
    public int getBlankType(long id) throws CheckedException {
        return 0;
    }

    /**
     * Gets a list of blanks that may be filtered by a provided staff member ID.
     *
     * @param assignedID The staff ID to filter by or -1 for no filtering.
     * @return The list of blank IDs.
     * @throws CheckedException The blanks could not be retrieved.
     */
    @Override
    public long[] getBlanks(long assignedID) throws CheckedException {
        return new long[0];
    }

    /**
     * Gets if a blank has been returned.
     *
     * @param id The blank ID.
     * @return If the blank has been returned.
     * @throws CheckedException The blank could not be retrieved.
     */
    @Override
    public boolean isBlankReturned(long id) throws CheckedException {
        return false;
    }

    /**
     * Gets if a blank has been blacklisted.
     *
     * @param id The blank ID.
     * @return If the blank has been blacklisted.
     * @throws CheckedException The blank could not be retrieved.
     */
    @Override
    public boolean isBlankBlacklisted(long id) throws CheckedException {
        return false;
    }

    /**
     * Gets if a blank has been voided.
     *
     * @param id The blank ID.
     * @return If the blank has been voided.
     * @throws CheckedException The blank could not be retrieved.
     */
    @Override
    public boolean isBlankVoided(long id) throws CheckedException {
        return false;
    }

    /**
     * Creates a new blank type with a description.
     *
     * @param typeCode    The 3 digit type code.
     * @param description The description of the type.
     * @throws CheckedException The blank type could not be created.
     */
    @Override
    public void createBlankType(int typeCode, String description) throws CheckedException {

    }

    /**
     * Gets a blank type description.
     *
     * @param typeCode The 3 digit type code.
     * @return The blank type description.
     * @throws CheckedException The blank type description could not be retrieved.
     */
    @Override
    public String getBlankTypeDescription(int typeCode) throws CheckedException {
        return null;
    }

    /**
     * Sets a blank types' description.
     *
     * @param typeCode    The 3 digit type code.
     * @param description The description of the type.
     * @throws CheckedException The description of the type could not be set.
     */
    @Override
    public void setBlankTypeDescription(int typeCode, String description) throws CheckedException {

    }

    /**
     * Deletes a blank type (Type-Description association).
     *
     * @param typeCode The 3 digit type code.
     */
    @Override
    public void deleteBlankType(int typeCode) throws CheckedException {

    }

    /**
     * Gets a list of blank types with descriptions associated with them.
     *
     * @return The list of 3 digit codes of blank types.
     * @throws CheckedException The blank types could not be retrieved.
     */
    @Override
    public int[] listBlankTypes() throws CheckedException {
        return new int[0];
    }

    /**
     * Gets the description of the blank's contents.
     *
     * @param id The blank ID.
     * @return The blank's description.
     * @throws CheckedException The description of the blank could not be retrieved.
     */
    @Override
    public String getBlankDescription(long id) throws CheckedException {
        return null;
    }

    /**
     * Sets the description of the blank's contents.
     *
     * @param id          The blank ID.
     * @param description The blank's description.
     * @throws CheckedException The description of the blank could not be stored.
     */
    @Override
    public void setBlankDescription(long id, String description) throws CheckedException {

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
