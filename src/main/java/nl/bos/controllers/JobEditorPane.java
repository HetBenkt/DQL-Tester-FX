package nl.bos.controllers;

import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.IDfTime;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import lombok.extern.java.Log;
import nl.bos.MyJobObject;
import nl.bos.Repository;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

@Log
public class JobEditorPane implements Initializable, ChangeListener {
    private Repository repository = Repository.getInstance();
    private Image running = new Image("nl/bos/icons/running.png");
    private Image active = new Image("nl/bos/icons/active.png");
    private Image inactive = new Image("nl/bos/icons/inactive.png");

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
    @FXML
    private RadioButton rbStateActive;
    @FXML
    private RadioButton rbStateInactive;
    @FXML
    private ChoiceBox cbTraceLevel;
    @FXML
    private CheckBox chkDeactivateOnFailure;
    @FXML
    private ChoiceBox cbDesignatedServer;
    @FXML
    private DatePicker dpStartDate;
    @FXML
    private ChoiceBox cbRepeat;
    @FXML
    private TextField txtFrequency;
    @FXML
    private TextField txtContinuousInterval;
    @FXML
    private CheckBox cbPassStandardArguments;
    @FXML
    private TextField txtMethod;
    @FXML
    private ListView lvArguments;
    @FXML
    private TextField txtRunning;
    @FXML
    private ImageView ivState;
    @FXML
    private TextField txtNrOfArguments;
    @FXML
    private Label lblStatus;
    @FXML
    private DatePicker dpEndDate;
    @FXML
    private TextField txtMaxIterations;
    @FXML
    private RadioButton rbEndDate;
    @FXML
    private RadioButton rbEndMaxIterations;
    @FXML
    private TextField txtNrOfJobsListed;
    @FXML
    private Button btnEditArguments;
    @FXML
    private Button btnBrowseMethod;
    @FXML
    private Button btnEditServer;
    @FXML
    private Button btnViewLog;
    @FXML
    private Button btnNewJob;
    @FXML
    private Button btnExportJob;
    @FXML
    private Button btnDeleteJob;
    @FXML
    private Button btnCopyJob;
    @FXML
    private Button btnUpdate;
    @FXML
    private Button btnExit;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            ObservableList jobIds = FXCollections.observableArrayList();
            ObservableList categories = FXCollections.observableArrayList();
            IDfCollection collection = repository.query("select r_object_id, title, object_name, is_inactive, a_current_status from dm_job order by title, object_name");
            while (collection.next()) {
                String type = collection.getString("title");
                jobIds.add(new MyJobObject(collection.getString("r_object_id"), collection.getString("object_name"), type, !collection.getBoolean("is_inactive"), collection.getString("a_current_status")));
                if (!categories.contains(type))
                    categories.add(type);
            }
            lvJobs.setItems(jobIds);
            txtNrOfJobsListed.setText(String.valueOf(jobIds.size()));
            lvJobs.setCellFactory(param -> new ListCell<MyJobObject>() {
                private ImageView imageView = new ImageView();

                @Override
                protected void updateItem(MyJobObject item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        if (item.isRunning())
                            imageView.setImage(running);
                        else if (item.isActive())
                            imageView.setImage(active);
                        else
                            imageView.setImage(inactive);
                        setText(item.getObjectName());
                        setGraphic(imageView);
                    }
                }
            });

            cbJobsFilter.setItems(categories);

            ObservableList levels = FXCollections.observableArrayList();
            for (int i = 0; i <= 10; i++) {
                levels.add(i);
            }
            cbTraceLevel.setItems(levels);

            ObservableList servers = FXCollections.observableArrayList();
            servers.add("Any Running Server");
            IDfCollection serverInfo = repository.query("select object_name, r_host_name from dm_server_config order by object_name");
            while (serverInfo.next()) {
                servers.add(String.format("%s.%s@%s", repository.getRepositoryName(), serverInfo.getString("object_name"), serverInfo.getString("r_host_name")));
            }
            cbDesignatedServer.setItems(servers);

            ObservableList repeats = FXCollections.observableArrayList();
            repeats.add("Minutes");
            repeats.add("Hours");
            repeats.add("Days");
            repeats.add("Weeks");
            repeats.add("Months");
            repeats.add("Years");
            repeats.add("Day Of Week");
            repeats.add("Day Of Month");
            repeats.add("Day Of Year");
            cbRepeat.setItems(repeats);
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
                rbStateActive.setSelected(!job.getBoolean("is_inactive"));
                rbStateInactive.setSelected(job.getBoolean("is_inactive"));
                cbTraceLevel.setValue(job.getInt("method_trace_level"));
                chkDeactivateOnFailure.setSelected(job.getBoolean("inactivate_after_failure"));
                cbDesignatedServer.setValue(job.getString("target_server"));

                IDfTime startDate = job.getTime("start_date");
                if (!startDate.isNullDate()) {
                    LocalDate localDate = LocalDate.of(startDate.getYear(), startDate.getMonth(), startDate.getDay());
                    dpStartDate.setValue(localDate);
                } else
                    dpStartDate.setValue(LocalDate.now());

                cbRepeat.setValue(getDisplayValue(job.getInt("run_mode")));
                txtFrequency.setText(String.valueOf(job.getInt("run_interval")));
                txtContinuousInterval.setText(String.valueOf(job.getInt("a_continuation_interval")));
                cbPassStandardArguments.setSelected(job.getBoolean("pass_standard_arguments"));
                txtMethod.setText(job.getString("method_name"));

                ObservableList arguments = FXCollections.observableArrayList();
                int methodArguments = job.getValueCount("method_arguments");
                for (int i = 0; i < methodArguments; i++) {
                    arguments.add(job.getRepeatingString("method_arguments", i));
                }
                lvArguments.setItems(arguments);

                txtNrOfArguments.setText(String.valueOf(methodArguments));

                if (job.getString("a_current_status").equals("STARTED")) {
                    ivState.setImage(running);
                    txtRunning.setText("running");
                } else if (!job.getBoolean("is_inactive"))
                    ivState.setImage(active);
                else
                    ivState.setImage(inactive);

                lblStatus.setText(job.getString("a_current_status"));

                IDfTime endDate = job.getTime("expiration_date");
                if (!endDate.isNullDate()) {
                    LocalDate localEndDate = LocalDate.of(endDate.getYear(), endDate.getMonth(), endDate.getDay());
                    dpEndDate.setValue(localEndDate);
                } else
                    dpEndDate.setValue(LocalDate.now());

                txtMaxIterations.setText(String.valueOf(job.getInt("max_iterations")));
                txtMaxIterations.setDisable(!hasEndIterationValue(job));

                rbEndDate.setSelected(!hasEndIterationValue(job));
                rbEndMaxIterations.setSelected(hasEndIterationValue(job));
            } catch (DfException e) {
                log.finest(e.getMessage());
            }
        } else
            log.info(String.format("Category is %s", observable));
    }

    private boolean hasEndIterationValue(IDfPersistentObject job) throws DfException {
        return job.getInt("max_iterations") != 0;
    }

    private String getDisplayValue(int runMode) {
        switch (runMode) {
            case 1:
                return "Minutes";
            case 2:
                return "Hours";
            case 3:
                return "Days";
            case 4:
                return "Weeks";
            case 5:
                return "Months";
            case 6:
                return "Years";
            case 7:
                return "Day Of Week";
            case 8:
                return "Day Of Month";
            case 9:
                return "Day Of Year";
            default:
                return "Unknown";
        }
    }

    public void handleExit(ActionEvent actionEvent) {
        log.info(String.valueOf(actionEvent.getSource()));
        Stage stage = (Stage) btnExit.getScene().getWindow();
        stage.close();
    }
}
