package nl.bos.menu.menuitem.action;

import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import nl.bos.Repository;
import nl.bos.utils.Resources;

import java.util.logging.Logger;

public class ManageJobsAction {
    private static final Logger LOGGER = Logger.getLogger(ManageJobsAction.class.getName());

    public ManageJobsAction() {
        Stage jobEditorStage = new Stage();
        Repository repository = Repository.getInstance();
        jobEditorStage.setTitle(String.format("Job Editor - %s", repository.getRepositoryName()));

        Resources resources = new Resources();
        VBox jobEditor = (VBox) resources.loadFXML("/nl/bos/views/JobEditor.fxml");
        jobEditorStage.setScene(new Scene(jobEditor));
        jobEditorStage.showAndWait();
    }
}
