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
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main extends Application {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    private final Repository repository = Repository.getInstance();

    private static Main mainClass;
    private static boolean devModeEnabled = false;

    private FXMLLoader bodyPaneLoader;
    private FXMLLoader rootPaneLoader;

    public FXMLLoader getBodyPaneLoader() {
        return bodyPaneLoader;
    }

    public FXMLLoader getRootPaneLoader() {
        return rootPaneLoader;
    }

    public Main() {
        mainClass = this;
    }

    public static synchronized Main getInstance() {
        if (mainClass == null) {
            mainClass = new Main();
        }
        return mainClass;
    }

    public static void main(String[] args) {
        Main main = new Main();
        if (args.length > 0) {
            main.repository.setCredentials(args[0], args[1], args[2], "");
            main.repository.createSessionManager();
            if (main.repository.isConnectionValid()) {
                LOGGER.info("Developer connection created");
                devModeEnabled = true;
            } else {
                LOGGER.info("Login with connect button");
            }
        } else {
            LOGGER.info("Login with connect button");
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> LOGGER.info("Shutdown hook")));
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
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
}
