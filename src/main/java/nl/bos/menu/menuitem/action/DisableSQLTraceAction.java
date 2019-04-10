package nl.bos.menu.menuitem.action;

import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.commands.admin.IDfAdminCommand;
import com.documentum.fc.commands.admin.impl.AdminApplyCommand;
import com.documentum.fc.common.DfException;
import nl.bos.Repository;
import nl.bos.utils.AppAlert;

import java.util.logging.Level;
import java.util.logging.Logger;

public class DisableSQLTraceAction {
    private static final Logger LOGGER = Logger.getLogger(ManageJobsAction.class.getName());

    public DisableSQLTraceAction() {
        try {
            IDfAdminCommand command = AdminApplyCommand.getCommand(IDfAdminCommand.APPLY_SET_OPTIONS);
            command.setString("OPTION", "sqltrace");
            command.setBoolean("VALUE", false);
            Repository repository = Repository.getInstance();
            IDfCollection execute = command.execute(repository.getSession());
            execute.next();
            if (execute.getBoolean("result")) {
                AppAlert.information("SQL Trace is disabled", "Please check the repository log for the disabled trace");
            }
            execute.close();
        } catch (DfException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}
