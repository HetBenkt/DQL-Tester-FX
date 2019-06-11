package nl.bos.contextmenu.menuitem.action;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import nl.bos.Repository;
import nl.bos.utils.Resources;

import java.awt.*;
import java.io.File;
import java.util.logging.Logger;

public class MenuItemOpenContentAction implements EventHandler<ActionEvent> {
    private static final Logger LOGGER = Logger.getLogger(MenuItemOpenContentAction.class.getName());

    private final Repository repository = Repository.getInstance();
    private final TableView result;

    public MenuItemOpenContentAction(MenuItem openContent, TableView result) {
        this.result = result;
        openContent.setOnAction(this);
    }

    @Override
    public void handle(ActionEvent actionEvent) {
        TablePosition focusedCell = (TablePosition) result.getSelectionModel().getSelectedCells().get(0);
        String id = (String) focusedCell.getTableColumn().getCellObservableValue(focusedCell.getRow()).getValue();

        String path = repository.downloadContent(id);
        if (Desktop.isDesktopSupported()) {
            Resources.openFile(new File(path));
        }
    }


}
