package Main;

import Helper.JDBC;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import Helper.General.*;

/**
*
Javadocs are here: src/javadocs/javadocs
HTML file is here: src/javadocs/javadocs/index.html
 *
 **/

/** The Main class creates an application for inventory management and adds sample data. */
public class Main extends Application {

    /**
     * Initializes the LoginScreen.fxml
     *
     * @param stage
     * @throws IOException
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("../View/LoginScreen.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 350, 300);
        stage.setTitle("Scheduling Application");
        stage.setScene(scene);

        // Set the primary stage using the StageManager
        StageManager.getInstance().setPrimaryStage(stage);

        stage.show();
    }

    /**
     * The Main method loads all database connection and launches the fmxl.
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        /*Locale.setDefault(new Locale("fr"));*/
        JDBC.getConnection();
        launch(args);
        JDBC.closeConnection();
    }

}