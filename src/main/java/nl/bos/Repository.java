package nl.bos;

import com.documentum.com.DfClientX;
import com.documentum.com.IDfClientX;
import com.documentum.fc.client.*;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfLoginInfo;
import lombok.extern.java.Log;

import java.text.MessageFormat;

import static nl.bos.ICredentials.*;

@Log
public class Repository {
    private static Repository repository;
    private IDfSessionManager sMgr;
    private IDfSession session;

    private Repository() {
        try {
            this.sMgr = createSessionManager(REPO_NAME, REPO_USERNAME, REPO_PASSKEY, null);
            this.session = createSession(REPO_NAME);
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
        if (repository.session != null && repository.session.isConnected()) {
            repository.session.disconnect();
        }
    }

    private IDfSessionManager createSessionManager(String repoName, String repoUsername, String repoPasskey, String repoDomain) throws DfException {
        IDfClientX clientx = new DfClientX();
        IDfClient client = clientx.getLocalClient();

        IDfSessionManager sMgr = client.newSessionManager();

        IDfLoginInfo loginInfoObj = clientx.getLoginInfo();
        loginInfoObj.setUser(repoUsername);
        loginInfoObj.setPassword(repoPasskey);
        loginInfoObj.setDomain(repoDomain);

        sMgr.setIdentity(repoName, loginInfoObj);
        return sMgr;
    }

    private IDfSession createSession(String repoName) throws DfException {
        return this.sMgr.getSession(repoName);
    }

    public IDfCollection query(String query) throws DfException {
        IDfClientX clientx = new DfClientX();
        IDfQuery q = clientx.getQuery();
        q.setDQL(query);

        IDfCollection collection = q.execute(repository.session, IDfQuery.DF_READ_QUERY);
        log.info(MessageFormat.format("Query executed %s", query));

        return collection;
    }
}
