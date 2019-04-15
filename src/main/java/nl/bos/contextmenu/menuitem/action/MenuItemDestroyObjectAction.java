package nl.bos.contextmenu.menuitem.action;

import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import nl.bos.Repository;
import nl.bos.utils.AppAlert;

import java.text.MessageFormat;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MenuItemDestroyObjectAction implements EventHandler<ActionEvent> {
    private static final Logger LOGGER = Logger.getLogger(MenuItemDestroyObjectAction.class.getName());

    private final Repository repository = Repository.getInstance();
    private final TableView result;

    public MenuItemDestroyObjectAction(MenuItem destroyObject, TableView result) {
        this.result = result;
        destroyObject.setOnAction(this);
    }

    @Override
    public void handle(ActionEvent actionEvent) {
        TablePosition focusedCell = (TablePosition) result.getSelectionModel().getSelectedCells().get(0);
        String id = (String) focusedCell.getTableColumn().getCellObservableValue(focusedCell.getRow()).getValue();

        try {
            LOGGER.info(id);

            String message = MessageFormat.format("Are you sure you want to destroy the selected object id ''{0}''?", id);
            Optional<ButtonType> deleteObjectConfirmation = AppAlert.warningWithResponse("Delete Object", message);

            deleteObjectConfirmation.ifPresent(type -> {
                if (type.getButtonData() == ButtonBar.ButtonData.YES) {
                    LOGGER.info("Deleting the object!");
                    try {
                        IDfPersistentObject object = repository.getSession().getObject(new DfId(id));
                        object.destroy();
                        String messageConfirmation = MessageFormat.format("Succesfully destroyed the object id ''{0}''!", id);
                        AppAlert.confirmation("Confirmation delete object", messageConfirmation);
                    } catch (DfException e) {
                        LOGGER.log(Level.SEVERE, e.getMessage(), e);
                        AppAlert.error("Error on delete object", e.getMessage());
                    }
                } else {
                    LOGGER.info("Object deletion cancelled!");
                }
            });
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}
