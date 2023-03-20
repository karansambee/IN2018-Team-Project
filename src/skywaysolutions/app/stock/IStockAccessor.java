package skywaysolutions.app.stock;

import skywaysolutions.app.utils.CheckedException;
import skywaysolutions.app.utils.IRepairable;

/**
 * Provides the stock management system for blanks and tickets.
 *
 * @author Alfred Manville
 */
public interface IStockAccessor extends IRepairable {
    /**
     * Creates a blank with the specified ID,
     * an ID of a staff member it's assigned to (Not assigned if -1)
     * and a description of the blanks contents.
     *
     * @param id The ID of the blank.
     * @param assignedID The staff member ID it is assigned to or -1 if not assigned.
     * @param description The description of the blank's contents.
     * @throws CheckedException The blank creation operation has failed.
     */
    void createBlank(long id, long assignedID, String description) throws CheckedException;

    /**
     * Returns a blank with the specified ID.
     *
     * @param id The ID of the blank.
     * @throws CheckedException The blank could not be marked as returned.
     */
    void returnBlank(long id) throws CheckedException;

    /**
     * Blacklists a blank with the specified ID.
     *
     * @param id The ID of the blank.
     * @throws CheckedException The blank could not be marked as blacklisted.
     */
    void blacklistBlank(long id) throws CheckedException;

    /**
     * Voids a blank with the specified ID.
     *
     * @param id The ID of the blank.
     * @throws CheckedException The blank could not be marked as voided.
     */
    void voidBlank(long id) throws CheckedException;

    /**
     * Re-assigns a blank with the specified ID to the
     * staff member with the specified ID.
     *
     * @param id The ID of the blank.
     * @param assignedID The ID of the staff member.
     * @throws CheckedException Blank re-assigning has failed.
     */
    void reAssignBlank(long id, long assignedID) throws CheckedException;

    /**
     * Gets the blank type code for the specified blank ID.
     *
     * @param id The blank ID.
     * @return A blank type code.
     */
    int getBlankType(long id) throws CheckedException;

    /**
     * Gets a list of blanks that may be filtered by a provided staff member ID.
     *
     * @param assignedID The staff ID to filter by or -1 for no filtering.
     * @return The list of blank IDs.
     * @throws CheckedException The blanks could not be retrieved.
     */
    long[] getBlanks(long assignedID) throws CheckedException;

    /**
     * Gets if a blank has been returned.
     *
     * @param id The blank ID.
     * @return If the blank has been returned.
     * @throws CheckedException The blank could not be retrieved.
     */
    boolean isBlankReturned(long id) throws CheckedException;

    /**
     * Gets if a blank has been blacklisted.
     *
     * @param id The blank ID.
     * @return If the blank has been blacklisted.
     * @throws CheckedException The blank could not be retrieved.
     */
    boolean isBlankBlacklisted(long id) throws CheckedException;

    /**
     * Gets if a blank has been voided.
     *
     * @param id The blank ID.
     * @return If the blank has been voided.
     * @throws CheckedException The blank could not be retrieved.
     */
    boolean isBlankVoided(long id) throws CheckedException;

    /**
     * Creates a new blank type with a description.
     *
     * @param typeCode The 3 digit type code.
     * @param description The description of the type.
     * @throws CheckedException The blank type could not be created.
     */
    void createBlankType(int typeCode, String description) throws CheckedException;

    /**
     * Gets a blank type description.
     *
     * @param typeCode The 3 digit type code.
     * @return The blank type description.
     * @throws CheckedException The blank type description could not be retrieved.
     */
    String getBlankTypeDescription(int typeCode) throws CheckedException;

    /**
     * Sets a blank types' description.
     * @param typeCode The 3 digit type code.
     * @param description The description of the type.
     * @throws CheckedException The description of the type could not be set.
     */
    void setBlankTypeDescription(int typeCode, String description) throws CheckedException;

    /**
     * Deletes a blank type (Type-Description association).
     *
     * @param typeCode The 3 digit type code.
     */
    void deleteBlankType(int typeCode) throws CheckedException;

    /**
     * Gets a list of blank types with descriptions associated with them.
     *
     * @return The list of 3 digit codes of blank types.
     * @throws CheckedException The blank types could not be retrieved.
     */
    int[] listBlankTypes() throws CheckedException;

    /**
     * Gets the description of the blank's contents.
     *
     * @param id The blank ID.
     * @return The blank's description.
     * @throws CheckedException The description of the blank could not be retrieved.
     */
    String getBlankDescription(long id) throws CheckedException;

    /**
     * Sets the description of the blank's contents.
     *
     * @param id The blank ID.
     * @param description The blank's description.
     * @throws CheckedException The description of the blank could not be stored.
     */
    void setBlankDescription(long id, String description) throws CheckedException;
}
