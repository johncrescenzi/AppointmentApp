package Model;

/**
 * The ReportMonth class encapsulates information about the total number of appointments
 * for a specific month.
 */
public class ReportMonth {
    public String appointmentMonth; // The month for which the report is generated.
    public int appointmentTotal; // The total number of appointments for the specified month.

    /**
     * Constructs a ReportMonth object with provided details.
     *
     * @param appointmentMonth  The month for which the report is generated.
     * @param appointmentTotal  The total number of appointments for the specified month.
     */
    public ReportMonth(String appointmentMonth, int appointmentTotal) {
        this.appointmentMonth = appointmentMonth;
        this.appointmentTotal = appointmentTotal;
    }

    /**
     * Retrieves the month for which the report is generated.
     *
     * @return The month of the report.
     */
    public String getAppointmentMonth() {
        return appointmentMonth;
    }

    /**
     * Retrieves the total number of appointments for the specified month.
     *
     * @return The total number of appointments.
     */
    public int getAppointmentTotal() {
        return appointmentTotal;
    }
}
