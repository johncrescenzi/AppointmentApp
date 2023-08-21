package Helper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * JDBC Helper class provides utility methods to manage database connectivity.
 * It abstracts database initialization, connection establishment, and disconnection.
 */
public class JDBC {
    private static PreparedStatement preparedStatement;

    private static final String protocol = "jdbc";
    private static final String vendorName = ":mysql:";
    private static final String ipAddress = "//127.0.0.1:3306/client_schedule";
    private static final String jdbcURL = protocol + vendorName + ipAddress + "?connectionTimeZone=SERVER";
    private static final String MYSQLJDBCDriver = "com.mysql.cj.jdbc.Driver";
    public static Connection connection = null;
    private static final String username = "sqlUser";
    private static final String password = "Passw0rd!";

    /**
     * Initializes a database connection and returns it.
     *
     * @return Connection object to the database.
     */
    public static Connection startConnection() {
        try {
            Class.forName(MYSQLJDBCDriver);
            connection = (Connection) DriverManager.getConnection(jdbcURL, username, password);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * Retrieves the current active database connection.
     *
     * @return Current active Connection object. Returns null if not connected.
     */
    public static Connection getConnection() {
        return connection;
    }

    /**
     * Closes the current active database connection.
     */
    public static void closeConnection() {
        try {
            if(connection != null) {
                connection.close();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Sets the PreparedStatement with the provided SQL statement.
     *
     * @param conn Connection object.
     * @param sqlStatement SQL statement string.
     * @return PreparedStatement object. Always returns null in the current implementation.
     * @throws SQLException If an SQL error occurs.
     */
    public static PreparedStatement setPreparedStatement(Connection conn, String sqlStatement) throws SQLException {
        preparedStatement = conn.prepareStatement(sqlStatement);
        return null;
    }

    /**
     * Retrieves the current active PreparedStatement.
     *
     * @return Current PreparedStatement object.
     */
    public static PreparedStatement getPreparedStatement() {
        return preparedStatement;
    }
}
