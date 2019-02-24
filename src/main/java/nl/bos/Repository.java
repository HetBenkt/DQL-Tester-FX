package nl.bos;

import com.documentum.com.DfClientX;
import com.documentum.com.IDfClientX;
import com.documentum.fc.client.*;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfLoginInfo;

import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Repository {
    private static final Logger LOGGER = Logger.getLogger(Repository.class.getName());

    public String getErrorMessage() {
        return errorMessage;
    }

    public IDfSession getSession() {
        return session;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public String getUserName() {
        return userName;
    }

    public IDfClient getClient() {
        return client;
    }

    private static Repository repository;
    private String errorMessage = "";
    private IDfSessionManager sessionManager;
    private IDfSession session;
    private String repositoryName;
    private String userName;
    private String passkey;
    private String domain;
    private final IDfClientX clientX = new DfClientX();
    private IDfClient client = null;

    private Repository() {
    }

    public static synchronized Repository getInstance() {
        if (repository == null) {
            repository = new Repository();
        }
        return repository;
    }

    public void disconnect() {
        if (repository != null && session != null && session.isConnected()) {
            sessionManager.release(session);
            sessionManager.clearIdentity(repositoryName);
        }
    }

    public IDfDocbaseMap obtainRepositoryMap() {
        IDfDocbaseMap docbaseMap = null;
        try {
            if (client == null)
                client = clientX.getLocalClient();
            docbaseMap = client.getDocbaseMap();
        } catch (DfException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return docbaseMap;
    }

    public void setCredentials(String repositoryName, String userName, String passkey, String domain) {
        this.repositoryName = repositoryName;
        this.userName = userName;
        this.passkey = passkey;
        this.domain = domain;
    }

    public IDfTypedObject obtainServerMap(String selectedRepository) {
        IDfTypedObject serverMap = null;
        try {
            if (client == null)
                client = clientX.getLocalClient();
            serverMap = client.getServerMap(selectedRepository);
        } catch (DfException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return serverMap;
    }

    private void createSession() {
        try {
            session = sessionManager.getSession(repositoryName);
        } catch (DfServiceException e) {
            LOGGER.finest(e.getMessage());
            errorMessage = e.getMessage();
            sessionManager.clearIdentity(repositoryName);
        }
    }

    public void setClient() {
        try {
            client = clientX.getLocalClient();
        } catch (DfException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public void createSessionManager() {
        try {
            if (sessionManager == null) {
                client = clientX.getLocalClient();
                sessionManager = client.newSessionManager();
            }

            IDfLoginInfo loginInfoObj = clientX.getLoginInfo();
            loginInfoObj.setUser(userName);
            loginInfoObj.setPassword(passkey);
            loginInfoObj.setDomain(domain);

            sessionManager.setIdentity(repositoryName, loginInfoObj);
        } catch (DfException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public boolean isConnectionValid() {
        createSession();
        return session != null && session.isConnected();
    }

    public IDfCollection query(String query) {
        IDfQuery q = clientX.getQuery();
        q.setDQL(query);

        IDfCollection collection = null;
        try {
            collection = q.execute(session, IDfQuery.DF_READ_QUERY);
        } catch (DfException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        String message = MessageFormat.format("Query executed: {0}", query);
        LOGGER.finest(message);

        return collection;
    }
}
