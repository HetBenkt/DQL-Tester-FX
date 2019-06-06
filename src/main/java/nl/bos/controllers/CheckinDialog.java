package nl.bos.controllers;

import java.io.File;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import nl.bos.Repository;
import nl.bos.utils.Resources;

public class CheckinDialog {
	private static final Logger LOGGER = Logger.getLogger(CheckinDialog.class.getName());
	
	@FXML
	private Button btnExecute;
	
	@FXML
	private Button btnCancel;
	
	@FXML
	private TextField filepath;

	private File checkin = null;

	private String objectId = null;
	
	private Stage checkinStage = null;
	
	@FXML
	public void initialize() {
		// No implementation needed
	}

	@FXML
	private void loadFile() {
		File checkin = Resources.createFileFromFileChooser("Select Content");

		if (checkin == null || !checkin.exists() || !checkin.canRead()) {
			return;
		}

	}

	@FXML
    private void handleCheckin() {
    	if(checkin!=null) {
    		Repository.getInstance().checkin(objectId, checkin);
    		checkinStage.fireEvent(new WindowEvent(checkinStage, WindowEvent.WINDOW_CLOSE_REQUEST));
    	}
    }

	public void checkinDialog(String id) {
		this.objectId = id;
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
