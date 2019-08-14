package nl.bos;

import javafx.application.Preloader;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.logging.Logger;

public class SplashScreenLoader extends Preloader {
    private static final Logger LOGGER = Logger.getLogger(SplashScreenLoader.class.getName());
    private ProgressBar bar;
    private Stage stage;
    private static SplashScreenLoader splashScreenLoader;

    public SplashScreenLoader() {
        LOGGER.info("Splashing...");
        splashScreenLoader = this;
    }

    public static synchronized SplashScreenLoader getInstance() {
        if (splashScreenLoader == null) {
            splashScreenLoader = new SplashScreenLoader();
        }
        return splashScreenLoader;
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        stage.setScene(createPreloaderScene());
        stage.show();
    }

    private Scene createPreloaderScene() {
        bar = new ProgressBar();
        BorderPane p = new BorderPane();
        p.setCenter(bar);
        return new Scene(p, 850, 825);
    }

    @Override
    public void handleStateChangeNotification(StateChangeNotification evt) {
        LOGGER.info(String.format("Change type: %s", evt.getType()));
    }

    @Override
    public void handleProgressNotification(ProgressNotification progressNotification) {
        LOGGER.info(String.format("Progess: %s", progressNotification.getProgress()));
        bar.setProgress(progressNotification.getProgress());
    }

    public Stage getStage() {
        return stage;
    }

    ProgressBar getBar() {
        return bar;
    }
}
