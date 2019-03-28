package nl.bos.menu.menuitem.action;

import nl.bos.controllers.QueryWithResult;
import nl.bos.utils.Controllers;

public class ShowCurrentSessionsAction {
    public ShowCurrentSessionsAction() {
        QueryWithResult queryWithResultController = (QueryWithResult) Controllers.get(QueryWithResult.class.getSimpleName());
        queryWithResultController.executeQuery("EXECUTE show_sessions");
    }
}
