import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChildController {

    @FXML
    private TextField setDBUser;

    @FXML
    private TextField setDBPassword;

    @FXML
    private TextField setDBName;

    @FXML
    private CheckBox checkBoxTables;

    private DataAccess dataAccess;
    private static Logger logger = Logger.getLogger("logger");

    @FXML
    public void connectToDB(ActionEvent event) {
        if (!setDBUser.getText().equals("") && !setDBPassword.getText().equals("") && !setDBName.getText().equals("")) {
            dataAccess.setUsername(setDBUser.getText());
            dataAccess.setPassword(setDBPassword.getText());
            dataAccess.setDatabaseName(setDBName.getText());
            logger.info("User confirmed credentials");
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Данные установлены");
            alert.showAndWait();
        } else {
            logger.info("User didn't fill all the fields");
            Alert alert = new Alert(Alert.AlertType.WARNING, "Заполните поля для авторизации");
            alert.show();
        }
    }

    private void checkCredentials(Stage window) {
        if (dataAccess.getUsername() == null || dataAccess.getPassword() == null) {
            logger.info("User has not confirmed credentials");
            Alert alert = new Alert(Alert.AlertType.WARNING, "Подтвердите данные");
            alert.show();
        } else {
            logger.info("All credentials confirmed");
            window.close();
            if (checkBoxTables.isSelected()) {
                logger.info("Started to setting up workspace");
                try {
                    dataAccess.setupWorkspace();
                } catch (SQLException e) {
                    logger.log(Level.SEVERE, "Got an exception: ", e);
                    Alert alert = new Alert(Alert.AlertType.WARNING, e.getMessage());
                    alert.show();
                }

            }
        }
    }

    public void setStage(Stage stage) {
        stage.setOnCloseRequest(event -> {
            event.consume();
            checkCredentials(stage);
        });
    }

    public void setDataAccess(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }
}
