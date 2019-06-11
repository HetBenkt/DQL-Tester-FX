package nl.bos.controllers;

import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfId;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import nl.bos.DescribeObjectTreeItem;
import nl.bos.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static nl.bos.Constants.TABLE;
import static nl.bos.Constants.TYPE;

public class DescribeObject {
    private static final Logger LOGGER = Logger.getLogger(DescribeObject.class.getName());

    private final Repository repository = Repository.getInstance();

    private DescribeObjectTreeItem currentSelected;
    private ObservableList<DescribeObjectTreeItem> items;

    @FXML
    private Button btnOk;
    @FXML
    private Button btnCancel;
    @FXML
    private TreeView tvTypesTables;
    @FXML
    private ComboBox<DescribeObjectTreeItem> cbTypesTables;
    @FXML
    private TextField txtNrOfItems;
    private Stage describeObjectStage;

    public DescribeObjectTreeItem getCurrentSelected() {
        return currentSelected;
    }

    @FXML
    public void initialize() {
        if (repository.getSession() != null) {
            items = FXCollections.observableArrayList();
            DescribeObjectTreeItem tiTypes = initTypeParentWithChildren();
            DescribeObjectTreeItem tiTables = initTableParentWithChildren();
            initTreeView(tiTypes, tiTables);
        }
    }

    private DescribeObjectTreeItem initTypeParentWithChildren() {
        DescribeObjectTreeItem parent = new DescribeObjectTreeItem("Types");

        try {
            Map<IDfId, DescribeObjectTreeItem> typeById = new HashMap<>();
            Map<IDfId, IDfId> superTypes = new HashMap<>();

            IDfCollection types = repository.query("select a.r_object_id, a.name, b.r_object_id as \"super_id\" from dm_type a, dm_type b where a.super_name = b.name union select r_object_id, name, '' as \"super_id\" from dm_type where super_name is nullstring order by 2 asc");
            while (types.next()) {
                IDfId typeID = types.getId("r_object_id");
                IDfId superID = types.getId("super_id");
                DescribeObjectTreeItem item = new DescribeObjectTreeItem(types.getString("name"), TYPE);
                items.add(item);

                typeById.put(typeID, item);
                superTypes.put(typeID, superID);
            }
            types.close();

            for (Map.Entry<IDfId, DescribeObjectTreeItem> entry : typeById.entrySet()) {
                IDfId key = entry.getKey();
                IDfId superType = superTypes.get(key);
                if (superType.equals(key)) {
                    parent.getChildren().add(entry.getValue());
                } else {
                    DescribeObjectTreeItem parentItem = typeById.get(superType);
                    if (parentItem == null) {
                        parent.getChildren().add(entry.getValue());
                    } else {
                        parentItem.getChildren().add(entry.getValue());
                    }
                }
            }

        } catch (DfException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        parent.sortTree();
        return parent;
    }

    /**
     * @noinspection unchecked
     */
    private DescribeObjectTreeItem initTableParentWithChildren() {
        DescribeObjectTreeItem parent = new DescribeObjectTreeItem("Tables");

        try {
            IDfCollection tables = repository.query("select object_name from dm_registered order by 1");
            while (tables.next()) {
                DescribeObjectTreeItem item = new DescribeObjectTreeItem(tables.getString("object_name"), TABLE);
                items.add(item);
                parent.getChildren().add(item);
            }
            tables.close();
        } catch (DfException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        return parent;
    }

    /**
     * @noinspection unchecked
     */
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

    @FXML
    private void handleOK(ActionEvent actionEvent) {
        LOGGER.log(Level.INFO, "Currently Selected: {0}", currentSelected);
        describeObjectStage.fireEvent(new WindowEvent(describeObjectStage, WindowEvent.WINDOW_CLOSE_REQUEST));
    }

    @FXML
    private void handleCancel(ActionEvent actionEvent) {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleTypesTables(ActionEvent actionEvent) {
        DescribeObjectTreeItem selectedItem = cbTypesTables.getSelectionModel().getSelectedItem();
        MultipleSelectionModel selectionModel = tvTypesTables.getSelectionModel();
        //noinspection unchecked
        selectionModel.select(selectedItem);
        currentSelected = selectedItem;
        btnOk.setDisable(false);
        tvTypesTables.scrollTo(selectionModel.getSelectedIndex());
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

    public void setStage(Stage describeObjectStage) {
        this.describeObjectStage = describeObjectStage;
    }
}
