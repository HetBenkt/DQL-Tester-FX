package nl.bos.controllers;

import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.common.DfException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.extern.java.Log;
import nl.bos.Repository;

import java.net.URL;
import java.util.ResourceBundle;

@Log
public class RepositoryBrowser implements Initializable {
    @FXML
    private TreeView treeview;
    private final Node cabinetIcon = new ImageView(
            new Image(getClass().getClassLoader().getResourceAsStream("cabinet_16.png"))
    );
    @FXML
    private void handleExit(ActionEvent actionEvent) {
        log.info(String.valueOf(actionEvent.getSource()));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.info(String.valueOf(location));

        TreeItem<String> rootItem = new TreeItem<String>("Dummy");
        rootItem.setExpanded(true);

        Repository repository = Repository.getRepositoryCon();
        try {
            IDfCollection cabinets = repository.query("select r_object_id, object_name, is_private from dm_cabinet order by object_name");
            while (cabinets.next()) {
                TreeItem<String> item = new TreeItem<String>(cabinets.getString("object_name"), new ImageView(
                        new Image(getClass().getClassLoader().getResourceAsStream("cabinet_16.png"))));
                rootItem.getChildren().add(item);
            }
        } catch (DfException e) {
            log.finest(e.getMessage());
        }

        treeview.setRoot(rootItem);
    }
}
