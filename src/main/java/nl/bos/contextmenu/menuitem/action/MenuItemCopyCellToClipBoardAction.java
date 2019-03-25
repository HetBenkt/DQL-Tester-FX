package nl.bos.contextmenu.menuitem.action;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

public class MenuItemCopyCellToClipBoardAction implements EventHandler<ActionEvent> {
    private final TableView result;

    public MenuItemCopyCellToClipBoardAction(MenuItem copyCellToClipBoard, TableView result) {
        this.result = result;
        copyCellToClipBoard.setOnAction(this);
    }

    @Override
    public void handle(ActionEvent actionEvent) {
        TablePosition focusedCell = (TablePosition) result.getSelectionModel().getSelectedCells().get(0);
        String value = (String) focusedCell.getTableColumn().getCellObservableValue(focusedCell.getRow()).getValue();

        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(value);
        clipboard.setContent(content);
    }
}
