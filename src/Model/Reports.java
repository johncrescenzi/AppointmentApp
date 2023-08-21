package Model;

/**
 * The Reports class holds information about custom reports, including country counts and names.
 */
public class Reports {
    private int countryCount; // The count of records associated with a specific country.
    private String countryName; // The name of the country for the report.

    /**
     * Constructs a Reports object with provided country details.
     *
     * @param countryName  The name of the country for the report.
     * @param countryCount The count of records associated with the specified country.
     */
    public Reports(String countryName, int countryCount) {
        this.countryCount = countryCount;
        this.countryName = countryName;
    }

    /**
     * Retrieves the name of the country associated with the report.
     *
     * @return The country name.
     */
    public String getCountryName() {
        return countryName;
    }

    /**
     * Retrieves the count of records associated with the specific country.
     *
     * @return The country count.
     */
    public int getCountryCount() {
        return countryCount;
    }
}
