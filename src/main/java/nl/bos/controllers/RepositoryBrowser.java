package nl.bos.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import lombok.extern.java.Log;

import java.net.URL;
import java.util.ResourceBundle;

@Log
public class RepositoryBrowser implements Initializable {
    @FXML
    private TreeView treeview;

    @FXML
    private void handleExit(ActionEvent actionEvent) {
        log.info(String.valueOf(actionEvent.getSource()));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.info(String.valueOf(location));

        TreeItem<String> rootItem = new TreeItem<String>("Inbox");
        rootItem.setExpanded(true);
        for (int i = 1; i < 6; i++) {
            TreeItem<String> item = new TreeItem<String>("Message" + i);
            rootItem.getChildren().add(item);
        }

        treeview.setRoot(rootItem);
    }
}
