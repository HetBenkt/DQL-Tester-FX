package nl.bos;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import nl.bos.utils.Resources;

import java.util.List;
import java.util.logging.Logger;

import static nl.bos.Constants.*;

public class MyApplication extends Application {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    private String externalForm;
    private Repository repository;
    private Resources resources;
    private BorderPane rootPane;
    private Image image;

    @Override
    public void init() {
        repository = Repository.getInstance();
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
        tryDevModeConnection();
        resources = new Resources();
        Platform.runLater(() -> {
            rootPane = (BorderPane) resources.loadFXML("/nl/bos/views/Menu.fxml");
            VBox bodyLayout = (VBox) resources.loadFXML("/nl/bos/views/QueryWithResult.fxml");
            rootPane.setCenter(bodyLayout);

            image = new Image(resources.getResourceStream("nl/bos/icons/logo_16.gif"));
            externalForm = resources.getResourceExternalForm("/nl/bos/themes/dql-keywords.css");
        });
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setScene(new Scene(rootPane));
        primaryStage.getScene().getStylesheets()
                .add(externalForm);
        primaryStage.getIcons().add(image);

        primaryStage.setTitle(APP_TITLE);
        primaryStage.sizeToScene();

        primaryStage.show();
        primaryStage.setMinWidth(primaryStage.getWidth());
        primaryStage.setMinHeight(primaryStage.getHeight());
        primaryStage.toFront();
    }

    private void shutdown() {
        LOGGER.info(MSG_SHUTDOWN_HOOK);
        repository.disconnect();
    }

    private void tryDevModeConnection() {
        List<String> parameters = getParameters().getRaw();

        if (parameters.size() < 3) {
            LOGGER.info(MSG_USE_CONNECT_BUTTON);
            return;
        }

        String repositoryName = parameters.get(0);
        String username = parameters.get(1);
        String password = parameters.get(2);

        repository.setCredentials(repositoryName, username, password, "");

        repository.createSessionManager();
        repository.createSession();

        if (repository.isConnected()) {
            LOGGER.info(MSG_DEV_CONNECTION_CREATED);

        } else {
            LOGGER.info(MSG_USE_CONNECT_BUTTON);
        }
    }
}
