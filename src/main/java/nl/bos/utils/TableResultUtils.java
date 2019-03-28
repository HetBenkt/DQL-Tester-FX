package nl.bos.utils;

import com.documentum.fc.common.DfException;
import nl.bos.Repository;
import nl.bos.controllers.QueryWithResult;

import java.util.Arrays;
import java.util.logging.Logger;

import static nl.bos.Constants.TABLE;
import static nl.bos.Constants.TYPE;

public class TableResultUtils {
    private static final Logger LOGGER = Logger.getLogger(TableResultUtils.class.getName());

    private final Repository repository = Repository.getInstance();

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
        String tableDescription = repository.getSession().describe(TABLE, "dm_dbo." + currentSelected);

        QueryWithResult queryWithResultController = (QueryWithResult) Controllers.get(QueryWithResult.class.getSimpleName());
        queryWithResultController.updateResultTableWithStringInput(tableDescription, Arrays.asList("Column", "Data Type", "Primary Key"));
    }

    private void updateTableWithTypeInfo(String currentSelected) throws DfException {
        String typeDescription = repository.getSession().describe(TYPE, currentSelected);

        QueryWithResult queryWithResultController = (QueryWithResult) Controllers.get(QueryWithResult.class.getSimpleName());
        queryWithResultController.updateResultTableWithStringInput(typeDescription, Arrays.asList("Attribute", "Data Type", "Repeating"));
    }
}
