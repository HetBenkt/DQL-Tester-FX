package nl.bos.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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
    @FXML
    private CheckBox cbResultPerItem;

    private TableView result;

    @FXML
    private void callExecuteAPI(ActionEvent actionEvent) {
        String script = generateScript(txaTemplate.getText());
        new ExecuteAPIScriptAction(script);
    }

    private String generateScript(String template) {
        if (cbResultPerItem.isSelected()) {
            StringBuilder templateBuilder = new StringBuilder();
            for (int rowIndex = 0; rowIndex < result.getItems().size(); rowIndex++) {
                templateBuilder.append(loopOverColums(rowIndex, template));
                templateBuilder.append(System.lineSeparator());
            }
            template = templateBuilder.toString();
        } else {
            template = loopOverColums(result.getSelectionModel().getSelectedIndex(), template);
        }
        return template;
    }

    private String loopOverColums(int rowIndex, String template) {
        List<TableColumn> columns = result.getColumns();
        int columnIndex = 0;
        for (TableColumn column : columns) {
            String cellData = String.valueOf(((AttributeTableColumn) result.getColumns().get(columnIndex)).getCellData(result.getItems().get(rowIndex)));
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
