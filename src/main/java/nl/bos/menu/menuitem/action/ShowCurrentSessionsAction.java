package nl.bos.menu.menuitem.action;

import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.common.DfException;
import nl.bos.Main;
import nl.bos.Repository;
import nl.bos.controllers.QueryWithResult;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ShowCurrentSessionsAction {
    private static final Logger LOGGER = Logger.getLogger(ManageJobsAction.class.getName());

    private final Repository repository = Repository.getInstance();

    public ShowCurrentSessionsAction() {
        try {
            IDfCollection showSessions = repository.query("EXECUTE show_sessions");
            QueryWithResult queryWithResultController = Main.getInstance().getBodyPaneLoader().getController();
            queryWithResultController.updateResultTable(showSessions);
            showSessions.close();
        } catch (DfException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

    }
}
