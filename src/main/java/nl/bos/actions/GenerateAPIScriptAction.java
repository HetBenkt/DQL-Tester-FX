package nl.bos.actions;

import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import nl.bos.Repository;
import nl.bos.controllers.GenerateAPIScript;
import nl.bos.utils.Resources;

import java.util.logging.Logger;

import static nl.bos.Constants.ROOT_SCENE_CSS;

public class GenerateAPIScriptAction {
    private static final Logger LOGGER = Logger.getLogger(GenerateAPIScriptAction.class.getName());

    public GenerateAPIScriptAction(TableView result) {
        Stage generateAPIScriptStage = new Stage();
        generateAPIScriptStage.setTitle(String.format("Generate API script (%s)", Repository.getInstance().getRepositoryName()));
        Resources resources = new Resources();
        VBox loginPane = (VBox) resources.loadFXML("/nl/bos/views/GenerateAPIScript.fxml");
        generateAPIScriptStage.setScene(new Scene(loginPane));
        generateAPIScriptStage.getScene().getStylesheets()
                .addAll(ROOT_SCENE_CSS);
        GenerateAPIScript controller = resources.getFxmlLoader().getController();
        controller.injectData(result);
        generateAPIScriptStage.showAndWait();
    }
}
