package nl.bos.menu.menuitem.action;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import nl.bos.Repository;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BrowseRepositoryAction {
    private static final Logger LOGGER = Logger.getLogger(BrowseRepositoryAction.class.getName());

    private final Repository repository = Repository.getInstance();

    public BrowseRepositoryAction() {
        Stage browseRepositoryStage = new Stage();
        browseRepositoryStage.initModality(Modality.APPLICATION_MODAL);
        browseRepositoryStage.setTitle(String.format("Repository Browser - %s (%s)", repository.getRepositoryName(), repository.getUserName()));
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/nl/bos/views/RepositoryBrowser.fxml"));
        try {
            VBox repositoryBrowser = fxmlLoader.load();
            browseRepositoryStage.setScene(new Scene(repositoryBrowser));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        browseRepositoryStage.showAndWait();
    }
}
