package nl.bos;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

import static org.testfx.api.FxToolkit.registerPrimaryStage;


public class MainTest extends ApplicationTest {
    private static final Logger log = Logger.getLogger(MainTest.class.getName());

    @BeforeClass
    public static void setupSpec() throws Exception {
        if (Boolean.getBoolean("headless")) {
            System.setProperty("testfx.robot", "glass");
            System.setProperty("testfx.headless", "true");
            System.setProperty("java.awt.headless", "true");
            System.setProperty("glass.platform", "Monocle");
            System.setProperty("monocle.platform", "Headless");
            System.setProperty("prism.order", "sw");
            System.setProperty("prism.text", "t2k"); //javafx_font.dll is missing in openjdk11, so this falls back to the default
            System.setProperty("prism.verbose", "true");
        }

        if (System.getProperty("glass.platform", "").equals("Monocle")) {
//when we are running test headless (using Monocle), the whole vm process crash with a EXCEPTION_ACCESS_VIOLATION
//it is some problem with -fx-effect in css files. We simply mess up how Node looks up what can be styleable
//in java fx. i.e. javafx doesn’t think that -fx-effect is a valid css function
            try {
                final Class styleablePropertiesClass = Class.forName("javafx.scene.Node$StyleableProperties");
                final Class cssMetaDataClass = Class.forName("javafx.css.CssMetaData");
                final Field propertyField = cssMetaDataClass.getDeclaredField("property");
                propertyField.setAccessible(true);

                Field field = styleablePropertiesClass.getDeclaredField("STYLEABLES");
                field.setAccessible(true);
                List origStyleables = (List) field.get(null);
                for (Object styleable : origStyleables) {
                    String propValue = (String) propertyField.get(styleable);
                    if (propValue.equals("-fx-effect")) {
                        propertyField.set(styleable, propValue + "sometexttomessup");
                    }
                }
            } catch (Exception e) {
                log.finest(e.getMessage());
            }
        }

        registerPrimaryStage();
    }

    @Test
    public void authenticationFailTest() {
        clickOn("#btnConnect");
        clickOn("#txtUsername");
        write("dummy");
        clickOn("#txtPassword");
        write("dummy");
        clickOn("#btnLogin");
        Assert.assertThat(Repository.getInstance().getErrorMessage(), CoreMatchers.containsString("[DM_SESSION_E_AUTH_FAIL]"));
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