package nl.bos.controllers;

import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import lombok.extern.java.Log;
import nl.bos.MyJobObject;
import nl.bos.Repository;

import java.net.URL;
import java.util.ResourceBundle;

@Log
public class JobEditorPane implements Initializable, ChangeListener {
    private Repository repository = Repository.getInstance();

    @FXML
    private ListView lvJobs;
    @FXML
    private ChoiceBox cbJobsFilter;
    @FXML
    private TextField txtObjectId;
    @FXML
    private TextField txtLastCompletionDate;
    @FXML
    private TextField txtNextInvocationDate;
    @FXML
    private TextField txtRunCompleted;
    @FXML
    private TextField txtLastReturnCode;
    @FXML
    private CheckBox chkIsContinued;
    @FXML
    private TextField txtName;
    @FXML
    private TextField txtType;
    @FXML
    private TextField txtDescription;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            ObservableList jobIds = FXCollections.observableArrayList();
            ObservableList categories = FXCollections.observableArrayList();
            IDfCollection collection = repository.query("select r_object_id, title, object_name from dm_job order by title, object_name");
            while (collection.next()) {
                String type = collection.getString("title");
                jobIds.add(new MyJobObject(collection.getString("r_object_id"), collection.getString("object_name"), type));
                if (!categories.contains(type))
                    categories.add(type);
            }
            lvJobs.setItems(jobIds);
            cbJobsFilter.setItems(categories);
        } catch (DfException e) {
            log.finest(e.getMessage());
        }

        lvJobs.getSelectionModel().selectedItemProperty().addListener(this);
        cbJobsFilter.getSelectionModel().selectedItemProperty().addListener(this);
    }

    @Override
    public void changed(ObservableValue observable, Object oldValue, Object newValue) {
        log.info(String.format("ListView selection changed from old value %s to the new value %s", oldValue, newValue));
        if (observable.getValue().getClass().equals(MyJobObject.class)) {
            try {
                IDfPersistentObject job = repository.getSession().getObject(new DfId(((MyJobObject) observable.getValue()).getObjectId()));
                txtObjectId.setText(job.getString("r_object_id"));
                txtLastCompletionDate.setText(job.getString("a_last_completion"));
                txtNextInvocationDate.setText(job.getString("a_next_invocation"));
                txtRunCompleted.setText(String.valueOf(job.getInt("a_iterations")));
                txtLastReturnCode.setText(job.getString("a_last_return_code"));
                chkIsContinued.setDisable(job.getBoolean("a_is_continued"));
                txtName.setText(job.getString("object_name"));
                txtType.setText(job.getString("title"));
                txtDescription.setText(job.getString("subject"));
            } catch (DfException e) {
                log.finest(e.getMessage());
            }
        } else
            log.info(String.format("Category is %s", observable));
    }
}
