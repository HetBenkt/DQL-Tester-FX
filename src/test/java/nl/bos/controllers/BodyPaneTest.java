package nl.bos.controllers;

import com.sun.javafx.application.PlatformImpl;
import javafx.scene.control.ChoiceBox;
import org.junit.*;
import org.mockito.Mock;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.powermock.reflect.Whitebox;

public class BodyPaneTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Mock
    private ChoiceBox<Object> mockedCmbHistory;

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
        Whitebox.setInternalState(bodyPane, "cmbHistory", mockedCmbHistory);

        //when
        ChoiceBox<Object> expected = bodyPane.getCmbHistory();

        //then
        Assert.assertEquals(expected, mockedCmbHistory);
    }

    @Test
    public void getTaStatement() {
    }

    @Test
    public void getJsonObject() {
    }

    @Test
    public void getFxmlLoader() {
    }

    @Test
    public void updateResultTable() {
    }

    @Test
    public void updateResultTableWithStringInput() {
    }
}