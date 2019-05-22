package nl.bos.menu.menuitem.action;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import nl.bos.Repository;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ManageUsersAction {
	private static final Logger LOGGER = Logger.getLogger(ManageUsersAction.class.getName());

	public ManageUsersAction() {
		Stage userEditorStage = new Stage();
		Repository repository = Repository.getInstance();
		userEditorStage.setTitle(String.format("User Editor - %s", repository.getRepositoryName()));
		userEditorStage.setResizable(false);
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/nl/bos/views/UserEditor.fxml"));

		try {
			AnchorPane userEditor = fxmlLoader.load();
			userEditorStage.setScene(new Scene(userEditor));

		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}

		userEditorStage.showAndWait();
	}
}
