package nl.bos.services;

import com.documentum.fc.client.*;
import com.documentum.fc.common.DfException;
import nl.bos.Repository;
import nl.bos.beans.UserObject;
import nl.bos.utils.Resources;
import nl.bos.utils.UIUtils;

import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserService {
	private static final Logger LOGGER = Logger.getLogger(UserService.class.getName());
	private static final String ATTR_USER_ADMIN = "user_admin";
	private static final String ATTR_USER_GLOBAL_UNIQUE_ID = "user_global_unique_id";
	private static final String ATTR_USER_LOGIN_DOMAIN = "user_login_domain";
	private static final String ATTR_USER_INITIALS = "user_initials";
	private static final String ATTR_USER_WEB_PAGE = "user_web_page";
	private static final String ATTR_FIRST_FAILED_AUTH_UTC_TIME = "first_failed_auth_utc_time";
	private static final String ATTR_LAST_LOGIN_UTC_TIME = "last_login_utc_time";
	private static final String ATTR_DEACTIVATED_UTC_TIME = "deactivated_utc_time";
	private static final String ATTR_DEACTIVATED_IP_ADDR = "deactivated_ip_addr";
	public static final int USER_ACTIVE = IDfUser.DF_USER_ACTIVE;
	public static final int USER_INACTIVE = IDfUser.DF_USER_INACTIVE;
	public static final int USER_LOCKED = IDfUser.DF_USER_LOCKED;
	public static final int USER_LOCKED_INACTIVE = IDfUser.DF_USER_LOCKED_INACTIVE;
	private static final String NO_ALIAS_SET = "<none>";
	private final Repository repository = Repository.getInstance();

	public static final List<String> userSources = new ArrayList<>();
	public static final Map<Integer, String> userPrivileges = new HashMap<>();
	public static final Map<Integer, String> userXPrivileges = new HashMap<>();
	public static final Map<Integer, String> clientCapabilities = new HashMap<>();
	public static final Map<Integer, String> basicPermissions = new HashMap<>();

	static {
		userSources.add("None");
		userSources.add("UNIX only");
		userSources.add("Domain only");
		userSources.add("UNIX first");
		userSources.add("Domain first");
		userSources.add("LDAP");
		userSources.add("Inline Password");
		userSources.add("dm_krb");

		userPrivileges.put(IDfUser.DF_PRIVILEGE_NONE, "None");
		userPrivileges.put(IDfUser.DF_PRIVILEGE_CREATE_TYPE, "Create Type");
		userPrivileges.put(IDfUser.DF_PRIVILEGE_CREATE_CABINET, "Create Cabinet");
		userPrivileges.put(IDfUser.DF_PRIVILEGE_CREATE_TYPE | IDfUser.DF_PRIVILEGE_CREATE_CABINET, "Create Cabinet and Type");
		userPrivileges.put(IDfUser.DF_PRIVILEGE_CREATE_GROUP, "Create Group");
		userPrivileges.put(IDfUser.DF_PRIVILEGE_CREATE_GROUP | IDfUser.DF_PRIVILEGE_CREATE_TYPE, "Create Group and Type");
		userPrivileges.put(IDfUser.DF_PRIVILEGE_CREATE_GROUP | IDfUser.DF_PRIVILEGE_CREATE_CABINET, "Create Group and Cabinet");
		userPrivileges.put(IDfUser.DF_PRIVILEGE_CREATE_GROUP | IDfUser.DF_PRIVILEGE_CREATE_CABINET | IDfUser.DF_PRIVILEGE_CREATE_TYPE, "Create Group, Cabinet and Type");
		userPrivileges.put(IDfUser.DF_PRIVILEGE_SYSADMIN, "System Administrator");
		userPrivileges.put(IDfUser.DF_PRIVILEGE_SUPERUSER, "Super User");

		userXPrivileges.put(0, "None");
		userXPrivileges.put(IDfUser.DF_XPRIVILEGE_VIEW_AUDIT, "View Audit");
		userXPrivileges.put(IDfUser.DF_XPRIVILEGE_CONFIG_AUDIT, "Config Audit");
		userXPrivileges.put(IDfUser.DF_XPRIVILEGE_CONFIG_AUDIT | IDfUser.DF_XPRIVILEGE_VIEW_AUDIT, "Config and View Audit");
		userXPrivileges.put(IDfUser.DF_XPRIVILEGE_PURGE_AUDIT, "Purge Audit");
		userXPrivileges.put(IDfUser.DF_XPRIVILEGE_CONFIG_AUDIT | IDfUser.DF_XPRIVILEGE_PURGE_AUDIT, "Config and Purge Audit");
		userXPrivileges.put(IDfUser.DF_XPRIVILEGE_VIEW_AUDIT | IDfUser.DF_XPRIVILEGE_PURGE_AUDIT, "View and Purge Audit");
		userXPrivileges.put(IDfUser.DF_XPRIVILEGE_CONFIG_AUDIT | IDfUser.DF_XPRIVILEGE_VIEW_AUDIT | IDfUser.DF_XPRIVILEGE_PURGE_AUDIT, "Config, View and Purge Audit");

		clientCapabilities.put(IDfUser.DF_CAPABILITY_NONE, "Consumer");
		clientCapabilities.put(IDfUser.DF_CAPABILITY_CONSUMER, "Consumer");
		clientCapabilities.put(IDfUser.DF_CAPABILITY_CONTRIBUTOR, "Contributor");
		clientCapabilities.put(IDfUser.DF_CAPABILITY_COORDINATOR, "Coordinator");
		clientCapabilities.put(IDfUser.DF_CAPABILITY_SYSTEM_ADMIN, "System Administrator");

		basicPermissions.put(IDfACL.DF_PERMIT_NONE, IDfACL.DF_PERMIT_NONE_STR);
		basicPermissions.put(IDfACL.DF_PERMIT_BROWSE, IDfACL.DF_PERMIT_BROWSE_STR);
		basicPermissions.put(IDfACL.DF_PERMIT_READ, IDfACL.DF_PERMIT_READ_STR);
		basicPermissions.put(IDfACL.DF_PERMIT_RELATE, IDfACL.DF_PERMIT_RELATE_STR);
		basicPermissions.put(IDfACL.DF_PERMIT_VERSION, IDfACL.DF_PERMIT_VERSION_STR);
		basicPermissions.put(IDfACL.DF_PERMIT_WRITE, IDfACL.DF_PERMIT_WRITE_STR);
		basicPermissions.put(IDfACL.DF_PERMIT_DELETE, IDfACL.DF_PERMIT_DELETE_STR);
	}

	private List<String> updatedAttributes = new ArrayList<>();

	public String getAliasSet(String aliasSetId) {
		if (!repository.isObjectId(aliasSetId)) {
			return NO_ALIAS_SET;
		}

		try {
			return ((IDfAliasSet) repository.getObjectById(aliasSetId)).getObjectName();
		} catch (DfException e) {
			e.printStackTrace();
		}

		return null;
	}

	public void exportUser(String userName) {
		try {
			IDfUser userObject = repository.getUserByName(userName);

			String[] attributesToExport = new String[] {
					"user_name",
					"user_os_name",
					"user_address",
					"user_group_name",
					"user_privileges",
					"default_folder",
					"user_db_name",
					"description",
					"acl_domain",
					"acl_name",
					"user_os_domain",
					"home_docbase",
					"user_state",
					"client_capability",
					"globally_managed",
					"user_delegation",
					"workflow_disabled",
					"alias_set_id",
					"user_source",
					"user_xprivileges",
					"user_ldap_dn",
					"failed_auth_attempt",
					"owner_def_permit",
					"group_def_permit",
					"world_def_permit",
					ATTR_USER_ADMIN,
					ATTR_USER_GLOBAL_UNIQUE_ID,
					"user_login_name",
					ATTR_USER_LOGIN_DOMAIN,
					ATTR_USER_INITIALS,
					ATTR_USER_WEB_PAGE,
					ATTR_DEACTIVATED_IP_ADDR
			};

			StringBuilder exportDataBuilder = new StringBuilder();
			exportDataBuilder.append(String.format("# *------------ dm_user - %s ------------*%n", userObject.getUserName()));
			exportDataBuilder.append("create,c,dm_user").append("\n");

			for (String attributeName : attributesToExport) {
				exportDataBuilder.append("set,c,l,").append(attributeName).append("\n");
				exportDataBuilder.append(userObject.getString(attributeName)).append("\n");
			}

			exportDataBuilder.append("save,c,l").append("\n");

			File tempFile = Resources.createTempFile("DcUser_" + userName, ".api");

			if (tempFile != null) {
				Resources.exportStringToFile(tempFile, exportDataBuilder.toString());
				if (Desktop.isDesktopSupported()) {
					Resources.openFile(tempFile);
				}
			}

		} catch (DfException e) {
			UIUtils.showExpendableExceptionAlert("Export User failed", "", "Could not export user", e);
			LOGGER.log(Level.SEVERE, "Could not export user", e);
		}
	}

	public UserObject getUserByName(String userName) {
		try {
			IDfUser dfcUserObject = repository.getUserByName(userName);
			UserObject userObject = new UserObject();

			userObject.setGlobally_managed(dfcUserObject.isGloballyManaged());
			userObject.setDocbase_owner(userName.equals(repository.getDocbaseOwner()));
			userObject.setR_object_id(dfcUserObject.getObjectId().getId());
			userObject.setR_modify_date(dfcUserObject.getModifyDate().asString(""));
			userObject.setUser_state(dfcUserObject.getUserState());
			userObject.setUser_name(userName);
			userObject.setUser_os_name(dfcUserObject.getUserOSName());
			userObject.setUser_os_domain(dfcUserObject.getUserOSDomain());
			userObject.setUser_source(dfcUserObject.getUserSourceAsString());
			userObject.setUser_address(dfcUserObject.getUserAddress());
			userObject.setUser_db_name(dfcUserObject.getUserDBName());
			userObject.setUser_privileges(dfcUserObject.getUserPrivileges());
			userObject.setUser_group_name(dfcUserObject.getUserGroupName());
			userObject.setDefault_folder(dfcUserObject.getDefaultFolder());
			userObject.setAcl_name(dfcUserObject.getACLName());
			userObject.setAcl_domain(dfcUserObject.getACLDomain());
			userObject.setHome_docbase(dfcUserObject.getHomeDocbase());
			userObject.setClient_capability(dfcUserObject.getClientCapability());
			userObject.setAlias_set_id(dfcUserObject.getAliasSet());
			userObject.setDescription(dfcUserObject.getDescription());
			userObject.setWorkflow_disabled(dfcUserObject.isWorkflowDisabled());
			userObject.setUser_delegation(dfcUserObject.getUserDelegation());
			userObject.setUser_ldap_dn(dfcUserObject.getUserDistinguishedLDAPName());
			userObject.setUser_xprivileges(dfcUserObject.getUserXPrivileges());
			userObject.setFailed_auth_attempt(dfcUserObject.getFailedAuthenticationAttempts());
			userObject.setHas_events(dfcUserObject.hasEvents());

			userObject.setOwner_permit(dfcUserObject.getOwnerDefPermit());
			userObject.setGroup_permit(dfcUserObject.getGroupDefPermit());
			userObject.setWorld_permit(dfcUserObject.getWorldDefPermit());
			userObject.setUser_admin(dfcUserObject.getString(ATTR_USER_ADMIN));
			userObject.setUser_global_unique_id(dfcUserObject.getString(ATTR_USER_GLOBAL_UNIQUE_ID));
			userObject.setUser_login_name(dfcUserObject.getUserLoginName());
			userObject.setUser_login_domain(dfcUserObject.getString(ATTR_USER_LOGIN_DOMAIN));
			userObject.setUser_initials(dfcUserObject.getString(ATTR_USER_INITIALS));
			userObject.setUser_password(dfcUserObject.getUserPassword());
			userObject.setUser_web_page(dfcUserObject.getString(ATTR_USER_WEB_PAGE));
			userObject.setFirst_failed_auth_utc_time(dfcUserObject.getTime(ATTR_FIRST_FAILED_AUTH_UTC_TIME).asString(""));
			userObject.setLast_login_utc_time(dfcUserObject.getTime(ATTR_LAST_LOGIN_UTC_TIME).asString(""));
			userObject.setDeactivated_utc_time(dfcUserObject.getTime(ATTR_DEACTIVATED_UTC_TIME).asString(""));
			userObject.setDeactivated_ip_addr(dfcUserObject.getString(ATTR_DEACTIVATED_IP_ADDR));

			java.util.List<String> restrictedFolderIds = new ArrayList<>();
			for (int i = 0; i < dfcUserObject.getValueCount(""); i++) {
				restrictedFolderIds.add(dfcUserObject.getRepeatingString("", i));
			}

			userObject.setRestricted_folder_ids(restrictedFolderIds);

			return userObject;

		} catch (DfException e) {
			UIUtils.showExpendableExceptionAlert("Retrieve User failed", "", "Could not retrieve user", e);
			LOGGER.log(Level.SEVERE, "Could not retrieve user", e);
		}

		return null;
	}

	public List<String> getHomeDocbaseList() {
		List<String> homeDocbaseList = new ArrayList<>();
		homeDocbaseList.add(repository.getRepositoryName());

		return homeDocbaseList;
	}

	public List<String> getAliasSets() {
		List<String> aliasSets = repository.getAliasSets();
		aliasSets.set(0, NO_ALIAS_SET);
		return aliasSets;
	}

	public void updateUser(UserObject userObject) {
		updatedAttributes.clear();
		IDfLocalTransaction localTransaction = null;
		try {
			localTransaction = repository.getSession().beginTransEx();
			IDfUser user = repository.getUserByName(userObject.getUser_name());

			updateIntIfChanged(user, "user_state", userObject.getUser_state());
			updateBooleanIfChanged(user, "globally_managed", userObject.isGlobally_managed());

			if (!user.getUserOSName().equals(userObject.getUser_os_name()) ||
					!user.getUserOSDomain().equals(userObject.getUser_os_domain())) {
				user.setUserOSName(userObject.getUser_os_name(), userObject.getUser_os_domain());
				updatedAttributes.add("user_os_name/user_os_domain");
			}

			updateStringIfChanged(user, "user_source", userObject.getUser_source());
			updateStringIfChanged(user, "user_address", userObject.getUser_address());
			updateStringIfChanged(user, "user_db_name", userObject.getUser_db_name());
			updateIntIfChanged(user, "user_privileges", userObject.getUser_privileges());
			updateStringIfChanged(user, "user_group_name", userObject.getUser_group_name());
//			updateStringIfChanged(user, "default_folder", userObject.getDefault_folder());	// TODO fix browse button

//			if (!user.getACLName().equals(userObject.getAcl_name()) ||	// TODO fix browse button
//					!user.getACLDomain().equals(userObject.getAcl_domain())) {
//				user.setDefaultACLEx(userObject.getAcl_domain(), userObject.getAcl_name());
//				updatedAttributes.add("default_acl");
//			}

			if (!user.getHomeDocbase().equals(userObject.getHome_docbase())) {
				user.changeHomeDocbase(userObject.getHome_docbase(), true);
				updatedAttributes.add("home_docbase");
			}

			updateIntIfChanged(user, "client_capability", userObject.getClient_capability());
			updateAliasSetIfChanged(user, userObject.getAlias_set_id());
			updateStringIfChanged(user, "description", userObject.getDescription());
			updateBooleanIfChanged(user, "workflow_disabled", userObject.isWorkflow_disabled());
			updateStringIfChanged(user, "user_delegation", userObject.getUser_delegation());
			updateStringIfChanged(user, "user_ldap_dn", userObject.getUser_ldap_dn());
			updateIntIfChanged(user, "user_xprivileges", userObject.getUser_xprivileges());
			updateIntIfChanged(user, "failed_auth_attempt", userObject.getFailed_auth_attempt());

			updateIntIfChanged(user, "owner_def_permit", userObject.getOwner_permit());
			updateIntIfChanged(user, "group_def_permit", userObject.getGroup_permit());
			updateIntIfChanged(user, "world_def_permit", userObject.getWorld_permit());
			updateStringIfChanged(user, ATTR_USER_ADMIN, userObject.getUser_admin());
			updateStringIfChanged(user, ATTR_USER_GLOBAL_UNIQUE_ID, userObject.getUser_global_unique_id());
			updateStringIfChanged(user, "user_login_name", userObject.getUser_login_name());
			updateStringIfChanged(user, ATTR_USER_LOGIN_DOMAIN, userObject.getUser_login_domain());
			updateStringIfChanged(user, ATTR_USER_INITIALS, userObject.getUser_initials());

			// TODO user password

			updateStringIfChanged(user, ATTR_USER_WEB_PAGE, userObject.getUser_web_page());
			updateStringIfChanged(user, ATTR_DEACTIVATED_IP_ADDR, userObject.getDeactivated_ip_addr());
//			updateRepeatingStringIfChanged(user, "restricted_folder_ids", userObject.getRestricted_folder_ids());	// TODO fix browse button

			user.save();

			LOGGER.log(Level.INFO, "Updated attributes:");
			updatedAttributes.forEach(attr -> LOGGER.log(Level.INFO, attr));

			repository.getSession().commitTransEx(localTransaction);
			localTransaction = null;

		} catch (DfException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		} finally {
			if (localTransaction != null) {
				try {
					repository.getSession().abortTransEx(localTransaction);
				} catch (DfException ignored) {

				}
			}
		}
	}

	private void updateAliasSetIfChanged(IDfUser user, String newAliasSet) throws DfException {
		if (NO_ALIAS_SET.equals(newAliasSet)) {
			newAliasSet = "";
		}

		String currentAliasSet = user.getAliasSet();
		if (!currentAliasSet.equals(newAliasSet)) {
			if (newAliasSet.isEmpty()) {
				user.setString("alias_set_id", "0000000000000000");
			} else {
				user.setAliasSet(newAliasSet);
			}
			updatedAttributes.add("alias_set");
		}
	}

	private void updateStringIfChanged(IDfUser user, String attribute, String newValue) throws DfException {
		if (!user.getString(attribute).equals(newValue)) {
			user.setString(attribute, newValue);
			updatedAttributes.add(attribute);
		}
	}

	private void updateIntIfChanged(IDfUser user, String attribute, int newValue) throws DfException {
		if (user.getInt(attribute) != newValue) {
			user.setInt(attribute, newValue);
			updatedAttributes.add(attribute);
		}
	}

	private void updateBooleanIfChanged(IDfUser user, String attribute, boolean newValue) throws DfException {
		if (user.getBoolean(attribute) != newValue) {
			user.setBoolean(attribute, newValue);
			updatedAttributes.add(attribute);
		}
	}

	private void updateRepeatingStringIfChanged(IDfUser user, String attribute, List<String> newValues) throws DfException {
		if (user.getValueCount(attribute) == 0 && newValues.isEmpty()) {
			return;
		}

		List<String> currentValues = Arrays.asList(user.getAllRepeatingStrings(attribute, ";").split(";"));
		if (!newValues.containsAll(currentValues) || !currentValues.containsAll(newValues)) {
			user.removeAll(attribute);
			for (int i = 0; i < newValues.size(); i++) {
				user.setRepeatingString(attribute, i, newValues.get(i));
			}
			updatedAttributes.add(attribute);
		}
	}

	public List<String> getFilteredUserlist(String text) {
		return repository.getFilteredUserList(text);
	}

	public int getClientCapability(String selectedItem) {
		for (Map.Entry<Integer, String> pair : clientCapabilities.entrySet()) {
			if (selectedItem.equals(pair.getValue())) {
				return pair.getKey();
			}
		}
		return 0;
	}

	public int getUserPrivilege(String selectedItem) {
		for (Map.Entry<Integer, String> pair : userPrivileges.entrySet()) {
			if (selectedItem.equals(pair.getValue())) {
				return pair.getKey();
			}
		}
		return 0;
	}

	public int getUserXPrivilege(String selectedItem) {
		for (Map.Entry<Integer, String> pair : userXPrivileges.entrySet()) {
			if (selectedItem.equals(pair.getValue())) {
				return pair.getKey();
			}
		}
		return 0;
	}

	public int getBasicPermission(String selectedItem) {
		for (Map.Entry<Integer, String> pair : basicPermissions.entrySet()) {
			if (selectedItem.equals(pair.getValue())) {
				return pair.getKey();
			}
		}
		return 0;
	}
}
