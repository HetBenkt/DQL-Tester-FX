package nl.bos.contextmenu;

import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import nl.bos.Repository;
import nl.bos.contextmenu.menuitem.action.*;
import nl.bos.controllers.ConnectionWithStatus;
import nl.bos.controllers.QueryWithResult;
import nl.bos.utils.Calculations;
import nl.bos.utils.Controllers;

import java.time.Instant;
import java.util.logging.Logger;

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

    private final TableView result;
    private final MenuItemShowPropertiesAction menuItemShowPropertiesAction;

    public ContextMenuOnResultTable(TableView result) {
        this.result = result;

        showProperties = new MenuItem("Properties");
        showProperties.setDisable(true);
        menuItemShowPropertiesAction = new MenuItemShowPropertiesAction(showProperties, result);

        copyCellToClipBoard = new MenuItem("Copy Cell Text into Clipboard");
        copyCellToClipBoard.setDisable(true);
        new MenuItemCopyCellToClipBoardAction(copyCellToClipBoard, result);

        copyRowToClipBoard = new MenuItem("Copy Row into Clipboard");
        copyRowToClipBoard.setDisable(true);
        new MenuItemCopyRowToClipBoardAction(copyRowToClipBoard, result);

        exportToCsv = new MenuItem("Export Results into CSV File/Clipboard");
        exportToCsv.setDisable(true);
        new MenuItemExportToCsvAction(exportToCsv, result);

        describeObject = new MenuItem("Describe Object");
        describeObject.setDisable(true);
        new MenuItemDescribeObjectAction(describeObject, result);

        getAttributes = new MenuItem("Get Attributes");
        getAttributes.setDisable(true);
        new MenuItemGetAttributesAction(getAttributes, result);

        destroyObject = new MenuItem("Destroy Object");
        destroyObject.setDisable(true);
        new MenuItemDestroyObjectAction(destroyObject, result);

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

    public void onRightMouseClick(MouseEvent t) {
        if (t.getButton() == MouseButton.PRIMARY) {
            contextMenu.hide();

            if (t.getTarget().getClass().getName().contains("TableColumnHeader")) {
                QueryWithResult queryWithResultController = (QueryWithResult) Controllers.get(QueryWithResult.class.getSimpleName());
                ConnectionWithStatus connectionWithStatusController = (ConnectionWithStatus) Controllers.get(ConnectionWithStatus.class.getSimpleName());

                Instant start = queryWithResultController.getStart();
                if (start != null) {
                    Instant end = Instant.now();
                    LOGGER.info("End..." + end);
                    connectionWithStatusController.getTimeSort().setText(Calculations.getDurationInSeconds(start, end));
                } else {
                    connectionWithStatusController.getTimeSort().setText("0.000 sec.");
                }

                queryWithResultController.cleanStart();
            }
        } else if (t.getButton() == MouseButton.SECONDARY) {
            validateMenuItems();
            contextMenu.show(result, t.getScreenX(), t.getScreenY());
        }
    }

    private void validateMenuItems() {
        exportToCsv.setDisable(hasNoRowsInResultTable());

        showProperties.setDisable(hasNoSelectedCellsInResultTable());
        copyCellToClipBoard.setDisable(hasNoSelectedCellsInResultTable());
        copyRowToClipBoard.setDisable(hasNoSelectedCellsInResultTable());
        describeObject.setDisable(selectionIsNotAnDescribeObjectType());

        getAttributes.setDisable(selectionIsNotAnObjectId());
        destroyObject.setDisable(selectionIsNotAnObjectId());
    }

    private boolean hasNoRowsInResultTable() {
        return result.getItems().isEmpty();
    }

    private boolean hasNoSelectedCellsInResultTable() {
        return result.getSelectionModel().getSelectedCells().isEmpty();
    }

    private boolean selectionIsNotAnDescribeObjectType() {
        if (result.getSelectionModel().getSelectedCells().size() == 0) {
            return true;
        } else {
            TablePosition focusedCell = (TablePosition) result.getSelectionModel().getSelectedCells().get(0);
            Object cellData = focusedCell.getTableColumn().getCellData(focusedCell.getRow());

            return !(repository.isTypeName(String.valueOf(cellData)) || repository.isTableName(String.valueOf(cellData)));
        }
    }

    private boolean selectionIsNotAnObjectId() {
        if (result.getSelectionModel().getSelectedCells().size() == 0) {
            return true;
        } else {
            TablePosition focusedCell = (TablePosition) result.getSelectionModel().getSelectedCells().get(0);
            Object cellData = focusedCell.getTableColumn().getCellData(focusedCell.getRow());

            return !repository.isObjectId(String.valueOf(cellData));
        }
    }

    public MenuItemShowPropertiesAction getMenuItemShowPropertiesAction() {
        return menuItemShowPropertiesAction;
    }
}
