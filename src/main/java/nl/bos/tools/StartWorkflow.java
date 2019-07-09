package nl.bos.tools;

import com.documentum.com.DfClientX;
import com.documentum.com.IDfClientX;
import com.documentum.fc.client.IDfClient;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfId;
import com.documentum.fc.common.IDfLoginInfo;

import java.util.logging.Level;
import java.util.logging.Logger;

abstract class StartWorkflow {
    static final Logger LOGGER = Logger.getLogger(StartWorkflow.class.getName());
    IDfSession session = null;

    StartWorkflow(String repository, String username, String password) {
        IDfSessionManager sessionManager = null;

        try {
            sessionManager = initSessionManager(repository, username, password);
            session = sessionManager.getSession(repository);

            LOGGER.info("Session created, now building the workflow");

            IDfId workflowId = startWorkflow();

            String message = String.format("Workflow is running with id: %s", workflowId.getId());
            LOGGER.info(message);
        } catch (DfException dfe) {
            LOGGER.log(Level.SEVERE, dfe.getMessage(), dfe);
        } finally {
            if (sessionManager != null)
                sessionManager.release(session);
        }
    }

    protected abstract IDfId startWorkflow() throws DfException;

    IDfSessionManager initSessionManager(String repository, String username, String password) throws DfException {
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

