package nl.bos.menu.menuitem.action;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import nl.bos.Repository;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ManageJobsAction {
    private static final Logger LOGGER = Logger.getLogger(ManageJobsAction.class.getName());

    private final Repository repository = Repository.getInstance();

    public ManageJobsAction() {
        Stage jobEditorStage = new Stage();
        jobEditorStage.setTitle(String.format("Job Editor - %s", repository.getRepositoryName()));
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/nl/bos/views/JobEditor.fxml"));
        try {
            VBox jobEditor = fxmlLoader.load();
            jobEditorStage.setScene(new Scene(jobEditor));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        jobEditorStage.showAndWait();
    }
}
