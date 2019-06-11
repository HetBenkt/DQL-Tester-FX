package nl.bos.controllers;

import java.io.File;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import nl.bos.Constants.Version;
import nl.bos.Repository;
import nl.bos.utils.Resources;

public class CheckinDialog {
	private static final Logger LOGGER = Logger.getLogger(CheckinDialog.class.getName());

	@FXML
	private Button btnOK;

	@FXML
	private Button btnCancel;

	@FXML
	private TextField filepath;

	private File checkinFile = null;

	private String objectId = null;

	private Stage checkinStage = null;

	@FXML
	private ToggleGroup checkinVersion = null;

	@FXML
	private CheckBox keepLock = null;

	private Repository repository = Repository.getInstance();

	@FXML
	public void initialize() {
		for (Toggle radio : checkinVersion.getToggles()) {
			radio.setUserData(Version.valueOf(((RadioButton) radio).getId()));
		}
	}

	@FXML
	private void loadFile() {
		checkinFile = Resources.selectFileFromFileChooser("Select Content", Resources.getExportPath());
		getCheckinFile();
	}

	private void getCheckinFile() {
		filepath.setText(checkinFile.getAbsolutePath());
		if (checkinFile == null || !checkinFile.exists() || !checkinFile.canRead()) {
			btnOK.setDisable(true);
			return;
		}
		LOGGER.info("Selected file " + checkinFile.getAbsolutePath());
		btnOK.setDisable(false);
	}

	@FXML
	private void handleCheckin() {
		if (checkinFile != null) {
			LOGGER.info("Checkin " + objectId + checkinVersion.getSelectedToggle().getUserData().toString());
			repository.checkin(objectId, checkinFile, (Version) checkinVersion.getSelectedToggle().getUserData(),
					keepLock.isSelected());
			checkinStage.fireEvent(new WindowEvent(checkinStage, WindowEvent.WINDOW_CLOSE_REQUEST));
		}
	}

	public void checkinDialog(String id) {
		this.objectId = id;
		String initPath = Resources.getContentPathFromCheckoutFile(id);
		if(initPath!=null) {
			checkinFile = new File(initPath);
			getCheckinFile();
		}
	}

	public void setStage(Stage stage) {
		this.checkinStage = stage;
	}

	@FXML
	private void handleExit(ActionEvent actionEvent) {
		LOGGER.info(String.valueOf(actionEvent.getSource()));
		Stage stage = (Stage) btnCancel.getScene().getWindow();
		stage.close();
	}

}
