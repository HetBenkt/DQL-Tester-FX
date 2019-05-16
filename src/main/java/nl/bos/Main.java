package nl.bos;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static nl.bos.Constants.*;

public class Main extends Application {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    private final Repository repository = Repository.getInstance();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
        tryDevModeConnection();

        try {
            BorderPane rootPane = new FXMLLoader(getClass().getResource("/nl/bos/views/Menu.fxml")).load();
            VBox bodyLayout = new FXMLLoader(getClass().getResource("/nl/bos/views/QueryWithResult.fxml")).load();

            rootPane.setCenter(bodyLayout);

            Image image = new Image(getClass().getClassLoader().getResourceAsStream("nl/bos/icons/logo_16.gif"));

            primaryStage.setScene(new Scene(rootPane));
            primaryStage.getIcons().add(image);

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

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
