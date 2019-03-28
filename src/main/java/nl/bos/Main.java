package nl.bos;

import com.documentum.fc.common.DfException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import nl.bos.controllers.ConnectionWithStatus;
import nl.bos.controllers.Menu;
import nl.bos.controllers.QueryWithResult;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main extends Application {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    private final Repository repository = Repository.getInstance();

    private boolean devModeEnabled = false;

    private FXMLLoader bodyPaneLoader;
    private FXMLLoader rootPaneLoader;

    public FXMLLoader getBodyPaneLoader() {
        return bodyPaneLoader;
    }

    public FXMLLoader getRootPaneLoader() {
        return rootPaneLoader;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
        tryDevModeConnection();

        try {
            rootPaneLoader = new FXMLLoader(getClass().getResource("/nl/bos/views/Menu.fxml"));
            BorderPane rootPane = rootPaneLoader.load();
            Menu menuLoaderController = rootPaneLoader.getController();
            bodyPaneLoader = new FXMLLoader(getClass().getResource("/nl/bos/views/QueryWithResult.fxml"));
            VBox bodyLayout = bodyPaneLoader.load();

            if (devModeEnabled) {
                QueryWithResult queryWithResultController = bodyPaneLoader.getController();
                ConnectionWithStatus connectionWithStatusController = queryWithResultController.getConnectionWithStatusFxmlLoader().getController();
                connectionWithStatusController.getBtnReadQuery().setDisable(false);
                connectionWithStatusController.getBtnFlushCache().setDisable(false);

                Button btnDisconnect = connectionWithStatusController.getBtnDisconnect();
                btnDisconnect.managedProperty().bindBidirectional(btnDisconnect.visibleProperty());
                btnDisconnect.setManaged(true);

                Button btnConnect = connectionWithStatusController.getBtnConnect();
                btnConnect.managedProperty().bindBidirectional(btnConnect.visibleProperty());
                btnConnect.setManaged(false);

                menuLoaderController.getMenubar().setDisable(false);

                connectionWithStatusController.updateNodes(repository.getSession());
            }

            rootPane.setCenter(bodyLayout);

            Image image = new Image(getClass().getClassLoader().getResourceAsStream("nl/bos/icons/logo_16.gif"));
            primaryStage.getIcons().add(image);
            primaryStage.setTitle("DQL Tester FX");
            primaryStage.setScene(new Scene(rootPane));

            primaryStage.show();
            primaryStage.toFront();

        } catch (IOException | DfException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    private void shutdown() {
        LOGGER.info("Shutdown");
        repository.disconnect();
    }

    private void tryDevModeConnection() {
        List<String> parameters = getParameters().getRaw();

        if (parameters.size() < 3) {
            LOGGER.info("Login with connect button");
            return;
        }

        String repositoryName = parameters.get(0);
        String username = parameters.get(1);
        String password = parameters.get(2);

        repository.setCredentials(repositoryName, username, password, "");

        repository.createSessionManager();
        repository.createSession();

        if (repository.isConnected()) {
            LOGGER.info("Developer connection created");
            devModeEnabled = true;

        } else {
            LOGGER.info("Login with connect button");
        }
    }
}
