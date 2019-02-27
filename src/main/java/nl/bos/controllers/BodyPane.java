package nl.bos.controllers;

import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.IDfAttr;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import nl.bos.MyTableColumn;
import nl.bos.Repository;
import nl.bos.utils.TableResultUtils;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.awt.*;
import java.io.*;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static nl.bos.Constants.TABLE;
import static nl.bos.Constants.TYPE;

public class BodyPane {
    private static final Logger LOGGER = Logger.getLogger(BodyPane.class.getName());

    private final Repository repositoryCon = Repository.getInstance();

    private final MenuItem miExportToCsv;
    private final MenuItem miProperties;
    private final MenuItem miGetAttributes;
    private final MenuItem miCopyCellToClipBoard;
    private final MenuItem miCopyRowToClipBoard;
    private final MenuItem miDescribeObject;
    private final MenuItem miDestroyObject;

    private JSONObject jsonObject = new JSONObject();
    private ContextMenu contextMenu = new ContextMenu();

    private FXMLLoader fxmlLoader;
    private String description;
    private String[] parsedDescription;
    private String describeObjectType;

    @FXML
    private ChoiceBox<Object> cmbHistory;
    @FXML
    private VBox vboxBody;
    @FXML
    private TextArea taStatement;
    @FXML
    private TableView tvResult;

    ChoiceBox<Object> getCmbHistory() {
        return cmbHistory;
    }

    TextArea getTaStatement() {
        return taStatement;
    }

    JSONObject getJsonObject() {
        return jsonObject;
    }

    public FXMLLoader getFxmlLoader() {
        return fxmlLoader;
    }

    public BodyPane() {
        miProperties = new MenuItem("Properties");
        miProperties.setDisable(true);
        handleMiProperties();

        miCopyCellToClipBoard = new MenuItem("Copy Cell Text into Clipboard");
        miCopyCellToClipBoard.setDisable(true);
        handleMiCopyCellToClipBoard();

        miCopyRowToClipBoard = new MenuItem("Copy Row into Clipboard");
        miCopyRowToClipBoard.setDisable(true);
        handleMiCopyRowToClipBoard();

        miExportToCsv = new MenuItem("Export Results into CSV File/Clipboard");
        miExportToCsv.setDisable(true);
        handleMiExportToCsv();

        miDescribeObject = new MenuItem("Describe Object");
        miDescribeObject.setDisable(true);
        handleMiDescribeObject();

        miGetAttributes = new MenuItem("Get Attributes");
        miGetAttributes.setDisable(true);
        handleMiGetAttributes();

        miDestroyObject = new MenuItem("Destroy Object");
        miDestroyObject.setDisable(true);
        handleMiDestroyObject();

        contextMenu.getItems().addAll(
                miProperties,
                new SeparatorMenuItem(),
                miCopyCellToClipBoard,
                miCopyRowToClipBoard,
                miExportToCsv,
                new SeparatorMenuItem(),
                miDescribeObject,
                new SeparatorMenuItem(),
                miGetAttributes,
                miDestroyObject
        );
    }

    @FXML
    private void initialize() {
        tvResult.getSelectionModel().setCellSelectionEnabled(true);

        try {
            fxmlLoader = new FXMLLoader(getClass().getResource("/nl/bos/views/InputPane.fxml"));
            BorderPane inputPane = fxmlLoader.load();
            vboxBody.getChildren().add(inputPane);
            cmbHistory.getSelectionModel().selectedIndexProperty().addListener((observableValue, oldValue, newValue) -> {
                String selectedHistoryItem = String.valueOf(cmbHistory.getItems().get((Integer) newValue));
                LOGGER.info(selectedHistoryItem);
                taStatement.setText(selectedHistoryItem);
            });
            loadHistory();
        } catch (IOException | ParseException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            createHistoryFile();
        }

        tvResult.addEventHandler(MouseEvent.MOUSE_CLICKED, t -> {
            if (t.getButton() == MouseButton.SECONDARY) {
                if (tvResult.getSelectionModel().getSelectedCells().size() != 0) {
                    TablePosition focusedCell = (TablePosition) tvResult.getSelectionModel().getSelectedCells().get(0);
                    MyTableColumn tableColumn = (MyTableColumn) focusedCell.getTableColumn();
                    IDfAttr attr = tableColumn.getAttr();
                    if (attr != null) {
                        Object cellData = focusedCell.getTableColumn().getCellData(focusedCell.getRow());
                        if (isObjectId(String.valueOf(cellData))) {
                            miGetAttributes.setDisable(false);
                            miDestroyObject.setDisable(false);
                        } else {
                            miGetAttributes.setDisable(true);
                            miDestroyObject.setDisable(true);
                        }
                        if (isTypeName(String.valueOf(cellData))) {
                            describeObjectType = TYPE;
                            miDescribeObject.setDisable(false);
                        } else if (isTableName(String.valueOf(cellData))) {
                            describeObjectType = TABLE;
                            miDescribeObject.setDisable(false);
                        } else {
                            miDescribeObject.setDisable(true);
                        }
                    } else {
                        miGetAttributes.setDisable(true);
                        miDestroyObject.setDisable(true);
                        miDescribeObject.setDisable(true);
                    }
                    miProperties.setDisable(false);
                    miCopyCellToClipBoard.setDisable(false);
                    miCopyRowToClipBoard.setDisable(false);
                } else {
                    miProperties.setDisable(true);
                    miCopyCellToClipBoard.setDisable(true);
                    miCopyRowToClipBoard.setDisable(true);
                }
                contextMenu.show(tvResult, t.getScreenX(), t.getScreenY());
            } else if (t.getButton() == MouseButton.PRIMARY)
                contextMenu.hide();
        });
    }

    private void loadHistory() throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        jsonObject = (JSONObject) parser.parse(new FileReader("history.json"));

        LOGGER.info(jsonObject.toJSONString());

        JSONArray msg = (JSONArray) jsonObject.get("queries");
        Iterator iterator = msg.iterator();
        List<Object> statements = new ArrayList<>();
        while (iterator.hasNext()) {
            statements.add(iterator.next());
        }
        ObservableList<Object> value = FXCollections.observableList(statements);
        cmbHistory.setItems(value);
    }

    private void createHistoryFile() {
        JSONArray list = new JSONArray();
        jsonObject.put("queries", list);

        try (FileWriter file = new FileWriter("history.json")) {
            file.write(jsonObject.toJSONString());
            file.flush();
            LOGGER.info("New history.json file is created");
        } catch (IOException ioe) {
            LOGGER.log(Level.SEVERE, ioe.getMessage(), ioe);
        }
    }

    private boolean isObjectId(String id) {
        String regex = "^[0-9a-f]{16}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(id);
        return matcher.find();
    }

    private boolean isTypeName(String name) {
        boolean result = false;
        try {
            IDfCollection nrOfTypes = repositoryCon.query(String.format("select count(r_object_id) as nroftypes from dm_type where name = '%s'", name));
            nrOfTypes.next();
            if (Integer.parseInt(nrOfTypes.getString("nroftypes")) > 0)
                result = true;
            nrOfTypes.close();
        } catch (DfException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return result;
    }

    private boolean isTableName(String name) {
        boolean result = false;
        try {
            IDfCollection nrOfTypes = repositoryCon.query(String.format("select count(r_object_id) as nroftables from dm_registered where object_name = '%s'", name));
            nrOfTypes.next();
            if (Integer.parseInt(nrOfTypes.getString("nroftables")) > 0)
                result = true;
            nrOfTypes.close();
        } catch (DfException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return result;
    }

    private void handleMiProperties() {
        miProperties.setOnAction(actionEvent -> {
            LOGGER.info(actionEvent.getSource().toString());
            TablePosition focusedCell = (TablePosition) tvResult.getSelectionModel().getSelectedCells().get(0);
            MyTableColumn tableColumn = (MyTableColumn) focusedCell.getTableColumn();
            IDfAttr attr = tableColumn.getAttr();

            String message;
            if (attr != null) {
                message = MessageFormat.format("Attribute Name: {0}\nData Type: {1,number,integer}\nSize: {2,number,integer}\nRepeating: {3}", attr.getName(), attr.getDataType(), attr.getLength(), String.valueOf(attr.isRepeating()));
            } else {
                int index = StringUtils.ordinalIndexOf(description, "\r\n\r\n", 2);
                message = description.substring(0, index);
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Properties");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    private void handleMiCopyCellToClipBoard() {
        miCopyCellToClipBoard.setOnAction(actionEvent -> {
            TablePosition focusedCell = (TablePosition) tvResult.getSelectionModel().getSelectedCells().get(0);
            String value = (String) focusedCell.getTableColumn().getCellObservableValue(focusedCell.getRow()).getValue();

            final Clipboard clipboard = Clipboard.getSystemClipboard();
            final ClipboardContent content = new ClipboardContent();
            content.putString(value);
            clipboard.setContent(content);
        });
    }

    private void handleMiCopyRowToClipBoard() {
        miCopyRowToClipBoard.setOnAction(actionEvent -> {
            TablePosition focusedCell = tvResult.getFocusModel().getFocusedCell();
            ObservableList<String> row = (ObservableList<String>) tvResult.getItems().get(focusedCell.getRow());

            StringBuilder value = new StringBuilder();

            for (String cellValue : row) {
                String appendText = cellValue + "\n";
                value.append(appendText);
            }

            final Clipboard clipboard = Clipboard.getSystemClipboard();
            final ClipboardContent content = new ClipboardContent();
            content.putString(value.toString());
            clipboard.setContent(content);
        });
    }

    private void handleMiExportToCsv() {
        miExportToCsv.setOnAction(actionEvent -> {
            LOGGER.info(actionEvent.getSource().toString());
            try {
                File tempFile = File.createTempFile("tmp_", ".csv");
                LOGGER.info(tempFile.getPath());
                InputStream tableResultContent = new ByteArrayInputStream(convertTableResultsToString().getBytes(Charset.forName("UTF-8")));
                ReadableByteChannel readableByteChannel = Channels.newChannel(tableResultContent);
                FileOutputStream fileOutputStream = new FileOutputStream(tempFile);
                FileChannel fileChannel = fileOutputStream.getChannel();
                fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);

                tableResultContent.close();
                readableByteChannel.close();
                fileChannel.close();

                if (!Desktop.isDesktopSupported()) {
                    LOGGER.info("Desktop is not supported");
                    return;
                }

                Desktop desktop = Desktop.getDesktop();
                if (tempFile.exists())
                    desktop.open(tempFile);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        });
    }

    private String convertTableResultsToString() {
        StringBuilder result = new StringBuilder();

        ObservableList columns = tvResult.getColumns();
        for (int i = 0; i < columns.size(); i++) {
            MyTableColumn column = (MyTableColumn) columns.get(i);
            if (i < columns.size() - 1) {
                String appendText = column.getText() + ";";
                result.append(appendText);
            } else
                result.append(column.getText());
        }
        result.append("\n");

        ObservableList<ObservableList<String>> rows = tvResult.getItems();
        for (ObservableList<String> row : rows) {
            for (int j = 0; j < row.size(); j++) {
                String value = row.get(j);
                if (j < row.size() - 1) {
                    String appendText = value + ";";
                    result.append(appendText);
                } else
                    result.append(value);
            }
            result.append("\n");
        }

        return result.toString();
    }

    private void handleMiDescribeObject() {
        miDescribeObject.setOnAction(actionEvent -> {
            LOGGER.info(actionEvent.getSource().toString());
            TablePosition focusedCell = (TablePosition) tvResult.getSelectionModel().getSelectedCells().get(0);
            String name = (String) focusedCell.getTableColumn().getCellObservableValue(focusedCell.getRow()).getValue();
            LOGGER.info(name);
            new TableResultUtils().updateTable(describeObjectType, name);
        });
    }

    private void handleMiGetAttributes() {
        miGetAttributes.setOnAction(actionEvent -> {
            LOGGER.info(actionEvent.getSource().toString());

            TablePosition focusedCell = (TablePosition) tvResult.getSelectionModel().getSelectedCells().get(0);
            String id = (String) focusedCell.getTableColumn().getCellObservableValue(focusedCell.getRow()).getValue();

            try {
                LOGGER.info(id);
                Stage getAttributes = new Stage();
                getAttributes.setTitle(String.format("Attributes List - %s (%s)", id, repositoryCon.getRepositoryName()));
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/nl/bos/views/GetAttributesPane.fxml"));
                VBox loginPane = fxmlLoader.load();
                Scene scene = new Scene(loginPane);
                getAttributes.setScene(scene);
                GetAttributesPane controller = fxmlLoader.getController();
                controller.initTextArea(repositoryCon.getSession().getObject(new DfId(id)));
                getAttributes.showAndWait();
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        });
    }

    private void handleMiDestroyObject() {
        miDestroyObject.setOnAction(actionEvent -> {
            TablePosition focusedCell = (TablePosition) tvResult.getSelectionModel().getSelectedCells().get(0);
            String id = (String) focusedCell.getTableColumn().getCellObservableValue(focusedCell.getRow()).getValue();

            try {
                LOGGER.info(id);
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Delete Object");
                alert.setHeaderText(null);
                String message = MessageFormat.format("Are you sure you want to destroy the selected object id ''{0}''?", id);
                alert.setContentText(message);
                ButtonType btnYes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
                ButtonType btnNo = new ButtonType("No", ButtonBar.ButtonData.NO);
                alert.getButtonTypes().setAll(btnYes, btnNo);
                alert.showAndWait().ifPresent(type -> {
                    if (type == btnYes) {
                        LOGGER.info("Deleting the object!");
                        try {
                            IDfPersistentObject object = repositoryCon.getSession().getObject(new DfId(id));
                            object.destroy();
                            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
                            confirmation.setTitle("Confirmation delete object");
                            confirmation.setHeaderText(null);
                            String messageConfirmation = MessageFormat.format("Succesfully destroyed the object id ''{0}''!", id);
                            confirmation.setContentText(messageConfirmation);
                            confirmation.showAndWait();
                        } catch (DfException e) {
                            LOGGER.log(Level.SEVERE, e.getMessage(), e);
                            Alert confirmation = new Alert(Alert.AlertType.ERROR);
                            confirmation.setTitle("Error on delete object");
                            confirmation.setHeaderText(null);
                            confirmation.setContentText(e.getMessage());
                            confirmation.showAndWait();
                        }
                    } else {
                        LOGGER.info("Object deletion cancelled!");
                    }
                });
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        });
    }

    void updateResultTable(IDfCollection collection) throws DfException {
        tvResult.getItems().clear();
        tvResult.getColumns().clear();

        List<MyTableColumn> columns = new ArrayList<>();
        ObservableList<ObservableList> rows = FXCollections.observableArrayList();

        int rowCount = 0;
        while (collection.next()) {
            rowCount++;
            ObservableList<String> row = FXCollections.observableArrayList();
            for (int i = 0; i < collection.getAttrCount(); i++) {
                IDfAttr attr = collection.getAttr(i);

                if (rowCount == 1) {
                    final int j = i;
                    MyTableColumn column = new MyTableColumn(attr.getName());
                    column.setAttr(attr);
                    column.setCellValueFactory((Callback<MyTableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>) param -> new SimpleStringProperty(param.getValue().get(j).toString()));
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
        tvResult.getColumns().addAll(columns);
        tvResult.setItems(rows);
        if (rows.size() > 0) {
            miExportToCsv.setDisable(false);
        } else {
            miExportToCsv.setDisable(true);
        }
    }

    public void updateResultTableWithStringInput(String description, List<String> columnNames) {
        this.description = description;
        tvResult.getItems().clear();
        tvResult.getColumns().clear();

        List<MyTableColumn> columns = new ArrayList<>();
        ObservableList<ObservableList> rows = FXCollections.observableArrayList();

        int rowCount = 0;

        while (rowCount < getRowSize(description)) {
            rowCount++;
            ObservableList<String> row = FXCollections.observableArrayList();
            for (int i = 0; i < columnNames.size(); i++) {

                if (rowCount == 1) {
                    final int j = i;
                    MyTableColumn column = new MyTableColumn(columnNames.get(j));
                    column.setCellValueFactory((Callback<MyTableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>) param -> new SimpleStringProperty(param.getValue().get(j).toString()));
                    columns.add(column);
                }

                row.add(getRowValue(rowCount - 1, i));
            }
            rows.add(row);
        }
        tvResult.getColumns().addAll(columns);
        tvResult.setItems(rows);
        if (rows.size() > 0) {
            miExportToCsv.setDisable(false);
        } else {
            miExportToCsv.setDisable(true);
        }
    }

    private int getRowSize(String description) {
        String fromColumns;
        String type = description.substring(0, description.indexOf("\t"));

        if (type.contains("Table")) {
            fromColumns = description.substring(description.indexOf("Columns:"));
        } else {
            fromColumns = description.substring(description.indexOf("Attributes:"));
        }

        String columnsInfo = fromColumns.substring(0, fromColumns.indexOf("\r\n"));
        String value = columnsInfo.substring(columnsInfo.indexOf(":") + 1, columnsInfo.length()).trim();

        parseDescription(description);

        return Integer.parseInt(value);
    }

    private void parseDescription(String descriptionInput) {
        String[] split;
        String type = descriptionInput.substring(0, descriptionInput.indexOf("\t"));

        if (type.contains("Table")) {
            split = descriptionInput.substring(descriptionInput.indexOf("\r\n", descriptionInput.indexOf("Columns:"))).replace("KEYED\r\n", " KEYED ").replace("\r\n", " NOT_KEYED ").replace(" NOT_KEYED", " FALSE").replace("KEYED", " TRUE").split(" ");
        } else {
            split = descriptionInput.substring(descriptionInput.indexOf("\r\n", descriptionInput.indexOf("Attributes:"))).replace("REPEATING\r\n", " REPEATING ").replace("\r\n", " NOT_REPEATING ").replace(" NOT_REPEATING", " FALSE").replace("REPEATING", " TRUE").split(" ");
        }

        split[0] = "";
        split[1] = "";
        split[2] = "";
        split[3] = "";
        parsedDescription = Arrays.stream(split).filter(value -> !value.equals("")).toArray(size -> new String[size]);
    }

    private String getRowValue(int rowIndex, int columnIndex) {
        String result = "";
        try {
            result = parsedDescription[(rowIndex * 3) + columnIndex];
        } catch (ArrayIndexOutOfBoundsException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return result;
    }

    @FXML
    private void handleDeleteHistoryItem(MouseEvent mouseEvent) {
        Object selectedItem = cmbHistory.getSelectionModel().getSelectedItem();
        int selectedIndex = cmbHistory.getSelectionModel().getSelectedIndex();

        if (cmbHistory.getItems().remove(selectedItem)) {
            ObservableList<Object> items = cmbHistory.getItems();
            try {
                JSONParser parser = new JSONParser();
                JSONObject jsonObjectHistory = (JSONObject) parser.parse(new FileReader("history.json"));
                JSONArray queries = (JSONArray) jsonObjectHistory.get("queries");

                queries.remove(selectedIndex);
                cmbHistory.setValue(cmbHistory.getItems().get(0));
                try (FileWriter file = new FileWriter("history.json")) {
                    file.write(jsonObjectHistory.toJSONString());
                    file.flush();
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, e.getMessage(), e);
                }
            } catch (IOException | ParseException e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
            cmbHistory.setItems(items);
        }
    }
}
