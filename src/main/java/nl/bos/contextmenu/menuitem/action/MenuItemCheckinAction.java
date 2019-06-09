package nl.bos.contextmenu.menuitem.action;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import nl.bos.Repository;
import nl.bos.controllers.CheckinDialog;
import nl.bos.controllers.GetAttributes;
import nl.bos.utils.AppAlert;
import nl.bos.utils.Resources;

public class MenuItemCheckinAction implements EventHandler<ActionEvent> {
	private static final Logger LOGGER = Logger.getLogger(MenuItemCheckinAction.class.getName());

	private final TableView result;

	public MenuItemCheckinAction(MenuItem destroyObject, TableView result) {
		this.result = result;
		destroyObject.setOnAction(this);
	}

	@Override
	public void handle(ActionEvent actionEvent) {
        LOGGER.info(actionEvent.getSource().toString());

        TablePosition focusedCell = (TablePosition) result.getSelectionModel().getSelectedCells().get(0);
        String id = (String) focusedCell.getTableColumn().getCellObservableValue(focusedCell.getRow()).getValue();
        LOGGER.info(id);

        Stage checkinStage = new Stage();
        checkinStage.setTitle("Checkin");
        Resources resources = new Resources();
        VBox checkinDialog = (VBox) resources.loadFXML("/nl/bos/views/dialogs/CheckinDialog.fxml");
        Scene scene = new Scene(checkinDialog);
        checkinStage.setScene(scene);
        CheckinDialog controller = resources.getFxmlLoader().getController();
        controller.setStage(checkinStage);
        controller.initialize();
        controller.checkinDialog(id);
        checkinStage.showAndWait();
	}
	

}
