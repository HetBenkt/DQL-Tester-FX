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
import nl.bos.controllers.ResultTable;
import nl.bos.utils.Resources;

import java.util.logging.Logger;

import static nl.bos.Constants.ROOT_SCENE_CSS;

public class MenuItemResultTableAction implements EventHandler<ActionEvent> {
    private static final Logger LOGGER = Logger.getLogger(MenuItemResultTableAction.class.getName());

    private final Repository repository = Repository.getInstance();
    private final TableView result;
    private String label;

    public MenuItemResultTableAction(MenuItem resultTableItem, TableView result, String label) {
        this.result = result;
        resultTableItem.setOnAction(this);
        this.label = label;
    }

    @Override
    public void handle(ActionEvent actionEvent) {
        LOGGER.info(actionEvent.getSource().toString());

        TablePosition focusedCell = (TablePosition) result.getSelectionModel().getSelectedCells().get(0);
        String id = (String) focusedCell.getTableColumn().getCellObservableValue(focusedCell.getRow()).getValue();
        LOGGER.info(id);

        Stage resultStage = new Stage();
        resultStage.setTitle(String.format("%s - %s (%s)", label, repository.getObjectName(id), repository.getRepositoryName()));
        Resources resources = new Resources();
        VBox resultPane = (VBox) resources.loadFXML("/nl/bos/views/ResultTable.fxml");
        resultStage.setScene(new Scene(resultPane));
        resultStage.getScene().getStylesheets()
                .addAll(ROOT_SCENE_CSS);

        ResultTable controller = resources.getFxmlLoader().getController();
        controller.loadResult(id);
        resultStage.showAndWait();
    }
}
