package nl.bos.contextmenu.menuitem.action;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import nl.bos.actions.GenerateAPIScriptAction;

import java.util.logging.Logger;

public class MenuItemGenerateAPIScriptAction implements EventHandler<ActionEvent> {
    private static final Logger LOGGER = Logger.getLogger(MenuItemGenerateAPIScriptAction.class.getName());

    private final TableView result;

    public MenuItemGenerateAPIScriptAction(MenuItem getAttributes, TableView result) {
        this.result = result;
        getAttributes.setOnAction(this);
    }

    @Override
    public void handle(ActionEvent actionEvent) {
        LOGGER.info(actionEvent.getSource().toString());

        TablePosition focusedCell = (TablePosition) result.getSelectionModel().getSelectedCells().get(0);
        String id = (String) focusedCell.getTableColumn().getCellObservableValue(focusedCell.getRow()).getValue();
        LOGGER.info(id);

        new GenerateAPIScriptAction(result);
    }
}
