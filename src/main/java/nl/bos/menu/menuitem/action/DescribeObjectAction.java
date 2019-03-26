package nl.bos.menu.menuitem.action;

import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import nl.bos.controllers.DescribeObject;
import nl.bos.utils.TableResultUtils;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DescribeObjectAction implements EventHandler<WindowEvent> {
    private static final Logger LOGGER = Logger.getLogger(DescribeObjectAction.class.getName());
    private final Stage describeObjectStage = new Stage();
    private FXMLLoader fxmlLoader;

    public Stage getDescribeObjectStage() {
        return describeObjectStage;
    }

    @Override
    public void handle(WindowEvent windowEvent) {
        DescribeObject describeObjectController = fxmlLoader.getController();
        String currentSelected = (String) describeObjectController.getCurrentSelected().getValue();
        String type = describeObjectController.getCurrentSelected().getType();
        String message = MessageFormat.format("Selected item ''{0}'' of type ''{1}''", currentSelected, type);
        LOGGER.info(message);
        new TableResultUtils().updateTable(type, currentSelected);
    }

    public void execute() {
        describeObjectStage.setTitle("Describe object");
        fxmlLoader = new FXMLLoader(getClass().getResource("/nl/bos/views/DescribeObject.fxml"));
        try {
            HBox describeObject = fxmlLoader.load();
            describeObjectStage.setScene(new Scene(describeObject));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        describeObjectStage.setOnCloseRequest(this);

        DescribeObject describeObjectController = fxmlLoader.getController();
        describeObjectController.initialize();
        describeObjectStage.showAndWait();
    }
}
