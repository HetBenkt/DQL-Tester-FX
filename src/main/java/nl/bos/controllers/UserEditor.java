package nl.bos.controllers;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import nl.bos.beans.UserObject;
import nl.bos.services.UserService;
import nl.bos.utils.DialogCallbackInterface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static nl.bos.Constants.ROOT_SCENE_CSS;

public class UserEditor implements DialogCallbackInterface {
	private static final Logger LOGGER = Logger.getLogger(UserEditor.class.getName());
	private final HashMap<Integer, Toggle> userStateToggles = new HashMap<>();

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
	private UserService userService;
	@FXML
	private Button btnUpdate;
	@FXML
	private Button btnExport;

	@FXML
	private void initialize() {
		userService = new UserService();

		userStateToggles.put(UserService.USER_ACTIVE, userStateActive);
		userStateToggles.put(UserService.USER_INACTIVE, userStateInactive);
		userStateToggles.put(UserService.USER_LOCKED, userStateLocked);
		userStateToggles.put(UserService.USER_LOCKED_INACTIVE, userStateLockedInactive);

		user_source.setItems(FXCollections.observableList(UserService.userSources));
		user_privilege.setItems(FXCollections.observableList(new ArrayList<>(UserService.userPrivileges.values())));
		user_xprivilege.setItems(FXCollections.observableList(new ArrayList<>(UserService.userXPrivileges.values())));
		client_capability.setItems(FXCollections.observableList(new ArrayList<>(UserService.clientCapabilities.values())));
		home_docbase.setItems(FXCollections.observableList(userService.getHomeDocbaseList()));

		final ObservableList<String> observableBasicPermissions = FXCollections.observableList(new ArrayList<>(UserService.basicPermissions.values()));
		owner_permit.setItems(observableBasicPermissions);
		group_permit.setItems(observableBasicPermissions);
		world_permit.setItems(observableBasicPermissions);

		List<String> aliasSets = userService.getAliasSets();
		alias_set.setItems(FXCollections.observableList(aliasSets));

		refreshUserList();
		userList.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> handleUserSelection(newValue));

		addChangeListeners();
	}

	private void handleUserSelection(String selectedUser) {
		LOGGER.info(String.format("Selected user %s", selectedUser));
		UserObject userObject = userService.getUserByName(selectedUser);
		assert userObject != null;
		updateUIFields(userObject);
		btnExport.setDisable(false);
	}

	private void refreshUserList() {
		List<String> filteredUsers = userService.getFilteredUserlist(userFilter.getText());
		ObservableList<String> observableUserList = FXCollections.observableList(filteredUsers);
		userList.setItems(observableUserList);
		userListCount.setText("" + filteredUsers.size());
	}

	public void updateUserFilter() {
		refreshUserList();
	}

	private void updateUIFields(UserObject userObject) {
		globally_managed.setSelected(userObject.isGlobally_managed());
		docbase_owner.setSelected(userObject.isDocbase_owner());
		r_object_id.setText(userObject.getR_object_id());
		r_modify_date.setText(userObject.getR_modify_date());
		userState.selectToggle(userStateToggles.get(userObject.getUser_state()));
		user_name.setText(userObject.getUser_name());
		user_os_name.setText(userObject.getUser_os_name());
		user_os_domain.setText(userObject.getUser_os_domain());
		user_source.getSelectionModel().select(userObject.getUser_source());
		user_address.setText(userObject.getUser_address());
		user_db_name.setText(userObject.getUser_db_name());
		user_privilege.getSelectionModel().select(UserService.userPrivileges.get(userObject.getUser_privileges()));
		default_group.setText(userObject.getUser_group_name());
		default_folder.setText(userObject.getDefault_folder());
		default_acl.setText(userObject.getAcl_domain() + "//" + userObject.getAcl_name());
		home_docbase.getSelectionModel().select(userObject.getHome_docbase());
		client_capability.getSelectionModel().select(UserService.clientCapabilities.get(userObject.getClient_capability()));
		alias_set.getSelectionModel().select(userService.getAliasSet(userObject.getAlias_set_id()));
		description.setText(userObject.getDescription());
		workflow_disabled.setSelected(userObject.isWorkflow_disabled());
		user_delegation.setText(userObject.getUser_delegation());
		distinguished_name.setText(userObject.getUser_ldap_dn());
		user_xprivilege.getSelectionModel().select(UserService.userXPrivileges.get(userObject.getUser_xprivileges()));
		failed_auth_attempt.setSelected(userObject.getFailed_auth_attempt() > -1);
		failed_auth_attempt_count.setText("" + userObject.getFailed_auth_attempt());
		has_events.setSelected(userObject.isHas_events());

		owner_permit.getSelectionModel().select(UserService.basicPermissions.get(userObject.getOwner_permit()));
		group_permit.getSelectionModel().select(UserService.basicPermissions.get(userObject.getGroup_permit()));
		world_permit.getSelectionModel().select(UserService.basicPermissions.get(userObject.getWorld_permit()));
		user_administrator.setText(userObject.getUser_admin());
		user_global_unique_id.setText(userObject.getUser_global_unique_id());
		user_login_name.setText(userObject.getUser_login_name());
		user_login_domain.setText(userObject.getUser_login_domain());
		user_initials.setText(userObject.getUser_initials());
		user_password.setText(userObject.getUser_password());
		user_web_page.setText(userObject.getUser_web_page());
		first_failed_auth_utc_time.setText(userObject.getFirst_failed_auth_utc_time());
		last_login_utc_time.setText(userObject.getLast_login_utc_time());
		deactivated_utc_time.setText(userObject.getDeactivated_utc_time());
		deactivated_ip_address.setText(userObject.getDeactivated_ip_addr());

		ObservableList<String> restrictedFolderIdList = FXCollections.observableList(userObject.getRestricted_folder_ids());
		restricted_folder_ids.setItems(restrictedFolderIdList);

		btnUpdate.setDisable(true);
	}

	public void closeWindow(ActionEvent actionEvent) {
		LOGGER.info(String.valueOf(actionEvent.getSource()));
		Stage stage = (Stage) userList.getScene().getWindow();
		stage.close();
	}

	public void emptyDefaultGroupField() {
		default_group.clear();
	}

	public void emptyDefaultFolderField() {
		default_folder.clear();
	}

	public void emptyDefaultACLField() {
		default_acl.clear();
	}

	@Override
	public void returnValue(String returnValue, String callbackMessage) {
		switch (callbackMessage) {
			case "defaultGroup":
				default_group.setText(returnValue);

				break;
			case "userDelegation":
				user_delegation.setText(returnValue);

				break;
			case "userAdministrator":
				user_administrator.setText(returnValue);
				break;
		}
	}

	public void browseDefaultGroup() {
		openSelectGroupDialog("defaultGroup", false);
	}

	public void browseUserDelegation() {
		openSelectUserDialog("Select Delegation User", "userDelegation");
	}

	public void browseUserAdministrator() {
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
			selectGroupStage.getScene().getStylesheets()
					.addAll(ROOT_SCENE_CSS);
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
			selectUserStage.getScene().getStylesheets()
					.addAll(ROOT_SCENE_CSS);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
		SelectUserDialog controller = fxmlLoader.getController();
		controller.setCallbackTarget(this, callbackMessage);
		selectUserStage.showAndWait();
	}

	public void exportUser() {
		userService.exportUser(user_name.getText());
	}

	public void updateUser() {
		String[] aclParts = default_acl.getText().split("//");
		String aclDomain = aclParts[0];
		String aclName = aclParts[1];

		UserObject updatedUserObject = userService.getUserByName(user_name.getText());
		updatedUserObject.setGlobally_managed(globally_managed.isSelected());
		updatedUserObject.setUser_state(getUserState());
		updatedUserObject.setUser_os_name(user_os_name.getText());
		updatedUserObject.setUser_os_domain(user_os_domain.getText());
		updatedUserObject.setUser_source(user_source.getSelectionModel().getSelectedItem());
		updatedUserObject.setUser_address(user_address.getText());
		updatedUserObject.setUser_db_name(user_db_name.getText());
		updatedUserObject.setUser_privileges(userService.getUserPrivilege(user_privilege.getSelectionModel().getSelectedItem()));
		updatedUserObject.setUser_group_name(default_group.getText());
		updatedUserObject.setDefault_folder(default_folder.getText());
		updatedUserObject.setAcl_domain(aclDomain);
		updatedUserObject.setAcl_name(aclName);
		updatedUserObject.setHome_docbase(home_docbase.getSelectionModel().getSelectedItem());
		updatedUserObject.setClient_capability(userService.getClientCapability(client_capability.getSelectionModel().getSelectedItem()));
		updatedUserObject.setAlias_set_id(alias_set.getSelectionModel().getSelectedItem());
		updatedUserObject.setDescription(description.getText());
		updatedUserObject.setWorkflow_disabled(workflow_disabled.isSelected());
		updatedUserObject.setUser_delegation(user_delegation.getText());
		updatedUserObject.setUser_ldap_dn(distinguished_name.getText());
		updatedUserObject.setUser_xprivileges(userService.getUserXPrivilege(user_xprivilege.getSelectionModel().getSelectedItem()));
		updatedUserObject.setFailed_auth_attempt(getFailedAuthAttempt());

		updatedUserObject.setOwner_permit(userService.getBasicPermission(owner_permit.getSelectionModel().getSelectedItem()));
		updatedUserObject.setGroup_permit(userService.getBasicPermission(group_permit.getSelectionModel().getSelectedItem()));
		updatedUserObject.setWorld_permit(userService.getBasicPermission(world_permit.getSelectionModel().getSelectedItem()));
		updatedUserObject.setUser_admin(user_administrator.getText());
		updatedUserObject.setUser_global_unique_id(user_global_unique_id.getText());
		updatedUserObject.setUser_login_name(user_login_name.getText());
		updatedUserObject.setUser_login_domain(user_login_domain.getText());
		updatedUserObject.setUser_initials(user_initials.getText());
		// TODO password
		updatedUserObject.setUser_web_page(user_web_page.getText());
		updatedUserObject.setDeactivated_ip_addr(deactivated_ip_address.getText());
		updatedUserObject.setRestricted_folder_ids(restricted_folder_ids.getItems());

		userService.updateUser(updatedUserObject);
	}

	private int getUserState() {
		if (userStateActive.isSelected()) {
			return UserService.USER_ACTIVE;
		}
		if (userStateInactive.isSelected()) {
			return UserService.USER_INACTIVE;
		}
		if (userStateLocked.isSelected()) {
			return UserService.USER_LOCKED;
		}
		if (userStateLockedInactive.isSelected()) {
			return UserService.USER_LOCKED_INACTIVE;
		}
		return 0;
	}

	private int getFailedAuthAttempt() {
		int displayedAttemptCount = Integer.parseInt(failed_auth_attempt_count.getText());
		if (failed_auth_attempt.isSelected() && displayedAttemptCount == -1) {
			return 0;
		} else if (!failed_auth_attempt.isSelected()) {
			return -1;
		}
		return displayedAttemptCount;
	}

	private void addChangeListeners() {
		ChangeListener btnUpdateListener = (observableValue, oldValue, newValue) -> btnUpdate.setDisable(false);

		globally_managed.selectedProperty().addListener(btnUpdateListener);
		userState.selectedToggleProperty().addListener(btnUpdateListener);
		user_name.textProperty().addListener(btnUpdateListener);
		user_os_name.textProperty().addListener(btnUpdateListener);
		user_os_domain.textProperty().addListener(btnUpdateListener);
		user_source.valueProperty().addListener(btnUpdateListener);
		user_address.textProperty().addListener(btnUpdateListener);
		user_db_name.textProperty().addListener(btnUpdateListener);
		user_privilege.valueProperty().addListener(btnUpdateListener);
		default_group.textProperty().addListener(btnUpdateListener);
		default_folder.textProperty().addListener(btnUpdateListener);
		default_acl.textProperty().addListener(btnUpdateListener);
		home_docbase.valueProperty().addListener(btnUpdateListener);
		client_capability.valueProperty().addListener(btnUpdateListener);
		alias_set.valueProperty().addListener(btnUpdateListener);
		description.textProperty().addListener(btnUpdateListener);
		workflow_disabled.selectedProperty().addListener(btnUpdateListener);
		user_delegation.textProperty().addListener(btnUpdateListener);
		distinguished_name.textProperty().addListener(btnUpdateListener);
		user_xprivilege.valueProperty().addListener(btnUpdateListener);
		failed_auth_attempt.selectedProperty().addListener(btnUpdateListener);

		owner_permit.valueProperty().addListener(btnUpdateListener);
		group_permit.valueProperty().addListener(btnUpdateListener);
		world_permit.valueProperty().addListener(btnUpdateListener);
		user_administrator.textProperty().addListener(btnUpdateListener);
		user_global_unique_id.textProperty().addListener(btnUpdateListener);
		user_login_name.textProperty().addListener(btnUpdateListener);
		user_login_domain.textProperty().addListener(btnUpdateListener);
		user_initials.textProperty().addListener(btnUpdateListener);
		user_password.textProperty().addListener(btnUpdateListener);
		user_web_page.textProperty().addListener(btnUpdateListener);
		deactivated_ip_address.textProperty().addListener(btnUpdateListener);
		restricted_folder_ids.itemsProperty().addListener(btnUpdateListener);
	}
}
