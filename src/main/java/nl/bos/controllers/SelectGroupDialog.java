package nl.bos.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import nl.bos.Repository;
import nl.bos.utils.DialogCallbackInterface;

import java.util.ArrayList;
import java.util.List;

public class SelectGroupDialog {
	private Repository repository = Repository.getInstance();
	private DialogCallbackInterface callbackTarget;
	private String callbackMessage;
	private boolean allowAllGroupsOption = false;

	@FXML
	private TextField groupFilter;
	@FXML
	private ListView<String> groupList;
	@FXML
	private Label groupListCount;
	@FXML
	private Button btnOK;
	@FXML
	private Button btnCancel;

	@FXML
	private void initialize() {
		groupList.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> updateOKButton(newValue));
		groupList.setOnMouseClicked(this::handleDoubleclick);

		refreshGroupList();
	}

	private void handleDoubleclick(MouseEvent mouseEvent) {
		if (mouseEvent.getClickCount() == 2) {
			submitValue(null);
		}
	}

	private void updateOKButton(String selectedGroup) {
		btnOK.setDisable(selectedGroup == null || selectedGroup.isEmpty());
	}

	public void onFilterChange(KeyEvent keyEvent) {
		refreshGroupList();
	}

	private void refreshGroupList() {
		String filterText = groupFilter.getText();
		List<String> groupNames = new ArrayList<>();

		if (allowAllGroupsOption) {
			groupNames.add("< All Groups >");
		}

		groupNames.addAll(repository.getFilteredGroupList(filterText));

		groupList.getItems().clear();
		groupList.setItems(FXCollections.observableList(groupNames));

		groupListCount.setText("" + groupNames.size());

		btnOK.setDisable(true);
	}

	public void submitValue(ActionEvent actionEvent) {
		callbackTarget.returnValue(groupList.getSelectionModel().getSelectedItem(), callbackMessage);
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

	public void setAllowAllGroupsOption(boolean allowAllGroupsOption) {
		this.allowAllGroupsOption = allowAllGroupsOption;
		refreshGroupList();
	}
}
