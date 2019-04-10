package nl.bos.contextmenu.menuitem.action;

import com.documentum.fc.common.IDfAttr;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import nl.bos.AttributeTableColumn;
import nl.bos.utils.AppAlert;
import org.apache.commons.lang.StringUtils;

import java.text.MessageFormat;
import java.util.logging.Logger;

public class MenuItemShowPropertiesAction implements EventHandler<ActionEvent> {
    private static final Logger LOGGER = Logger.getLogger(MenuItemShowPropertiesAction.class.getName());
    private final TableView result;

    private String description;

    public MenuItemShowPropertiesAction(MenuItem showProperties, TableView result) {
        this.result = result;
        showProperties.setOnAction(this);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public void handle(ActionEvent actionEvent) {
        LOGGER.info(actionEvent.getSource().toString());
        TablePosition focusedCell = (TablePosition) result.getSelectionModel().getSelectedCells().get(0);
        AttributeTableColumn tableColumn = (AttributeTableColumn) focusedCell.getTableColumn();
        IDfAttr attr = tableColumn.getAttr();

        String message;
        if (attr != null) {
            message = MessageFormat.format("Attribute Name: {0}\nData Type: {1,number,integer}\nSize: {2,number,integer}\nRepeating: {3}", attr.getName(), attr.getDataType(), attr.getLength(), String.valueOf(attr.isRepeating()));
        } else {
            int index = StringUtils.ordinalIndexOf(description, "\r\n\r\n", 2);
            message = description.substring(0, index);
        }

        AppAlert.information("Properties", message);
    }
}
