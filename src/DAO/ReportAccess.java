package DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import Helper.JDBC;
import Model.Appointments;
import Model.Reports;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * The ReportAccess class provides methods to access and generate reports related to appointments and countries.
 */
public class ReportAccess extends Appointments {

    /**
     * Constructs a ReportAccess object with the provided appointment information.
     *
     * @param appointmentID The ID of the appointment.
     * @param appointmentTitle The title of the appointment.
     * @param appointmentDescription The description of the appointment.
     * @param appointmentLocation The location of the appointment.
     * @param appointmentType The type of the appointment.
     * @param start The start time of the appointment.
     * @param end The end time of the appointment.
     * @param customerID The ID of the associated customer.
     * @param userID The ID of the associated user.
     * @param contactID The ID of the associated contact.
     */
    public ReportAccess(int appointmentID, String appointmentTitle, String appointmentDescription, String appointmentLocation, String appointmentType, LocalDateTime start, LocalDateTime end, int customerID, int userID, int contactID) {
        super(appointmentID, appointmentTitle, appointmentDescription, appointmentLocation, appointmentType, start, end, customerID, userID, contactID);
    }

    /**
     * Retrieves a list of countries and the total number of appointments per country.
     *
     * @return An ObservableList containing country-wise appointment counts.
     * @throws SQLException If there's an error accessing the database.
     */
    public static ObservableList<Reports> getCountries() throws SQLException {
        ObservableList<Reports> countriesObservableList = FXCollections.observableArrayList();

        // SQL query to retrieve countries and their appointment counts
        String sql = "select countries.Country, count(*) as countryCount from customers inner join first_level_divisions on customers.Division_ID = first_level_divisions.Division_ID inner join countries on countries.Country_ID = first_level_divisions.Country_ID where  customers.Division_ID = first_level_divisions.Division_ID group by first_level_divisions.Country_ID order by count(*) desc";

        PreparedStatement ps = JDBC.getConnection().prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        // Retrieve and process country-wise appointment count data from the result set
        while (rs.next()) {
            String countryName = rs.getString("Country");
            int countryCount = rs.getInt("countryCount");

            // Create a Reports object with retrieved data and add it to the list
            Reports report = new Reports(countryName, countryCount);
            countriesObservableList.add(report);
        }

        return countriesObservableList;
    }
}
