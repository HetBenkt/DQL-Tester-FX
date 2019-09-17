package nl.bos.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import nl.bos.AttributeTableColumn;
import nl.bos.menu.menuitem.action.ExecuteAPIScriptAction;

import java.util.List;

public class GenerateAPIScript {
    @FXML
    private Button btnClose;
    @FXML
    private TextArea txaTemplate;
    @FXML
    private TextArea txaColumns;
    private TableView result;

    @FXML
    private void callExecuteAPI(ActionEvent actionEvent) {
        String script = generateScript(txaTemplate.getText());
        new ExecuteAPIScriptAction(script);
    }

    private String generateScript(String template) {
        List<TableColumn> columns = result.getColumns();
        int columnIndex = 0;
        for (TableColumn column : columns) {
            String cellData = String.valueOf(((AttributeTableColumn) result.getColumns().get(columnIndex)).getCellData(result.getItems().get(result.getSelectionModel().getSelectedIndex())));
            template = template.replace(String.format("{%s}", column.getText()), cellData);
            columnIndex++;
        }
        return template;
    }

    @FXML
    private void closeScreen(ActionEvent actionEvent) {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }

    public void injectData(TableView result) {
        this.result = result;
        List<TableColumn> columns = result.getColumns();
        StringBuilder stringBuilder = new StringBuilder();
        for (TableColumn column : columns) {
            stringBuilder.append(String.format("{%s} - ", column.getText()));
        }

        txaColumns.setText(String.valueOf(stringBuilder));
    }
}
