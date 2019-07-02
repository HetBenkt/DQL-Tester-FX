package nl.bos.menu.menuitem.action;

import java.util.logging.Logger;

import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import nl.bos.Repository;
import nl.bos.utils.Resources;

public class ExecuteDQLScriptAction {
    private static final Logger LOGGER = Logger.getLogger(ExecuteDQLScriptAction.class.getName());

    public ExecuteDQLScriptAction() {
        Stage dqlScriptStage = new Stage();
        Repository repository = Repository.getInstance();
        dqlScriptStage.setTitle(String.format("Execute DQL Script - %s", repository.getRepositoryName()));

        Resources resources = new Resources();
        VBox dqlScriptPane = (VBox) resources.loadFXML("/nl/bos/views/ExecuteDQLScript.fxml");

        dqlScriptStage.setScene(new Scene(dqlScriptPane));
        
		dqlScriptStage.getScene().getStylesheets().add(resources.getResourceExternalForm("/nl/bos/themes/dql-keywords.css"));
        dqlScriptStage.showAndWait();
    }
}
