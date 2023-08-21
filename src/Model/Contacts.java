package Model;

/**
 * The Contacts class represents a contact entity with attributes such as ID, name, and email.
 * This model class is typically used to hold and manipulate data associated with a contact.
 */
public class Contacts {

    /** The unique identifier for the contact. */
    public int contactID;

    /** The name of the contact. */
    public String contactName;

    /** The email address associated with the contact. */
    public String contactEmail;

    /**
     * Constructs a new Contacts object with the provided ID, name, and email.
     *
     * @param contactID The unique identifier for the contact.
     * @param contactName The name of the contact.
     * @param contactEmail The email address associated with the contact.
     */
    public Contacts(int contactID, String contactName, String contactEmail) {
        this.contactID = contactID;
        this.contactName = contactName;
        this.contactEmail = contactEmail;
    }

    /**
     * Retrieves the contact's ID.
     *
     * @return The unique identifier for the contact.
     */
    public int getId() {
        return contactID;
    }

    /**
     * Retrieves the contact's name.
     *
     * @return The name of the contact.
     */
    public String getContactName() {
        return contactName;
    }

    /**
     * Retrieves the contact's email.
     *
     * @return The email address associated with the contact.
     */
    public String getContactEmail() {
        return contactEmail;
    }
}
