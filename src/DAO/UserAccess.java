package DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import Helper.JDBC;
import Model.Users;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The UserAccess class provides methods to access and manipulate user-related data
 * from the database. This class extends the Users model and offers functionalities
 * such as user validation and fetching all users from the database.
 */

public class UserAccess extends Users {

    public UserAccess(int userID, String userName, String userPassword) {
        super();
    }

    /**
     * This method validates the user for the login form.
     * @param password
     * @param username
     */
    public static int validateUser(String username, String password)
    {
        try
        {
            String sqlQuery = "SELECT * FROM users WHERE user_name = '" + username + "' AND password = '" + password +"'";

            PreparedStatement ps = JDBC.getConnection().prepareStatement(sqlQuery);
            ResultSet rs = ps.executeQuery();
            rs.next();
            if (rs.getString("User_Name").equals(username))
            {
                if (rs.getString("Password").equals(password))
                {
                    return rs.getInt("User_ID");

                }
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Method to pull in all user data to getAllUsers observablelist.
     * @throws SQLException
     * @return usersObservableList
     */
    public static ObservableList<UserAccess> getAllUsers() throws SQLException {
        ObservableList<UserAccess> usersObservableList = FXCollections.observableArrayList();
        String sql = "SELECT * from users";
        PreparedStatement ps = JDBC.getConnection().prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            int userID = rs.getInt("User_ID");
            String userName = rs.getString("User_Name");
            String userPassword = rs.getString("Password");
            UserAccess user = new UserAccess(userID, userName, userPassword);
            usersObservableList.add(user);
        }
        return usersObservableList;
    }
}
