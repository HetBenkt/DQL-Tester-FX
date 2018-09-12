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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

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
    private JSONObject jsonObject;

    public void initialize(URL location, ResourceBundle resources) {
        try {
            BorderPane inputPane = FXMLLoader.load(getClass().getResource("/nl/bos/views/InputPane.fxml"));
            vboxBody.getChildren().add(inputPane);
        } catch (IOException e) {
            log.info(e.getMessage());
        }
        cmbHistory.getSelectionModel().selectedIndexProperty().addListener(this);

        try {
            loadHistory();
        } catch (IOException e) {
            log.info(e.getMessage());
            createHistoryFile();
        } catch (ParseException e) {
            log.info(e.getMessage());
        }
    }

    private void createHistoryFile() {
        jsonObject = new JSONObject();
        JSONArray list = new JSONArray();
        jsonObject.put("queries", list);

        try (FileWriter file = new FileWriter("history.json")) {
            file.write(jsonObject.toJSONString());
            file.flush();
        } catch (IOException ioe) {
            log.info(ioe.getMessage());
        }
    }

    private void loadHistory() throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(new FileReader("history.json"));

        jsonObject = (JSONObject) obj;
        log.info(jsonObject.toJSONString());

        JSONArray msg = (JSONArray) jsonObject.get("queries");
        Iterator<String> iterator = msg.iterator();
        List<String> statements = new ArrayList<>();
        while (iterator.hasNext()) {
            statements.add(iterator.next());
        }
        ObservableList value = FXCollections.observableList(statements);
        cmbHistory.setItems(value);
    }

    public void changed(ObservableValue observable, Object oldValue, Object newValue) {
        String selectedHistoryItem = String.valueOf(cmbHistory.getItems().get((Integer) newValue));
        log.info(selectedHistoryItem);
        taStatement.setText(selectedHistoryItem);
    }
}
