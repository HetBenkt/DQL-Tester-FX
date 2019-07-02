package nl.bos.tools;

import com.documentum.com.DfClientX;
import com.documentum.com.IDfClientX;
import com.documentum.fc.client.*;
import com.documentum.fc.common.*;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StartWorkflow {
    private static final Logger LOGGER = Logger.getLogger(StartWorkflow.class.getName());

    private StartWorkflow(String repository, String username, String password) {
        IDfSessionManager sessionManager = null;
        IDfSession session = null;

        try {
            sessionManager = initSessionManager(repository, username, password);
            session = sessionManager.getSession(repository);

            LOGGER.info("Session created, now building the workflow");
            IDfWorkflowBuilder wfBldrObj = session.newWorkflowBuilder(new DfId("4b12d591800015e0")); //dm_process.r_object_id
            wfBldrObj.initWorkflow();
            IDfId workflowId = wfBldrObj.runWorkflow();

            IDfList objList = new DfList();
            IDfSysObject sysObject = (IDfSysObject) session.newObject("dm_sysobject");
            sysObject.setObjectName(String.format("test_%s", String.valueOf(new Date().getTime())));
            sysObject.setSubject("process_test");
            sysObject.save();
            //select r_object_id, object_name, subject from dm_sysobject where subject = 'process_test'
            //delete dm_sysobject objects where subject = 'process_test'

            objList.append(sysObject.getObjectId());
            wfBldrObj.addPackage("Auto-Activity-1", "Input:0", "Package0",
                    "dm_sysobject", null, false, objList);
            String message = String.format("Workflow is running with id: %s", workflowId.getId());
            LOGGER.info(message);
        } catch (DfException dfe) {
            LOGGER.log(Level.SEVERE, dfe.getMessage(), dfe);
        } finally {
            if (sessionManager != null)
                sessionManager.release(session);
        }
    }

    public static void main(String[] args) {
        new StartWorkflow(args[0], args[1], args[2]);
    }

    private IDfSessionManager initSessionManager(String repository, String username, String password) throws DfException {
        IDfClientX clientx = new DfClientX();
        IDfClient client = clientx.getLocalClient();

        IDfSessionManager sessionManager = client.newSessionManager();

        IDfLoginInfo loginInfoObj = clientx.getLoginInfo();
        loginInfoObj.setUser(username);
        loginInfoObj.setPassword(password);
        loginInfoObj.setDomain(null);

        sessionManager.setIdentity(repository, loginInfoObj);
        return sessionManager;
    }
}
