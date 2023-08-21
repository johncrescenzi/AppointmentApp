package Model;

/**
 * The Customers class represents customer information including their ID, name, address, postal code,
 * phone number, division ID, and division name.
 */
public class Customers {
    private String divisionName; // The name of the division associated with the customer.
    private int customerID; // The unique identifier of the customer.
    private String customerName; // The name of the customer.
    private String customerAddress; // The address of the customer.
    private String customerPostalCode; // The postal code of the customer.
    private String customerPhoneNumber; // The phone number of the customer.
    private int divisionID; // The ID of the division associated with the customer.

    /**
     * Constructs a Customers object with provided details.
     *
     * @param customerID         The unique identifier of the customer.
     * @param customerName       The name of the customer.
     * @param customerAddress    The address of the customer.
     * @param customerPostalCode The postal code of the customer.
     * @param customerPhoneNumber The phone number of the customer.
     * @param divisionID         The ID of the division associated with the customer.
     * @param divisionName       The name of the division associated with the customer.
     */
    public Customers(int customerID, String customerName, String customerAddress, String customerPostalCode,
                     String customerPhoneNumber, int divisionID, String divisionName) {

        this.customerID = customerID;
        this.customerName = customerName;
        this.customerAddress = customerAddress;
        this.customerPostalCode = customerPostalCode;
        this.customerPhoneNumber = customerPhoneNumber;
        this.divisionID = divisionID;
        this.divisionName = divisionName;
    }

    /**
     * Retrieves the unique identifier of the customer.
     *
     * @return The customer's unique identifier.
     */
    public Integer getCustomerID() {
        return customerID;
    }

    /**
     * Sets the unique identifier of the customer.
     *
     * @param customerID The new customer ID to be set.
     */
    public void setCustomerID(Integer customerID) {
        this.customerID = customerID;
    }

    /**
     * Retrieves the name of the customer.
     *
     * @return The customer's name.
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * Sets the name of the customer.
     *
     * @param customerName The new customer name to be set.
     */
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    /**
     * Retrieves the address of the customer.
     *
     * @return The customer's address.
     */
    public String getCustomerAddress() {
        return customerAddress;
    }

    /**
     * Sets the address of the customer.
     *
     * @param customerAddress The new customer address to be set.
     */
    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    /**
     * Retrieves the postal code of the customer.
     *
     * @return The customer's postal code.
     */
    public String getCustomerPostalCode() {
        return customerPostalCode;
    }

    /**
     * Sets the postal code of the customer.
     *
     * @param customerPostalCode The new customer postal code to be set.
     */
    public void setCustomerPostalCode(String customerPostalCode) {
        this.customerPostalCode = customerPostalCode;
    }

    /**
     * Retrieves the phone number of the customer.
     *
     * @return The customer's phone number.
     */
    public String getCustomerPhone() {
        return customerPhoneNumber;
    }

    /**
     * Sets the phone number of the customer.
     *
     * @param customerPhoneNumber The new customer phone number to be set.
     */
    public void setCustomerPhone(String customerPhoneNumber) {
        this.customerPhoneNumber = customerPhoneNumber;
    }

    /**
     * Retrieves the division ID associated with the customer.
     *
     * @return The division ID.
     */
    public Integer getCustomerDivisionID() {
        return divisionID;
    }

    /**
     * Retrieves the name of the division associated with the customer.
     *
     * @return The name of the division.
     */
    public String getDivisionName() {
        return divisionName;
    }

    /**
     * Sets the division ID associated with the customer.
     *
     * @param divisionID The new division ID to be set.
     */
    public void setCustomerDivisionID(Integer divisionID) {
        this.divisionID = divisionID;
    }
}
