package Controller;

import DAO.AppointmentAccess;
import DAO.UserAccess;
import Helper.General;
import Helper.JDBC;
import Model.Appointments;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Locale;
import java.util.ResourceBundle;

import static Helper.General.showAlertAndReturn;

/**
 * The LoginController class provides methods to handle user login actions,
 * log login attempts, and adjust the locale based on the system's settings.
 * <p>
 * When a user attempts to login, this controller verifies the user credentials
 * and transitions the user to the main application screen upon successful login.
 * It also notifies users of any upcoming appointments.
 * </p>
 * <p>
 * The controller records all login attempts, both successful and failed,
 * into a "login_activity.txt" file.
 * </p>
 */
public class LoginController implements Initializable {

    @FXML
    private Button loginButton;
    @FXML
    private TextField loginScreenLocationField, loginScreenPassword, loginScreenUsername;
    @FXML
    private Label passwordField, usernameField, loginField, locationText;

    private ResourceBundle rb;
    private Locale defaultLocale;

    /**
     * Initializes the login screen elements and sets the appropriate locale.
     * This method also initiates a connection to the database.
     *
     * @param url  The location used to resolve relative paths for the root object.
     * @param rb   The resources used to localize the root object.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Ensure database connection is initiated at the start
        JDBC.startConnection();

        // Determine the user's location
        ZoneId zone = ZoneId.systemDefault();
        loginScreenLocationField.setText(String.valueOf(zone));

        initLocaleAndText();
    }

    /**
     * Handles the action event when the login button is pressed.
     * This method attempts to validate the user credentials, logs the attempt,
     * and either grants access to the main application or displays an error.
     *
     * @param event The action event associated with pressing the login button.
     * @throws SQLException if there's a database access error.
     * @throws IOException if there's an error with file operations.
     */

    @FXML
    private void loginButton(ActionEvent event) throws SQLException, IOException {
        ObservableList<Appointments> allAppointments = AppointmentAccess.getAllAppointments();
        LocalDateTime currentTimeMinus15Min = LocalDateTime.now().minusMinutes(15);
        LocalDateTime currentTimePlus15Min = LocalDateTime.now().plusMinutes(15);
        boolean appointmentWithin15Min = false;

        String usernameInput = loginScreenUsername.getText();
        String passwordInput = loginScreenPassword.getText();
        int userId = UserAccess.validateUser(usernameInput, passwordInput);

        if (userId > 0) {
            logActivity("user: " + usernameInput + " successfully logged in at: " + Timestamp.valueOf(LocalDateTime.now()));
            General.transitionToScene("../View/MainScreen.fxml");

            for (Appointments appointment : allAppointments) {
                LocalDateTime startTime = appointment.getStart();
                if (startTime.isAfter(currentTimeMinus15Min) && startTime.isBefore(currentTimePlus15Min)) {
                    appointmentWithin15Min = true;

                    String appointmentDetails = String.format("Appointment ID: %d, Date: %s, Time: %s",
                            appointment.getAppointmentID(),
                            startTime.toLocalDate(),
                            startTime.toLocalTime());

                    showAlertAndReturn(rb.getString("AppointmentSoon") + ": " + appointmentDetails, Alert.AlertType.INFORMATION);
                    break;
                }
            }

            if (!appointmentWithin15Min) {
                showAlertAndReturn(rb.getString("NoUpcomingAppointments"), Alert.AlertType.INFORMATION);
            }
        } else {
            logActivity("user: " + usernameInput + " failed login attempt at: " + Timestamp.valueOf(LocalDateTime.now()));
            showAlertAndReturn(rb.getString("Incorrect"), Alert.AlertType.ERROR);
        }
    }

    /**
     * Exits the application when the cancel button is pressed.
     *
     * @param event The action event associated with pressing the cancel button.
     */

    public void cancelButton(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    /**
     * Initializes the locale for the user interface and sets the text fields
     * according to the resolved locale.
     */
    private void initLocaleAndText() {
        defaultLocale = Locale.getDefault();

        if (defaultLocale.getLanguage().equals("fr")) {
            rb = ResourceBundle.getBundle("Lang/login", Locale.FRENCH);
        } else {
            rb = ResourceBundle.getBundle("Lang/login", Locale.ENGLISH);
        }

        loginField.setText(rb.getString("Login"));
        usernameField.setText(rb.getString("Username"));
        passwordField.setText(rb.getString("Password"));
        loginButton.setText(rb.getString("Login"));
        locationText.setText(rb.getString("Location"));
    }

    /**
     * Logs messages into the "login_activity.txt" file.
     * This method is used to record login attempts.
     *
     * @param logMessage The message to be logged into the file.
     */
    private void logActivity(String logMessage) {
        try (FileWriter fileWriter = new FileWriter("login_activity.txt", true);
             PrintWriter outputFile = new PrintWriter(fileWriter)) {
            outputFile.println(logMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

