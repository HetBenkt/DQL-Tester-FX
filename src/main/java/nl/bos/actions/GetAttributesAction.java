package nl.bos.actions;

import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import nl.bos.Repository;
import nl.bos.controllers.GetAttributes;
import nl.bos.utils.Resources;

import java.util.logging.Logger;

public class GetAttributesAction {
	private static final Logger LOGGER = Logger.getLogger(GetAttributesAction.class.getName());

	public GetAttributesAction(String id) {
		LOGGER.info("GetAttributesAction(" + id + ")");
		Stage getAttributesStage = new Stage();
		getAttributesStage.setTitle(String.format("Attributes List - %s (%s)", id, Repository.getInstance().getRepositoryName()));
		Resources resources = new Resources();
		VBox loginPane = (VBox) resources.loadFXML("/nl/bos/views/GetAttributes.fxml");
		Scene scene = new Scene(loginPane);
		getAttributesStage.setScene(scene);
		GetAttributes controller = resources.getFxmlLoader().getController();
		controller.dumpObject(id);
		getAttributesStage.showAndWait();
	}
}
