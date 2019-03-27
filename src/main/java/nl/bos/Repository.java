package nl.bos;

import com.documentum.com.DfClientX;
import com.documentum.com.IDfClientX;
import com.documentum.fc.client.*;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfLoginInfo;
import nl.bos.utils.AppAlert;

import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static nl.bos.Constants.NR_OF_TABLES;
import static nl.bos.Constants.NR_OF_TYPES;

public class Repository {
    private static final Logger LOGGER = Logger.getLogger(Repository.class.getName());

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
        IDfDocbaseMap repositoryMap = null;
        try {
            if (client == null)
                client = clientX.getLocalClient();
            repositoryMap = client.getDocbaseMap();
        } catch (DfException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return repositoryMap;
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
        return isConnected();
    }

    public IDfCollection query(String query) {
        IDfQuery q = clientX.getQuery();
        q.setDQL(query);

        IDfCollection collection = null;
        try {
            collection = q.execute(session, IDfQuery.DF_READ_QUERY);
        } catch (DfException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            AppAlert.warn("Information Dialog", e.getMessage());
        }
        String message = MessageFormat.format("Query executed: {0}", query);
        LOGGER.finest(message);

        return collection;
    }

    public boolean isTypeName(String name) {
        boolean result = false;
        try {
            IDfCollection nrOfTypes = repository.query(String.format("select count(r_object_id) as %s from dm_type where name = '%s'", NR_OF_TYPES, name));
            nrOfTypes.next();
            if (Integer.parseInt(nrOfTypes.getString(NR_OF_TYPES)) > 0)
                result = true;
            nrOfTypes.close();
        } catch (DfException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return result;
    }

    public boolean isTableName(String name) {
        boolean result = false;
        try {
            IDfCollection nrOfTypes = repository.query(String.format("select count(r_object_id) as %s from dm_registered where object_name = '%s'", NR_OF_TABLES, name));
            nrOfTypes.next();
            if (Integer.parseInt(nrOfTypes.getString(NR_OF_TABLES)) > 0)
                result = true;
            nrOfTypes.close();
        } catch (DfException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return result;
    }

    public boolean isObjectId(String id) {
        String regex = "^[0-9a-f]{16}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(id);
        return matcher.find();
    }

    public boolean isConnected() {
        return session != null && session.isConnected();
    }
}
