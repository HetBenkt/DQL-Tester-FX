package nl.bos;

import com.documentum.com.DfClientX;
import com.documentum.com.IDfClientX;
import com.documentum.fc.client.*;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.IDfLoginInfo;
import nl.bos.utils.AppAlert;

import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static nl.bos.Constants.MSG_TITLE_INFO_DIALOG;

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
    private String secureMode;
    private final IDfClientX clientX;
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
		clientX = new DfClientX();
    }

    public static synchronized Repository getInstance() {
        if (repository == null) {
            repository = new Repository();
        }
        return repository;
    }

    public void disconnect() {
        if (repository != null && isConnected()) {
            sessionManager.release(session);
            sessionManager.clearIdentity(repositoryName);
        }
    }

    public IDfDocbaseMap obtainRepositoryMap() {
        IDfDocbaseMap repositoryMap = null;

        try {
            if (client == null) {
				client = clientX.getLocalClient();
			}

            repositoryMap = client.getDocbaseMap();

        } catch (DfException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        return repositoryMap;
    }

    public void setCredentials(String repositoryName, String userName, String passkey, String domain) {
        setCredentials(repositoryName, userName, passkey, domain, "default");
    }

    public void setCredentials(String repositoryName, String userName, String passkey, String domain, String secureMode) {
        this.repositoryName = repositoryName;
        this.userName = userName;
        this.passkey = passkey;
        this.domain = domain;
        switch (secureMode == null ? "" : secureMode) {
            case "default":
                this.secureMode = null;
                break;
            case "native":
                this.secureMode = IDfLoginInfo.SECURITY_MODE_NATIVE;
                break;
            case "secure":
                this.secureMode = IDfLoginInfo.SECURITY_MODE_SECURE;
                break;
            case "try_native_first":
                this.secureMode = IDfLoginInfo.SECURITY_MODE_TRY_NATIVE_FIRST;
                break;
            case "try_secure_first":
                this.secureMode = IDfLoginInfo.SECURITY_MODE_TRY_SECURE_FIRST;
                break;
            default:
                this.secureMode = null;
        }
    }

    public IDfTypedObject obtainServerMap(String selectedRepository) {
        IDfTypedObject serverMap = null;

        try {
            if (client == null) {
				client = clientX.getLocalClient();
			}

            serverMap = client.getServerMap(selectedRepository);

        } catch (DfException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        return serverMap;
    }

    public void createSession() {
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

            if (secureMode != null) {
                loginInfoObj.setSecurityMode(secureMode);
            }

            sessionManager.setIdentity(repositoryName, loginInfoObj);

        } catch (DfException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public IDfCollection query(String query) {
        IDfQuery q = clientX.getQuery();
        q.setDQL(query);

        IDfCollection collection = null;

        try {
            collection = q.execute(session, IDfQuery.DF_READ_QUERY);

        } catch (DfException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            AppAlert.warning(MSG_TITLE_INFO_DIALOG, e.getMessage());
        }

        String message = MessageFormat.format("Query executed: {0}", query);
        LOGGER.finest(message);

        return collection;
    }

    public boolean isTypeName(String name) {
    	return hasResults(String.format("SELECT 1 FROM dm_type WHERE name = '%s'", name));
    }

	public boolean isTableName(String name) {
		return hasResults(String.format("SELECT 1 FROM dm_registered WHERE object_name = '%s'", name));
    }

	private boolean hasResults(String dqlStatement) {
		IDfCollection collection = null;

		try {
			collection = query(dqlStatement);
			return collection.next();

		} catch (DfException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);

		} finally {
			try {
				assert collection != null;
				collection.close();

			} catch (DfException ignored) {
			    //ignored
			}
		}

		return false;
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

	public IDfPersistentObject getObjectById(String objectId) throws DfException {
		return session.getObject(new DfId(objectId));
	}
}
