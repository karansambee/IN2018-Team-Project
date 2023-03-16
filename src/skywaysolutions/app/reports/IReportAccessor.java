package skywaysolutions.app.reports;

import skywaysolutions.app.utils.CheckedException;

/**
 * This interface provides the ability to access report instances.
 *
 * @author Alfred Manville
 */
public interface IReportAccessor {
    /**
     * Gets the available report type names.
     *
     * @return The names of the available report types.
     */
    String[] getAvailableReportTypes();

    /**
     * Sets the current report type.
     *
     * @param reportType The report type name to set to.
     * @throws CheckedException The report type could not be set to.
     */
    void setReportType(String reportType) throws CheckedException;

    /**
     * Gets the current report type.
     *
     * @return The current report type name.
     * @throws CheckedException The current report type name could not be retrieved.
     */
    String getReportType() throws CheckedException;

    /**
     * Gets the current report generator.
     *
     * @return The current report generator.
     * @throws CheckedException The report generator could not be obtained.
     */
    IReportGenerator getReportGenerator() throws CheckedException;
}
