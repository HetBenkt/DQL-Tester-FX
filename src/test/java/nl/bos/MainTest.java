package nl.bos;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

import static org.junit.matchers.JUnitMatchers.containsString;


public class MainTest extends ApplicationTest {
    private static final Logger log = Logger.getLogger(MainTest.class.getName());

    @Before
    public void setup() {

    }

    @Test
    public void authenticationFailTest() {
        clickOn("#btnConnect");
        clickOn("#txtUsername");
        write("dummy");
        clickOn("#txtPassword");
        write("dummy");
        clickOn("#btnLogin");
        Assert.assertThat(Repository.getInstance().getErrorMessage(), containsString("[DM_SESSION_E_AUTH_FAIL]"));
        clickOn("OK");
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader rootPaneLoader = new FXMLLoader(getClass().getResource("/nl/bos/views/RootPane.fxml"));
        BorderPane rootPane = rootPaneLoader.load();
        FXMLLoader bodyPaneLoader = new FXMLLoader(getClass().getResource("/nl/bos/views/BodyPane.fxml"));
        VBox bodyLayout = bodyPaneLoader.load();

        rootPane.setCenter(bodyLayout);

        primaryStage.setTitle("DQL Tester FX");
        primaryStage.setScene(new Scene(rootPane));

        primaryStage.show();
        primaryStage.toFront();
    }

    @After
    public void tearDown() throws TimeoutException {
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }
}