package nl.bos.controllers;

import com.sun.javafx.application.PlatformImpl;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.powermock.reflect.Whitebox;

import static org.powermock.api.mockito.PowerMockito.mock;

public class QueryWithResultTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @BeforeClass
    public static void initToolkit() {
        PlatformImpl.startup(() -> {
        });
    }

    @Test
    public void getCmbHistory() {
        //given
        final QueryWithResult queryWithResult = new QueryWithResult();
        ChoiceBox mockedCmbHistory = mock(ChoiceBox.class);
        Whitebox.setInternalState(queryWithResult, "historyStatements", mockedCmbHistory);

        //when
        ChoiceBox<Object> expected = queryWithResult.getHistoryStatements();

        //then
        Assert.assertEquals(expected, mockedCmbHistory);
    }

    @Test
    public void getTaStatement() {
        //given
        final QueryWithResult queryWithResult = new QueryWithResult();
        TextArea mockedTaStatement = mock(TextArea.class);
        Whitebox.setInternalState(queryWithResult, "statement", mockedTaStatement);

        //when
        TextArea expected = queryWithResult.getStatement();

        //then
        Assert.assertEquals(expected, mockedTaStatement);
    }

    @Test
    public void getJsonObject() {
        //given
        final QueryWithResult queryWithResult = new QueryWithResult();
        JSONObject mockedJsonObject = mock(JSONObject.class);
        Whitebox.setInternalState(queryWithResult, "jsonObject", mockedJsonObject);

        //when
        JSONObject expected = queryWithResult.getJsonObject();

        //then
        Assert.assertEquals(expected, mockedJsonObject);
    }

    @Test
    public void getFxmlLoader() {
        //given
        final QueryWithResult queryWithResult = new QueryWithResult();
        FXMLLoader mockedFxmlLoader = mock(FXMLLoader.class);
        Whitebox.setInternalState(queryWithResult, "connectionWithStatusFxmlLoader", mockedFxmlLoader);

        //when
        FXMLLoader expected = queryWithResult.getConnectionWithStatusFxmlLoader();

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