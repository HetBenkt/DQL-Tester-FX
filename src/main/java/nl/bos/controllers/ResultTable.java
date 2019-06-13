package nl.bos.controllers;

import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.common.DfException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import nl.bos.Repository;

import java.util.logging.Level;
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

        Stage stage = (Stage) btnOk.getScene().getWindow();
        if (stage.getTitle().contains("Versions")) {
            getVersions(id);
        } else {
            getRenditions(id);
        }
    }

    private void getVersions(String id) {
        IDfPersistentObject object = repository.getObjectById(id);
        try {
            String chronicleId = object.getString("i_chronicle_id");
            IDfCollection collection = repository.query(String.format("select r_object_id, object_name, r_version_label, i_chronicle_id, r_creation_date, r_modify_date, a_content_type from dm_sysobject (ALL) where i_chronicle_id = '%s'", chronicleId));
            int counter = 0;
            while (collection.next()) {
                counter++;
            }
            txtCount.setText(String.valueOf(counter));
        } catch (DfException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    private void getRenditions(String id) {
        try {
            IDfCollection collection = repository.query(String.format("select * from dmr_content where any parent_id = '%s'", id));
            int counter = 0;
            while (collection.next()) {
                counter++;
            }
            txtCount.setText(String.valueOf(counter));
        } catch (DfException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
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
