package DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import Helper.JDBC;
import Model.FirstLevelDivision;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The FirstLevelDivisionAccess class provides methods to access and manage first-level division information in the database.
 */
public class FirstLevelDivisionAccess extends FirstLevelDivision {

    /**
     * Constructs a FirstLevelDivisionAccess object with the provided division ID, division name, and country ID.
     *
     * @param divisionID The ID of the first-level division.
     * @param divisionName The name of the first-level division.
     * @param country_ID The ID of the associated country.
     */
    public FirstLevelDivisionAccess(int divisionID, String divisionName, int country_ID) {
        super(divisionID, divisionName, country_ID);
    }


    /**
     * Retrieves a list of all first-level divisions along with their information from the database.
     *
     * @return An ObservableList containing all first-level divisions' information.
     * @throws SQLException If there's an error accessing the database.
     */
    public static ObservableList<FirstLevelDivisionAccess> getAllFirstLevelDivisions() throws SQLException {
        ObservableList<FirstLevelDivisionAccess> firstLevelDivisionsObservableList = FXCollections.observableArrayList();
        String sql = "SELECT * from first_level_divisions";
        PreparedStatement ps = JDBC.getConnection().prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        // Retrieve and process first-level division data from the result set
        while (rs.next()) {
            int divisionID = rs.getInt("Division_ID");
            String divisionName = rs.getString("Division");
            int country_ID = rs.getInt("COUNTRY_ID");

            // Create a FirstLevelDivisionAccess object with retrieved data and add it to the list
            FirstLevelDivisionAccess firstLevelDivision = new FirstLevelDivisionAccess(divisionID, divisionName, country_ID);
            firstLevelDivisionsObservableList.add(firstLevelDivision);
        }

        return firstLevelDivisionsObservableList;
    }

    public static ObservableList<FirstLevelDivisionAccess> getDivisionsByCountryID(int countryID) throws SQLException {
        ObservableList<FirstLevelDivisionAccess> firstLevelDivisionsObservableList = FXCollections.observableArrayList();
        String sql = "SELECT * FROM first_level_divisions WHERE COUNTRY_ID = ?";
        PreparedStatement ps = JDBC.getConnection().prepareStatement(sql);
        ps.setInt(1, countryID);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            int divisionID = rs.getInt("Division_ID");
            String divisionName = rs.getString("Division");

            FirstLevelDivisionAccess firstLevelDivision = new FirstLevelDivisionAccess(divisionID, divisionName, countryID);
            firstLevelDivisionsObservableList.add(firstLevelDivision);
        }

        return firstLevelDivisionsObservableList;
    }
}
