package nl.bos.contextmenu.menuitem.action;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import nl.bos.controllers.CheckinDialog;
import nl.bos.utils.Resources;

import java.util.logging.Logger;

import static nl.bos.Constants.ROOT_SCENE_CSS;

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
        checkinStage.setScene(new Scene(checkinDialog));
        checkinStage.getScene().getStylesheets()
                .addAll(ROOT_SCENE_CSS);

        CheckinDialog controller = resources.getFxmlLoader().getController();
        controller.setStage(checkinStage);
        controller.initialize();
        controller.checkinDialog(id);
        checkinStage.showAndWait();
	}
	

}
