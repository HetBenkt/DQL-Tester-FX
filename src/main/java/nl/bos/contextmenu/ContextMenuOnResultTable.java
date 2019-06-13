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
	private final MenuItem versions;
	private final MenuItem renditions;

	private final MenuItem openContent;
	private final MenuItem exportContent;
	private final MenuItem checkout;
	private final MenuItem checkin;
	private final MenuItem cancelcheckout;

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

		versions = new MenuItem("Versions");
		versions.setDisable(true);
		new MenuItemResultTableAction(versions, result, "Versions");

		renditions = new MenuItem("Renditions");
		renditions.setDisable(true);
		new MenuItemResultTableAction(renditions, result, "Renditions");

		openContent = new MenuItem("Open Content");
		openContent.setDisable(true);
		new MenuItemOpenContentAction(openContent, result);

		exportContent = new MenuItem("Export Content");
		exportContent.setDisable(true);
		new MenuItemExportContentAction(exportContent, result);

		checkout = new MenuItem("Check Out Document");
		checkout.setDisable(true);
		new MenuItemCheckoutAction(checkout, result);

		checkin = new MenuItem("Check In Document");
		checkin.setDisable(true);
		new MenuItemCheckinAction(checkin, result);
		
		cancelcheckout = new MenuItem("Cancel Checkout");
		cancelcheckout.setDisable(true);
		new MenuItemCancelCheckoutAction(cancelcheckout, result);

		contextMenu.getItems().addAll(openContent, new SeparatorMenuItem(), showProperties, new SeparatorMenuItem(), copyCellToClipBoard, copyRowToClipBoard,
				exportToCsv, new SeparatorMenuItem(), describeObject, new SeparatorMenuItem(), getAttributes, destroyObject, new SeparatorMenuItem(), versions, renditions, new SeparatorMenuItem(),
				checkout, checkin, cancelcheckout, new SeparatorMenuItem(), exportContent);
	}

	public void onRightMouseClick(MouseEvent t) {
		if (t.getButton() == MouseButton.PRIMARY) {
			contextMenu.hide();

			if (t.getTarget().getClass().getName().contains("TableColumnHeader")) {
				QueryWithResult queryWithResultController = (QueryWithResult) Controllers
						.get(QueryWithResult.class.getSimpleName());
				ConnectionWithStatus connectionWithStatusController = (ConnectionWithStatus) Controllers
						.get(ConnectionWithStatus.class.getSimpleName());

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
		String selectedCell = null;
		if (result.getSelectionModel().getSelectedCells().size() > 0) {
			TablePosition focusedCell = (TablePosition) result.getSelectionModel().getSelectedCells().get(0);
			Object cellData = focusedCell.getTableColumn().getCellData(focusedCell.getRow());
			selectedCell = String.valueOf(cellData);
		}

		exportToCsv.setDisable(hasNoRowsInResultTable());

		showProperties.setDisable(hasNoSelectedCellsInResultTable());
		copyCellToClipBoard.setDisable(selectedCell == null);
		copyRowToClipBoard.setDisable(selectedCell == null);
		describeObject.setDisable(selectionIsNotAnDescribeObjectType(selectedCell));

		getAttributes.setDisable(selectionIsNotAnObjectId(selectedCell));
		destroyObject.setDisable(selectionIsNotAnObjectId(selectedCell));
		openContent.setDisable(selectionIsNotAnDocumentType(selectedCell));
		exportContent.setDisable(selectionIsNotAnDocumentType(selectedCell));
		checkout.setDisable(selectionCanBeCheckedOut(selectedCell));
		checkin.setDisable(selectionIsCheckedOut(selectedCell));
		cancelcheckout.setDisable(selectionIsCheckedOut(selectedCell));
		versions.setDisable(selectionIsNotAnDocumentType(selectedCell));
		renditions.setDisable(selectionIsNotAnDocumentType(selectedCell));
	}

	private boolean hasNoRowsInResultTable() {
		return result.getItems().isEmpty();
	}

	private boolean hasNoSelectedCellsInResultTable() {
		return result.getSelectionModel().getSelectedCells().isEmpty();
	}

	private boolean selectionIsNotAnDescribeObjectType(String selectedCell) {
		if (selectedCell == null) {
			return true;
		} else {
			return !(repository.isTypeName(selectedCell) || repository.isTableName(selectedCell));
		}
	}

	private boolean selectionIsNotAnDocumentType(String id) {
		if (id == null || repository.isObjectId(id)) {
			return true;
		} else {
            return !repository.isDocumentType(repository.getObjectById(id));
		}
	}

	private boolean selectionIsNotAnObjectId(String id) {
		if (id == null || repository.isObjectId(id)) {
			return true;
		} else {
			return repository.isObjectId(id);
		}
	}

	private boolean selectionCanBeCheckedOut(String id) {
		if (id == null || repository.isObjectId(id)) {
			return true;
		} else {
			return !repository.canCheckOut(id);
		}
	}

	private boolean selectionIsCheckedOut(String id) {
		if (id == null || repository.isObjectId(id)) {
			return true;
		} else {
			return !repository.isCheckedOut(id);
		}
	}

	public MenuItemShowPropertiesAction getMenuItemShowPropertiesAction() {
		return menuItemShowPropertiesAction;
	}
}
