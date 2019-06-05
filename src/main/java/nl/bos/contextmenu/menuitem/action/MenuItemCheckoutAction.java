package nl.bos.contextmenu.menuitem.action;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import nl.bos.Repository;
import nl.bos.utils.AppAlert;
import nl.bos.utils.Resources;

public class MenuItemCheckoutAction implements EventHandler<ActionEvent> {
	private static final Logger LOGGER = Logger.getLogger(MenuItemCheckoutAction.class.getName());

	private final Repository repository = Repository.getInstance();
	private final TableView result;

	public MenuItemCheckoutAction(MenuItem destroyObject, TableView result) {
		this.result = result;
		destroyObject.setOnAction(this);
	}

	@Override
	public void handle(ActionEvent actionEvent) {
		TablePosition focusedCell = (TablePosition) result.getSelectionModel().getSelectedCells().get(0);
		String id = (String) focusedCell.getTableColumn().getCellObservableValue(focusedCell.getRow()).getValue();

		repository.checkoutContent(id);
	}
	

}
