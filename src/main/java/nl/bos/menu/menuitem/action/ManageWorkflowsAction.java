package nl.bos.menu.menuitem.action;

import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import nl.bos.Repository;
import nl.bos.utils.Resources;

import java.util.logging.Logger;

import static nl.bos.Constants.ROOT_SCENE_CSS;

public class ManageWorkflowsAction {
    private static final Logger LOGGER = Logger.getLogger(ManageWorkflowsAction.class.getName());

    public ManageWorkflowsAction() {
        Stage manageWorkflowsStage = new Stage();
        Repository repository = Repository.getInstance();
        manageWorkflowsStage.setTitle(String.format("Workflow Editor - %s", repository.getRepositoryName()));

        Resources resources = new Resources();
        VBox workflowEditor = (VBox) resources.loadFXML("/nl/bos/views/WorkflowEditor.fxml");
        manageWorkflowsStage.setScene(new Scene(workflowEditor));
        manageWorkflowsStage.getScene().getStylesheets()
                .addAll(ROOT_SCENE_CSS);

        manageWorkflowsStage.showAndWait();
    }
}
