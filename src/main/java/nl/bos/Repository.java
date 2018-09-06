package nl.bos;

import com.documentum.com.DfClientX;
import com.documentum.com.IDfClientX;
import com.documentum.fc.client.*;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfLoginInfo;
import lombok.extern.java.Log;

@Log
public class Repository {
    private static Repository repository;
    private IDfSessionManager sMgr;
    private IDfSession session;
    private static String repositoryName;
    private static String userName;
    private static String passkey;
    private static String domain;

    private Repository() {
        try {
            this.sMgr = createSessionManager(repositoryName, userName, passkey, domain);
            this.session = createSession(repositoryName);
        } catch (DfException e) {
            log.info(e.getMessage());
        }
    }

    public static synchronized Repository getRepositoryCon() {
        if (repository == null) {
            repository = new Repository();
        }
        return repository;
    }

    public static void disconnect() throws DfException {
        if (repository != null && repository.session != null && repository.session.isConnected()) {
            repository.session.disconnect();
        }
    }

    public static IDfDocbaseMap obtainRepositoryMap() throws DfException {
        IDfClientX clientx = new DfClientX();
        IDfClient client = clientx.getLocalClient();
        return client.getDocbaseMap();
    }

    public static void setCredentials(String repositoryName, String userName, String passkey, String domain) {
        Repository.repositoryName = repositoryName;
        Repository.userName = userName;
        Repository.passkey = passkey;
        Repository.domain = domain;
    }

    private IDfSession createSession(String repoName) throws DfException {
        return this.sMgr.getSession(repoName);
    }

    private IDfSessionManager createSessionManager(String repoName, String repoUsername, String repoPasskey, String repoDomain) throws DfException {
        IDfClientX clientx = new DfClientX();
        IDfClient client = clientx.getLocalClient();

        IDfSessionManager newSessionManager = client.newSessionManager();

        IDfLoginInfo loginInfoObj = clientx.getLoginInfo();
        loginInfoObj.setUser(repoUsername);
        loginInfoObj.setPassword(repoPasskey);
        loginInfoObj.setDomain(repoDomain);

        newSessionManager.setIdentity(repoName, loginInfoObj);
        return newSessionManager;
    }

    public IDfCollection query(String query) throws DfException {
        IDfClientX clientx = new DfClientX();
        IDfQuery q = clientx.getQuery();
        q.setDQL(query);

        IDfCollection collection = q.execute(repository.session, IDfQuery.DF_READ_QUERY);
        log.info(String.format("Query executed %s", query));

        return collection;
    }
}
