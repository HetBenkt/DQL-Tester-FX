package nl.bos;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.extern.java.Log;

@Log
public class Main extends Application {
    @Getter
    private static FXMLLoader bodyPaneLoader;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        BorderPane rootPane = FXMLLoader.load(getClass().getResource("/nl/bos/views/RootPane.fxml"));
        bodyPaneLoader = new FXMLLoader(getClass().getResource("/nl/bos/views/BodyPane.fxml"));
        VBox bodyLayout = bodyPaneLoader.load();

        rootPane.setCenter(bodyLayout);

        primaryStage.setTitle("DQL Tester 16.4");
        primaryStage.setScene(new Scene(rootPane));
        primaryStage.show();
    }
}
