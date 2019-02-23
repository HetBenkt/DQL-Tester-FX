package nl.bos.controllers;

import com.documentum.fc.client.DfACL;
import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.common.DfException;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import nl.bos.MyTreeItem;
import nl.bos.Repository;

import java.util.List;
import java.util.logging.Logger;

import static nl.bos.Constants.*;

public class RepositoryBrowserPane implements ChangeListener<TreeItem<MyTreeItem>>, EventHandler<ActionEvent> {
    private static final Logger log = Logger.getLogger(RepositoryBrowserPane.class.getName());

    @FXML
    private TreeView<MyTreeItem> treeView;
    @FXML
    private TextField txtObjectId;
    @FXML
    private TextField txtObjectType;
    @FXML
    private TextField txtContentType;
    @FXML
    private TextField txtContentSize;
    @FXML
    private TextField txtCreationDate;
    @FXML
    private TextField txtModifyDate;
    @FXML
    private TextField txtLockOwner;
    @FXML
    private TextField txtLockMachine;
    @FXML
    private TextField txtLockDate;
    @FXML
    private TextField txtAclName;
    @FXML
    private TextField txtPermission;
    @FXML
    private TextField txtVersion;
    @FXML
    private Button btnExit;
    @FXML
    private Label lblNrOfItems;
    @FXML
    private CheckBox ckbShowAllCabinets;
    @FXML
    private CheckBox ckbShowAllVersions;

    private MyTreeItem rootItem;

    private final Repository repositoryCon = Repository.getInstance();
    private final ContextMenu rootContextMenu = new ContextMenu();
    private MyTreeNode selected;

    @FXML
    private void handleExit(ActionEvent actionEvent) {
        log.info(String.valueOf(actionEvent.getSource()));
        Stage stage = (Stage) btnExit.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void initialize() {
        MenuItem miDump = new MenuItem("Get Attributes");
        miDump.setOnAction(this);
        rootContextMenu.getItems().add(miDump);

        rootItem = new MyTreeItem(null, repositoryCon.getRepositoryName(), TYPE_REPOSITORY, "");
        TreeItem<MyTreeItem> treeItemBrowser = buildTreeItemBrowser(rootItem);
        treeItemBrowser.setExpanded(true);
        treeView.setRoot(treeItemBrowser);
        treeView.getSelectionModel().selectedItemProperty().addListener(this);
        treeView.addEventHandler(MouseEvent.MOUSE_RELEASED, mouseEvent -> {
            log.finest(String.format("Clickcount: %s", String.valueOf(mouseEvent.getClickCount())));
            selected = (MyTreeNode) treeView.getSelectionModel().getSelectedItem();
            if (selected != null && !selected.isExpanded())
                selected.isFirstTimeChildren = true;
            if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                selected = (MyTreeNode) treeView.getSelectionModel().getSelectedItem();
                //item is selected - this prevents fail when clicking on empty space
                if (selected != null && !selected.getValue().getType().equals(TYPE_REPOSITORY)) {
                    //open context menu on current screen position
                    rootContextMenu.show(treeView, mouseEvent.getScreenX(), mouseEvent.getScreenY());
                }
            } else {
                //any other click cause hiding menu
                rootContextMenu.hide();
            }
        });
    }

    private TreeItem<MyTreeItem> buildTreeItemBrowser(MyTreeItem treeItem) {
        return createNode(treeItem);
    }

    // This method creates a TreeItem to represent the given File. It does this
    // by overriding the TreeItem.getChildren() and TreeItem.isLeaf() methods
    // anonymously, but this could be better abstracted by creating a
    // 'FileTreeItem' subclass of TreeItem. However, this is left as an exercise
    // for the reader.
    private TreeItem<MyTreeItem> createNode(final MyTreeItem treeItem) {
        Image image = new Image(getClass().getClassLoader().getResourceAsStream(String.format("nl/bos/icons/type/t_%s_16.gif", treeItem.getType())));
        try {
            if (treeItem.getType().equals(TYPE_CABINET)) {
                boolean isPrivate = treeItem.getObject().getBoolean(ATTR_IS_PRIVATE);
                if (isPrivate)
                    image = new Image(getClass().getClassLoader().getResourceAsStream("nl/bos/icons/type/t_mycabinet_16.gif"));
            } else if (treeItem.getType().equals(TYPE_DOCUMENT)) {
                String lockOwner = treeItem.getObject().getString(ATTR_R_LOCK_OWNER);
                if (!lockOwner.equals(""))
                    image = new Image(getClass().getClassLoader().getResourceAsStream("nl/bos/icons/type/t_dm_document_lock_16.gif"));
            }

        } catch (DfException e) {
            log.finest(e.getMessage());
        }
        ImageView imageView = new ImageView(image);
        return new MyTreeNode(treeItem, imageView);
    }

    @Override
    public void changed(ObservableValue<? extends TreeItem<MyTreeItem>> observable, TreeItem<MyTreeItem> oldValue, TreeItem<MyTreeItem> newValue) {
        MyTreeItem selectedItem = newValue.getValue();
        log.info(String.format("Selected item: %s", selectedItem.getName()));
        IDfPersistentObject selectedObject = selectedItem.getObject();
        if (selectedObject != null) {
            try {
                txtObjectId.setText(selectedObject.getObjectId().getId());
                txtObjectType.setText(selectedObject.getType().getName());
                txtContentType.setText(selectedObject.getString("a_content_type"));
                txtContentSize.setText(selectedObject.getString("r_content_size"));
                txtCreationDate.setText(selectedObject.getTime("r_creation_date").asString(""));
                txtModifyDate.setText(selectedObject.getTime("r_modify_date").asString(""));
                txtLockOwner.setText(selectedObject.getString("r_lock_owner"));
                txtLockMachine.setText(selectedObject.getString("r_lock_machine"));
                txtLockDate.setText(selectedObject.getTime("r_lock_date").asString(""));
                txtAclName.setText(selectedObject.getString("acl_name"));
                txtPermission.setText(convertPermitToLabel(selectedObject.getInt("owner_permit")));
                txtVersion.setText(getRepeatingValue(selectedObject, "r_version_label"));
            } catch (DfException e) {
                log.finest(e.getMessage());
            }
        } else {
            txtObjectId.setText("");
            txtObjectType.setText("");
            txtContentType.setText("");
            txtContentSize.setText("");
            txtCreationDate.setText("");
            txtModifyDate.setText("");
            txtLockOwner.setText("");
            txtLockMachine.setText("");
            txtLockDate.setText("");
            txtAclName.setText("");
            txtPermission.setText("");
            txtVersion.setText("");
        }
    }

    private String getRepeatingValue(IDfPersistentObject object, String attribute) throws DfException {
        int count = object.getValueCount(attribute);
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < count; i++) {
            result.append(object.getRepeatingString(attribute, i));
            if (i != count - 1)
                result.append(", ");
        }
        return String.valueOf(result);
    }

    private String convertPermitToLabel(int permit) {
        if (permit == DfACL.DF_PERMIT_NONE)
            return DfACL.DF_PERMIT_NONE_STR;
        if (permit == DfACL.DF_PERMIT_BROWSE)
            return DfACL.DF_PERMIT_BROWSE_STR;
        if (permit == DfACL.DF_PERMIT_READ)
            return DfACL.DF_PERMIT_READ_STR;
        if (permit == DfACL.DF_PERMIT_RELATE)
            return DfACL.DF_PERMIT_RELATE_STR;
        if (permit == DfACL.DF_PERMIT_VERSION)
            return DfACL.DF_PERMIT_VERSION_STR;
        if (permit == DfACL.DF_PERMIT_WRITE)
            return DfACL.DF_PERMIT_WRITE_STR;
        if (permit == DfACL.DF_PERMIT_DELETE)
            return DfACL.DF_PERMIT_DELETE_STR;
        return "";
    }

    @Override
    public void handle(ActionEvent event) {
        try {
            log.info(selected.getValue().getObject().getObjectId().getId());
            Stage dumpAttributes = new Stage();
            dumpAttributes.setTitle(String.format("Attributes List - %s (%s)", selected.getValue().getObject().getObjectId().getId(), repositoryCon.getRepositoryName()));
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/nl/bos/views/GetAttributesPane.fxml"));
            VBox loginPane = fxmlLoader.load();
            Scene scene = new Scene(loginPane);
            dumpAttributes.setScene(scene);
            GetAttributesPane controller = fxmlLoader.getController();
            controller.initTextArea(selected.getValue().getObject());
            dumpAttributes.showAndWait();
        } catch (Exception e) {
            log.finest(e.getMessage());
        }
    }

    @FXML
    private void handleShowAllCabinets(ActionEvent actionEvent) {
        TreeItem<MyTreeItem> treeItemBrowser = buildTreeItemBrowser(rootItem);
        treeItemBrowser.setExpanded(true);
        treeView.setRoot(treeItemBrowser);
    }

    private class MyTreeNode extends TreeItem<MyTreeItem> {
        // We cache whether the File is a leaf or not. A File is a leaf if
        // it is not a directory and does not have any files contained within
        // it. We cache this as isLeaf() is called often, and doing the
        // actual check on File is expensive.
        private boolean isLeaf;

        // We do the children and leaf testing only once, and then set these
        // booleans to false so that we do not check again during this
        // run. A more complete implementation may need to handle more
        // dynamic file system situations (such as where a folder has files
        // added after the TreeView is shown). Again, this is left as an
        // exercise for the reader.
        private boolean isFirstTimeChildren;
        private boolean isFirstTimeLeaf;

        private MyTreeNode(MyTreeItem treeItem, ImageView imageView) {
            super(treeItem, imageView);
            isFirstTimeChildren = true;
            isFirstTimeLeaf = true;
        }


        @Override
        public ObservableList<TreeItem<MyTreeItem>> getChildren() {
            if (isFirstTimeChildren) {
                isFirstTimeChildren = false;

                // First getChildren() call, so we actually go off and
                // determine the children of the File contained in this TreeItem.
                super.getChildren().setAll(buildChildren(this));
            }
            return super.getChildren();
        }

        @Override
        public boolean isLeaf() {
            if (isFirstTimeLeaf) {
                isFirstTimeLeaf = false;
                MyTreeItem treeItem = getValue();
                isLeaf = !treeItem.isDirectory();
            }
            return isLeaf;
        }

        private ObservableList<TreeItem<MyTreeItem>> buildChildren(MyTreeNode parent) {
            MyTreeItem parentItem = parent.getValue();
            if (parentItem != null && parentItem.isDirectory()) {
                List<MyTreeItem> treeItems = parentItem.listObjects(parentItem, ckbShowAllCabinets.isSelected(), ckbShowAllVersions.isSelected());
                lblNrOfItems.setText(String.format("%s items found", treeItems.size()));
                ObservableList<TreeItem<MyTreeItem>> children = FXCollections.observableArrayList();
                for (MyTreeItem treeItem : treeItems) {
                    children.add(createNode(treeItem));
                }
                return children;
            }
            return FXCollections.emptyObservableList();
        }
    }
}
