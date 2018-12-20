package nl.bos.controllers;

import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfAttr;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
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
import java.util.logging.Logger;

public class BodyPane implements Initializable, ChangeListener {
    private static final Logger log = Logger.getLogger(BodyPane.class.getName());

    public TextArea getTaStatement() {
        return taStatement;
    }

    public ChoiceBox<String> getCmbHistory() {
        return cmbHistory;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public FXMLLoader getFxmlLoader() {
        return fxmlLoader;
    }

    @FXML
    private VBox vboxBody;
    @FXML
    private TextArea taStatement;
    @FXML
    private ChoiceBox<String> cmbHistory;
    @FXML
    private TableView tbResult;
    private JSONObject jsonObject;
    private FXMLLoader fxmlLoader;

    public void initialize(URL location, ResourceBundle resources) {
        try {
            fxmlLoader = new FXMLLoader(getClass().getResource("/nl/bos/views/InputPane.fxml"));
            BorderPane inputPane = fxmlLoader.load();
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
        ObservableList<String> value = FXCollections.observableList(statements);
        cmbHistory.setItems(value);
    }

    public void changed(ObservableValue observable, Object oldValue, Object newValue) {
        String selectedHistoryItem = String.valueOf(cmbHistory.getItems().get((Integer) newValue));
        log.info(selectedHistoryItem);
        taStatement.setText(selectedHistoryItem);
    }

    protected void updateResultTable(IDfCollection collection) throws DfException {
        tbResult.getItems().clear();
        tbResult.getColumns().clear();

        List<TableColumn> columns = new ArrayList<>();
        ObservableList<ObservableList> rows = FXCollections.observableArrayList();

        int rowCount = 0;
        while (collection.next()) {
            rowCount++;
            ObservableList<String> row = FXCollections.observableArrayList();
            for (int i = 0; i < collection.getAttrCount(); i++) {
                IDfAttr attr = collection.getAttr(i);

                if (rowCount == 1) {
                    final int j = i;
                    TableColumn column = new TableColumn(attr.getName());
                    column.setCellValueFactory(new PropertyValueFactory<>(attr.getName()));
                    column.setCellValueFactory((Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>) param -> new SimpleStringProperty(param.getValue().get(j).toString()));
                    columns.add(column);
                }

                switch (attr.getDataType()) {
                    case IDfAttr.DM_BOOLEAN:
                        row.add(String.valueOf(collection.getBoolean(attr.getName())));
                        break;
                    case IDfAttr.DM_DOUBLE:
                        row.add(String.valueOf(collection.getDouble(attr.getName())));
                        break;
                    case IDfAttr.DM_ID:
                        row.add(String.valueOf(collection.getId(attr.getName())));
                        break;
                    case IDfAttr.DM_INTEGER:
                        row.add(String.valueOf(collection.getInt(attr.getName())));
                        break;
                    case IDfAttr.DM_STRING:
                        row.add(String.valueOf(collection.getString(attr.getName())));
                        break;
                    case IDfAttr.DM_TIME:
                        row.add(String.valueOf(collection.getTime(attr.getName())));
                        break;
                    default:
                        log.finest("Error occurred while displaying the results.");
                        row.add("N/A");
                        break;
                }
            }
            rows.add(row);
        }
        tbResult.getColumns().addAll(columns);
        tbResult.setItems(rows);
    }
}
