package nl.bos.menu.menuitem.action;

import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import nl.bos.Repository;
import nl.bos.utils.Resources;

import java.util.logging.Logger;

public class ExecuteDQLScriptAction {
    private static final Logger LOGGER = Logger.getLogger(ExecuteDQLScriptAction.class.getName());

    public ExecuteDQLScriptAction() {
        Stage dqlScriptStage = new Stage();
        Repository repository = Repository.getInstance();
        dqlScriptStage.setTitle(String.format("Execute DQL Script - %s", repository.getRepositoryName()));

        Resources resources = new Resources();
        AnchorPane dqlScriptPane = (AnchorPane) resources.loadFXML("/nl/bos/views/ExecuteDQLScript.fxml");

        dqlScriptStage.setScene(new Scene(dqlScriptPane));
        dqlScriptStage.showAndWait();
    }
}
