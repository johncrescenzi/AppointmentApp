package Helper;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * Utility class containing methods and nested classes for general purposes
 * such as scene transitions, alerts, validations, and more.
 */
public class General {

    /**
     * Singleton class responsible for managing the primary stage.
     */
    public static class StageManager {
        private static StageManager instance;   // Singleton instance
        private Stage primaryStage;             // Primary stage reference

        // Private constructor to prevent external instantiation.
        private StageManager() {
        }

        /**
         * Provides access to the singleton instance of StageManager.
         *
         * @return the singleton instance of StageManager.
         */
        public static StageManager getInstance() {
            if (instance == null) {
                instance = new StageManager();
            }
            return instance;
        }

        /**
         * Set the primary stage.
         *
         * @param stage the primary stage to set.
         */
        public void setPrimaryStage(Stage stage) {
            this.primaryStage = stage;
        }

        /**
         * Get the primary stage.
         *
         * @return the primary stage.
         */
        public Stage getPrimaryStage() {
            return primaryStage;
        }

        /**
         * Switch to a new scene.
         *
         * @param root the root node of the new scene.
         */
        public void switchScene(Parent root) {
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();
            primaryStage.show();
        }
    }

    /**
     * Transitions the primary stage to a new scene.
     *
     * @param fxmlPath  Path to the FXML file defining the new scene.
     * @throws IOException if the FXML file cannot be loaded.
     */
    public static void transitionToScene(String fxmlPath) throws IOException {
        Parent parent = FXMLLoader.load(General.class.getResource(fxmlPath));
        StageManager.getInstance().switchScene(parent);
    }

    /**
     * Displays an alert dialog.
     *
     * @param message The content message of the alert.
     * @param type    The type of the alert.
     * @return        Always false, facilitating usage in conditional checks.
     */
    public static boolean showAlertAndReturn(String message, Alert.AlertType type) {
        Alert alert = new Alert(type, message);
        alert.showAndWait();
        return false;
    }

    /**
     * Determines if a given string is numeric.
     *
     * @param str The string to check.
     * @return    true if the string is numeric, false otherwise.
     */
    public static boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Exception thrown when a validation process fails.
     */
    public class ValidationException extends Exception {
        /**
         * Constructor specifying the error message.
         *
         * @param message The error message.
         */
        public ValidationException(String message) {
            super(message);
        }
    }

    /**
     * Validates that a TextField is not empty.
     *
     * @param textField  TextField to validate.
     * @param fieldName  Label used in potential error message.
     * @return           true if valid, false (with alert) if invalid.
     */
    public static boolean validateInputField(TextField textField, String fieldName) {
        if (textField.getText().isEmpty()) {
            return showAlertAndReturn(fieldName + " cannot be empty", Alert.AlertType.ERROR);
        }
        return true;
    }

    /**
     * Validates that a ComboBox has a selection.
     *
     * @param comboBox  ComboBox to validate.
     * @param fieldName Label used in potential error message.
     * @return          true if valid, false (with alert) if invalid.
     */
    public static boolean validateComboBox(ComboBox comboBox, String fieldName) {
        if (comboBox.getValue() == null) {
            return showAlertAndReturn(fieldName + " cannot be empty", Alert.AlertType.ERROR);
        }
        return true;
    }

    /**
     * Validates that a DatePicker has a selected date.
     *
     * @param datePicker DatePicker to validate.
     * @param fieldName  Label used in potential error message.
     * @return           true if valid, false (with alert) if invalid.
     */
    public static boolean validateDatePicker(DatePicker datePicker, String fieldName) {
        if (datePicker.getValue() == null) {
            return showAlertAndReturn(fieldName + " cannot be empty", Alert.AlertType.ERROR);
        }
        return true;
    }

    /**
     * Validates that the content of a TextField is numeric.
     * Allows for dashes if the field is for a phone number.
     *
     * @param textField  TextField to validate.
     * @param fieldName  Label used in potential error message.
     * @return           true if valid, false (with alert) if invalid.
     */
    public static boolean validateNumeric(TextField textField, String fieldName) {
        if ("Phone".equals(fieldName)) {
            if (!textField.getText().matches(".*[0-9].*") || !textField.getText().matches("[0-9-]+")) {
                return showAlertAndReturn(fieldName + " must contain at least one number and can contain dashes.", Alert.AlertType.ERROR);
            }
        } else if (!"Phone".equals(fieldName) && !textField.getText().matches("[0-9]+")) {
            return showAlertAndReturn(fieldName + " must be numeric.", Alert.AlertType.ERROR);
        }
        return true;
    }
}
