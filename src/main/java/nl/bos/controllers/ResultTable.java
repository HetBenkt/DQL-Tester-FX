package nl.bos.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import nl.bos.Repository;

import java.util.logging.Logger;

public class ResultTable {
    private static final Logger LOGGER = Logger.getLogger(ResultTable.class.getName());

    private final Repository repository = Repository.getInstance();

    @FXML
    private Button btnOk;
    @FXML
    private Button btnRefresh;
    @FXML
    private TableView tvResults;
    @FXML
    private TextField txtCount;

    public void loadResult(String id) {
        LOGGER.info(id);
        txtCount.setText("0");
    }

    @FXML
    private void handleRefresh(ActionEvent actionEvent) {

    }

    @FXML
    private void handleOk(ActionEvent actionEvent) {
        Stage stage = (Stage) btnOk.getScene().getWindow();
        stage.close();
    }
}
