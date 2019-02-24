package nl.bos.utils;

import com.documentum.fc.common.DfException;
import nl.bos.Main;
import nl.bos.Repository;
import nl.bos.controllers.BodyPane;

import java.util.Arrays;
import java.util.logging.Logger;

import static nl.bos.Constants.TABLE;
import static nl.bos.Constants.TYPE;

public class TableResultUtils {
    private static final Logger LOGGER = Logger.getLogger(TableResultUtils.class.getName());
    private final Repository repositoryCon = Repository.getInstance();
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
        BodyPane bodyPaneController = main.getBodyPaneLoader().getController();
        String tableDesciption = repositoryCon.getSession().describe(TABLE, "dm_dbo." + currentSelected);
        bodyPaneController.updateResultTableWithStringInput(tableDesciption, Arrays.asList("Column", "Data Type", "Primary Key"));
    }

    private void updateTableWithTypeInfo(String currentSelected) throws DfException {
        BodyPane bodyPaneController = main.getBodyPaneLoader().getController();
        String typeDesciption = repositoryCon.getSession().describe(TYPE, currentSelected);
        bodyPaneController.updateResultTableWithStringInput(typeDesciption, Arrays.asList("Attribute", "Data Type", "Repeating"));
    }
}
