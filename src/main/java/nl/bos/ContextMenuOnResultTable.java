package nl.bos;

import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.IDfAttr;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.MenuItem;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import nl.bos.controllers.GetAttributes;
import nl.bos.utils.TableResultUtils;
import org.apache.commons.lang.StringUtils;

import java.awt.*;
import java.io.*;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import static nl.bos.Constants.TABLE;
import static nl.bos.Constants.TYPE;

public class ContextMenuOnResultTable {
    private static final Logger LOGGER = Logger.getLogger(ContextMenuOnResultTable.class.getName());

    private final Repository repository = Repository.getInstance();

    private final ContextMenu contextMenu = new ContextMenu();

    private final MenuItem exportToCsv;
    private final MenuItem showProperties;
    private final MenuItem getAttributes;
    private final MenuItem copyCellToClipBoard;
    private final MenuItem copyRowToClipBoard;
    private final MenuItem describeObject;
    private final MenuItem destroyObject;

    private String description;
    private String describeObjectType;
    private TableView result;

    public ContextMenuOnResultTable(TableView result) {
        this.result = result;

        showProperties = new MenuItem("Properties");
        showProperties.setDisable(true);
        handleMiProperties();

        copyCellToClipBoard = new MenuItem("Copy Cell Text into Clipboard");
        copyCellToClipBoard.setDisable(true);
        handleMiCopyCellToClipBoard();

        copyRowToClipBoard = new MenuItem("Copy Row into Clipboard");
        copyRowToClipBoard.setDisable(true);
        handleMiCopyRowToClipBoard();

        exportToCsv = new MenuItem("Export Results into CSV File/Clipboard");
        exportToCsv.setDisable(true);
        handleMiExportToCsv();

        describeObject = new MenuItem("Describe Object");
        describeObject.setDisable(true);
        handleMiDescribeObject();

        getAttributes = new MenuItem("Get Attributes");
        getAttributes.setDisable(true);
        handleMiGetAttributes();

        destroyObject = new MenuItem("Destroy Object");
        destroyObject.setDisable(true);
        handleMiDestroyObject();

        contextMenu.getItems().addAll(
                showProperties,
                new SeparatorMenuItem(),
                copyCellToClipBoard,
                copyRowToClipBoard,
                exportToCsv,
                new SeparatorMenuItem(),
                describeObject,
                new SeparatorMenuItem(),
                getAttributes,
                destroyObject
        );
    }

    public void rightMouseClick(MouseEvent t) {
        if (t.getButton() == MouseButton.SECONDARY) {
            if (result.getSelectionModel().getSelectedCells().size() != 0) {
                TablePosition focusedCell = (TablePosition) result.getSelectionModel().getSelectedCells().get(0);
                AttributeTableColumn tableColumn = (AttributeTableColumn) focusedCell.getTableColumn();
                IDfAttr attr = tableColumn.getAttr();
                if (attr != null) {
                    Object cellData = focusedCell.getTableColumn().getCellData(focusedCell.getRow());
                    if (repository.isObjectId(String.valueOf(cellData))) {
                        getAttributes.setDisable(false);
                        destroyObject.setDisable(false);
                    } else {
                        getAttributes.setDisable(true);
                        destroyObject.setDisable(true);
                    }
                    if (repository.isTypeName(String.valueOf(cellData))) {
                        describeObjectType = TYPE;
                        describeObject.setDisable(false);
                    } else if (repository.isTableName(String.valueOf(cellData))) {
                        describeObjectType = TABLE;
                        describeObject.setDisable(false);
                    } else {
                        describeObject.setDisable(true);
                    }
                } else {
                    getAttributes.setDisable(true);
                    destroyObject.setDisable(true);
                    describeObject.setDisable(true);
                }
                showProperties.setDisable(false);
                copyCellToClipBoard.setDisable(false);
                copyRowToClipBoard.setDisable(false);
            } else {
                showProperties.setDisable(true);
                copyCellToClipBoard.setDisable(true);
                copyRowToClipBoard.setDisable(true);
            }
            contextMenu.show(result, t.getScreenX(), t.getScreenY());
        } else if (t.getButton() == MouseButton.PRIMARY) {
            contextMenu.hide();
        }
    }

    public void revalidate(ObservableList<ObservableList> rows) {
        exportToCsv.setDisable(rows.isEmpty());
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private void handleMiProperties() {
        showProperties.setOnAction(actionEvent -> {
            LOGGER.info(actionEvent.getSource().toString());
            TablePosition focusedCell = (TablePosition) result.getSelectionModel().getSelectedCells().get(0);
            AttributeTableColumn tableColumn = (AttributeTableColumn) focusedCell.getTableColumn();
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
        copyCellToClipBoard.setOnAction(actionEvent -> {
            TablePosition focusedCell = (TablePosition) result.getSelectionModel().getSelectedCells().get(0);
            String value = (String) focusedCell.getTableColumn().getCellObservableValue(focusedCell.getRow()).getValue();

            final Clipboard clipboard = Clipboard.getSystemClipboard();
            final ClipboardContent content = new ClipboardContent();
            content.putString(value);
            clipboard.setContent(content);
        });
    }

    private void handleMiCopyRowToClipBoard() {
        copyRowToClipBoard.setOnAction(actionEvent -> {
            TablePosition focusedCell = result.getFocusModel().getFocusedCell();
            //noinspection unchecked
            ObservableList<String> row = (ObservableList<String>) result.getItems().get(focusedCell.getRow());

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
        exportToCsv.setOnAction(actionEvent -> {
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
        StringBuilder tableResult = new StringBuilder();

        ObservableList columns = result.getColumns();
        for (int i = 0; i < columns.size(); i++) {
            AttributeTableColumn column = (AttributeTableColumn) columns.get(i);
            if (i < columns.size() - 1) {
                String appendText = column.getText() + ";";
                tableResult.append(appendText);
            } else
                tableResult.append(column.getText());
        }
        tableResult.append("\n");

        //noinspection unchecked
        ObservableList<ObservableList<String>> rows = result.getItems();
        for (ObservableList<String> row : rows) {
            for (int j = 0; j < row.size(); j++) {
                String value = row.get(j);
                if (j < row.size() - 1) {
                    String appendText = value + ";";
                    tableResult.append(appendText);
                } else
                    tableResult.append(value);
            }
            tableResult.append("\n");
        }

        return tableResult.toString();
    }

    private void handleMiDescribeObject() {
        describeObject.setOnAction(actionEvent -> {
            LOGGER.info(actionEvent.getSource().toString());
            TablePosition focusedCell = (TablePosition) result.getSelectionModel().getSelectedCells().get(0);
            String name = (String) focusedCell.getTableColumn().getCellObservableValue(focusedCell.getRow()).getValue();
            LOGGER.info(name);
            new TableResultUtils().updateTable(describeObjectType, name);
        });
    }

    private void handleMiGetAttributes() {
        getAttributes.setOnAction(actionEvent -> {
            LOGGER.info(actionEvent.getSource().toString());

            TablePosition focusedCell = (TablePosition) result.getSelectionModel().getSelectedCells().get(0);
            String id = (String) focusedCell.getTableColumn().getCellObservableValue(focusedCell.getRow()).getValue();

            try {
                LOGGER.info(id);
                Stage getAttributes = new Stage();
                getAttributes.setTitle(String.format("Attributes List - %s (%s)", id, repository.getRepositoryName()));
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/nl/bos/views/GetAttributes.fxml"));
                VBox loginPane = fxmlLoader.load();
                Scene scene = new Scene(loginPane);
                getAttributes.setScene(scene);
                GetAttributes controller = fxmlLoader.getController();
                controller.initTextArea(repository.getSession().getObject(new DfId(id)));
                getAttributes.showAndWait();
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        });
    }

    private void handleMiDestroyObject() {
        destroyObject.setOnAction(actionEvent -> {
            TablePosition focusedCell = (TablePosition) result.getSelectionModel().getSelectedCells().get(0);
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
                            IDfPersistentObject object = repository.getSession().getObject(new DfId(id));
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
}
