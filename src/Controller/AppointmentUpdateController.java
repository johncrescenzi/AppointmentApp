package Controller;

import DAO.AppointmentAccess;
import DAO.ContactAccess;
import DAO.CustomerAccess;
import DAO.UserAccess;
import Helper.General;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import Helper.JDBC;
import Model.Appointments;
import Model.Contacts;
import Model.Customers;
import Model.Users;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;

import static Helper.General.*;
import static Helper.JDBC.connection;
import static Helper.TimeUtility.convertTimeDateUTC;

/**
 * Controller for updating appointments.
 * This controller provides functionalities to update appointment details like ID, description, location, etc.
 * Additionally, it handles validation and formatting of the date and time fields.
 */

public class AppointmentUpdateController {

    @FXML
    private TextField updateAppointmentCustomerID;
    @FXML
    private TextField updateAppointmentUserID;
    @FXML
    private TextField updateAppointmentDescription;
    @FXML
    private DatePicker updateAppointmentEndDate;
    @FXML
    private ComboBox<String> updateAppointmentEndTime;
    @FXML
    private TextField updateAppointmentID;
    @FXML
    private TextField updateAppointmentLocation;
    @FXML
    private Button updateAppointmentSave;
    @FXML
    private DatePicker updateAppointmentStartDate;
    @FXML
    private ComboBox<String> updateAppointmentStartTime;
    @FXML
    private TextField updateAppointmentTitle;
    @FXML
    private ComboBox<String> updateAppointmentContact;
    @FXML
    private Button updateAppointmentsCancel;
    @FXML
    private TextField updateAppointmentType;

    private TableView<Appointments> appointmentsTable;
    private Appointments selectedAppointment;

    private Appointments appointmentToUpdate;


    /**
     * Sets the appointment details to be updated.
     *
     * @param appointment The appointment to update.
     */
    public void setAppointment(Appointments appointment) {
        this.appointmentToUpdate = appointment;
        populateFields();
    }
    private class ValidationException extends Exception {
        private ValidationException(String message) {
            super(message);
        }
    }


    /**
     * Populates the input fields with the details of the selected appointment.
     */

    private void populateFields() {
        if (appointmentToUpdate != null) {
            updateAppointmentID.setText(String.valueOf(appointmentToUpdate.getAppointmentID()));
            updateAppointmentTitle.setText(appointmentToUpdate.getAppointmentTitle());
            updateAppointmentDescription.setText(appointmentToUpdate.getAppointmentDescription());
            updateAppointmentLocation.setText(appointmentToUpdate.getAppointmentLocation());
            updateAppointmentType.setText(appointmentToUpdate.getAppointmentType());
            updateAppointmentCustomerID.setText(String.valueOf(appointmentToUpdate.getCustomerID()));
            updateAppointmentUserID.setText(String.valueOf(appointmentToUpdate.getUserID()));

            // Populate the DatePickers with the dates from the LocalDateTime objects
            updateAppointmentStartDate.setValue(appointmentToUpdate.getStart().toLocalDate());
            updateAppointmentEndDate.setValue(appointmentToUpdate.getEnd().toLocalDate());

            // Set values for the ComboBoxes with the times from the LocalDateTime objects
            updateAppointmentStartTime.setValue(appointmentToUpdate.getStart().toLocalTime().toString());
            updateAppointmentEndTime.setValue(appointmentToUpdate.getEnd().toLocalTime().toString());

            // Get the contact's name using the contact ID from the appointment
            String contactName = getContactNameById(appointmentToUpdate.getContactID());
            updateAppointmentContact.setValue(contactName);
        }
    }

    /**
     * Fetches contact name by its ID.
     *
     * @param contactId The ID of the contact.
     * @return The contact name corresponding to the provided ID.
     */

    private String getContactNameById(int contactId) {
        try {
            ObservableList<Contacts> contacts = ContactAccess.getAllContacts();
            for (Contacts contact : contacts) {
                if (contact.getId() == contactId) {
                    return contact.getContactName();
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching contact name: " + e.getMessage());
            // Handle error or logging as needed.
        }
        return null;  // Return null if the contact is not found
    }


    /**
     * Sets the appointment table and the currently selected appointment.
     *
     * @param table The table view containing the list of appointments.
     */
    public void setAppointmentsTable(TableView<Appointments> table) {
        this.appointmentsTable = table;
        this.selectedAppointment = table.getSelectionModel().getSelectedItem();
    }

    /**
     * Initializes the controller. Populates the contact names and appointment times.
     */
    @FXML
    public void initialize() throws SQLException {
        populateContacts();
        populateAppointmentTimes();
    }

    /**
     * Event handler for saving the updated appointment details.
     *
     * @param event The action event associated with the save operation.
     */
    @FXML
    void updateAppointmentSave(ActionEvent event) throws IOException {

        try {
            Connection connection = JDBC.startConnection();
            if (connection == null) {
                showAlertAndReturn("Failed to connect to the database.", Alert.AlertType.ERROR);
                return;
            }

            // Validation for empty fields
            if (!validateInputField(updateAppointmentTitle, "Appointment Title") ||
                    !validateInputField(updateAppointmentDescription, "Appointment Description") ||
                    !validateInputField(updateAppointmentLocation, "Appointment Location") ||
                    !validateInputField(updateAppointmentType, "Appointment Type") ||
                    !validateInputField(updateAppointmentCustomerID, "Customer ID") ||
                    !validateInputField(updateAppointmentUserID, "User ID") ||
                    !validateComboBox(updateAppointmentContact, "Appointment Contact") ||
                    !validateComboBox(updateAppointmentStartTime, "Start Time") ||
                    !validateComboBox(updateAppointmentEndTime, "End Time") ||
                    !validateDatePicker(updateAppointmentStartDate, "Start Date") ||
                    !validateDatePicker(updateAppointmentEndDate, "End Date") ||
                    !validateNumeric(updateAppointmentCustomerID, "Customer ID") ||
                    !validateNumeric(updateAppointmentUserID, "User ID")) {
                return;  // Stop execution if any validation fails
            }

            try {
                handleDatabaseOperations();
                General.transitionToScene("../View/MainScreen.fxml");
            } catch (AppointmentUpdateController.ValidationException e) {
                showAlertAndReturn(e.getMessage(), Alert.AlertType.ERROR);
            }
        } catch (SQLException throwables) {
            showAlertAndReturn("Database error. Please try again.", Alert.AlertType.ERROR);
            throwables.printStackTrace();
        }
    }

    /**
     * Handles the operations associated with updating the appointment in the database.
     */

    private void handleDatabaseOperations() throws SQLException, ValidationException {
        if (!updateAppointmentTitle.getText().isEmpty() && !updateAppointmentDescription.getText().isEmpty() && !updateAppointmentLocation.getText().isEmpty() && !updateAppointmentType.getText().isEmpty() && updateAppointmentStartDate.getValue() != null && updateAppointmentEndDate.getValue() != null && !updateAppointmentStartTime.getValue().isEmpty() && !updateAppointmentEndTime.getValue().isEmpty() && !updateAppointmentCustomerID.getText().isEmpty()) {

            ObservableList<Customers> getAllCustomers = CustomerAccess.getAllCustomers(connection);
            ObservableList<Integer> storeCustomerIDs = FXCollections.observableArrayList();
            ObservableList<UserAccess> getAllUsers = UserAccess.getAllUsers();
            ObservableList<Integer> storeUserIDs = FXCollections.observableArrayList();
            ObservableList<Appointments> getAllAppointments = AppointmentAccess.getAllAppointments();

            getAllCustomers.stream().map(Customers::getCustomerID).forEach(storeCustomerIDs::add);
            getAllUsers.stream().map(Users::getUserID).forEach(storeUserIDs::add);

            LocalDate localDateEnd = updateAppointmentEndDate.getValue();
            LocalDate localDateStart = updateAppointmentStartDate.getValue();

            DateTimeFormatter minHourFormat = DateTimeFormatter.ofPattern("HH:mm");
            String appointmentStartDate = updateAppointmentStartDate.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String appointmentStartTime = updateAppointmentStartTime.getValue();

            String endDate = updateAppointmentEndDate.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String endTime = updateAppointmentEndTime.getValue();

            System.out.println("thisDate + thisStart " + appointmentStartDate + " " + appointmentStartTime + ":00");
            String startUTC = convertTimeDateUTC(appointmentStartDate + " " + appointmentStartTime + ":00");
            String endUTC = convertTimeDateUTC(endDate + " " + endTime + ":00");

            LocalTime localTimeStart = LocalTime.parse(updateAppointmentStartTime.getValue(), minHourFormat);
            LocalTime LocalTimeEnd = LocalTime.parse(updateAppointmentEndTime.getValue(), minHourFormat);

            LocalDateTime dateTimeStart = LocalDateTime.of(localDateStart, localTimeStart);
            LocalDateTime dateTimeEnd = LocalDateTime.of(localDateEnd, LocalTimeEnd);

            ZonedDateTime zoneDtStart = ZonedDateTime.of(dateTimeStart, ZoneId.systemDefault());
            ZonedDateTime zoneDtEnd = ZonedDateTime.of(dateTimeEnd, ZoneId.systemDefault());

            ZonedDateTime convertStartEST = zoneDtStart.withZoneSameInstant(ZoneId.of("America/New_York"));
            ZonedDateTime convertEndEST = zoneDtEnd.withZoneSameInstant(ZoneId.of("America/New_York"));

            LocalTime startAppointmentTimeToCheck = convertStartEST.toLocalTime();
            LocalTime endAppointmentTimeToCheck = convertEndEST.toLocalTime();

            DayOfWeek startAppointmentDayToCheck = convertStartEST.toLocalDate().getDayOfWeek();
            DayOfWeek endAppointmentDayToCheck = convertEndEST.toLocalDate().getDayOfWeek();

            int startAppointmentDayToCheckInt = startAppointmentDayToCheck.getValue();
            int endAppointmentDayToCheckInt = endAppointmentDayToCheck.getValue();

            int workWeekStart = DayOfWeek.MONDAY.getValue();
            int workWeekEnd = DayOfWeek.FRIDAY.getValue();

            LocalTime estBusinessStart = LocalTime.of(8, 0, 0);
            LocalTime estBusinessEnd = LocalTime.of(22, 0, 0);


            if (startAppointmentDayToCheckInt < workWeekStart || startAppointmentDayToCheckInt > workWeekEnd || endAppointmentDayToCheckInt < workWeekStart || endAppointmentDayToCheckInt > workWeekEnd) {
                System.out.println("day is outside of business hours");
                throw new AppointmentUpdateController.ValidationException("The day is outside of business days: Monday - Friday");
            }

            if (startAppointmentTimeToCheck.isBefore(estBusinessStart) || startAppointmentTimeToCheck.isAfter(estBusinessEnd) || endAppointmentTimeToCheck.isBefore(estBusinessStart) || endAppointmentTimeToCheck.isAfter(estBusinessEnd)) {
                System.out.println("Time is outside of business hours.");
                throw new AppointmentUpdateController.ValidationException("Time is outside of business hours (8am-10pm EST):");
            }

            int customerID = Integer.parseInt(updateAppointmentCustomerID.getText());
            int appointmentID = Integer.parseInt(updateAppointmentID.getText());

            if (dateTimeStart.isAfter(dateTimeEnd)) {
                System.out.println("Appointment's start time is after its end time.");
                throw new AppointmentUpdateController.ValidationException("Appointment's start time is after its end time.");
            }

            if (dateTimeStart.isEqual(dateTimeEnd)) {
                System.out.println("Appointment's start and end time are the same.");
                throw new AppointmentUpdateController.ValidationException("Appointment's start and end time are the same.");
            }
            for (Appointments appointment : getAllAppointments) {
                LocalDateTime checkStart = appointment.getStart();
                LocalDateTime checkEnd = appointment.getEnd();


                if (customerID == appointment.getCustomerID() && appointmentID != appointment.getAppointmentID()) {
                    if (dateTimeStart.isBefore(checkEnd) && dateTimeEnd.isAfter(checkStart)) {
                        System.out.println("Appointment's time overlaps with an existing appointment's time.");
                        throw new ValidationException("Appointment's time overlaps with an existing appointment's time.");
                    }
                }


                if ((customerID == appointment.getCustomerID()) &&
                        (dateTimeStart.isBefore(checkStart)) && (dateTimeEnd.isAfter(checkEnd))) {
                    System.out.println("Appointment's time overlaps with an existing appointment's time.");
                    throw new AppointmentUpdateController.ValidationException("Appointment's time overlaps with an existing appointment's time.");
                }

                if ((customerID == appointment.getCustomerID()) &&
                        (dateTimeStart.isAfter(checkStart)) && (dateTimeStart.isBefore(checkEnd))) {
                    System.out.println("The start time overlaps with an existing appointment.");
                    throw new AppointmentUpdateController.ValidationException("The start time overlaps with an existing appointment.");
                }

                if (customerID == appointment.getCustomerID() &&
                        (dateTimeEnd.isAfter(checkStart)) && (dateTimeEnd.isBefore(checkEnd))) {
                    System.out.println("The end time overlaps with an existing appointment.");
                    throw new AppointmentUpdateController.ValidationException("The end time overlaps with an existing appointment.");
                }
            }

            String updateStatement = "UPDATE appointments SET Title = ?, Description = ?, Location = ?, Type = ?, Start = ?, End = ?, Last_Update = ?, Last_Updated_By = ?, Customer_ID = ?, User_ID = ?, Contact_ID = ? WHERE Appointment_ID = ?";
            JDBC.setPreparedStatement(JDBC.getConnection(), updateStatement);
            PreparedStatement ps = JDBC.getPreparedStatement();

            ps.setString(1, updateAppointmentTitle.getText());
            ps.setString(2, updateAppointmentDescription.getText());
            ps.setString(3, updateAppointmentLocation.getText());
            ps.setString(4, updateAppointmentType.getText());
            ps.setTimestamp(5, Timestamp.valueOf(startUTC));
            ps.setTimestamp(6, Timestamp.valueOf(endUTC));
            ps.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(8, "admin");
            ps.setInt(9, Integer.parseInt(updateAppointmentCustomerID.getText()));
            ps.setInt(10, Integer.parseInt(updateAppointmentUserID.getText()));
            ps.setInt(11, Integer.parseInt(ContactAccess.findContactID(updateAppointmentContact.getValue())));
            ps.setInt(12, appointmentToUpdate.getAppointmentID());

            ps.execute();
        }

    }

    /**
     * Event handler for cancelling the update operation and transitioning back to the main screen.
     *
     * @param event The action event associated with the cancel operation.
     */

    @FXML
    public void updateAppointmentsCancel(ActionEvent event) throws IOException {
        showAlertAndReturn("Are you sure you want to cancel?", Alert.AlertType.CONFIRMATION);
        General.transitionToScene("../View/MainScreen.fxml");
    }

    /**
     * Populates the ComboBox with all contact names.
     */
    private void populateContacts() throws SQLException {
        ObservableList<Contacts> contactsObservableList = ContactAccess.getAllContacts();
        ObservableList<String> allContactsNames = FXCollections.observableArrayList();
        contactsObservableList.forEach(contact -> allContactsNames.add(contact.getContactName()));
        updateAppointmentContact.setItems(allContactsNames);
    }

    /**
     * Populates the ComboBoxes with appointment times.
     */

    private void populateAppointmentTimes() {
        ObservableList<String> appointmentTimes = FXCollections.observableArrayList();
        LocalTime firstAppointment = LocalTime.MIN.plusHours(8);
        LocalTime lastAppointment = LocalTime.MAX.minusHours(1).minusMinutes(45);

        if (!firstAppointment.equals(0) || !lastAppointment.equals(0)) {
            while (firstAppointment.isBefore(lastAppointment)) {
                appointmentTimes.add(String.valueOf(firstAppointment));
                firstAppointment = firstAppointment.plusMinutes(15);
            }
        }
        updateAppointmentStartTime.setItems(appointmentTimes);
        updateAppointmentEndTime.setItems(appointmentTimes);
    }

}