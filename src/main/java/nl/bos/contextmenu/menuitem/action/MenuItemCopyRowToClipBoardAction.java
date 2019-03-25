package nl.bos.contextmenu.menuitem.action;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

public class MenuItemCopyRowToClipBoardAction implements EventHandler<ActionEvent> {
    private final TableView result;

    public MenuItemCopyRowToClipBoardAction(MenuItem copyRowToClipBoard, TableView result) {
        this.result = result;
        copyRowToClipBoard.setOnAction(this);
    }

    @Override
    public void handle(ActionEvent actionEvent) {
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
    }
}
