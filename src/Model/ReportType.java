package Model;

/**
 * The ReportType class encapsulates information about the total number of appointments
 * for a specific appointment type.
 */
public class ReportType {
    public String appointmentType; // The type of the appointment.
    public int appointmentTotal;   // The total number of appointments of the specified type.

    /**
     * Constructs a ReportType object with provided details.
     *
     * @param appointmentType   The type of the appointment.
     * @param appointmentTotal  The total number of appointments of the specified type.
     */
    public ReportType(String appointmentType, int appointmentTotal) {
        this.appointmentType = appointmentType;
        this.appointmentTotal = appointmentTotal;
    }

    /**
     * Retrieves the appointment type for which the report is generated.
     *
     * @return The appointment type.
     */
    public String getAppointmentType() {
        return appointmentType;
    }

    /**
     * Retrieves the total number of appointments of the specified type.
     *
     * @return The total number of appointments.
     */
    public int getAppointmentTotal() {
        return appointmentTotal;
    }
}
