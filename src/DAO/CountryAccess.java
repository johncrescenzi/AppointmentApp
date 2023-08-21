package DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import Helper.JDBC;
import Model.Country;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The CountryAccess class provides methods to access and manage country information in the database.
 * It extends the Country class to represent a specific country with ID and name.
 */
public class CountryAccess extends Country {

    /**
     * Constructs a CountryAccess object with the provided country ID and name.
     *
     * @param countryID   The ID of the country.
     * @param countryName The name of the country.
     *
     */
    public CountryAccess(int countryID, String countryName) {
        super(countryID, countryName);
    }

    /**
     * Retrieves a list of all countries stored in the database.
     *
     * @return An ObservableList containing all countries with their IDs and names.
     * @throws SQLException If there's an error accessing the database.
     */
    public static ObservableList<CountryAccess> getCountries() throws SQLException {
        ObservableList<CountryAccess> countriesObservableList = FXCollections.observableArrayList();
        String sql = "SELECT Country_ID, Country FROM countries";
        PreparedStatement ps = JDBC.getConnection().prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            int countryID = rs.getInt("Country_ID");
            String countryName = rs.getString("Country");
            CountryAccess country = new CountryAccess(countryID, countryName);
            countriesObservableList.add(country);
        }

        return countriesObservableList;
    }
}
