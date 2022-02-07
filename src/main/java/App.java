import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class App extends Application {

    private static Logger logger = Logger.getLogger("logger");

    @Override
    public void start(Stage primaryStage) {
        logger.info("App started working");
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/ParserWindow.fxml"));
            Parent owner = loader.load();
            primaryStage.setTitle("Статистика сайта");
            primaryStage.setScene(new Scene(owner, 696, 824));
            primaryStage.show();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Got an exception: ", e);
        }
    }

    public static void main(String[] args) {
        try {
            LogManager.getLogManager().readConfiguration(App.class.getResourceAsStream("logging.properties"));
        } catch (IOException e) {
            System.out.println("Couldn't set logger config: " + e.getMessage());
        }
        Application.launch(args);
    }
}
