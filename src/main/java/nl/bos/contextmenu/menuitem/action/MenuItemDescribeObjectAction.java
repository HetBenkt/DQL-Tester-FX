package nl.bos.contextmenu.menuitem.action;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import nl.bos.Repository;
import nl.bos.utils.TableResultUtils;

import java.util.logging.Logger;

import static nl.bos.Constants.TABLE;
import static nl.bos.Constants.TYPE;

public class MenuItemDescribeObjectAction implements EventHandler<ActionEvent> {
    private static final Logger LOGGER = Logger.getLogger(MenuItemDescribeObjectAction.class.getName());

    private final Repository repository = Repository.getInstance();

    private final TableView result;
    private String describeObjectType;

    public MenuItemDescribeObjectAction(MenuItem describeObject, TableView result) {
        this.result = result;
        describeObject.setOnAction(this);
    }

    @Override
    public void handle(ActionEvent actionEvent) {
        updateDescribeObjectType();

        TablePosition focusedCell = (TablePosition) result.getSelectionModel().getSelectedCells().get(0);
        String name = (String) focusedCell.getTableColumn().getCellObservableValue(focusedCell.getRow()).getValue();
        LOGGER.info(name);
        new TableResultUtils().updateTable(describeObjectType, name);
    }

    private void updateDescribeObjectType() {
        if (result.getSelectionModel().getSelectedCells().size() != 0) {
            TablePosition focusedCell = (TablePosition) result.getSelectionModel().getSelectedCells().get(0);
            Object cellData = focusedCell.getTableColumn().getCellData(focusedCell.getRow());

            if (repository.isTypeName(String.valueOf(cellData))) {
                describeObjectType = TYPE;
            } else if (repository.isTableName(String.valueOf(cellData))) {
                describeObjectType = TABLE;
            }
        }
    }
}
