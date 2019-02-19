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
import javafx.stage.WindowEvent;
import nl.bos.DescribeObjectTreeItem;
import nl.bos.Repository;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import static nl.bos.Constants.TABLE;
import static nl.bos.Constants.TYPE;

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

    public DescribeObjectTreeItem getCurrentSelected() {
        return currentSelected;
    }

    private ObservableList<DescribeObjectTreeItem> items;

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
        Stage describeObjectStage = RootPane.getDescribeObjectStage();
        describeObjectStage.fireEvent(new WindowEvent(describeObjectStage, WindowEvent.WINDOW_CLOSE_REQUEST));
    }

    @FXML
    private void handleCancel(ActionEvent actionEvent) {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (repositoryCon.getClient() != null) {
            items = FXCollections.observableArrayList();
            DescribeObjectTreeItem tiTypes = initTypeParentWithChildren();
            DescribeObjectTreeItem tiTables = initTableParentWithChildren();
            initTreeView(tiTypes, tiTables);
        }
    }

    private void initTreeView(DescribeObjectTreeItem... treeItems) {
        SortedList<DescribeObjectTreeItem> sorted = items.sorted();
        cbTypesTables.setItems(sorted);
        txtNrOfItems.setText(String.valueOf(items.size()));

        TreeItem rootItem = new TreeItem();
        rootItem.setExpanded(true);

        rootItem.getChildren().addAll(treeItems);

        tvTypesTables.setRoot(rootItem);
        tvTypesTables.setShowRoot(false);

        tvTypesTables.getSelectionModel().selectedItemProperty().addListener((observableValue, oldItem, newItem) -> {
            cbTypesTables.getSelectionModel().select((DescribeObjectTreeItem) newItem);
            currentSelected = (DescribeObjectTreeItem) newItem;
            btnOk.setDisable(false);
        });
    }

    private DescribeObjectTreeItem initTableParentWithChildren() {
        DescribeObjectTreeItem parent = new DescribeObjectTreeItem("Tables");

        try {
            IDfCollection tables = repositoryCon.query("select object_name from dm_registered order by 1");
            while (tables.next()) {
                DescribeObjectTreeItem item = new DescribeObjectTreeItem(tables.getString("object_name"), TABLE);
                items.add(item);
                parent.getChildren().add(item);
            }
            tables.close();
        } catch (DfException e) {
            log.finest(e.getMessage());
        }

        return parent;
    }

    private DescribeObjectTreeItem initTypeParentWithChildren() {
        DescribeObjectTreeItem parent = new DescribeObjectTreeItem("Types");

        try {
            IDfCollection types = repositoryCon.query("select name from dm_type order by 1");
            while (types.next()) {
                DescribeObjectTreeItem item = new DescribeObjectTreeItem(types.getString("name"), TYPE);
                items.add(item);
                parent.getChildren().add(item);
            }
            types.close();
        } catch (DfException e) {
            log.finest(e.getMessage());
        }

        return parent;
    }

    @FXML
    private void handleTypeTable(ActionEvent actionEvent) {
        items = FXCollections.observableArrayList();
        DescribeObjectTreeItem tiTypes = initTypeParentWithChildren();
        DescribeObjectTreeItem tiTables = initTableParentWithChildren();
        initTreeView(tiTypes, tiTables);
    }

    @FXML
    private void handleType(ActionEvent actionEvent) {
        items = FXCollections.observableArrayList();
        DescribeObjectTreeItem tiTypes = initTypeParentWithChildren();
        initTreeView(tiTypes);

    }

    @FXML
    private void handleTable(ActionEvent actionEvent) {
        items = FXCollections.observableArrayList();
        DescribeObjectTreeItem tiTables = initTableParentWithChildren();
        initTreeView(tiTables);
    }
}