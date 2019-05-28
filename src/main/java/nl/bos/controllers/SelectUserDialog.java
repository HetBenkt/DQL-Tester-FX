package nl.bos.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import nl.bos.Repository;
import nl.bos.utils.DialogCallbackInterface;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SelectUserDialog implements DialogCallbackInterface {
	private static final Logger LOGGER = Logger.getLogger(UserEditor.class.getName());

	private Repository repository = Repository.getInstance();
	private DialogCallbackInterface callbackTarget;
	private String callbackMessage;

	@FXML
	private TextField groupFilter;
	@FXML
	private TextField userFilter;
	@FXML
	private ListView<String> userList;
	@FXML
	private Label userListCount;
	@FXML
	private Button btnOK;
	@FXML
	private Button btnCancel;

	@FXML
	private void initialize() {
		userList.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> updateOKButton(newValue));
		userList.setOnMouseClicked(this::handleDoubleclick);

		refreshUserList();
	}

	private void handleDoubleclick(MouseEvent mouseEvent) {
		if (mouseEvent.getClickCount() == 2) {
			submitValue(null);
		}
	}

	private void updateOKButton(String selectedUser) {
		btnOK.setDisable(selectedUser == null || selectedUser.isEmpty());
	}

	public void onFilterChange(KeyEvent keyEvent) {
		refreshUserList();
	}

	private void refreshUserList() {
		String filterText = userFilter.getText();
		String filterGroup = groupFilter.getText();

		List<String> userNames = null;

		if (filterGroup.equals("< All Groups >")) {
			userNames = repository.getFilteredUserList(filterText);

		} else {
			userNames = repository.getFilteredUserList(filterText, filterGroup);
		}

		userList.getItems().clear();
		userList.setItems(FXCollections.observableList(userNames));

		userListCount.setText("" + userNames.size());

		btnOK.setDisable(true);
	}

	public void submitValue(ActionEvent actionEvent) {
		callbackTarget.returnValue(userList.getSelectionModel().getSelectedItem(), callbackMessage);
		closeWindow(actionEvent);
	}

	public void closeWindow(ActionEvent actionEvent) {
		Stage stage = (Stage) btnCancel.getScene().getWindow();
		stage.close();
	}

	public void setCallbackTarget(DialogCallbackInterface callbackTarget, String callbackMessage) {
		this.callbackTarget = callbackTarget;
		this.callbackMessage = callbackMessage;
	}

	public void browseGroup(ActionEvent actionEvent) {
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
		controller.setAllowAllGroupsOption(true);
		controller.setCallbackTarget(this, null);

		selectGroupStage.showAndWait();
	}

	@Override
	public void returnValue(String returnValue, String callbackMessage2) {
		groupFilter.setText(returnValue);
		refreshUserList();
	}
}
