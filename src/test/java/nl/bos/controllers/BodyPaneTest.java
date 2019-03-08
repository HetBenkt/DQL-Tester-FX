package nl.bos.controllers;

import com.sun.javafx.application.PlatformImpl;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import org.json.JSONObject;
import org.junit.*;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.powermock.reflect.Whitebox;

import static org.powermock.api.mockito.PowerMockito.mock;

public class BodyPaneTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @BeforeClass
    public static void javaFXInit() {
        PlatformImpl.startup(() -> {
        });
    }

    @AfterClass
    public static void javaFXExit() {
        PlatformImpl.exit();
    }

    @Test
    public void getCmbHistory() {
        //given
        final BodyPane bodyPane = new BodyPane();
        ChoiceBox mockedCmbHistory = mock(ChoiceBox.class);
        Whitebox.setInternalState(bodyPane, "cmbHistory", mockedCmbHistory);

        //when
        ChoiceBox<Object> expected = bodyPane.getCmbHistory();

        //then
        Assert.assertEquals(expected, mockedCmbHistory);
    }

    @Test
    public void getTaStatement() {
        //given
        final BodyPane bodyPane = new BodyPane();
        TextArea mockedTaStatement = mock(TextArea.class);
        Whitebox.setInternalState(bodyPane, "taStatement", mockedTaStatement);

        //when
        TextArea expected = bodyPane.getTaStatement();

        //then
        Assert.assertEquals(expected, mockedTaStatement);
    }

    @Test
    public void getJsonObject() {
        //given
        final BodyPane bodyPane = new BodyPane();
        JSONObject mockedJsonObject = mock(JSONObject.class);
        Whitebox.setInternalState(bodyPane, "jsonObject", mockedJsonObject);

        //when
        JSONObject expected = bodyPane.getJsonObject();

        //then
        Assert.assertEquals(expected, mockedJsonObject);
    }

    @Test
    public void getFxmlLoader() {
        //given
        final BodyPane bodyPane = new BodyPane();
        FXMLLoader mockedFxmlLoader = mock(FXMLLoader.class);
        Whitebox.setInternalState(bodyPane, "fxmlLoader", mockedFxmlLoader);

        //when
        FXMLLoader expected = bodyPane.getFxmlLoader();

        //then
        Assert.assertEquals(expected, mockedFxmlLoader);
    }

    @Test
    public void updateResultTable() {
    }

    @Test
    public void updateResultTableWithStringInput() {
    }
}