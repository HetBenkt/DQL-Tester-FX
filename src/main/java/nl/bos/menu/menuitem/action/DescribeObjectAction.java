package nl.bos.menu.menuitem.action;

import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import nl.bos.DescribeObjectTreeItem;
import nl.bos.controllers.DescribeObject;
import nl.bos.utils.Resources;
import nl.bos.utils.TableResultUtils;

import java.text.MessageFormat;
import java.util.logging.Logger;

public class DescribeObjectAction implements EventHandler<WindowEvent> {
    private static final Logger LOGGER = Logger.getLogger(DescribeObjectAction.class.getName());
    private final Stage describeObjectStage = new Stage();
    private Resources resources = new Resources();

    @Override
    public void handle(WindowEvent windowEvent) {
        DescribeObject describeObjectController = resources.getFxmlLoader().getController();
        DescribeObjectTreeItem currentSelected = describeObjectController.getCurrentSelected();

        if (currentSelected != null) {
            String currentSelectedValue = (String) currentSelected.getValue();
            String type = currentSelected.getType();
            String message = MessageFormat.format("Selected item ''{0}'' of type ''{1}''", currentSelected, type);
            LOGGER.info(message);
            new TableResultUtils().updateTable(type, currentSelectedValue);
        }
    }

    public void execute() {
        describeObjectStage.setTitle("Describe object");
        HBox describeObject = (HBox) resources.loadFXML("/nl/bos/views/DescribeObject.fxml");
        describeObjectStage.setScene(new Scene(describeObject));
        describeObjectStage.setOnCloseRequest(this);

        DescribeObject describeObjectController = resources.getFxmlLoader().getController();
        describeObjectController.setStage(describeObjectStage);
        describeObjectController.initialize();
        describeObjectStage.showAndWait();
    }
}
