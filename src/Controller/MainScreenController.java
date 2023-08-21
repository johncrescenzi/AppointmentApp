package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

import static Helper.General.transitionToScene;

/**
 * This class provides the controller for the main screen of the application.
 * It defines methods for user interactions like exiting the application and navigating to the Reports screen.
 */
public class MainScreenController {

    /**
     * This method handles the action to close and exit the application.
     * When invoked, it prompts the user with a confirmation dialog. If the user confirms,
     * the application window is closed.
     *
     * @param ExitButton The action event that triggered this method, typically a button click.
     */
    public void handleExitButton(ActionEvent ExitButton) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to exit?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Stage stage = (Stage) ((Node) ExitButton.getSource()).getScene().getWindow();
            stage.close();
        }
    }

    /**
     * This method handles the navigation to the Reports screen.
     * When invoked, it transitions the user interface to the "ReportScreen.fxml" view.
     *
     * @param event The action event that triggered this method.
     * @throws IOException If there's an issue with loading the ReportScreen FXML file.
     */
    @FXML
    public void handleReportsButton(ActionEvent event) throws IOException {
        transitionToScene("../View/ReportScreen.fxml");
    }
}
