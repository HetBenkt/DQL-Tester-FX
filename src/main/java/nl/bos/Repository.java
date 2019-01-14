package nl.bos;

import com.documentum.com.DfClientX;
import com.documentum.com.IDfClientX;
import com.documentum.fc.client.*;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfLoginInfo;

import java.text.MessageFormat;
import java.util.logging.Logger;

public class Repository {
    private static final Logger log = Logger.getLogger(Repository.class.getName());

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

    public IDfDocbaseMap obtainRepositoryMap() throws DfException {
        if (client == null)
            client = clientX.getLocalClient();
        return client.getDocbaseMap();
    }

    public void setCredentials(String repositoryName, String userName, String passkey, String domain) {
        this.repositoryName = repositoryName;
        this.userName = userName;
        this.passkey = passkey;
        this.domain = domain;
    }

    public IDfTypedObject obtainServerMap(String selectedRepository) throws DfException {
        if (client == null)
            client = clientX.getLocalClient();
        return client.getServerMap(selectedRepository);
    }

    private void createSession() {
        try {
            session = sessionManager.getSession(repositoryName);
        } catch (DfServiceException e) {
            log.finest(e.getMessage());
            errorMessage = e.getMessage();
            sessionManager.clearIdentity(repositoryName);
        }
    }

    public void setClient() throws DfException {
        client = clientX.getLocalClient();
    }

    public void createSessionManager() throws DfException {
        if (sessionManager == null) {
            client = clientX.getLocalClient();
            sessionManager = client.newSessionManager();
        }

        IDfLoginInfo loginInfoObj = clientX.getLoginInfo();
        loginInfoObj.setUser(userName);
        loginInfoObj.setPassword(passkey);
        loginInfoObj.setDomain(domain);

        sessionManager.setIdentity(repositoryName, loginInfoObj);
    }

    public boolean isConnectionValid() {
        createSession();
        return session != null && session.isConnected();
    }

    public IDfCollection query(String query) throws DfException {
        IDfQuery q = clientX.getQuery();
        q.setDQL(query);

        IDfCollection collection = q.execute(session, IDfQuery.DF_READ_QUERY);
        String message = MessageFormat.format("Query executed: {0}", query);
        log.finest(message);

        return collection;
    }
}
