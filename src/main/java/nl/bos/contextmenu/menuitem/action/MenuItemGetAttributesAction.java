package nl.bos.contextmenu.menuitem.action;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import nl.bos.Repository;
import nl.bos.controllers.GetAttributes;
import nl.bos.utils.Resources;

import java.util.logging.Logger;

public class MenuItemGetAttributesAction implements EventHandler<ActionEvent> {
    private static final Logger LOGGER = Logger.getLogger(MenuItemGetAttributesAction.class.getName());

    private final Repository repository = Repository.getInstance();
    private final TableView result;

    public MenuItemGetAttributesAction(MenuItem getAttributes, TableView result) {
        this.result = result;
        getAttributes.setOnAction(this);
    }

    @Override
    public void handle(ActionEvent actionEvent) {
        LOGGER.info(actionEvent.getSource().toString());

        TablePosition focusedCell = (TablePosition) result.getSelectionModel().getSelectedCells().get(0);
        String id = (String) focusedCell.getTableColumn().getCellObservableValue(focusedCell.getRow()).getValue();
        LOGGER.info(id);

        Stage getAttributesStage = new Stage();
        getAttributesStage.setTitle(String.format("Attributes List - %s (%s)", id, repository.getRepositoryName()));
        Resources resources = new Resources();
        VBox loginPane = (VBox) resources.loadFXML("/nl/bos/views/GetAttributes.fxml");
        Scene scene = new Scene(loginPane);
        getAttributesStage.setScene(scene);
        GetAttributes controller = resources.getFxmlLoader().getController();
        controller.dumpObject(id);
        getAttributesStage.showAndWait();
    }
}
