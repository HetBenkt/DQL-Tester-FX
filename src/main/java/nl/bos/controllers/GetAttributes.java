package nl.bos.controllers;

import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.common.DfException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import nl.bos.Repository;
import nl.bos.utils.AppAlert;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetAttributes {
    private static final Logger LOGGER = Logger.getLogger(GetAttributes.class.getName());

    private final Repository repository = Repository.getInstance();
    private final List<Integer> previousIndexes = new ArrayList<>();

	private String dumpContent = "";
    private int lastIndex;

    @FXML
    private TextField txtObjectId;
    @FXML
    private Button btnExit;
    @FXML
    private TextArea txaAttributes;
    @FXML
    private TextField txtSearch;
    @FXML
    private CheckBox chkCaseSensitive;
    @FXML
    private VBox vboxGetAttributes;

    /**
     * @noinspection EmptyMethod
     */
    @FXML
    private void initialize() {
        //No implementation needed
    }

    @FXML
    private void handleExit(ActionEvent actionEvent) {
        LOGGER.info(String.valueOf(actionEvent.getSource()));
        Stage stage = (Stage) btnExit.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleFind(ActionEvent actionEvent) {
    	lastIndex = 0;
        previousIndexes.clear();

		handleFindNext(actionEvent);
    }

    @FXML
    private void handleFindPrevious(ActionEvent actionEvent) {
		String searchTerm = txtSearch.getText();
		String content = dumpContent.replace("\n", " ");

		if (!chkCaseSensitive.isSelected()) {
			searchTerm = searchTerm.toUpperCase();
			content = content.toUpperCase();
		}

		searchFindPrevious(searchTerm, content);
    }

    private void searchFindPrevious(String patternText, String matchText) {
        Pattern pattern = Pattern.compile(patternText);
        Matcher matcher = pattern.matcher(matchText);

        if (previousIndexes.isEmpty()) {
        	return;
		}

		if (!matcher.find(previousIndexes.get(previousIndexes.size() - 1))) {
			AppAlert.warning("Information Dialog", String.format("String not found: %s", patternText));
			return;
		}

		if (previousIndexes.size() > 1) {
			previousIndexes.remove(previousIndexes.size() - 1);
		}

		lastIndex = matcher.end();
		txaAttributes.selectRange(matcher.start(), lastIndex);
	}

    @FXML
    private void handleFindNext(ActionEvent actionEvent) {
		String searchTerm = txtSearch.getText();
		String content = dumpContent.replace("\n", " ");

		if (!chkCaseSensitive.isSelected()) {
			searchTerm = searchTerm.toUpperCase();
			content = content.toUpperCase();
		}

		searchFindNext(searchTerm, content);
    }

    private void searchFindNext(String patternText, String matchText) {
        Pattern pattern = Pattern.compile(patternText);
        Matcher matcher = pattern.matcher(matchText);

        if (!matcher.find(lastIndex)) {
        	if (lastIndex == 0) {
				AppAlert.warning("Information Dialog", String.format("String not found: %s", patternText));

			} else {
				AppAlert.warning("Information Dialog", "EOF reached!");
			}

        	return;
		}

		int index = lastIndex - txtSearch.getText().length();

		if (index < 0) {
			index = 0;
		}

		previousIndexes.add(index);
		lastIndex = matcher.end();
		txaAttributes.selectRange(matcher.start(), lastIndex);
    }

    @FXML
    private void handleDump(ActionEvent actionEvent) {
    	String dumpId = txtObjectId.getText();

    	if (!repository.isObjectId(dumpId)) {
    		AppAlert.warning("Invalid object ID", String.format("The given object ID is not valid: %s", dumpId));
    		return;
		}

    	dumpObject(txtObjectId.getText());
    }

	public void dumpObject(String objectId) {
    	txtObjectId.setText(objectId);

		try {
			IDfPersistentObject object = repository.getObjectById(objectId);

			dumpContent = object.dump();
			txaAttributes.setText(dumpContent);

			Stage getAttributesStage = (Stage) vboxGetAttributes.getScene().getWindow();
			getAttributesStage.setTitle(String.format("Attributes List - %s (%s)", objectId, repository.getRepositoryName()));

		} catch (DfException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			AppAlert.error("Information Dialog", e.getMessage());
		}
	}

	@FXML
    private void handleCaseSensitive(ActionEvent actionEvent) {
        lastIndex = 0;
        previousIndexes.clear();
        previousIndexes.add(lastIndex);
    }
}
