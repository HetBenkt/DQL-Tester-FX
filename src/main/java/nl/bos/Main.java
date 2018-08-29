package nl.bos;

import com.documentum.fc.client.IDfCollection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.extern.java.Log;

@Log
public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Repository repository = Repository.getRepositoryCon();
        IDfCollection query = repository.query("select * from dm_docbase_config");
        while (query.next()) {
            log.info(query.getString("r_object_id"));
        }

        BorderPane rootPane = FXMLLoader.load(getClass().getResource("/nl/bos/views/RootPane.fxml"));
        VBox bodyLayout = FXMLLoader.load(getClass().getResource("/nl/bos/views/BodyPane.fxml"));

        rootPane.setCenter(bodyLayout);

        primaryStage.setTitle("DQL Tester 16.4");
        primaryStage.setScene(new Scene(rootPane));
        primaryStage.show();

    }
}
