package nl.bos;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.extern.java.Log;
import nl.bos.controllers.BodyPane;
import nl.bos.controllers.InputPane;
import nl.bos.controllers.RootPane;

@Log
public class Main extends Application {
    @Getter
    private static FXMLLoader bodyPaneLoader;
    private static boolean devModeEnabled = false;
    @Getter
    private static FXMLLoader rootPaneLoader;

    public static void main(String[] args) {
        if (args.length > 0) {
            Repository.setCredentials(args[0], args[1], args[2], "");
            if (Repository.isConnectionValid()) {
                log.info("Developer connection created");
                devModeEnabled = true;
            } else {
                log.info("Login with connect button");
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

        primaryStage.setTitle("DQL Tester 16.4");
        primaryStage.setScene(new Scene(rootPane));

        primaryStage.show();
    }
}
