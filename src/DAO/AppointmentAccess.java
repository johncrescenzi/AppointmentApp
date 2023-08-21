package DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import Helper.JDBC;
import Model.Appointments;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * The AppointmentAccess class provides methods to access and manage appointment data in the database.
 */
public class AppointmentAccess {

    /**
     * Retrieves a list of all appointments stored in the database.
     *
     * @return An ObservableList containing all appointments.
     * @throws SQLException If there's an error accessing the database.
     */
    public static ObservableList<Appointments> getAllAppointments() throws SQLException {
        ObservableList<Appointments> appointmentsObservableList = FXCollections.observableArrayList();
        String sql = "SELECT * FROM appointments";
        PreparedStatement ps = JDBC.startConnection().prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            int appointmentID = rs.getInt("Appointment_ID");
            String appointmentTitle = rs.getString("Title");
            String appointmentDescription = rs.getString("Description");
            String appointmentLocation = rs.getString("Location");
            String appointmentType = rs.getString("Type");
            LocalDateTime start = rs.getTimestamp("Start").toLocalDateTime();
            LocalDateTime end = rs.getTimestamp("End").toLocalDateTime();
            int customerID = rs.getInt("Customer_ID");
            int userID = rs.getInt("User_ID");
            int contactID = rs.getInt("Contact_ID");

            Appointments appointment = new Appointments(appointmentID, appointmentTitle, appointmentDescription,
                    appointmentLocation, appointmentType, start, end, customerID, userID, contactID);

            appointmentsObservableList.add(appointment);
        }

        return appointmentsObservableList;
    }

    /**
     * Deletes an appointment from the database based on the provided appointment ID.
     *
     * @param appointmentID The ID of the appointment to be deleted.
     * @param connection    The database connection to use.
     * @return The number of affected rows (1 if successful, 0 if not).
     * @throws SQLException If there's an error accessing the database.
     */
    public static int deleteAppointment(int appointmentID, Connection connection) throws SQLException {
        String query = "DELETE FROM appointments WHERE Appointment_ID=?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, appointmentID);
        int result = ps.executeUpdate();
        ps.close();
        return result;
    }
}
