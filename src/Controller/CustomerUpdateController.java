package Controller;

import DAO.CountryAccess;
import DAO.FirstLevelDivisionAccess;
import Helper.General;
import Model.Country;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import Model.Customers;
import DAO.CustomerAccess;
import Helper.JDBC;
import javafx.scene.control.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * The CustomerUpdateController class provides methods and event handlers
 * to manage the updating of customer records.
 * It offers functionality such as validating input, populating dropdown lists,
 * and updating customer information in the database.
 *
 * This controller operates in tandem with the CustomerUpdateScreen.fxml.
 */

public class CustomerUpdateController {

    private TableView<Customers> customerRecordsTable;

    @FXML
    private TextField updateCustomerId;

    @FXML
    private TextField updateCustomerName;

    @FXML
    private TextField updateCustomerAddress;

    @FXML
    private TextField updateCustomerPostalCode;

    @FXML
    private TextField updateCustomerPhone;

    @FXML
    private ComboBox<String> updateCustomerCountry;

    @FXML
    private ComboBox<String> updateCustomerState;

    /**
     * Sets the customer records table and initializes the country dropdown list.
     *
     * @param table The main TableView of customers.
     * @throws SQLException If there's an issue accessing the database.
     */
    public void setCustomerRecordsTable(TableView<Customers> table) throws SQLException {
        this.customerRecordsTable = table;
        ObservableList<CountryAccess> allCountries = CountryAccess.getCountries();
        ObservableList<String> countryNames = FXCollections.observableArrayList();

        allCountries.stream().map(Country::getCountryName).forEach(countryNames::add);
        if (updateCustomerCountry != null) {
            updateCustomerCountry.setItems(countryNames);
        }

    }

    /**
     * Populates the country dropdown list based on available countries from the database.
     *
     * @throws SQLException If there's an issue accessing the database.
     */

    private void populateCountries() throws SQLException {
        ObservableList<CountryAccess> allCountries = CountryAccess.getCountries();
        ObservableList<String> countryNames = FXCollections.observableArrayList();
        for (CountryAccess country : allCountries) {
            countryNames.add(country.getCountryName());
        }
        updateCustomerCountry.setItems(countryNames);
    }

    /**
     * Populates the divisions (states/provinces) dropdown list based on a provided country ID.
     *
     * @param countryID The ID of the selected country.
     * @throws SQLException If there's an issue accessing the database.
     */
    private void populateDivisionsForCountryID(int countryID) throws SQLException {
        ObservableList<FirstLevelDivisionAccess> divisions = FirstLevelDivisionAccess.getDivisionsByCountryID(countryID);
        ObservableList<String> divisionNames = FXCollections.observableArrayList();
        for (FirstLevelDivisionAccess division : divisions) {
            divisionNames.add(division.getDivisionName());
        }
        updateCustomerState.setItems(divisionNames);
    }

    /**
     * Returns the country ID associated with the given country name.
     *
     * @param countryName The name of the country.
     * @return The ID of the country or -1 if not found.
     * @throws SQLException If there's an issue accessing the database.
     */
    private int getCountryIDByName(String countryName) throws SQLException {
        for (CountryAccess country : CountryAccess.getCountries()) {
            if (country.getCountryName().equals(countryName)) {
                return country.getCountryID();
            }
        }
        return -1;  // default, can be changed to throw an exception if the country ID is not found
    }

    private Customers currentCustomer;

    /**
     * Sets the customer to be updated and populates the fields with its data.
     *
     * @param customer The customer object to be updated.
     * @throws SQLException If there's an issue accessing the database.
     */

    public void setCustomerToUpdate(Customers customer) throws SQLException {
        this.currentCustomer = customer;

        // populate the fields
        updateCustomerId.setText(String.valueOf(customer.getCustomerID()));
        updateCustomerName.setText(customer.getCustomerName());
        updateCustomerAddress.setText(customer.getCustomerAddress());
        updateCustomerPostalCode.setText(customer.getCustomerPostalCode());
        updateCustomerPhone.setText(customer.getCustomerPhone());

        // Populate the countries and set the selected country
        populateCountries();

        // Note: You'll need to find the country ID based on divisionID if you don't store it in the Customers object
        int countryID = getCountryIDByDivision(customer.getCustomerDivisionID());
        updateCustomerCountry.setValue(getCountryNameByID(countryID));

        // Populate and set the state ComboBox based on the selected country
        populateDivisionsForCountryID(countryID);
        updateCustomerState.setValue(customer.getDivisionName());



        updateCustomerCountry.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            try {
                if (newValue != null) {
                    int selectedCountryID = getCountryIDByName(newValue);
                    populateDivisionsForCountryID(selectedCountryID);
                }
            } catch (SQLException e) {
                // handle exception, perhaps with a dialog or logging
                e.printStackTrace();
            }
        });
    }

    /**
     * Determines the country ID associated with a given division ID.
     *
     * @param divisionID The ID of the division.
     * @return The associated country ID or -1 if not found.
     * @throws SQLException If there's an issue accessing the database.
     */
    private int getCountryIDByDivision(int divisionID) throws SQLException {
        // Assuming you have a method in FirstLevelDivisionAccess that retrieves the country ID based on division ID
        for (FirstLevelDivisionAccess division : FirstLevelDivisionAccess.getAllFirstLevelDivisions()) {
            if (division.getDivisionID() == divisionID) {
                return division.getCountry_ID();
            }
        }
        return -1;  // default, can be changed to throw an exception if the country ID is not found
    }

    /**
     * Returns the name of a country based on its ID.
     *
     * @param countryID The ID of the country.
     * @return The name of the country or null if not found.
     * @throws SQLException If there's an issue accessing the database.
     */

    private String getCountryNameByID(int countryID) throws SQLException {
        for (CountryAccess country : CountryAccess.getCountries()) {
            if (country.getCountryID() == countryID) {
                return country.getCountryName();
            }
        }
        return null;  // default, can be changed to throw an exception if the country name is not found
    }

    /**
     * Event handler for the cancel button, which transitions the user back to the main screen.
     *
     * @param event The associated action event.
     * @throws IOException If there's an issue with transitioning screens.
     */

    @FXML
    public void customerRecordsCancel(ActionEvent event) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to cancel without saving?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            General.transitionToScene("../View/MainScreen.fxml");
        }
        General.transitionToScene( "../View/MainScreen.fxml");
    }

    /**
     * Validates the input data in the update form.
     *
     * @return true if input data is valid, otherwise false.
     */

    private boolean isValidInput() {
        if (!General.validateInputField(updateCustomerId, "Customer ID")) {
            return false;
        }
        if (!General.validateNumeric(updateCustomerId, "Customer ID")) {
            return false;
        }
        if (!General.validateInputField(updateCustomerName, "Customer Name")) {
            return false;
        }
        if (!General.validateInputField(updateCustomerAddress, "Customer Address")) {
            return false;
        }
        if (!General.validateInputField(updateCustomerPostalCode, "Postal Code")) {
            return false;
        }
        if (!General.validateInputField(updateCustomerPhone, "Phone")) {
            return false;
        }
        if (!General.validateNumeric(updateCustomerPhone, "Phone")) {
            return false;
        }
        if (!General.validateComboBox(updateCustomerCountry, "Country")) {
            return false;
        }
        if (!General.validateComboBox(updateCustomerState, "State/Province")) {
            return false;
        }
        return true;
    }

    /**
     * Event handler for the update button.
     * Updates the customer's record in the database and refreshes the table view.
     *
     * @param event The associated action event.
     * @throws IOException If there's an issue with transitioning screens.
     */

    @FXML
    void customerRecordsUpdateCustomer(ActionEvent event) throws IOException {

        if (!isValidInput()) {
            return;
        }

        try {
            Connection connection = JDBC.startConnection();

            if (!updateCustomerName.getText().isEmpty() || !updateCustomerAddress.getText().isEmpty() || !updateCustomerAddress.getText().isEmpty() || !updateCustomerPostalCode.getText().isEmpty() || !updateCustomerPhone.getText().isEmpty() || !(updateCustomerCountry.getValue() == null) || !(updateCustomerState.getValue() == null))
            {
                int firstLevelDivisionName = 0;
                for (FirstLevelDivisionAccess firstLevelDivision : FirstLevelDivisionAccess.getAllFirstLevelDivisions()) {
                    if (updateCustomerState.getSelectionModel().getSelectedItem().equals(firstLevelDivision.getDivisionName())) {
                        firstLevelDivisionName = firstLevelDivision.getDivisionID();
                    }
                }

                // Use an UPDATE statement
                String updateStatement = "UPDATE customers SET Customer_Name=?, Address=?, Postal_Code=?, Phone=?, Last_Update=?, Last_Updated_By=?, Division_ID=? WHERE Customer_ID=?";
                JDBC.setPreparedStatement(JDBC.getConnection(), updateStatement);
                PreparedStatement ps = JDBC.getPreparedStatement();

                // Set parameters for the UPDATE statement
                ps.setString(1, updateCustomerName.getText());
                ps.setString(2, updateCustomerAddress.getText());
                ps.setString(3, updateCustomerPostalCode.getText());
                ps.setString(4, updateCustomerPhone.getText());
                ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
                ps.setString(6, "admin");
                ps.setInt(7, firstLevelDivisionName);
                ps.setInt(8, currentCustomer.getCustomerID());  // Use the ID from the currentCustomer

                ps.executeUpdate();

                ObservableList<Customers> refreshCustomersList = CustomerAccess.getAllCustomers(connection);
                customerRecordsTable.setItems(refreshCustomersList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        General.transitionToScene("../View/MainScreen.fxml");
    }

}
