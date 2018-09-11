package nl.bos.controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.extern.java.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

@Log
public class BodyPane implements Initializable, ChangeListener {
    @FXML
    private VBox vboxBody;
    @FXML
    @Getter
    private TextArea taStatement;
    @FXML
    @Getter
    private ChoiceBox cmbHistory;
    @FXML
    private TableView tbResult;
    @Getter
    private Properties history = new Properties();

    public void initialize(URL location, ResourceBundle resources) {
        try {
            BorderPane inputPane = FXMLLoader.load(getClass().getResource("/nl/bos/views/InputPane.fxml"));
            vboxBody.getChildren().add(inputPane);
        } catch (IOException e) {
            log.info(e.getMessage());
        }
        cmbHistory.getSelectionModel().selectedIndexProperty().addListener(this);
        loadHistory();
    }

    private void loadHistory() {
        try {
            InputStream in = new FileInputStream("history.properties");
            if (in != null) {
                history.load(in);
                List<String> statements = getPropertyList(history, "q");
                ObservableList value = FXCollections.observableList(statements);
                cmbHistory.setItems(value);
            }
        } catch (IOException e) {
            log.info(e.getMessage());
            File file = new File("history.properties");
            try {
                if (file.createNewFile())
                    log.info("History file created.");
            } catch (IOException e1) {
                log.info(e1.getMessage());
            }

        }
    }

    private List<String> getPropertyList(Properties properties, String name) {
        List<String> result = new ArrayList<String>();
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            if (((String) entry.getKey()).startsWith(name)) {
                result.add((String) entry.getValue());
            }
        }
        return result;
    }

    public void changed(ObservableValue observable, Object oldValue, Object newValue) {
        String selectedHistoryItem = String.valueOf(cmbHistory.getItems().get((Integer) newValue));
        log.info(selectedHistoryItem);
        taStatement.setText(selectedHistoryItem);
    }
}
