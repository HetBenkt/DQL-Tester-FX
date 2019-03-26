package nl.bos.utils;

import com.documentum.fc.common.DfException;
import nl.bos.Main;
import nl.bos.Repository;
import nl.bos.controllers.QueryWithResult;

import java.util.Arrays;
import java.util.logging.Logger;

import static nl.bos.Constants.TABLE;
import static nl.bos.Constants.TYPE;

public class TableResultUtils {
    private static final Logger LOGGER = Logger.getLogger(TableResultUtils.class.getName());

    private final Repository repository = Repository.getInstance();
    private final Main main = Main.getInstance();

    public void updateTable(String type, String currentSelected) {
        try {
            switch (type) {
                case TYPE:
                    updateTableWithTypeInfo(currentSelected);
                    break;
                case TABLE:
                    updateTableWithTableInfo(currentSelected);
                    break;
                default:
                    LOGGER.info("Do nothing");
                    break;
            }
        } catch (DfException e) {
            LOGGER.finest(e.getMessage());
        }
    }

    private void updateTableWithTableInfo(String currentSelected) throws DfException {
        QueryWithResult queryWithResultController = main.getBodyPaneLoader().getController();
        String tableDescription = repository.getSession().describe(TABLE, "dm_dbo." + currentSelected);
        queryWithResultController.updateResultTableWithStringInput(tableDescription, Arrays.asList("Column", "Data Type", "Primary Key"));
    }

    private void updateTableWithTypeInfo(String currentSelected) throws DfException {
        QueryWithResult queryWithResultController = main.getBodyPaneLoader().getController();
        String typeDescription = repository.getSession().describe(TYPE, currentSelected);
        queryWithResultController.updateResultTableWithStringInput(typeDescription, Arrays.asList("Attribute", "Data Type", "Repeating"));
    }
}
