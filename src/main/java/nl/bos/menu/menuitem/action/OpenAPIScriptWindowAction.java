package nl.bos.menu.menuitem.action;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OpenAPIScriptWindowAction {
    private static final Logger LOGGER = Logger.getLogger(OpenAPIScriptWindowAction.class.getName());

    public OpenAPIScriptWindowAction() {
        Stage apiScriptStage = new Stage();
        apiScriptStage.setTitle("Execute API Script");

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/nl/bos/views/APIScriptWindow.fxml"));

        try {
            AnchorPane apiScriptPane = fxmlLoader.load();
            apiScriptStage.setScene(new Scene(apiScriptPane));

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        apiScriptStage.showAndWait();
    }
}
