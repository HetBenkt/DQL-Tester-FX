package nl.bos;

import com.documentum.fc.common.DfException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import nl.bos.controllers.BodyPane;
import nl.bos.controllers.InputPane;
import nl.bos.controllers.RootPane;

import java.util.logging.Logger;

public class Main extends Application {
    private static final Logger log = Logger.getLogger(Main.class.getName());

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
            try {
                repositoryCon.createSessionManager();
                if (repositoryCon.isConnectionValid()) {
                    log.info("Developer connection created");
                    devModeEnabled = true;
                } else {
                    log.info("Login with connect button");
                }
            } catch (DfException dfe) {
                log.finest(dfe.getMessage());
            }
        } else {
            log.info("Login with connect button");
        }

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
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
        }

        rootPane.setCenter(bodyLayout);

        primaryStage.setTitle("DQL Tester FX");
        primaryStage.setScene(new Scene(rootPane));

        primaryStage.show();
        primaryStage.toFront();
    }
}
