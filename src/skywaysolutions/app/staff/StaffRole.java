package skywaysolutions.app.staff;

/**
 * Provides the staff role enum.
 *
 * @author Alfred Manville
 */
public enum StaffRole {
    /**
     * Any staff role, used for filtering.
     */
    Any(-1),
    /**
     * Travel Advisor.
     */
    Advisor(0),
    /**
     * Office Manager.
     */
    Manager(1),
    /**
     * System Administrator.
     */
    Administrator(2);
    private final int theValue;
    StaffRole(int value) {
        theValue = value;
    }
    /**
     * Get the integer value of the role.
     *
     * @return The integer value of the role.
     */
    public int getValue() {
        return theValue;
    }
    /**
     * Gets a string that can be displayed.
     *
     * @return The display string.
     */
    @Override
    public String toString() {
        return switch (theValue) {
            case -1 -> "Any";
            case 1 -> "Manager";
            case 2 -> "Administrator";
            default -> "Advisor";
        };
    }
    /**
     * Gets the staff role given its ID.
     *
     * @param theValueIn The staff role ID.
     * @return The staff role enum value.
     */
    public static StaffRole getStaffRoleFromValue(int theValueIn) {
        return switch (theValueIn) {
            case -1 -> StaffRole.Any;
            case 1 -> StaffRole.Manager;
            case 2 -> StaffRole.Administrator;
            default -> StaffRole.Advisor;
        };
    }
}
