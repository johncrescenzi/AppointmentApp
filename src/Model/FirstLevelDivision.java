package Model;

/**
 * The FirstLevelDivision class represents a geographical or administrative division within a country.
 * This model class provides attributes to hold and manipulate data associated with a first-level division,
 * such as its unique ID, name, and the associated country's ID.
 */
public class FirstLevelDivision {

    /** The unique identifier for the division. */
    private int divisionID;

    /** The name of the division. */
    private String divisionName;

    /** The ID of the country to which this division belongs. */
    public int country_ID;

    /**
     * Constructs a new FirstLevelDivision object with the specified details.
     *
     * @param divisionID   The unique identifier of the division.
     * @param divisionName The name of the division.
     * @param country_ID   The ID of the associated country.
     */
    public FirstLevelDivision(int divisionID, String divisionName, int country_ID) {
        this.divisionID = divisionID;
        this.divisionName = divisionName;
        this.country_ID = country_ID;
    }

    /**
     * Retrieves the division's unique identifier.
     *
     * @return The division's ID.
     */
    public int getDivisionID() {
        return divisionID;
    }

    /**
     * Retrieves the name of the division.
     *
     * @return The division's name.
     */
    public String getDivisionName() {
        return divisionName;
    }

    /**
     * Retrieves the ID of the country to which this division belongs.
     *
     * @return The ID of the associated country.
     */
    public int getCountry_ID() {
        return country_ID;
    }
}
