package Controller;

import DAO.AppointmentAccess;
import DAO.ContactAccess;
import DAO.ReportAccess;
import Model.*;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;


import java.io.IOException;
import java.sql.SQLException;
import java.time.Month;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static Helper.General.transitionToScene;

/**
 * The ReportController class manages and controls the report view for the application.
 */
public class ReportController {

    @FXML private TableView<Appointments> allAppointmentsTable;
    @FXML private TableColumn<?, ?> appointmentContact;
    @FXML private TableColumn<?, ?> appointmentCustomerID;
    @FXML private TableColumn<?, ?> appointmentDescription;
    @FXML private TableColumn<?, ?> appointmentEnd;
    @FXML private TableColumn<?, ?> appointmentID;
    @FXML private TableColumn<?, ?> appointmentLocation;
    @FXML private TableColumn<?, ?> appointmentStart;
    @FXML private TableColumn<?, ?> appointmentTitle;
    @FXML private TableColumn<?, ?> appointmentTotalsAppointmentTypeCol;
    @FXML private TableColumn<?, ?> appointmentTotalsByMonth;
    @FXML private TableColumn<?, ?> appointmentTotalsMonthTotal;
    @FXML private TableColumn<?, ?> appointmentTotalsTypeTotalCol;
    @FXML private TableColumn<?, ?> appointmentType;
    @FXML private ComboBox<String> contactScheduleContactBox;
    @FXML private TableColumn<?, ?> tableContactID;
    @FXML private TableView<ReportType> appointmentTotalsAppointmentType;
    @FXML private Tab appointmentTotalsTab;
    @FXML private TableView<ReportMonth> appointmentTotalAppointmentByMonth;
    @FXML private TableView<Reports> customerByCountry;
    @FXML private TableColumn<?, ?> countryName;
    @FXML private TableColumn<?, ?> countryCounter;
    @FXML private Tab reportCustomerByCountry;

    @FXML private Button backButton;

    /**
     * Initializes the controller class. Sets up table columns and the contact combo box.
     *
     * @throws SQLException if any SQL related errors occur.
     */

    public void initialize() throws SQLException {
        setupTableColumns();
        setupContactComboBox();
    }

    /**
     * Handles the back button event. Transitions to the main screen view.
     *
     * @param event The action event associated with the button press.
     * @throws IOException if there's an error loading the next scene.
     */
    @FXML
    public void handleBackButton(ActionEvent event) throws IOException {
        transitionToScene("../View/MainScreen.fxml");
    }
    /**
     * Generates the appointment totals report when the corresponding tab is selected.
     */
    @FXML
    public void handleAppointmentTotalsTabSelected() {
        if (appointmentTotalsTab.isSelected()) {
            generateAppointmentTotalsReport();
        }
    }

    /**
     * Generates the customer by country report when the corresponding tab is selected.
     */

    @FXML
    public void handleCustomerByCountrySelected() {
        if (reportCustomerByCountry.isSelected()) {
            generateCustomerByCountryReport();
        }
    }

    /**
     * Sets up the table columns by associating them with the corresponding properties of the model.
     */
    private void setupTableColumns() {
        countryName.setCellValueFactory(new PropertyValueFactory<>("countryName"));
        countryCounter.setCellValueFactory(new PropertyValueFactory<>("countryCount"));
        appointmentID.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        appointmentTitle.setCellValueFactory(new PropertyValueFactory<>("appointmentTitle"));
        appointmentDescription.setCellValueFactory(new PropertyValueFactory<>("appointmentDescription"));
        appointmentLocation.setCellValueFactory(new PropertyValueFactory<>("appointmentLocation"));
        appointmentType.setCellValueFactory(new PropertyValueFactory<>("appointmentType"));
        appointmentStart.setCellValueFactory(new PropertyValueFactory<>("start"));
        appointmentEnd.setCellValueFactory(new PropertyValueFactory<>("end"));
        appointmentCustomerID.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        tableContactID.setCellValueFactory(new PropertyValueFactory<>("contactID"));

        appointmentTotalsAppointmentTypeCol.setCellValueFactory(new PropertyValueFactory<>("appointmentType"));
        appointmentTotalsTypeTotalCol.setCellValueFactory(new PropertyValueFactory<>("appointmentTotal"));

        appointmentTotalsByMonth.setCellValueFactory(new PropertyValueFactory<>("appointmentMonth"));
        appointmentTotalsMonthTotal.setCellValueFactory(new PropertyValueFactory<>("appointmentTotal"));

    }

    /**
     * Sets up the contact combo box with contact names from the database.
     *
     * @throws SQLException if there's an error fetching contacts from the database.
     */
    private void setupContactComboBox() throws SQLException {
        // Lambda Expression Justification:
        // This stream and lambda combination facilitates the transformation of a list of Contacts into a list of their respective names.
        // It simplifies the code by directly mapping each Contact object to its name, avoiding the need for explicit looping.
        List<String> allContactNames = ContactAccess.getAllContacts().stream()
                .map(Contacts::getContactName)
                .collect(Collectors.toList());
        contactScheduleContactBox.setItems(FXCollections.observableArrayList(allContactNames));
    }

    /**
     * Updates the appointment data in the table view based on the selected contact in the combo box.
     */
    @FXML
    public void appointmentDataByContact() {
        try {
            String selectedContactName = contactScheduleContactBox.getSelectionModel().getSelectedItem();
            List<Appointments> appointmentsForContact = fetchAppointmentsForContact(selectedContactName);
            allAppointmentsTable.setItems(FXCollections.observableArrayList(appointmentsForContact));
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Fetches appointments associated with a given contact name.
     *
     * // Lambda Expression Justification:
     *         // The stream and lambda expression here help filter the appointments associated with a specific contactID.
     *         // This way, the code remains concise and readable without the need for explicit iteration and condition checking.
     *
     * @param contactName Name of the contact.
     * @return A list of appointments associated with the contact.
     * @throws SQLException if there's an error fetching appointments from the database.
     */

    private List<Appointments> fetchAppointmentsForContact(String contactName) throws SQLException {
        int contactID = fetchContactIdByName(contactName);
        // Lambda Expression Justification:
        // The stream and lambda expression here help filter the appointments associated with a specific contactID.
        // This way, the code remains concise and readable without the need for explicit iteration and condition checking.
        return AppointmentAccess.getAllAppointments().stream()
                .filter(appointment -> appointment.getContactID() == contactID)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves the ID of a contact by their name.
     *
     * // Lambda Expression Justification:
     *         // Here, the stream and lambda combination search for the contact with the given name.
     *         // It makes the code more intuitive as it directly relates the logic of filtering with the condition.
     *
     * @param contactName Name of the contact.
     * @return The ID of the contact.
     * @throws SQLException if there's an error fetching the contact from the database.
     */
    private int fetchContactIdByName(String contactName) throws SQLException {
        // Lambda Expression Justification:
        // Here, the stream and lambda combination search for the contact with the given name.
        // It makes the code more intuitive as it directly relates the logic of filtering with the condition.
        return ContactAccess.getAllContacts().stream()
                .filter(contact -> contactName.equals(contact.getContactName()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Contact not found")).getId();
    }

    /**
     * Generates a report showing appointment totals and updates the table view with the results.
     *
     * // Lambda Expression Justification:
     *             // This lambda stream is utilized to transform a list of Appointments into a list of Months of those appointments.
     *             // It promotes code simplicity and avoids the requirement for manual iteration.
     * // Lambda Expression Justification:
     *             // This lambda stream aids in identifying unique appointment types from a list of Appointments.
     *             // The distinct() method combined with map helps in fetching a distinct list of types.
     * // Lambda Expression Justification:
     *             // This lambda stream constructs a new list of ReportMonth objects by counting occurrences of each month.
     *             // The combination of distinct() and map methods here efficiently computes the frequency of each month without multiple nested loops.
     * // Lambda Expression Justification:
     *             // This lambda stream constructs a new list of ReportType objects by counting the occurrences of each unique appointment type.
     *             // It keeps the logic concise and avoids the necessity for multiple loops or conditions.
     */
    @FXML
    public void generateAppointmentTotalsReport() {
        try {
            List<Appointments> allAppointments = AppointmentAccess.getAllAppointments();


            // Lambda Expression Justification:
            // This lambda stream is utilized to transform a list of Appointments into a list of Months of those appointments.
            // It promotes code simplicity and avoids the requirement for manual iteration.
            List<Month> appointmentMonths = allAppointments.stream()
                    .map(appointment -> appointment.getStart().getMonth())
                    .collect(Collectors.toList());

            // Lambda Expression Justification:
            // This lambda stream aids in identifying unique appointment types from a list of Appointments.
            // The distinct() method combined with map helps in fetching a distinct list of types.
            List<String> uniqueAppointmentTypes = allAppointments.stream()
                    .map(Appointments::getAppointmentType)
                    .distinct()
                    .collect(Collectors.toList());

            // Lambda Expression Justification:
            // This lambda stream constructs a new list of ReportMonth objects by counting occurrences of each month.
            // The combination of distinct() and map methods here efficiently computes the frequency of each month without multiple nested loops.
            List<ReportMonth> reportMonths = appointmentMonths.stream()
                    .distinct()
                    .map(month -> new ReportMonth(month.name(), Collections.frequency(appointmentMonths, month)))
                    .collect(Collectors.toList());

            // Lambda Expression Justification:
            // This lambda stream constructs a new list of ReportType objects by counting the occurrences of each unique appointment type.
            // It keeps the logic concise and avoids the necessity for multiple loops or conditions.
            List<ReportType> reportTypes = uniqueAppointmentTypes.stream()
                    .map(type -> new ReportType(type, (int) allAppointments.stream().filter(a -> a.getAppointmentType().equals(type)).count()))
                    .collect(Collectors.toList());

            appointmentTotalsAppointmentType.setItems(FXCollections.observableArrayList(reportTypes));
            appointmentTotalAppointmentByMonth.setItems(FXCollections.observableArrayList(reportMonths));



        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Generates a report of customers by country and updates the table view with the results.
     */
    @FXML
    public void generateCustomerByCountryReport() {
        try {
            List<Reports> countriesReport = ReportAccess.getCountries();
            customerByCountry.setItems(FXCollections.observableArrayList(countriesReport));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

}