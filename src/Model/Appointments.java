package Model;

import java.time.LocalDateTime;

/**
 * Represents an appointment with various attributes and details.
 */
public class Appointments {
    private int appointmentID;
    private String appointmentTitle;
    private String appointmentDescription;
    private String appointmentLocation;
    private String appointmentType;
    private LocalDateTime start;
    private LocalDateTime end;
    public int customerID;
    public int userID;
    public int contactID;

    /**
     * Constructs an instance of the Appointments class with provided details.
     *
     * @param appointmentID         The unique identifier for the appointment.
     * @param appointmentTitle      The title of the appointment.
     * @param appointmentDescription The description of the appointment.
     * @param appointmentLocation   The location where the appointment will take place.
     * @param appointmentType       The type/category of the appointment.
     * @param start                 The start date and time of the appointment.
     * @param end                   The end date and time of the appointment.
     * @param customerID            The ID of the associated customer.
     * @param userID                The ID of the user responsible for the appointment.
     * @param contactID             The ID of the contact person related to the appointment.
     */
    public Appointments(int appointmentID, String appointmentTitle, String appointmentDescription,
                        String appointmentLocation, String appointmentType, LocalDateTime start, LocalDateTime end, int customerID,
                        int userID, int contactID) {
        this.appointmentID = appointmentID;
        this.appointmentTitle = appointmentTitle;
        this.appointmentDescription = appointmentDescription;
        this.appointmentLocation = appointmentLocation;
        this.appointmentType = appointmentType;
        this.start = start;
        this.end = end;
        this.customerID = customerID;
        this.userID = userID;
        this.contactID = contactID;
    }

    /**
     * Gets the unique identifier of the appointment.
     *
     * @return The appointment's ID.
     */
    public int getAppointmentID() {
        return appointmentID;
    }

    /**
     * Gets the title of the appointment.
     *
     * @return The appointment's title.
     */
    public String getAppointmentTitle() {
        return appointmentTitle;
    }

    /**
     * Gets the description of the appointment.
     *
     * @return The appointment's description.
     */
    public String getAppointmentDescription() {
        return appointmentDescription;
    }

    /**
     * Gets the location where the appointment will take place.
     *
     * @return The appointment's location.
     */
    public String getAppointmentLocation() {
        return appointmentLocation;
    }

    /**
     * Gets the type/category of the appointment.
     *
     * @return The appointment's type.
     */
    public String getAppointmentType() {
        return appointmentType;
    }

    /**
     * Gets the start date and time of the appointment.
     *
     * @return The appointment's start date and time.
     */
    public LocalDateTime getStart() {
        return start;
    }

    /**
     * Gets the end date and time of the appointment.
     *
     * @return The appointment's end date and time.
     */
    public LocalDateTime getEnd() {
        return end;
    }

    /**
     * Gets the ID of the associated customer.
     *
     * @return The customer's ID.
     */
    public int getCustomerID() {
        return customerID;
    }

    /**
     * Gets the ID of the user responsible for the appointment.
     *
     * @return The user's ID.
     */
    public int getUserID() {
        return userID;
    }

    /**
     * Gets the ID of the contact person related to the appointment.
     *
     * @return The contact's ID.
     */
    public int getContactID() {
        return contactID;
    }
}
