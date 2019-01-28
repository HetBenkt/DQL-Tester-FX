package nl.bos.controllers;

import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.common.DfException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import nl.bos.Repository;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class DescribeObjectPane implements Initializable {
    private static final Logger log = Logger.getLogger(DescribeObjectPane.class.getName());
    private final Repository repositoryCon = Repository.getInstance();
    @FXML
    private Button btnOk, btnCancel;
    @FXML
    private TreeView tvTypesTables;
    @FXML
    private ComboBox<String> cbTypesTables;
    @FXML
    private TextField txtNrOfItems;

    @FXML
    private void handleTypesTables(ActionEvent actionEvent) {

    }

    @FXML
    private void handleOK(ActionEvent actionEvent) {

    }

    @FXML
    private void handleCancel(ActionEvent actionEvent) {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<String> items = FXCollections.observableArrayList();
        TreeItem<String> tiTypes = new TreeItem<>("Types");
        TreeItem<String> tiTables = new TreeItem<>("Tables");

        try {
            IDfCollection types = repositoryCon.query("select name from dm_type order by 1");
            while (types.next()) {
                items.add(types.getString("name"));
                TreeItem<String> item = new TreeItem<>(types.getString("name"));
                tiTypes.getChildren().add(item);
            }
            types.close();

            IDfCollection tables = repositoryCon.query("select object_name from dm_registered order by 1");
            while (tables.next()) {
                items.add(tables.getString("object_name"));
                TreeItem<String> item = new TreeItem<>(tables.getString("object_name"));
                tiTables.getChildren().add(item);
            }
            tables.close();

        } catch (DfException e) {
            log.info(e.getMessage());
        }

        FXCollections.sort(items);
        cbTypesTables.setItems(items);
        txtNrOfItems.setText(String.valueOf(items.size()));

        TreeItem rootItem = new TreeItem("Items");
        rootItem.setExpanded(true);

        rootItem.getChildren().add(tiTypes);
        rootItem.getChildren().add(tiTables);

        tvTypesTables.setRoot(rootItem);
    }
}
