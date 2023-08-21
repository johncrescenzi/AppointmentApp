package Controller;

import DAO.CountryAccess;
import DAO.FirstLevelDivisionAccess;
import Helper.General;
import Helper.JDBC;
import Model.Customers;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static Helper.General.showAlertAndReturn;

/**
 * This class serves as a controller for the Add Customer screen.
 * It provides functionality to input customer data and save it to the database.
 * It also handles the user interactions with the UI components on the screen.
 */
public class CustomerAddController implements Initializable {
    private TableView<Customers> addCustomerRecordsTable;

    @FXML
    private TextField addCustomerId;

    @FXML
    private TextField addCustomerName;

    @FXML
    private TextField addCustomerAddress;

    @FXML
    private TextField addCustomerPostalCode;

    @FXML
    private TextField addCustomerPhone;

    @FXML
    private ComboBox<String> addCustomerCountry;

    @FXML
    private ComboBox<String> addCustomerState;

    private static final int US_ID = 1;
    private static final int UK_ID = 2;
    private static final int CANADA_ID = 3;

    /**
     * Handles the cancel button action on the Add Customer screen.
     * Redirects the user back to the main screen.
     *
     * @param event The action event triggered.
     * @throws IOException if there's an error during screen transition.
     */

    @FXML
    public void customerRecordsCancel(ActionEvent event) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to cancel without saving?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            General.transitionToScene("../View/MainScreen.fxml");
        }
        General.transitionToScene("../View/MainScreen.fxml");
    }


    /**
     * Handles the save customer button action.
     * If the input is valid, the customer data is saved to the database.
     * After saving, the user is redirected to the main screen.
     *
     * @param event The action event triggered.
     */

    @FXML
    void customerRecordsSaveCustomer(ActionEvent event) {

        if (isValidInput()) {
            try {
                Connection connection = JDBC.startConnection();

                if (allRequiredFieldsFilled()) {
                    saveCustomerData();
                    General.transitionToScene("../View/MainScreen.fxml");
                    showAlertAndReturn("Successfully saved the customer.", Alert.AlertType.CONFIRMATION);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Checks if the user input is valid based on specific criteria.
     *
     * @return true if the input is valid, false otherwise.
     */

    private boolean isValidInput() {


        // Validate Customer Name
        if (!General.validateInputField(addCustomerName, "Customer Name")) {
            return false;
        }

        // Validate Customer Address
        if (!General.validateInputField(addCustomerAddress, "Customer Address")) {
            return false;
        }

        // Validate Postal Code
        if (!General.validateInputField(addCustomerPostalCode, "Postal Code")) {
            return false;
        }

        // Validate Phone Number
        if (!General.validateInputField(addCustomerPhone, "Phone")) {
            return false;
        }
        if (!General.validateNumeric(addCustomerPhone, "Phone")) {
            return false;
        }

        // Validate Country ComboBox
        if (!General.validateComboBox(addCustomerCountry, "Country")) {
            return false;
        }

        // Validate State/Province ComboBox
        if (!General.validateComboBox(addCustomerState, "State/Province")) {
            return false;
        }

        return true; // Return true only if all validations passed
    }

    /**
     * Populates the states/provinces ComboBox based on the selected country.
     *
     * @param event The action event triggered.
     */
    public void addCustomerCountry(ActionEvent event) {
        try {
            JDBC.startConnection();
            String selectedCountry = addCustomerCountry.getValue();
            if (selectedCountry != null) {
                updateStatesBasedOnSelectedCountry(selectedCountry);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Populates the country ComboBox with available countries from the database.
     */

    private void populateCountries() {
        try {
            ObservableList<CountryAccess> allCountries = CountryAccess.getCountries();
            ObservableList<String> countryNames = allCountries.stream()
                    .map(CountryAccess::getCountryName)   // Assuming a getCountryName exists in CountryAccess or its parent class
                    .collect(Collectors.toCollection(FXCollections::observableArrayList));
            addCustomerCountry.setItems(countryNames);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if all the required fields are filled by the user.
     *
     * @return true if all required fields are filled, false otherwise.
     */

    private boolean allRequiredFieldsFilled() {
        return !addCustomerName.getText().isEmpty() &&
                !addCustomerAddress.getText().isEmpty() &&
                !addCustomerPostalCode.getText().isEmpty() &&
                !addCustomerPhone.getText().isEmpty() &&
                addCustomerCountry.getValue() != null &&
                addCustomerState.getValue() != null;
    }

    /**
     * Saves the customer data to the database.
     *
     * @throws SQLException if there's a database-related error.
     */

    private void saveCustomerData() throws SQLException {
        int firstLevelDivisionId = getFirstLevelDivisionId();

            try {
                Connection connection = JDBC.startConnection();


                if (!addCustomerName.getText().isEmpty() || !addCustomerName.getText().isEmpty() || !addCustomerAddress.getText().isEmpty() || !addCustomerPostalCode.getText().isEmpty() || !addCustomerPhone.getText().isEmpty() || !addCustomerCountry.getValue().isEmpty() || !addCustomerState.getValue().isEmpty()) {



                    int firstLevelDivisionName = 0;
                    for (FirstLevelDivisionAccess firstLevelDivision : FirstLevelDivisionAccess.getAllFirstLevelDivisions()) {
                        if (addCustomerState.getSelectionModel().getSelectedItem().equals(firstLevelDivision.getDivisionName())) {
                            firstLevelDivisionName = firstLevelDivision.getDivisionID();
                        }
                    }

                    String insertStatement = "INSERT INTO customers (Customer_ID, Customer_Name, Address, Postal_Code, Phone, Create_Date, Created_By, Last_Update, Last_Updated_By, Division_ID) VALUES (?,?,?,?,?,?,?,?,?,?)";
                    JDBC.setPreparedStatement(JDBC.getConnection(), insertStatement);
                    PreparedStatement ps = JDBC.getPreparedStatement();
                    int newCustomerID = Integer.parseInt(String.valueOf((int) (Math.random() * 100)));
                    ps.setInt(1, newCustomerID);
                    ps.setString(2, addCustomerName.getText());
                    ps.setString(3, addCustomerAddress.getText());
                    ps.setString(4, addCustomerPostalCode.getText());
                    ps.setString(5, addCustomerPhone.getText());
                    ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
                    ps.setString(7, "admin");
                    ps.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
                    ps.setString(9, "admin");
                    ps.setInt(10, firstLevelDivisionName);
                    ps.execute();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }


    /**
     * Fetches the ID of the selected state/province.
     *
     * @return ID of the selected state/province.
     * @throws SQLException if there's a database-related error.
     */

    private int getFirstLevelDivisionId() throws SQLException {
        return FirstLevelDivisionAccess.getAllFirstLevelDivisions().stream()
                .filter(div -> div.getDivisionName().equals(addCustomerState.getSelectionModel().getSelectedItem()))
                .findFirst()
                .map(FirstLevelDivisionAccess::getDivisionID)
                .orElse(0);
    }


    /**
     * Fetches the ID of the country based on its name.
     *
     * @param countryName Name of the country.
     * @return ID of the country. Returns -1 if not found.
     */
    private int getCountryIDByName(String countryName) {
        try {
            for (CountryAccess country : CountryAccess.getCountries()) {
                if (country.getCountryName().equals(countryName)) {
                    return country.getCountryID();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // return an invalid ID if not found
    }


    /**
     * Updates the state/province ComboBox based on the selected country.
     *
     * @param selectedCountry Name of the selected country.
     * @throws SQLException if there's a database-related error.
     */

    private void updateStatesBasedOnSelectedCountry(String selectedCountry) throws SQLException {
        ObservableList<String> states = FXCollections.observableArrayList();



        int countryID = getCountryIDByName(selectedCountry);
        ObservableList<FirstLevelDivisionAccess> divisionsByCountry = FirstLevelDivisionAccess.getDivisionsByCountryID(countryID);
        for (FirstLevelDivisionAccess division : divisionsByCountry) {
            states.add(division.getDivisionName());
        }

        addCustomerState.setItems(states);
    }


    /**
     * Initializes the Add Customer screen by populating the country ComboBox.
     * Also sets up a listener to update the state/province ComboBox based on the country selection.
     *
     * @param url  The location used to resolve relative paths for the root object.
     * @param rb   The resources used to localize the root object.
     */

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        populateCountries();

        addCustomerCountry.valueProperty().addListener((obs, oldVal, newVal) -> {
            try {
                updateStatesBasedOnSelectedCountry(newVal);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}

