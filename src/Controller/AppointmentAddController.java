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
import java.util.Optional;

import static Helper.General.*;
import static Helper.JDBC.connection;
import static Helper.TimeUtility.*;

/**
 * The {@code AppointmentAddController} class is responsible for managing the functionalities
 * associated with adding a new appointment. It provides the ability to enter details for a new
 * appointment, validate them, and add the appointment to the database.
 * <p>
 * The controller interacts with various UI components like TextFields, ComboBoxes, and DatePickers
 * to get user input and display appointment details.
 * </p>
 * <p>
 * On the database side, it connects to the database using the JDBC helper class, retrieves data from
 * various tables, and updates the database as needed.
 * </p>
 */
public class AppointmentAddController {

    @FXML
    private TextField addAppointmentCustomerID;
    @FXML
    private TextField addAppointmentUserID;
    @FXML
    private TextField addAppointmentDescription;
    @FXML
    private DatePicker addAppointmentEndDate;
    @FXML
    private ComboBox<String> addAppointmentEndTime;
    @FXML
    private TextField addAppointmentID;
    @FXML
    private TextField addAppointmentLocation;
    @FXML
    private Button addAppointmentSave;
    @FXML
    private DatePicker addAppointmentStartDate;
    @FXML
    private ComboBox<String> addAppointmentStartTime;
    @FXML
    private TextField addAppointmentTitle;
    @FXML
    private ComboBox<String> addAppointmentContact;
    @FXML
    private Button addAppointmentsCancel;
    @FXML
    private TextField addAppointmentType;

    private class ValidationException extends Exception {
        private ValidationException(String message) {
            super(message);
        }
    }

    /**
     * Called after the FXML has been loaded, this method initializes the controller class.
     * Populates necessary data for contacts and appointment times in the corresponding fields.
     *
     * @throws SQLException if any SQL operation fails during initialization.
     */
    @FXML
    public void initialize() throws SQLException {
        populateContacts();
        populateAppointmentTimes();
    }

    /**
     * Handles the event when the user attempts to save a new appointment.
     * This involves validating all the input fields, handling the database operations,
     * and transitioning to the main screen upon success.
     *
     * @param event An ActionEvent object representing the triggered event.
     * @throws IOException if there's an issue transitioning to another scene.
     */

    @FXML
    void addAppointmentSave(ActionEvent event) throws IOException {

        try {
            Connection connection = JDBC.startConnection();
            if (connection == null) {
                showAlertAndReturn("Failed to connect to the database.", Alert.AlertType.ERROR);
                return;
            }

            // Validation for empty fields
            if (!validateInputField(addAppointmentTitle, "Appointment Title") ||
                    !validateInputField(addAppointmentDescription, "Appointment Description") ||
                    !validateInputField(addAppointmentLocation, "Appointment Location") ||
                    !validateInputField(addAppointmentType, "Appointment Type") ||
                    !validateInputField(addAppointmentCustomerID, "Customer ID") ||
                    !validateInputField(addAppointmentUserID, "User ID") ||
                    !validateComboBox(addAppointmentContact, "Appointment Contact") ||
                    !validateComboBox(addAppointmentStartTime, "Start Time") ||
                    !validateComboBox(addAppointmentEndTime, "End Time") ||
                    !validateDatePicker(addAppointmentStartDate, "Start Date") ||
                    !validateDatePicker(addAppointmentEndDate, "End Date") ||
                    !validateNumeric(addAppointmentCustomerID, "Customer ID") ||
                    !validateNumeric(addAppointmentUserID, "User ID")) {
                return;  // Stop execution if any validation fails
            }

            try {
                handleDatabaseOperations();
                General.transitionToScene("../View/MainScreen.fxml");
            } catch (ValidationException e) {
                showAlertAndReturn(e.getMessage(), Alert.AlertType.ERROR);
            }
        } catch (SQLException throwables) {
            showAlertAndReturn("Database error. Please try again.", Alert.AlertType.ERROR);
            throwables.printStackTrace();
        }
    }

    /**
     * Responsible for all database operations related to appointment addition. It performs:
     * 1. Checking for overlapping appointments.
     * 2. Verifying business hours and days.
     * 3. Inserting the new appointment record into the database.
     *
     * @throws SQLException if any SQL operation fails during database operations.
     */
    private void handleDatabaseOperations() throws SQLException, ValidationException {

            ObservableList<Customers> getAllCustomers = CustomerAccess.getAllCustomers(connection);
            ObservableList<Integer> storeCustomerIDs = FXCollections.observableArrayList();
            ObservableList<UserAccess> getAllUsers = UserAccess.getAllUsers();
            ObservableList<Integer> storeUserIDs = FXCollections.observableArrayList();
            ObservableList<Appointments> getAllAppointments = AppointmentAccess.getAllAppointments();

            getAllCustomers.stream().map(Customers::getCustomerID).forEach(storeCustomerIDs::add);
            getAllUsers.stream().map(Users::getUserID).forEach(storeUserIDs::add);

            LocalDate localDateEnd = addAppointmentEndDate.getValue();
            LocalDate localDateStart = addAppointmentStartDate.getValue();

            DateTimeFormatter minHourFormat = DateTimeFormatter.ofPattern("HH:mm");
            String appointmentStartDate = addAppointmentStartDate.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String appointmentStartTime = addAppointmentStartTime.getValue();

            String endDate = addAppointmentEndDate.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String endTime = addAppointmentEndTime.getValue();

            System.out.println("thisDate + thisStart " + appointmentStartDate + " " + appointmentStartTime + ":00");
            String startUTC = convertTimeDateUTC(appointmentStartDate + " " + appointmentStartTime + ":00");
            String endUTC = convertTimeDateUTC(endDate + " " + endTime + ":00");

            LocalTime localTimeStart = LocalTime.parse(addAppointmentStartTime.getValue(), minHourFormat);
            LocalTime LocalTimeEnd = LocalTime.parse(addAppointmentEndTime.getValue(), minHourFormat);

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
                throw new ValidationException("The day is outside of business days: Monday - Friday");
            }

            if (startAppointmentTimeToCheck.isBefore(estBusinessStart) || startAppointmentTimeToCheck.isAfter(estBusinessEnd) || endAppointmentTimeToCheck.isBefore(estBusinessStart) || endAppointmentTimeToCheck.isAfter(estBusinessEnd)) {
                System.out.println("Time is outside of business hours.");
                throw new ValidationException("Time is out of business hours: 8am - 10pm.");
            }

            int newAppointmentID = Integer.parseInt(String.valueOf((int) (Math.random() * 100)));
            int customerID = Integer.parseInt(addAppointmentCustomerID.getText());

            if (!storeCustomerIDs.contains(customerID)) {
                System.out.println("The Customer ID does not exist.");
                throw new ValidationException("The Customer ID does not exist.");
            }

            if (dateTimeStart.isAfter(dateTimeEnd)) {
                System.out.println("Appointment's start time is after its end time.");
                throw new ValidationException("Appointment's start time is after its end time.");
            }

            if (dateTimeStart.isEqual(dateTimeEnd)) {
                System.out.println("Appointment's start and end time are the same.");
                throw new ValidationException("Appointment's start time and end time are the same.");
            }
            for (Appointments appointment : getAllAppointments) {
                LocalDateTime checkStart = appointment.getStart();
                LocalDateTime checkEnd = appointment.getEnd();

                if (customerID == appointment.getCustomerID() && newAppointmentID != appointment.getAppointmentID()) {
                    if (dateTimeStart.isBefore(checkEnd) && dateTimeEnd.isAfter(checkStart)) {
                        System.out.println("Appointment's time overlaps with an existing appointment's time.");
                        throw new ValidationException("Appointment's time overlaps with an existing appointment's time.");
                    }
                }

                if ((customerID == appointment.getCustomerID()) && (newAppointmentID != appointment.getAppointmentID()) &&
                        (dateTimeStart.isBefore(checkStart)) && (dateTimeEnd.isAfter(checkEnd))) {
                    System.out.println("Appointment's time overlaps with an existing appointment's time.");
                    throw new ValidationException("Appointment's time overlaps with an existing appointment's time.");
                }

                if ((customerID == appointment.getCustomerID()) && (newAppointmentID != appointment.getAppointmentID()) &&
                        (dateTimeStart.isAfter(checkStart)) && (dateTimeStart.isBefore(checkEnd))) {
                    System.out.println("The start time overlaps with an existing appointment.");
                    throw new ValidationException("The start time overlaps with an existing appointment.");
                }

                if (customerID == appointment.getCustomerID() && (newAppointmentID != appointment.getAppointmentID()) &&
                        (dateTimeEnd.isAfter(checkStart)) && (dateTimeEnd.isBefore(checkEnd))) {
                    System.out.println("The end time overlaps with an existing appointment.");
                    throw new ValidationException("The end time overlaps with an existing appointment.");
                }

            }

            String insertStatement = "INSERT INTO appointments (Appointment_ID, Title, Description, Location, Type, Start, End, Create_Date, Created_By, Last_Update, Last_Updated_By, Customer_ID, User_ID, Contact_ID) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

            JDBC.setPreparedStatement(JDBC.getConnection(), insertStatement);
            PreparedStatement ps = JDBC.getPreparedStatement();
            ps.setInt(1, newAppointmentID);
            ps.setString(2, addAppointmentTitle.getText());
            ps.setString(3, addAppointmentDescription.getText());
            ps.setString(4, addAppointmentLocation.getText());
            ps.setString(5, addAppointmentType.getText());
            ps.setTimestamp(6, Timestamp.valueOf(startUTC));
            ps.setTimestamp(7, Timestamp.valueOf(endUTC));
            ps.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(9, "admin");
            ps.setTimestamp(10, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(11, 1);
            ps.setInt(12, Integer.parseInt(addAppointmentCustomerID.getText()));
            ps.setInt(13, Integer.parseInt(addAppointmentUserID.getText()));
            ps.setInt(14, Integer.parseInt(ContactAccess.findContactID(addAppointmentContact.getValue())));

            ps.execute();
            showAlertAndReturn("Appointment added successfully!", Alert.AlertType.INFORMATION);
        }


    /**
     * Handles the event of canceling the addition of a new appointment.
     * Presents a confirmation alert to the user and returns to the main screen if the cancellation is confirmed.
     *
     * @param event An ActionEvent object representing the triggered event.
     * @throws IOException if there's an issue transitioning to another scene.
     */

    @FXML
    public void addAppointmentsCancel(ActionEvent event) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to cancel without saving?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            General.transitionToScene("../View/MainScreen.fxml");
        }
    }

    /**
     * Fetches all contacts from the database and populates the contact ComboBox
     * with contact names for the user's selection.
     *
     * @throws SQLException if any SQL operation fails during fetching of contacts.
     */
    private void populateContacts() throws SQLException {
        ObservableList<Contacts> contactsObservableList = ContactAccess.getAllContacts();
        ObservableList<String> allContactsNames = FXCollections.observableArrayList();
        contactsObservableList.forEach(contact -> allContactsNames.add(contact.getContactName()));
        addAppointmentContact.setItems(allContactsNames);
    }

    /**
     * Populates the ComboBoxes for appointment start and end times. The times are
     * set in 15-minute intervals, starting from 8:00 AM to 10:45 PM.
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
        addAppointmentStartTime.setItems(appointmentTimes);
        addAppointmentEndTime.setItems(appointmentTimes);
    }
}
