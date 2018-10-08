package nl.bos;

import com.documentum.com.DfClientX;
import com.documentum.com.IDfClientX;
import com.documentum.fc.client.*;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfLoginInfo;
import lombok.Getter;
import lombok.extern.java.Log;

@Log
public class Repository {
    private static Repository repository;
    @Getter
    private String errorMessage = "";
    private IDfSessionManager sessionManager;
    @Getter
    private IDfSession session;
    @Getter
    private String repositoryName;
    @Getter
    private String userName;
    private String passkey;
    private String domain;
    private static IDfClientX clientx = new DfClientX();
    private static IDfClient client = null;


    private Repository() {
    }

    public static synchronized Repository getRepositoryCon() {
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
            client = clientx.getLocalClient();
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
            client = clientx.getLocalClient();
        return client.getServerMap(selectedRepository);
    }

    public void createSession() {
        try {
            session = sessionManager.getSession(repositoryName);
        } catch (DfServiceException e) {
            log.finest(e.getMessage());
            errorMessage = e.getMessage();
            sessionManager.clearIdentity(repositoryName);
        }
    }

    public void createSessionManager() throws DfException {
        if (sessionManager == null) {
            client = clientx.getLocalClient();
            sessionManager = client.newSessionManager();
        }

        IDfLoginInfo loginInfoObj = clientx.getLoginInfo();
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
        IDfQuery q = clientx.getQuery();
        q.setDQL(query);

        IDfCollection collection = q.execute(session, IDfQuery.DF_READ_QUERY);
        log.info(String.format("Query executed %s", query));

        return collection;
    }
}
