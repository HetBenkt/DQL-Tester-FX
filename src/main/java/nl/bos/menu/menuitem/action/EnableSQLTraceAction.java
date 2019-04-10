package nl.bos.menu.menuitem.action;

import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfList;
import com.documentum.fc.common.IDfList;
import nl.bos.Repository;
import nl.bos.utils.AppAlert;

import java.util.logging.Level;
import java.util.logging.Logger;

public class EnableSQLTraceAction {
    private static final Logger LOGGER = Logger.getLogger(ManageJobsAction.class.getName());

    public EnableSQLTraceAction() {
        try {
            IDfList args = new DfList();
            args.append("OPTION");
            args.append("VALUE");
            IDfList dataType = new DfList();
            dataType.append("S");
            dataType.append("B");
            IDfList values = new DfList();
            values.append("sqltrace");
            values.append("T");
            Repository repository = Repository.getInstance();
            IDfCollection setOptions = repository.getSession().apply(null, "SET_OPTIONS", args, dataType, values);
            setOptions.next();
            if (setOptions.getBoolean("result")) {
                AppAlert.information("SQL Trace is enabled", "Please check the repository log for the enabled trace");
            }
            setOptions.close();
        } catch (DfException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}
