package nl.bos.contextmenu.menuitem.action;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import nl.bos.Repository;
import nl.bos.beans.RenditionObject;

import java.util.logging.Logger;

public class MenuItemExportContentAction implements EventHandler<ActionEvent> {
    private static final Logger LOGGER = Logger.getLogger(MenuItemExportContentAction.class.getName());

	private final Repository repository = Repository.getInstance();
	private final TableView result;

    public MenuItemExportContentAction(MenuItem exportContent, TableView result) {
		this.result = result;
        exportContent.setOnAction(this);
	}

	@Override
	public void handle(ActionEvent actionEvent) {
		TablePosition focusedCell = (TablePosition) result.getSelectionModel().getSelectedCells().get(0);
		String id = (String) focusedCell.getTableColumn().getCellObservableValue(focusedCell.getRow()).getValue();

        if (id.startsWith("06")) {
            RenditionObject cellData = (RenditionObject) result.getSelectionModel().getSelectedItems().get(0);
            id = repository.getParentId(id);
            repository.downloadContent(id, cellData.getFullFormat());
        } else {
            repository.downloadContent(id);
        }
    }
}
