package nl.bos.contextmenu.menuitem.action;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import nl.bos.AttributeTableColumn;

import java.awt.*;
import java.io.*;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MenuItemExportToCsvAction implements EventHandler<ActionEvent> {
    private static final Logger LOGGER = Logger.getLogger(MenuItemExportToCsvAction.class.getName());
    private final TableView result;

    public MenuItemExportToCsvAction(MenuItem exportToCsv, TableView result) {
        this.result = result;

        exportToCsv.setOnAction(this);
    }

    @Override
    public void handle(ActionEvent actionEvent) {
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
}
