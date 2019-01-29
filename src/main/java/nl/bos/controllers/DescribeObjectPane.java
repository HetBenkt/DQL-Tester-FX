package nl.bos.controllers;

import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.common.DfException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import nl.bos.DescribeObjectTreeItem;
import nl.bos.Repository;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class DescribeObjectPane implements Initializable {
    private static final Logger log = Logger.getLogger(DescribeObjectPane.class.getName());
    private final Repository repositoryCon = Repository.getInstance();
    private DescribeObjectTreeItem currentSelected;

    @FXML
    private Button btnOk, btnCancel;
    @FXML
    private TreeView tvTypesTables;
    @FXML
    private ComboBox<DescribeObjectTreeItem> cbTypesTables;
    @FXML
    private TextField txtNrOfItems;

    @FXML
    private void handleTypesTables(ActionEvent actionEvent) {
        DescribeObjectTreeItem selectedItem = cbTypesTables.getSelectionModel().getSelectedItem();
        MultipleSelectionModel selectionModel = tvTypesTables.getSelectionModel();
        selectionModel.select(selectedItem);
        currentSelected = selectedItem;
        btnOk.setDisable(false);
        tvTypesTables.scrollTo(selectionModel.getSelectedIndex());
    }

    @FXML
    private void handleOK(ActionEvent actionEvent) {
        log.info(String.valueOf(currentSelected));
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleCancel(ActionEvent actionEvent) {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<DescribeObjectTreeItem> items = FXCollections.observableArrayList();
        DescribeObjectTreeItem tiTypes = new DescribeObjectTreeItem("Types");
        DescribeObjectTreeItem tiTables = new DescribeObjectTreeItem("Tables");

        try {
            IDfCollection types = repositoryCon.query("select name from dm_type order by 1");
            while (types.next()) {
                DescribeObjectTreeItem item = new DescribeObjectTreeItem(types.getString("name"));
                items.add(item);
                tiTypes.getChildren().add(item);
            }
            types.close();

            IDfCollection tables = repositoryCon.query("select object_name from dm_registered order by 1");
            while (tables.next()) {
                DescribeObjectTreeItem item = new DescribeObjectTreeItem(tables.getString("object_name"));
                items.add(item);
                tiTables.getChildren().add(item);
            }
            tables.close();

        } catch (DfException e) {
            log.info(e.getMessage());
        }

        SortedList<DescribeObjectTreeItem> sorted = items.sorted();
        cbTypesTables.setItems(sorted);
        txtNrOfItems.setText(String.valueOf(items.size()));

        TreeItem rootItem = new TreeItem();
        rootItem.setExpanded(true);

        rootItem.getChildren().add(tiTypes);
        rootItem.getChildren().add(tiTables);

        tvTypesTables.setRoot(rootItem);
        tvTypesTables.setShowRoot(false);

        tvTypesTables.getSelectionModel().selectedItemProperty().addListener((observableValue, oldItem, newItem) -> {
            cbTypesTables.getSelectionModel().select((DescribeObjectTreeItem) newItem);
            currentSelected = (DescribeObjectTreeItem) newItem;
            btnOk.setDisable(false);
        });
    }

    @FXML
    private void handleTypeTable(ActionEvent actionEvent) {

    }

    @FXML
    private void handleType(ActionEvent actionEvent) {

    }

    @FXML
    private void handleTable(ActionEvent actionEvent) {

    }
}
