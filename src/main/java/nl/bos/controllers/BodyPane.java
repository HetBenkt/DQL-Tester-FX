package nl.bos.controllers;

import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.IDfAttr;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import static nl.bos.Constants.ATTR_R_OBJECT_ID;

public class BodyPane implements Initializable {
    private static final Logger log = Logger.getLogger(BodyPane.class.getName());
    private final MenuItem miExportToCsv, miProperties, miGetAttributes, miCopyCellToClipBoard, miCopyRowToClipBoard, miDescribeObject, miFindCellText;
    private final Repository repositoryCon = Repository.getInstance();

    @FXML
    private ChoiceBox<Object> cmbHistory;
    @FXML
    private VBox vboxBody;
    @FXML
    private TextArea taStatement;
    @FXML
    private TableView tvResult;
    private JSONObject jsonObject;
    private FXMLLoader fxmlLoader;
    private ContextMenu contextMenu = new ContextMenu();

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

        miFindCellText = new MenuItem("Find Cell Text");
        miFindCellText.setDisable(true);
        handleMiFindCellText();

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
                new SeparatorMenuItem(),
                miFindCellText
        );
    }

    private void handleMiFindCellText() {
        //todo
    }

    private void handleMiDescribeObject() {
        //todo
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

    private void handleMiGetAttributes() {
        miGetAttributes.setOnAction(actionEvent -> {
            log.info(actionEvent.getSource().toString());

            TablePosition focusedCell = (TablePosition) tvResult.getSelectionModel().getSelectedCells().get(0);
            String id = (String) focusedCell.getTableColumn().getCellObservableValue(focusedCell.getRow()).getValue();

            try {
                log.info(id);
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
                log.finest(e.getMessage());
            }
        });
    }

    private void handleMiProperties() {
        miProperties.setOnAction(actionEvent -> {
            log.info(actionEvent.getSource().toString());
            TablePosition focusedCell = (TablePosition) tvResult.getSelectionModel().getSelectedCells().get(0);
            MyTableColumn tableColumn = (MyTableColumn) focusedCell.getTableColumn();
            IDfAttr attr = tableColumn.getAttr();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Properties");
            alert.setHeaderText(null);
            String message = MessageFormat.format("Attribute Name: {0}\nData Type: {1,number,integer}\nSize: {2,number,integer}\nRepeating: {3}", attr.getName(), attr.getDataType(), attr.getLength(), String.valueOf(attr.isRepeating()));
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    private void handleMiExportToCsv() {
        miExportToCsv.setOnAction(actionEvent -> {
            log.info(actionEvent.getSource().toString());
            try {
                File tempFile = File.createTempFile("tmp_", ".csv");
                log.info(tempFile.getPath());
                InputStream tableResultContent = new ByteArrayInputStream(convertTableResultsToString().getBytes(Charset.forName("UTF-8")));
                ReadableByteChannel readableByteChannel = Channels.newChannel(tableResultContent);
                FileOutputStream fileOutputStream = new FileOutputStream(tempFile);
                FileChannel fileChannel = fileOutputStream.getChannel();
                fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);

                tableResultContent.close();
                readableByteChannel.close();
                fileChannel.close();

                if (!Desktop.isDesktopSupported()) {
                    log.info("Desktop is not supported");
                    return;
                }

                Desktop desktop = Desktop.getDesktop();
                if (tempFile.exists())
                    desktop.open(tempFile);
            } catch (IOException e) {
                log.finest(e.getMessage());
            }
        });
    }

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

    public void initialize(URL location, ResourceBundle resources) {
        tvResult.getSelectionModel().setCellSelectionEnabled(true);

        try {
            fxmlLoader = new FXMLLoader(getClass().getResource("/nl/bos/views/InputPane.fxml"));
            BorderPane inputPane = fxmlLoader.load();
            vboxBody.getChildren().add(inputPane);
        } catch (IOException e) {
            log.info(e.getMessage());
        }
        cmbHistory.getSelectionModel().selectedIndexProperty().addListener((observableValue, oldValue, newValue) -> {
            String selectedHistoryItem = String.valueOf(cmbHistory.getItems().get((Integer) newValue));
            log.info(selectedHistoryItem);
            taStatement.setText(selectedHistoryItem);
        });

        try {
            loadHistory();
        } catch (IOException e) {
            log.info(e.getMessage());
            createHistoryFile();
        } catch (ParseException e) {
            log.info(e.getMessage());
        }

        tvResult.addEventHandler(MouseEvent.MOUSE_CLICKED, t -> {
            if (t.getButton() == MouseButton.SECONDARY) {
                TablePosition focusedCell = (TablePosition) tvResult.getSelectionModel().getSelectedCells().get(0);
                MyTableColumn tableColumn = (MyTableColumn) focusedCell.getTableColumn();
                IDfAttr attr = tableColumn.getAttr();
                if (attr.getDataType() == IDfAttr.DM_STRING && attr.getLength() == 16 && attr.getName().equals(ATTR_R_OBJECT_ID))
                    miGetAttributes.setDisable(false);
                else
                    miGetAttributes.setDisable(true);
                contextMenu.show(tvResult, t.getScreenX(), t.getScreenY());
            } else if (t.getButton() == MouseButton.PRIMARY)
                contextMenu.hide();
        });
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
        Iterator iterator = msg.iterator();
        List<Object> statements = new ArrayList<>();
        while (iterator.hasNext()) {
            statements.add(iterator.next());
        }
        ObservableList<Object> value = FXCollections.observableList(statements);
        cmbHistory.setItems(value);
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
                        log.finest("Error occurred while displaying the results.");
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
            miProperties.setDisable(false);
            miCopyCellToClipBoard.setDisable(false);
            miCopyRowToClipBoard.setDisable(false);
        } else {
            miExportToCsv.setDisable(true);
            miProperties.setDisable(true);
            miCopyCellToClipBoard.setDisable(true);
            miCopyRowToClipBoard.setDisable(true);
        }
    }
}
