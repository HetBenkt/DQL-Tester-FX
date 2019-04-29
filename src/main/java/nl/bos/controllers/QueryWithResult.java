package nl.bos.controllers;

import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfAttr;
import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import nl.bos.AttributeTableColumn;
import nl.bos.Repository;
import nl.bos.contextmenu.ContextMenuOnResultTable;
import nl.bos.utils.Calculations;
import nl.bos.utils.Controllers;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static nl.bos.Constants.HISTORY_JSON;
import static nl.bos.Constants.QUERIES;

public class QueryWithResult {
    private static final Logger LOGGER = Logger.getLogger(QueryWithResult.class.getName());

    private ContextMenuOnResultTable contextMenuOnResultTable;

    private JSONObject jsonObject;

    private FXMLLoader connectionWithStatusFxmlLoader;
    private String[] parsedDescription;

    @FXML
    private ComboBox<String> historyStatements;
    @FXML
    private VBox queryWithResultBox;
    @FXML
    private TextArea statement;
    @FXML
    private TableView result;

    private Instant start;

    ComboBox<String> getHistoryStatements() {
        return historyStatements;
    }

    TextArea getStatement() {
        return statement;
    }

    JSONObject getJsonObject() {
        return jsonObject;
    }

    FXMLLoader getConnectionWithStatusFxmlLoader() {
        return connectionWithStatusFxmlLoader;
    }

    public Instant getStart() {
        return start;
    }

    public void cleanStart() {
        this.start = null;
    }

    @FXML
    private void initialize() {
        Controllers.put(this.getClass().getSimpleName(), this);

        contextMenuOnResultTable = new ContextMenuOnResultTable(result);
        result.getSelectionModel().setCellSelectionEnabled(true);
        result.addEventHandler(MouseEvent.MOUSE_CLICKED, contextMenuOnResultTable::onRightMouseClick);
        result.getSortOrder().addListener((InvalidationListener) change -> {
            start = Instant.now();
            LOGGER.info("Start..." + Instant.now());
        });
        loadConnectionWithStatusFxml();

        if (historyFileReady()) {
            loadHistory();
        }

        historyStatements.getSelectionModel().selectedIndexProperty().addListener((observableValue, oldValue, newValue) -> onHistoryStatementsSelection(newValue));
    }

    private boolean historyFileReady() {
        File historyFile = new File(HISTORY_JSON);
        if (historyFile.exists()) {
            return true;
        } else {
            return isHistoryFileCreated();
        }
    }

    private void onHistoryStatementsSelection(Number newValue) {
        if (newValue.intValue() != -1) {
            String selectedHistoryItem = String.valueOf(historyStatements.getItems().get((Integer) newValue));
            LOGGER.info(selectedHistoryItem);
            statement.setText(selectedHistoryItem);
        } else
            statement.setText("");
    }

    private void loadConnectionWithStatusFxml() {
        try {
            connectionWithStatusFxmlLoader = new FXMLLoader(getClass().getResource("/nl/bos/views/ConnectionWithStatus.fxml"));
            BorderPane connectionWithStatus = connectionWithStatusFxmlLoader.load();
            queryWithResultBox.getChildren().add(connectionWithStatus);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    private void loadHistory() {
        try {
            String history = new String(Files.readAllBytes(Paths.get(HISTORY_JSON)), StandardCharsets.UTF_8);
            jsonObject = new JSONObject(history);

            LOGGER.info(jsonObject.toString());

            JSONArray msg = (JSONArray) jsonObject.get(QUERIES);
            Iterator<Object> iterator = msg.iterator();
            List<String> statements = new ArrayList<>();
            while (iterator.hasNext()) {
                statements.add((String) iterator.next());
            }
            ObservableList<String> value = FXCollections.observableList(statements);
            historyStatements.setItems(value);
            Callback<ListView<String>, ListCell<String>> factory = lv -> new ListCell<>() {


                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null) {
                        setText(item.substring(0, Math.min(item.length(), 100)).replaceAll("\n", " "));
                        setTooltip(new Tooltip(item));
                    }
                }

            };
            historyStatements.setCellFactory(factory);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    private boolean isHistoryFileCreated() {
        JSONArray list = new JSONArray();
        jsonObject = new JSONObject();
        jsonObject.put(QUERIES, list);

        try (FileWriter file = new FileWriter(HISTORY_JSON)) {
            file.write(jsonObject.toString());
            file.flush();
            LOGGER.info(MessageFormat.format("New {0} file is created", HISTORY_JSON));
            return true;
        } catch (IOException ioe) {
            LOGGER.log(Level.SEVERE, ioe.getMessage(), ioe);
        }
        return false;
    }

    @FXML
    private void handleDeleteHistoryItem(MouseEvent mouseEvent) {
        Object selectedItem = historyStatements.getSelectionModel().getSelectedItem();
        int selectedIndex = historyStatements.getSelectionModel().getSelectedIndex();

        if (historyStatements.getItems().remove(selectedItem)) {
            ObservableList<String> items = historyStatements.getItems();
            try {
                String history = new String(Files.readAllBytes(Paths.get(HISTORY_JSON)), StandardCharsets.UTF_8);
                jsonObject = new JSONObject(history);

                JSONArray queries = (JSONArray) jsonObject.get(QUERIES);
                queries.remove(selectedIndex);
                if (queries.length() > 0)
                    historyStatements.setValue(historyStatements.getItems().get(0));
                try (FileWriter file = new FileWriter(HISTORY_JSON)) {
                    file.write(jsonObject.toString());
                    file.flush();
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, e.getMessage(), e);
                }
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
            historyStatements.setItems(items);
        }
    }

    /**
     * @noinspection unchecked
     */
    int updateResultTable(IDfCollection collection) throws DfException {
        result.getItems().clear();
        result.getColumns().clear();

        List<AttributeTableColumn> columns = new ArrayList<>();
        ObservableList<ObservableList> rows = FXCollections.observableArrayList();

        int rowCount = 0;
        while (collection.next()) {
            rowCount++;
            ObservableList<String> row = FXCollections.observableArrayList();
            for (int i = 0; i < collection.getAttrCount(); i++) {
                IDfAttr attr = collection.getAttr(i);

                if (rowCount == 1) {
                    final int j = i;
                    AttributeTableColumn column = new AttributeTableColumn(attr.getName());
                    column.setAttr(attr);
                    column.setCellValueFactory((Callback<AttributeTableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>) param -> new SimpleStringProperty(param.getValue().get(j).toString()));
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
                        LOGGER.finest("Error occurred while displaying the results.");
                        row.add("N/A");
                        break;
                }
            }
            rows.add(row);
        }
        result.getColumns().addAll(columns);
        result.setItems(rows);

        return rowCount;
    }

    /**
     * @noinspection unchecked
     */
    public void updateResultTableWithStringInput(String description, List<String> columnNames) {
        contextMenuOnResultTable.getMenuItemShowPropertiesAction().setDescription(description);

        result.getItems().clear();
        result.getColumns().clear();

        List<AttributeTableColumn> columns = new ArrayList<>();
        ObservableList<ObservableList> rows = FXCollections.observableArrayList();

        int rowCount = 0;

        while (rowCount < getRowSize(description)) {
            rowCount++;
            ObservableList<String> row = FXCollections.observableArrayList();
            for (int i = 0; i < columnNames.size(); i++) {

                if (rowCount == 1) {
                    final int j = i;
                    AttributeTableColumn column = new AttributeTableColumn(columnNames.get(j));
                    column.setCellValueFactory((Callback<AttributeTableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>) param -> new SimpleStringProperty(param.getValue().get(j).toString()));
                    columns.add(column);
                }

                row.add(getRowValue(rowCount - 1, i));
            }
            rows.add(row);
        }
        result.getColumns().addAll(columns);
        result.setItems(rows);
    }

    private int getRowSize(String description) {
        String fromColumns;
        String type = description.substring(0, description.indexOf('\t'));

        if (type.contains("Table")) {
            fromColumns = description.substring(description.indexOf("Columns:"));
        } else {
            fromColumns = description.substring(description.indexOf("Attributes:"));
        }

        String columnsInfo = fromColumns.substring(0, fromColumns.indexOf("\r\n"));
        String value = columnsInfo.substring(columnsInfo.indexOf(':') + 1).trim();

        parseDescription(description);

        return Integer.parseInt(value);
    }

    private String getRowValue(int rowIndex, int columnIndex) {
        String rowValue = "";

        try {
            rowValue = parsedDescription[(rowIndex * 3) + columnIndex];

        } catch (ArrayIndexOutOfBoundsException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        return rowValue;
    }

    private void parseDescription(String descriptionInput) {
        String[] split;
        String type = descriptionInput.substring(0, descriptionInput.indexOf('\t'));

        if (type.contains("Table")) {
            split = descriptionInput.substring(descriptionInput.indexOf("\r\n", descriptionInput.indexOf("Columns:")))
                    .replace("KEYED\r\n", " KEYED ")
                    .replace("\r\n", " NOT_KEYED ")
                    .replace(" NOT_KEYED", " FALSE")
                    .replace("KEYED", " TRUE")
                    .split(" ");
        } else {
            split = descriptionInput.substring(descriptionInput.indexOf("\r\n", descriptionInput.indexOf("Attributes:"))).replace("REPEATING\r\n", " REPEATING ").replace("\r\n", " NOT_REPEATING ").replace(" NOT_REPEATING", " FALSE").replace("REPEATING", " TRUE").split(" ");
        }

        split[0] = "";
        split[1] = "";
        split[2] = "";
        split[3] = "";
        parsedDescription = Arrays.stream(split).filter(value -> !value.equals("")).toArray(String[]::new);
    }

    public void executeQuery(String query) {
        ConnectionWithStatus connectionWithStatusController = (ConnectionWithStatus) Controllers.get(ConnectionWithStatus.class.getSimpleName());

        Instant start = Instant.now();
        IDfCollection collection = Repository.getInstance().query(query);
        Instant end = Instant.now();
        connectionWithStatusController.getTimeQuery().setText(Calculations.getDurationInSeconds(start, end));

        if (collection == null) {
            return;
        }

        try {
            Instant startList = Instant.now();
            updateResultTable(collection);
            Instant endList = Instant.now();
            connectionWithStatusController.getTimeList().setText(Calculations.getDurationInSeconds(startList, endList));

            collection.close();

        } catch (DfException e) {
            LOGGER.log(Level.SEVERE, String.format("Error running query: [%s]", query), e);
        }
    }
}
