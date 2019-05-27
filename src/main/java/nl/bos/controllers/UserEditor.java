package nl.bos.controllers;

import com.documentum.fc.client.IDfACL;
import com.documentum.fc.client.IDfUser;
import com.documentum.fc.common.DfException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import nl.bos.Repository;
import nl.bos.utils.AppAlert;
import nl.bos.utils.DialogCallbackInterface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserEditor implements DialogCallbackInterface {
	private static final Logger LOGGER = Logger.getLogger(UserEditor.class.getName());
	private final Repository repository = Repository.getInstance();

	private final HashMap<Integer, Toggle> userStateToggles = new HashMap<>();

	private static final List<String> userSources = new ArrayList<>();
	private static final Map<Integer, String> userPrivileges = new HashMap<>();
	private static final Map<Integer, String> userXPrivileges = new HashMap<>();
	private static final Map<Integer, String> clientCapabilities = new HashMap<>();
	private static final Map<Integer, String> basicPermissions = new HashMap<>();

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

	@FXML
	private ListView<String> userList;
	@FXML
	private Label userListCount;
	@FXML
	private TextField userFilter;

	@FXML
	private CheckBox globally_managed;
	@FXML
	private CheckBox docbase_owner;
	@FXML
	private TextField r_object_id;
	@FXML
	private TextField r_modify_date;
	@FXML
	private ToggleGroup userState;
	@FXML
	private RadioButton userStateActive;
	@FXML
	private RadioButton userStateInactive;
	@FXML
	private RadioButton userStateLocked;
	@FXML
	private RadioButton userStateLockedInactive;
	@FXML
	private TextField user_name;
	@FXML
	private TextField user_os_name;
	@FXML
	private TextField user_os_domain;
	@FXML
	private ComboBox<String> user_source;
	@FXML
	private TextField user_address;
	@FXML
	private TextField user_db_name;
	@FXML
	private ComboBox<String> user_privilege;
	@FXML
	private TextField default_group;
	@FXML
	private TextField default_folder;
	@FXML
	private TextField default_acl;
	@FXML
	private ComboBox<String> home_docbase;
	@FXML
	private ComboBox<String> client_capability;
	@FXML
	private ComboBox<String> alias_set;
	@FXML
	private TextField description;
	@FXML
	private CheckBox workflow_disabled;
	@FXML
	private TextField user_delegation;
	@FXML
	private TextField distinguished_name;
	@FXML
	private ComboBox<String> user_xprivilege;
	@FXML
	private CheckBox failed_auth_attempt;
	@FXML
	private Label failed_auth_attempt_count;
	@FXML
	private CheckBox has_events;

	@FXML
	private ComboBox<String> owner_permit;
	@FXML
	private ComboBox<String> group_permit;
	@FXML
	private ComboBox<String> world_permit;
	@FXML
	private TextField user_administrator;
	@FXML
	private TextField user_global_unique_id;
	@FXML
	private TextField user_login_name;
	@FXML
	private TextField user_login_domain;
	@FXML
	private TextField user_initials;
	@FXML
	private TextField user_web_page;
	@FXML
	private TextField first_failed_auth_utc_time;
	@FXML
	private TextField last_login_utc_time;
	@FXML
	private TextField deactivated_utc_time;
	@FXML
	private TextField deactivated_ip_address;
	@FXML
	private ListView<String> restricted_folder_ids;
	@FXML
	private PasswordField user_password;

	@FXML
	private void initialize() {
		userStateToggles.put(IDfUser.DF_USER_ACTIVE, userStateActive);
		userStateToggles.put(IDfUser.DF_USER_INACTIVE, userStateInactive);
		userStateToggles.put(IDfUser.DF_USER_LOCKED, userStateLocked);
		userStateToggles.put(IDfUser.DF_USER_LOCKED_INACTIVE, userStateLockedInactive);

		user_source.setItems(FXCollections.observableList(userSources));
		user_privilege.setItems(FXCollections.observableList(new ArrayList<>(userPrivileges.values())));
		user_xprivilege.setItems(FXCollections.observableList(new ArrayList<>(userXPrivileges.values())));
		client_capability.setItems(FXCollections.observableList(new ArrayList<>(clientCapabilities.values())));

		List<String> homeDocbases = new ArrayList<>();
		homeDocbases.add(repository.getRepositoryName());
		home_docbase.setItems(FXCollections.observableList(homeDocbases));

		final ObservableList<String> observableBasicPermissions = FXCollections.observableList(new ArrayList<>(basicPermissions.values()));
		owner_permit.setItems(observableBasicPermissions);
		group_permit.setItems(observableBasicPermissions);
		world_permit.setItems(observableBasicPermissions);

		List<String> aliasSets = repository.getAliasSets();
		alias_set.setItems(FXCollections.observableList(aliasSets));

		refreshUserList();
		userList.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> handleUserSelection(newValue));
	}

	private void handleUserSelection(String selectedUser) {
		LOGGER.info(String.format("Selected user %s", selectedUser));

		try {
			IDfUser userObject = repository.getUserByName(selectedUser);

			if (userObject == null) {
				throw new DfException();
			}

			updateUIFields(userObject);

		} catch (DfException e) {
			AppAlert.error("User not found", String.format("Could not retrieve user %s!", selectedUser));
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	private void refreshUserList() {
		List<String> filteredUsers = repository.getFilteredUserList(userFilter.getText());
		ObservableList<String> observableUserList = FXCollections.observableList(filteredUsers);
		userList.setItems(observableUserList);
		userListCount.setText("" + filteredUsers.size());
	}

	public void updateUserFilter(KeyEvent keyEvent) {
		refreshUserList();
	}

	private void updateUIFields(IDfUser userObject) throws DfException {
		globally_managed.setSelected(userObject.isGloballyManaged());
		docbase_owner.setSelected(userObject.getUserName().equals(repository.getDocbaseOwner()));
		r_object_id.setText(userObject.getObjectId().getId());
		r_modify_date.setText(userObject.getModifyDate().asString(""));
		userState.selectToggle(userStateToggles.get(userObject.getUserState()));
		user_name.setText(userObject.getUserName());
		user_os_name.setText(userObject.getUserOSName());
		user_os_domain.setText(userObject.getUserOSDomain());
		user_source.getSelectionModel().select(userObject.getUserSourceAsString());
		user_address.setText(userObject.getUserAddress());
		user_db_name.setText(userObject.getUserDBName());
		user_privilege.getSelectionModel().select(userPrivileges.get(userObject.getUserPrivileges()));
		default_group.setText(userObject.getUserGroupName());
		default_folder.setText(userObject.getDefaultFolder());
		default_acl.setText(userObject.getACLName());
		home_docbase.getSelectionModel().select(userObject.getHomeDocbase());
		client_capability.getSelectionModel().select(clientCapabilities.get(userObject.getClientCapability()));
		alias_set.getSelectionModel().select(userObject.getAliasSet());
		description.setText(userObject.getDescription());
		workflow_disabled.setSelected(userObject.isWorkflowDisabled());
		user_delegation.setText(userObject.getUserDelegation());
		distinguished_name.setText(userObject.getUserDistinguishedLDAPName());
		user_xprivilege.getSelectionModel().select(userXPrivileges.get(userObject.getUserXPrivileges()));
		failed_auth_attempt.setSelected(userObject.getFailedAuthenticationAttempts() > -1);
		failed_auth_attempt_count.setText("" + userObject.getFailedAuthenticationAttempts());
		has_events.setSelected(userObject.hasEvents());

		owner_permit.getSelectionModel().select(basicPermissions.get(userObject.getOwnerDefPermit()));
		group_permit.getSelectionModel().select(basicPermissions.get(userObject.getGroupDefPermit()));
		world_permit.getSelectionModel().select(basicPermissions.get(userObject.getWorldDefPermit()));
		user_administrator.setText(userObject.getString("user_admin"));
		user_global_unique_id.setText(userObject.getString("user_global_unique_id"));
		user_login_name.setText(userObject.getUserLoginName());
		user_login_domain.setText(userObject.getString("user_login_domain"));
		user_initials.setText(userObject.getString("user_initials"));
		user_password.setText(userObject.getUserPassword());
		user_web_page.setText(userObject.getString("user_web_page"));
		first_failed_auth_utc_time.setText(userObject.getTime("first_failed_auth_utc_time").asString(""));
		last_login_utc_time.setText(userObject.getTime("last_login_utc_time").asString(""));
		deactivated_utc_time.setText(userObject.getTime("deactivated_utc_time").asString(""));
		deactivated_ip_address.setText(userObject.getString("deactivated_ip_addr"));

		List<String> restrictedFolders = new ArrayList<>();
		for (int i = 0; i < userObject.getValueCount("restricted_folder_ids"); i++) {
			restrictedFolders.add(userObject.getRepeatingString("restricted_folder_ids", i));
		}

		ObservableList<String> restrictedFolderIdList = FXCollections.observableList(restrictedFolders);
		restricted_folder_ids.setItems(restrictedFolderIdList);
	}

	public void closeWindow(ActionEvent actionEvent) {
		LOGGER.info(String.valueOf(actionEvent.getSource()));
		Stage stage = (Stage) userList.getScene().getWindow();
		stage.close();
	}

	public void emptyDefaultGroupField(MouseEvent mouseEvent) {
		default_group.clear();
	}

	public void emptyDefaultFolderField(MouseEvent mouseEvent) {
		default_folder.clear();
	}

	public void emptyDefaultACLField(MouseEvent mouseEvent) {
		default_acl.clear();
	}

	@Override
	public void returnValue(String returnValue, String callbackMessage) {
		if (callbackMessage.equals("defaultGroup")) {
			default_group.setText(returnValue);

		} else if (callbackMessage.equals("userDelegation")) {
			user_delegation.setText(returnValue);

		} else if (callbackMessage.equals("userAdministrator")) {
			user_administrator.setText(returnValue);
		}
	}

	public void browseDefaultGroup(ActionEvent actionEvent) {
		openSelectGroupDialog("defaultGroup", false);
	}

	public void browseUserDelegation(ActionEvent actionEvent) {
		openSelectUserDialog("Select Delegation User", "userDelegation");
	}

	public void browseUserAdministrator(ActionEvent actionEvent) {
		openSelectUserDialog("Select User Administrator", "userAdministrator");
	}

	private void openSelectGroupDialog(String callbackMessage, boolean allowAllGroupsOption) {
		Stage selectGroupStage = new Stage();
		selectGroupStage.setTitle("Select a Group");
		selectGroupStage.setResizable(false);
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/nl/bos/views/dialogs/SelectGroupDialog.fxml"));

		try {
			AnchorPane selectGroupPane = fxmlLoader.load();
			selectGroupStage.setScene(new Scene(selectGroupPane));

		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}

		SelectGroupDialog controller = fxmlLoader.getController();
		controller.setAllowAllGroupsOption(allowAllGroupsOption);
		controller.setCallbackTarget(this, callbackMessage);

		selectGroupStage.showAndWait();
	}

	private void openSelectUserDialog(String dialogTitle, String callbackMessage) {
		Stage selectUserStage = new Stage();
		selectUserStage.setTitle(dialogTitle);
		selectUserStage.setResizable(false);
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/nl/bos/views/dialogs/SelectUserDialog.fxml"));

		try {
			AnchorPane selectUserPane = fxmlLoader.load();
			selectUserStage.setScene(new Scene(selectUserPane));

		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}

		SelectUserDialog controller = fxmlLoader.getController();
		controller.setCallbackTarget(this, callbackMessage);

		selectUserStage.showAndWait();
	}
}
