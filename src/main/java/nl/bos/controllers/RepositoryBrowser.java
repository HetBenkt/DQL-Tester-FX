package nl.bos.controllers;

import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.common.DfException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lombok.extern.java.Log;
import nl.bos.MyTreeItem;
import nl.bos.Repository;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

@Log
public class RepositoryBrowser implements Initializable, EventHandler<MouseEvent> {
    @FXML
    private TreeView<File> treeview;
    private Repository repositoryCon = Repository.getInstance();
    private String currentPath = "";

    @FXML
    private void handleExit(ActionEvent actionEvent) {
        log.info(String.valueOf(actionEvent.getSource()));
        Stage browseRepositoryStage = RootPane.getBrowseRepositoryStage();
        browseRepositoryStage.fireEvent(new WindowEvent(browseRepositoryStage, WindowEvent.WINDOW_CLOSE_REQUEST));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.info(String.valueOf(location));
        Repository repositoryCon = Repository.getInstance();

        MyTreeItem rootItem = new MyTreeItem(repositoryCon.getRepositoryName(), "repository", new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("repository_16.png"))));
        rootItem.setExpanded(true);

        try {
            IDfCollection cabinets = repositoryCon.query("select r_object_id, object_name, is_private from dm_cabinet order by object_name");
            while (cabinets.next()) {
                MyTreeItem item = new MyTreeItem(cabinets.getString("object_name"), "cabinet", new ImageView(
                        new Image(getClass().getClassLoader().getResourceAsStream("cabinet_16.png"))));
                rootItem.getChildren().add(item);
            }
        } catch (DfException e) {
            log.finest(e.getMessage());
        }

        treeview.setRoot(buildFileSystemBrowser2());
        treeview.setOnMouseClicked(this);
//        treeview.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
//            log.info(newValue.getValue());
//            updateFolders(newValue);
//        });
//
//        rootItem.expandedProperty().addListener((observable, oldValue, newValue) -> {
//            log.info(String.valueOf(newValue));
//            BooleanProperty bb = (BooleanProperty) observable;
//            log.info(String.valueOf(bb.getBean()));
//            MyTreeItem item = (MyTreeItem) bb.getBean();
//            log.info(String.valueOf(item.getValue()));
//        });
    }

    private void updateFolders(MyTreeItem parentItem) {
        ObservableList<TreeItem<String>> children = parentItem.getChildren();
        try {
            IDfCollection folders = repositoryCon.query(String.format("select r_object_id, object_name from dm_folder where folder('%s') order by object_name", currentPath));
            while (folders.next()) {
                MyTreeItem item = new MyTreeItem(folders.getString("object_name"), "folder", new ImageView(
                        new Image(getClass().getClassLoader().getResourceAsStream("folder_16.png"))));
                children.add(item);
            }
        } catch (DfException e) {
            log.finest(e.getMessage());
        }
        parentItem.setExpanded(true);
    }

    @Override
    public void handle(MouseEvent event) {
        if (event.getClickCount() == 2) {
            TreeItem<File> item = treeview.getSelectionModel().getSelectedItem();
            log.info(String.format("Open folder: %s ", item.getValue()));
//            currentPath = String.format("%s/%s", currentPath, item.getValue());
//            if(!item.isExpanded())
//                updateFolders(item);
        }
    }

    private TreeItem<String> buildFileSystemBrowser() {
        return createNode("EP", "repository", new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("repository_16.png"))));
    }

    private MyTreeItem createNode(String name, String type, ImageView imageView) {
        return new MyTreeItem(name, type, imageView) {
            private boolean isFirstTimeChildren = true;

            @Override
            public ObservableList<TreeItem<String>> getChildren() {
                if (isFirstTimeChildren) {
                    isFirstTimeChildren = false;
                    super.getChildren().setAll(buildChildren(this));
                }
                return super.getChildren();
            }

            private ObservableList<MyTreeItem> buildChildren(MyTreeItem treeItem) {
                if (treeItem != null && treeItem.isDirectory()) {
                    String[] folders = treeItem.getFolders();
                    if (folders != null) {
                        ObservableList<MyTreeItem> children = FXCollections.observableArrayList();

                        for (String folder : folders) {
                            children.add(createNode(folder, "folder", new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("folder_16.png")))));
                        }

                        return children;
                    }
                }

                return FXCollections.emptyObservableList();
            }
        };
    }

    private TreeItem<File> buildFileSystemBrowser2() {
        return createNode(new File("/"));
    }

    // This method creates a TreeItem to represent the given File. It does this
    // by overriding the TreeItem.getChildren() and TreeItem.isLeaf() methods
    // anonymously, but this could be better abstracted by creating a
    // 'FileTreeItem' subclass of TreeItem. However, this is left as an exercise
    // for the reader.
    private TreeItem<File> createNode(final File f) {
        return new TreeItem<File>(f) {
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
            private boolean isFirstTimeChildren = true;
            private boolean isFirstTimeLeaf = true;

            @Override
            public ObservableList<TreeItem<File>> getChildren() {
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
                    File f = getValue();
                    isLeaf = f.isFile();
                }

                return isLeaf;
            }

            private ObservableList<TreeItem<File>> buildChildren(TreeItem<File> TreeItem) {
                File f = TreeItem.getValue();
                if (f != null && f.isDirectory()) {
                    File[] files = f.listFiles();
                    if (files != null) {
                        ObservableList<TreeItem<File>> children = FXCollections.observableArrayList();

                        for (File childFile : files) {
                            children.add(createNode(childFile));
                        }

                        return children;
                    }
                }

                return FXCollections.emptyObservableList();
            }
        };
    }
}
