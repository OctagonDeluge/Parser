import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class MainController {

    private HTMLParser HTMLParser;
    private LinkedHashMap<String, Integer> wordStatistics;
    private final boolean ASCENDING = true;
    private final boolean DESCENDING = false;
    private static Logger logger = Logger.getLogger("logger");
    private DataAccess dataAccess;

    @FXML
    private TextField setUrl;

    @FXML
    private TextArea result;

    public MainController() {
        HTMLParser = new HTMLParser();
        wordStatistics = new LinkedHashMap<>();
        dataAccess = new DataAccess();
    }

    @FXML
    private void getStatistics(ActionEvent event) {
        logger.info("Started work");
        if (Pattern.matches("\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]", setUrl.getText())) {
            try {
                wordStatistics.clear();
                wordStatistics.putAll(HTMLParser.parse(setUrl.getText()));
                sorting(wordStatistics, ASCENDING);
                result.clear();
                for (String key :
                        wordStatistics.keySet()) {
                    result.appendText(key + " : " + wordStatistics.get(key) + "\n");
                }
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Got an exception: ", e);
                result.setText("Unknown host");
            }
        } else {
            logger.info("Got invalid input");
            result.clear();
            Alert alert = new Alert(Alert.AlertType.WARNING, "Введена невалиданя ссылка");
            alert.showAndWait();
        }
    }

    @FXML
    void setDatabase(ActionEvent event) {
        try {
            Stage childWindow = new Stage();
            FXMLLoader childLoader = new FXMLLoader();
            childLoader.setLocation(getClass().getResource("/DatabaseSetupWindow.fxml"));
            Parent child = childLoader.load();
            ChildController childController = childLoader.getController();
            childController.setStage(childWindow);
            childController.setDataAccess(dataAccess);
            childWindow.setTitle("Подключение базы данных");
            childWindow.setScene(new Scene(child, 403, 417));
            childWindow.show();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Got an exception: ", e);
        }
    }

    @FXML
    void pushToDB(ActionEvent event) {
        if (setUrl.getText().equals("") || wordStatistics.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Ссылка на источник отсутствует");
            alert.showAndWait();
        } else if (dataAccess.getUsername() == null || dataAccess.getPassword() == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "База данных не подключена");
            alert.showAndWait();
        } else {
            try {
                dataAccess.addStatistics(setUrl.getText(), wordStatistics);
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Got an exception: ", e);
                Alert alert = new Alert(Alert.AlertType.WARNING, "Авторизация не пройдена");
                alert.showAndWait();
            }
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Данные внесены в базу данных");
            alert.showAndWait();
        }
    }

    @FXML
    private void setAscSort(ActionEvent event) {
        logger.info("Started work (ASCENDING SORT)");
        sorting(wordStatistics, ASCENDING);
        result.clear();
        for (String key :
                wordStatistics.keySet()) {
            result.appendText(key + " : " + wordStatistics.get(key) + "\n");
        }
    }

    @FXML
    private void setDescSort(ActionEvent event) {
        logger.info("Started work (DESCENDING SORT)");
        sorting(wordStatistics, DESCENDING);
        result.clear();
        for (String key :
                wordStatistics.keySet()) {
            result.appendText(key + " : " + wordStatistics.get(key) + "\n");
        }
    }

    private void sorting(Map<String, Integer> map, boolean sortType) {
        logger.info("Started work");
        Map<String, Integer> temp = new LinkedHashMap<>();
        if (sortType) {
            map.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByValue())
                    .forEach(element -> temp.put(element.getKey(), element.getValue()));
        } else {
            map.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .forEach(element -> temp.put(element.getKey(), element.getValue()));
        }
        map.clear();
        map.putAll(temp);
    }
}

