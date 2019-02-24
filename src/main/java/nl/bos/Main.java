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
import nl.bos.controllers.BodyPane;
import nl.bos.controllers.InputPane;
import nl.bos.controllers.RootPane;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main extends Application {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public FXMLLoader getBodyPaneLoader() {
        return bodyPaneLoader;
    }

    public FXMLLoader getRootPaneLoader() {
        return rootPaneLoader;
    }

    private static Main mainClass;
    private static boolean devModeEnabled = false;
    private FXMLLoader bodyPaneLoader;
    private FXMLLoader rootPaneLoader;

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
        if (args.length > 0) {
            Repository repositoryCon = Repository.getInstance();
            repositoryCon.setCredentials(args[0], args[1], args[2], "");
            repositoryCon.createSessionManager();
            if (repositoryCon.isConnectionValid()) {
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
            rootPaneLoader = new FXMLLoader(getClass().getResource("/nl/bos/views/RootPane.fxml"));
            BorderPane rootPane = rootPaneLoader.load();
            RootPane rootPaneLoaderController = rootPaneLoader.getController();
            bodyPaneLoader = new FXMLLoader(getClass().getResource("/nl/bos/views/BodyPane.fxml"));
            VBox bodyLayout = bodyPaneLoader.load();

            if (devModeEnabled) {
                BodyPane bodyPaneController = bodyPaneLoader.getController();
                InputPane inputPaneController = bodyPaneController.getFxmlLoader().getController();
                inputPaneController.getBtnReadQuery().setDisable(false);
                inputPaneController.getBtnFlushCache().setDisable(false);

                Button btnDisconnect = inputPaneController.getBtnDisconnect();
                btnDisconnect.managedProperty().bindBidirectional(btnDisconnect.visibleProperty());
                btnDisconnect.setManaged(true);

                Button btnConnect = inputPaneController.getBtnConnect();
                btnConnect.managedProperty().bindBidirectional(btnConnect.visibleProperty());
                btnConnect.setManaged(false);

                rootPaneLoaderController.getMenubar().setDisable(false);

                inputPaneController.updateNodes(Repository.getInstance().getSession());
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
