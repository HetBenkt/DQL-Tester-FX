package nl.bos;

import javafx.application.Preloader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class SplashScreenLoader extends Preloader {
    private ProgressBar bar;
    private Stage stage;


    @Override
    public void start(Stage stage) {
        this.stage = stage;
        stage.setResizable(false);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(createPreloaderScene());
        stage.show();
    }

    private Scene createPreloaderScene() {
        bar = new ProgressBar();
        bar.setPrefWidth(300);
        BorderPane borderPane = new BorderPane();
        BorderPane.setAlignment(bar, Pos.CENTER);
        BorderPane.setMargin(bar, new Insets(50)); // optional
        borderPane.setBottom(bar);
        Image image = new Image("nl/bos/background.png");
        borderPane.setBackground(new Background(new BackgroundImage(image,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                BackgroundSize.DEFAULT)));
        return new Scene(borderPane, 875, 875);
    }

    @Override
    public void handleStateChangeNotification(StateChangeNotification stateChangeNotification) {
        if (stateChangeNotification.getType().equals(StateChangeNotification.Type.BEFORE_START)) {
            stage.close();
        }
    }
}
