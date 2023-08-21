package Controller;

import DAO.AppointmentAccess;
import DAO.CountryAccess;
import DAO.CustomerAccess;
import DAO.FirstLevelDivisionAccess;
import Helper.General;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import Helper.JDBC;
import Model.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import Model.Customers;

import static Helper.General.showAlertAndReturn;


/**
 * Controller for managing customer records. This includes displaying customer details,
 * adding, updating, and deleting customer records.
 *
 **/

 public class CustomerController implements Initializable {

    public Button customerRecordsAddCustomerButton;
    public Button customerRecordsDeleteButton;
    public Button customerRecordsUpdateCustomerButton;
    @FXML
    private TableColumn<?, ?> customerRecordsTableName;
    @FXML
    private Button customerRecordsAddCustomer;
    @FXML
    private Button customerRecordsCancel;
    @FXML
    private TableView<Customers> customerRecordsTable;
    @FXML
    private TableColumn<?, ?> customerRecordsTableAddress;
    @FXML
    private TableColumn<?, ?> customerRecordsTableID;
    @FXML
    private TableColumn<?, ?> customerRecordsTablePhone;
    @FXML
    private TableColumn<?, ?> customerRecordsTablePostalCode;
    @FXML
    private TableColumn<?, ?> customerRecordsTableState;
    @FXML
    private TableColumn<?, ?> customerRecordsTableCountry;


    /**
     * Sets the customer records table.
     *
     * @param table The TableView containing customer records.
     */
    public void setCustomerRecordsTable(TableView<Customers> table) {
        this.customerRecordsTable = table;
    }

    /**
     * Initialize the customer controller.
     * This method populates the customer records table with the relevant data.
     *
     * @param url A URL to initialize resources.
     * @param resourceBundle A ResourceBundle for localization.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        try {
            Connection connection = JDBC.startConnection();

            ObservableList<CountryAccess> allCountries = CountryAccess.getCountries();
            ObservableList<String> countryNames = FXCollections.observableArrayList();
            ObservableList<FirstLevelDivisionAccess> allFirstLevelDivisions = FirstLevelDivisionAccess.getAllFirstLevelDivisions();
            ObservableList<String> firstLevelDivisionAllNames = FXCollections.observableArrayList();
            ObservableList<Customers> allCustomersList = CustomerAccess.getAllCustomers(connection);

            customerRecordsTableID.setCellValueFactory(new PropertyValueFactory<>("customerID"));
            customerRecordsTableName.setCellValueFactory(new PropertyValueFactory<>("customerName"));
            customerRecordsTableAddress.setCellValueFactory(new PropertyValueFactory<>("customerAddress"));
            customerRecordsTablePostalCode.setCellValueFactory(new PropertyValueFactory<>("customerPostalCode"));
            customerRecordsTablePhone.setCellValueFactory(new PropertyValueFactory<>("customerPhone"));
            customerRecordsTableState.setCellValueFactory(new PropertyValueFactory<>("divisionName"));

            ObservableList<Customers> refreshCustomersList = CustomerAccess.getAllCustomers(connection);
            customerRecordsTable.setItems(refreshCustomersList);


            allCountries.stream().map(Country::getCountryName).forEach(countryNames::add);

            allFirstLevelDivisions.forEach(firstLevelDivision -> firstLevelDivisionAllNames.add(firstLevelDivision.getDivisionName()));

            customerRecordsTable.setItems(allCustomersList);

            // Disable Update and Delete button by default
            customerRecordsUpdateCustomerButton.setDisable(true);
            customerRecordsDeleteButton.setDisable(true);

            // Listeners for the customer being selected so Update and Delete can be used.
            customerRecordsTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) { // if a customer is selected
                    customerRecordsUpdateCustomerButton.setDisable(false);
                    customerRecordsDeleteButton.setDisable(false);
                } else {
                    customerRecordsUpdateCustomerButton.setDisable(true);
                    customerRecordsDeleteButton.setDisable(true);
                }
            });



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Handler for updating customer records.
     * Launches the customer update screen for the selected customer.
     *
     * @param event The action event.
     * @throws IOException If there is an error loading the FXML.
     * @throws SQLException If there is an error with the database operation.
     */
    @FXML
    void customerRecordsUpdateCustomer(ActionEvent event) throws IOException, SQLException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("../View/CustomerUpdateScreen.fxml"));
        Parent root = loader.load();

        CustomerUpdateController nextController = loader.getController();

        nextController.setCustomerRecordsTable(customerRecordsTable);

        Customers selectedCustomer = customerRecordsTable.getSelectionModel().getSelectedItem();

        if(selectedCustomer == null) {
            showAlertAndReturn("Please select an appointment to update.", Alert.AlertType.ERROR);
            return;
        }

        if (selectedCustomer != null) {
            nextController.setCustomerToUpdate(selectedCustomer);
        }

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    /**
     * Handler for adding new customer records.
     * Transitions to the customer addition screen.
     *
     * @param event The action event.
     * @throws IOException If there is an error loading the FXML.
     */
    @FXML
    void customerRecordsAddCustomer (ActionEvent event) throws IOException {
        General.transitionToScene("../View/CustomerAddScreen.fxml");
    }

    /**
     * Handler for deleting a customer record.
     * Deletes the selected customer record and updates the table view.
     *
     * @param event The action event.
     * @throws Exception If any error occurs during processing.
     */

    @FXML
    void customerRecordsDeleteCustomer(ActionEvent event) throws Exception {

        Connection connection = JDBC.startConnection();
        ObservableList<Appointments> getAllAppointmentsList = AppointmentAccess.getAllAppointments();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete the selected customer and all appointments? ");
        Optional<ButtonType> confirmation = alert.showAndWait();
        if (confirmation.isPresent() && confirmation.get() == ButtonType.OK) {
            int deleteCustomerID = customerRecordsTable.getSelectionModel().getSelectedItem().getCustomerID();
            AppointmentAccess.deleteAppointment(deleteCustomerID, connection);

            String sqlDelete = "DELETE FROM customers WHERE Customer_ID = ?";
            JDBC.setPreparedStatement(JDBC.getConnection(), sqlDelete);

            PreparedStatement psDelete = JDBC.getPreparedStatement();
            int customerFromTable = customerRecordsTable.getSelectionModel().getSelectedItem().getCustomerID();

            for (Appointments appointment : getAllAppointmentsList) {
                int customerFromAppointments = appointment.getCustomerID();
                if (customerFromTable == customerFromAppointments) {
                    String deleteStatementAppointments = "DELETE FROM appointments WHERE Appointment_ID = ?";
                    JDBC.setPreparedStatement(JDBC.getConnection(), deleteStatementAppointments);
                }
            }
            psDelete.setInt(1, customerFromTable);
            psDelete.execute();
            ObservableList<Customers> refreshCustomersList = CustomerAccess.getAllCustomers(connection);
            customerRecordsTable.setItems(refreshCustomersList);
        }
    }

}
