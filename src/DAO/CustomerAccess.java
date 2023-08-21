package DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import Helper.JDBC;
import Model.Customers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The CustomerAccess class provides methods to access and manage customer information in the database.
 */
public class CustomerAccess {

    /**
     * Retrieves a list of all customers along with their information from the database.
     *
     * @param connection The database connection to use for the query.
     * @return An ObservableList containing all customers' information.
     * @throws SQLException If there's an error accessing the database.
     */
    public static ObservableList<Customers> getAllCustomers(Connection connection) throws SQLException {

        // SQL query to retrieve customer information along with corresponding division information
        String query = "SELECT customers.Customer_ID, customers.Customer_Name, customers.Address, customers.Postal_Code, customers.Phone, customers.Division_ID, first_level_divisions.Division from customers INNER JOIN  first_level_divisions ON customers.Division_ID = first_level_divisions.Division_ID";

        PreparedStatement ps = JDBC.getConnection().prepareStatement(query);
        ResultSet rs = ps.executeQuery();

        ObservableList<Customers> customersObservableList = FXCollections.observableArrayList();

        // Retrieve and process customer data from the result set
        while (rs.next()) {
            int customerID = rs.getInt("Customer_ID");
            String customerName = rs.getString("Customer_Name");
            String customerAddress = rs.getString("Address");
            String customerPostalCode = rs.getString("Postal_Code");
            String customerPhone = rs.getString("Phone");
            int divisionID = rs.getInt("Division_ID");
            String divisionName = rs.getString("Division");

            // Create a Customers object with retrieved data and add it to the list
            Customers customer = new Customers(customerID, customerName, customerAddress, customerPostalCode, customerPhone, divisionID, divisionName);
            customersObservableList.add(customer);
        }

        return customersObservableList;
    }
}
