package Model;

/**
 * The Users class represents user information, including their ID, name, and password.
 */
public class Users {

    public int userID; // The unique identifier of the user.
    public String userName; // The username of the user.
    public String userPassword; // The password of the user.

    /**
     * Constructs a Users object with default values.
     */
    public Users() {
        this.userID = userID;
        this.userName = userName;
        this.userPassword = userPassword;
    }

    /**
     * Retrieves the unique identifier of the user.
     *
     * @return The user's ID.
     */
    public int getUserID() {
        return userID;
    }

    /**
     * Retrieves the username of the user.
     *
     * @return The user's username.
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Retrieves the password of the user.
     *
     * @return The user's password.
     */
    public String getUserPassword() {
        return userPassword;
    }
}
