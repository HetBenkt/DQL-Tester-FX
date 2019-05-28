package nl.bos.menu.menuitem.action;

import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import nl.bos.Repository;
import nl.bos.utils.Resources;

import java.util.logging.Logger;

public class ExecuteAPIScriptAction {
    private static final Logger LOGGER = Logger.getLogger(ExecuteAPIScriptAction.class.getName());

    public ExecuteAPIScriptAction() {
        Stage apiScriptStage = new Stage();
        Repository repository = Repository.getInstance();
        apiScriptStage.setTitle(String.format("Execute API Script - %s", repository.getRepositoryName()));

        Resources resources = new Resources();
        VBox apiScriptPane = (VBox) resources.loadFXML("/nl/bos/views/ExecuteAPIScript.fxml");

        apiScriptStage.setScene(new Scene(apiScriptPane));
        apiScriptStage.showAndWait();
    }
}
