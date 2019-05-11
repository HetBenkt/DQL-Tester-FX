package nl.bos.contextmenu.menuitem.action;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import nl.bos.Repository;
import nl.bos.controllers.GetAttributes;

import java.util.logging.Level;
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

        try {
            LOGGER.info(id);
            Stage getAttributesStage = new Stage();
            getAttributesStage.setTitle(String.format("Attributes List - %s (%s)", id, repository.getRepositoryName()));
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/nl/bos/views/GetAttributes.fxml"));
            VBox loginPane = fxmlLoader.load();
            Scene scene = new Scene(loginPane);
            getAttributesStage.setScene(scene);
            GetAttributes controller = fxmlLoader.getController();
            controller.dumpObject(id);
            getAttributesStage.showAndWait();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}
