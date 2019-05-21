package nl.bos.contextmenu.menuitem.action;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import nl.bos.AttributeTableColumn;
import nl.bos.utils.Resources;

import java.awt.*;
import java.io.File;
import java.util.logging.Logger;

public class MenuItemExportToCsvAction implements EventHandler<ActionEvent> {
    private static final Logger LOGGER = Logger.getLogger(MenuItemExportToCsvAction.class.getName());
    private TableView result;

    public MenuItemExportToCsvAction(MenuItem exportToCsv, TableView result) {
        this.result = result;

        exportToCsv.setOnAction(this);
    }

    @Override
    public void handle(ActionEvent actionEvent) {
        File tempFile = Resources.createTempFile("_tmp", ".csv");
        Resources.exportStringToFile(tempFile, convertTableResultsToString());
        if (Desktop.isDesktopSupported()) {
            Resources.openCSV(tempFile);
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
