package nl.bos.menu.menuitem.action;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import nl.bos.Repository;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExecuteDQLScriptAction {
    private static final Logger LOGGER = Logger.getLogger(ExecuteDQLScriptAction.class.getName());

    public ExecuteDQLScriptAction() {
        Stage dqlScriptStage = new Stage();
        Repository repository = Repository.getInstance();
        dqlScriptStage.setTitle(String.format("Execute DQL Script - %s", repository.getRepositoryName()));

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/nl/bos/views/ExecuteDQLScript.fxml"));

        try {
            AnchorPane dqlScriptPane = fxmlLoader.load();
            dqlScriptStage.setScene(new Scene(dqlScriptPane));

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        dqlScriptStage.showAndWait();
    }
}
