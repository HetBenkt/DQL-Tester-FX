package nl.bos.menu.menuitem.action;

import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import nl.bos.Repository;
import nl.bos.utils.Resources;

import java.util.logging.Logger;

public class BrowseRepositoryAction {
    private static final Logger LOGGER = Logger.getLogger(BrowseRepositoryAction.class.getName());

    public BrowseRepositoryAction() {
        Stage browseRepositoryStage = new Stage();
        browseRepositoryStage.initModality(Modality.APPLICATION_MODAL);
        Repository repository = Repository.getInstance();
        browseRepositoryStage.setTitle(String.format("Repository Browser - %s (%s)", repository.getRepositoryName(), repository.getUserName()));

        Resources resources = new Resources();
        VBox repositoryBrowser = (VBox) resources.loadFXML("/nl/bos/views/RepositoryBrowser.fxml");
        browseRepositoryStage.setScene(new Scene(repositoryBrowser));
        browseRepositoryStage.showAndWait();
    }
}
