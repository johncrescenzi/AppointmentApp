package Controller;

import DAO.AppointmentAccess;
import Helper.General;
import Helper.JDBC;
import Model.Appointments;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;


/**
 * The AppointmentController class handles the CRUD operations for Appointments and
 * manages the display and behavior of the appointments UI.
 */


public class AppointmentController {

    @FXML private RadioButton allAppointmentRadio;
    @FXML private RadioButton appointmentWeekRadio;
    @FXML private RadioButton appointmentMonthRadio;

    @FXML
    private TableView<Appointments> allAppointmentsTable;
    @FXML private TableColumn<?, ?> appointmentContact;
    @FXML private TableColumn<?, ?> appointmentCustomerID;
    @FXML private TableColumn<?, ?> appointmentDescription;
    @FXML private TableColumn<?, ?> appointmentEnd;
    @FXML private TableColumn<?, ?> appointmentID;
    @FXML private TableColumn<?, ?> appointmentLocation;
    @FXML private TableColumn<?, ?> appointmentStart;
    @FXML private TableColumn<?, ?> appointmentTitle;
    @FXML private TableColumn<?, ?> appointmentType;
    @FXML private TableColumn<?, ?> tableContactID;
    @FXML private TableColumn<?, ?> tableUserID;
    @FXML private Button deleteAppointment;
    @FXML private Button updateAppointment;


    /**
     * Sets up the initial configuration and data loading for the appointment view.
     */
    @FXML
    public void initialize() {
        configureTableColumns();
        loadAllAppointments();
        deleteAppointment.setDisable(true);
        updateAppointment.setDisable(true);

        // Listeners for Appointment Selection
        allAppointmentsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            deleteAppointment.setDisable(newSelection == null);
            updateAppointment.setDisable(newSelection == null);
        });
    }

    /**
     * Configures the display columns for the appointments table.
     */
    @FXML
    private void configureTableColumns() {
        appointmentID.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        appointmentTitle.setCellValueFactory(new PropertyValueFactory<>("appointmentTitle"));
        appointmentDescription.setCellValueFactory(new PropertyValueFactory<>("appointmentDescription"));
        appointmentLocation.setCellValueFactory(new PropertyValueFactory<>("appointmentLocation"));
        appointmentType.setCellValueFactory(new PropertyValueFactory<>("appointmentType"));
        appointmentStart.setCellValueFactory(new PropertyValueFactory<>("start"));
        appointmentEnd.setCellValueFactory(new PropertyValueFactory<>("end"));
        appointmentCustomerID.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        tableContactID.setCellValueFactory(new PropertyValueFactory<>("contactID"));
        tableUserID.setCellValueFactory(new PropertyValueFactory<>("userID"));
    }

    /**
     * Transitions to the Add Appointment screen.
     *
     * @param event The UI action that triggered this method.
     * @throws IOException If there is an issue loading the new scene.
     */
    @FXML
    void addAppointment(ActionEvent event) throws IOException {
        General.transitionToScene("../View/AppointmentAddScreen.fxml");
    }

    /**
     * Transitions to the Update Appointment screen if an appointment is selected.
     *
     * @param event The UI action that triggered this method.
     * @throws IOException If there is an issue loading the new scene.
     */
    @FXML
    void updateAppointment(ActionEvent event) throws IOException {
        Appointments selectedAppointment = allAppointmentsTable.getSelectionModel().getSelectedItem();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("../View/AppointmentUpdateScreen.fxml"));
        Parent root = loader.load();
        AppointmentUpdateController controller = loader.getController();
        controller.setAppointment(selectedAppointment);

        // Transitioning to the new FXML:
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Deletes the selected appointment after user confirmation.
     *
     * @param event The UI action that triggered this method.
     * @throws Exception If there's an error during deletion or database access.
     */
    @FXML
    void deleteAppointment(ActionEvent event) throws Exception {
        try {
            Connection connection = JDBC.getConnection();
            int deleteAppointmentID = allAppointmentsTable.getSelectionModel().getSelectedItem().getAppointmentID();
            String deleteAppointmentType = allAppointmentsTable.getSelectionModel().getSelectedItem().getAppointmentType();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete the selected appointment with appointment id: " + deleteAppointmentID + " and appointment type " + deleteAppointmentType);
            Optional<ButtonType> confirmation = alert.showAndWait();
            if (confirmation.isPresent() && confirmation.get() == ButtonType.OK) {
                AppointmentAccess.deleteAppointment(deleteAppointmentID, connection);
                ObservableList<Appointments> allAppointmentsList = AppointmentAccess.getAllAppointments();
                allAppointmentsTable.setItems(allAppointmentsList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Filters and displays appointments from the past week.
     *
     * @param event The UI action that triggered this method.
     * @throws SQLException If there's an error accessing the database.
     */
    @FXML
    void appointmentWeekSelected(ActionEvent event) throws SQLException {
        try {
            ObservableList<Appointments> allAppointmentsList = AppointmentAccess.getAllAppointments();
            ObservableList<Appointments> appointmentsWeek = FXCollections.observableArrayList();
            LocalDateTime weekStart = LocalDateTime.now().minusWeeks(1);
            LocalDateTime weekEnd = LocalDateTime.now().plusWeeks(1);
            if (allAppointmentsList != null)
                allAppointmentsList.forEach(appointment -> {
                    if (appointment.getEnd().isAfter(weekStart) && appointment.getEnd().isBefore(weekEnd)) {
                        appointmentsWeek.add(appointment);
                    }
                    allAppointmentsTable.setItems(appointmentsWeek);
                });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Filters and displays appointments from the past month.
     *
     * @param event The UI action that triggered this method.
     * @throws SQLException If there's an error accessing the database.
     */
    @FXML
    void appointmentMonthSelected(ActionEvent event) throws SQLException {
        try {
            ObservableList<Appointments> allAppointmentsList = AppointmentAccess.getAllAppointments();
            ObservableList<Appointments> appointmentsMonth = FXCollections.observableArrayList();
            LocalDateTime currentMonthStart = LocalDateTime.now().minusMonths(1);
            LocalDateTime currentMonthEnd = LocalDateTime.now().plusMonths(1);
            if (allAppointmentsList != null)
                allAppointmentsList.forEach(appointment -> {
                    if (appointment.getEnd().isAfter(currentMonthStart) && appointment.getEnd().isBefore(currentMonthEnd)) {
                        appointmentsMonth.add(appointment);
                    }
                    allAppointmentsTable.setItems(appointmentsMonth);
                });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Displays all appointments without any filtering.
     *
     * @param event The UI action that triggered this method.
     * @throws SQLException If there's an error accessing the database.
     */
    @FXML
    void appointmentAllSelected(ActionEvent event) throws SQLException {
        try {
            ObservableList<Appointments> allAppointmentsList = AppointmentAccess.getAllAppointments();
            if (allAppointmentsList != null)
                for (Model.Appointments appointment : allAppointmentsList) {
                    allAppointmentsTable.setItems(allAppointmentsList);
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Fetches and displays all appointments in the table.
     */
    private void loadAllAppointments() {
        try {
            ObservableList<Appointments> allAppointmentsList = AppointmentAccess.getAllAppointments();
            allAppointmentsTable.setItems(allAppointmentsList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}