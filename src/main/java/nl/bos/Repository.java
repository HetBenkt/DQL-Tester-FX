package nl.bos;

import com.documentum.com.DfClientX;
import com.documentum.com.IDfClientX;
import com.documentum.fc.client.*;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.IDfLoginInfo;

import nl.bos.Constants.Version;
import nl.bos.utils.AppAlert;
import nl.bos.utils.Resources;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
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

	void setCredentials(String repositoryName, String userName, String passkey, String domain) {
		setCredentials(repositoryName, userName, passkey, domain, "default");
	}

	public void setCredentials(String repositoryName, String userName, String passkey, String domain,
			String secureMode) {
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
				// ignored
			}
		}

		return false;
	}

	public boolean isObjectId(String id) {
		if (id == null) {
			return false;
		}
		String regex = "[0-9a-f]{16}";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(id);
		return matcher.matches();
	}

	public boolean isConnected() {
		return session != null && session.isConnected();
	}

	public IDfPersistentObject getObjectById(String objectId) throws DfException {
		return session.getObject(new DfId(objectId));
	}

	public String getIdFromObject(IDfPersistentObject object) {
		String result = "";
		try {
			result = object.getObjectId().getId();
		} catch (DfException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
		return result;
	}

	public List<String> getFilteredUserList(String userFilter) {
		String query = String.format(
				"SELECT user_name FROM dm_user WHERE r_is_group = false AND user_name LIKE '%s%%' ORDER BY 1 ASC",
				userFilter);
		return getSingleAttributeList(query, "user_name");
	}

	public List<String> getFilteredUserList(String userFilter, String groupFilter) {
		String query = String.format(
				"SELECT DISTINCT u.user_name FROM dm_user u, dm_group g WHERE u.r_is_group = false AND u.user_name LIKE '%s%%' AND ANY g.users_names = u.user_name ORDER BY 1 ASC",
				userFilter, groupFilter);
		return getSingleAttributeList(query, "user_name");
	}

	public IDfUser getUserByName(String userName) throws DfException {
		return session.getUser(userName);
	}

	public String getDocbaseOwner() {
		IDfCollection collection = null;

		try {
			collection = query("SELECT owner_name FROM dm_docbase_config");
			collection.next();
			return collection.getString("owner_name");

		} catch (DfException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);

		} finally {
			try {
				assert collection != null;
				collection.close();

			} catch (DfException ignored) {
			}
		}

		return null;
	}

	public List<String> getAliasSets() {
		return getSingleAttributeList("SELECT object_name FROM dm_alias_set ORDER BY 1 ASC", "object_name");
	}

	public List<String> getFilteredGroupList(String groupFilter) {
		String query = String.format("SELECT group_name FROM dm_group WHERE group_name LIKE '%s%%' ORDER BY 1 ASC",
				groupFilter);
		return getSingleAttributeList(query, "group_name");
	}

	private List<String> getSingleAttributeList(String query, String attributeName) {
		List<String> result = new ArrayList<>();

		IDfCollection collection = null;

		try {
			collection = query(query);

			while (collection.next()) {
				result.add(collection.getString(attributeName));
			}

		} catch (DfException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);

		} finally {
			try {
				assert collection != null;
				collection.close();

			} catch (DfException ignored) {
			}
		}

		return result;
	}

	public boolean isDocumentType(IDfPersistentObject object) {
		try {
			if (!object.isInstanceOf("dm_sysobject") || object.isInstanceOf("dm_folder")) {
				return false;
			} else {
				return true;
			}
		} catch (DfException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
		return false;
	}

	public boolean canCheckOut(String objectId) {
		LOGGER.log(Level.FINE, "Test if user can checkout" + objectId);
		IDfPersistentObject object = repository.getPersistentObject(objectId);
		return canCheckOut(object);

	}

	public boolean canCheckOut(IDfPersistentObject object) {
		try {
			if (object.isInstanceOf("dm_sysobject") && !object.isInstanceOf("dm_folder")) {
				IDfSysObject sysObj = (IDfSysObject) object;
				if (IDfACL.DF_PERMIT_VERSION <= sysObj.getPermit() && !sysObj.isCheckedOut()) {
					return true;
				}
			} else {
				return false;
			}
		} catch (DfException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
		return false;
	}

	public boolean isCheckedOut(String objectId) {
		IDfPersistentObject object = repository.getPersistentObject(objectId);
		LOGGER.log(Level.FINE, "Test if user can checkout" + objectId);
		return isCheckedOut(object);

	}

	public boolean isCheckedOut(IDfPersistentObject object) {
		try {

			if (object.isInstanceOf("dm_sysobject")) {
				IDfSysObject sysObj = (IDfSysObject) object;
				return sysObj.isCheckedOut();
			} else {
				return false;
			}
		} catch (DfException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
		return false;
	}

	public boolean checkin(String objectId, File content, Version version, boolean keepLock) {
		IDfPersistentObject object = repository.getPersistentObject(objectId);
		try {
			checkin(object, content, version, keepLock);
		} catch (DfException e) {
			return false;
		}
		return true;
	}

	public void checkin(IDfPersistentObject sysObject, File content, Version version, boolean keepLock)
			throws DfException {
		try {
			if (sysObject.isInstanceOf("dm_sysobject")) {
				IDfSysObject sysObj = (IDfSysObject) sysObject;
				if (sysObj.isCheckedOut()) {
					sysObj.setFile(content.getAbsolutePath());
					// should offer choice to user if they want to keep lock, which version number
					switch (version) {
					case SAMEVER:
						sysObj.save();
						if (!keepLock) {
							sysObj.cancelCheckout();
						}
						break;
					case MAJOR:
						sysObj.checkin(keepLock, sysObj.getVersionPolicy().getNextMajorLabel() + ", CURRENT");
						break;
					default:
						sysObj.checkin(keepLock, null);
					}
				}
			}
		} catch (DfException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			throw e;
		}
	}

	public IDfPersistentObject getPersistentObject(String id) {
		try {
			return getObjectById(id);
		} catch (DfException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
		return null;
	}

	public String getObjectName(String id) {
		try {
			IDfPersistentObject objectById = getObjectById(id);
			return objectById.getString("object_name");
		} catch (DfException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
		return null;
	}

	public void cancelCheckout(String id) {
		try {
			LOGGER.info(id);

			IDfSysObject sysObject = (IDfSysObject) repository.getSession().getObject(new DfId(id));
			cancelCheckout(sysObject);
		} catch (DfException e) {
			AppAlert.error("Unable to cancel Checkout", id);
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	public void cancelCheckout(IDfSysObject sysObject) throws DfException {
		try {
			sysObject.cancelCheckout();
		} catch (DfException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			throw e;
		}
	}

	public void checkoutContent(String id) {
		try {
			LOGGER.info(id);

			IDfSysObject sysObject = (IDfSysObject) repository.getSession().getObject(new DfId(id));
			checkoutContent(sysObject);
		} catch (DfException e) {
			AppAlert.error("Error while trying to retrieve content", id);
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	public void checkoutContent(IDfSysObject sysObject) throws DfException {
		try {
			sysObject.checkout();
			downloadContent(sysObject);

		} catch (DfException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			throw e;
		}
	}

	/** Get document primary content */
	public void downloadContent(String id) {
		LOGGER.info(id);
		IDfSysObject sysObject;
		try {
			sysObject = (IDfSysObject) repository.getSession().getObject(new DfId(id));
			downloadContent(sysObject);
		} catch (DfException e) {
			AppAlert.error("Error while trying to retrieve content", id);
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	public void downloadContent(IDfSysObject sysObject) throws DfException {
		try {
			String objectName = sysObject.getObjectName();
			if (!(sysObject.getContentSize() > 0)) {
				AppAlert.warning("Content is empty", objectName);
			} else {
				final String extension = repository.getSession().getFormat(sysObject.getContentType())
						.getDOSExtension();
				final String path = sysObject.getFile(new File(Resources.getExportPath(),
						objectName.replaceAll("[^a-zA-Z0-9._]", "-") + "." + extension).getAbsolutePath());
				LOGGER.info("Downloaded document to path " + path);
			}

		} catch (DfException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			throw e;
		}
	}
}
