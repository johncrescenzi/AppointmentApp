package Model;

/**
 * Represents a country with a unique identifier and its name.
 */
public class Country {
    private int countryID; // The unique identifier for the country.
    private String countryName; // The name of the country.

    /**
     * Constructs a Country object with provided details.
     *
     * @param countryID   The unique identifier for the country.
     * @param countryName The name of the country.
     */
    public Country(int countryID, String countryName) {
        this.countryID = countryID;
        this.countryName = countryName;
    }

    /**
     * Retrieves the unique identifier of the country.
     *
     * @return The country's unique identifier.
     */
    public int getCountryID() {
        return countryID;
    }

    /**
     * Retrieves the name of the country.
     *
     * @return The country's name.
     */
    public String getCountryName() {
        return countryName;
    }
}
