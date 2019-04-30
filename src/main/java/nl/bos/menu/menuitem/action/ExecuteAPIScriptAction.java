package nl.bos.menu.menuitem.action;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import nl.bos.Repository;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExecuteAPIScriptAction {
    private static final Logger LOGGER = Logger.getLogger(ExecuteAPIScriptAction.class.getName());

    public ExecuteAPIScriptAction() {
        Stage apiScriptStage = new Stage();
        Repository repository = Repository.getInstance();
        apiScriptStage.setTitle(String.format("Execute API Script - %s", repository.getRepositoryName()));

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/nl/bos/views/ExecuteAPIScript.fxml"));

        try {
            VBox apiScriptPane = fxmlLoader.load();
            apiScriptStage.setScene(new Scene(apiScriptPane));

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        apiScriptStage.showAndWait();
    }
}
