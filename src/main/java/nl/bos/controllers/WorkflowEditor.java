package nl.bos.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import nl.bos.beans.WorkflowObject;
import nl.bos.services.WorkflowService;

import java.util.logging.Logger;


public class WorkflowEditor {
    private static final Logger LOGGER = Logger.getLogger(WorkflowEditor.class.getName());

    @FXML
    private TableView<WorkflowObject> tvResults;
    @FXML
    private TextArea txaErrorContents;
    @FXML
    private TextField txtSupervisor;
    @FXML
    private TextField txtObject;

    private WorkflowService workflowService;

    public WorkflowEditor() {
        this.workflowService = new WorkflowService(WorkflowService.ServiceStates.ALL);
    }

    @FXML
    private void initialize() {
        initColumnsResultTableView();
        tvResults.getItems().addAll(workflowService.getWorkflows());
        tvResults.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, workflowObject) -> {
            if (workflowObject != null) {
                txaErrorContents.setText(workflowObject.getExecOsError());
                workflowService.getPackages(workflowObject.getWorkitemId());
            }
        });
    }

    private void initColumnsResultTableView() {
        TableColumn workflowId = new TableColumn("workflow_id");
        workflowId.setCellValueFactory(new PropertyValueFactory<>("workflowId"));

        TableColumn workflowName = new TableColumn("workflow_name");
        workflowName.setCellValueFactory(new PropertyValueFactory<>("workflowName"));

        TableColumn workitemId = new TableColumn("workitem_id");
        workitemId.setCellValueFactory(new PropertyValueFactory<>("workitemId"));

        TableColumn processName = new TableColumn("pname");
        processName.setCellValueFactory(new PropertyValueFactory<>("processName"));

        TableColumn activityName = new TableColumn("actname");
        activityName.setCellValueFactory(new PropertyValueFactory<>("activityName"));

        TableColumn activitySeqNo = new TableColumn("act_seqno");
        activitySeqNo.setCellValueFactory(new PropertyValueFactory<>("activitySeqNo"));

        TableColumn runtimeState = new TableColumn("rs");
        runtimeState.setCellValueFactory(new PropertyValueFactory<>("runtimeState"));

        TableColumn performerName = new TableColumn("r_performer_name");
        performerName.setCellValueFactory(new PropertyValueFactory<>("performerName"));

        TableColumn supervisorName = new TableColumn("supervisor_name");
        supervisorName.setCellValueFactory(new PropertyValueFactory<>("supervisorName"));

        TableColumn event = new TableColumn("event");
        event.setCellValueFactory(new PropertyValueFactory<>("event"));

        TableColumn workqueueName = new TableColumn("a_wq_name");
        workqueueName.setCellValueFactory(new PropertyValueFactory<>("workqueueName"));

        TableColumn startDate = new TableColumn("r_start_date");
        startDate.setCellValueFactory(new PropertyValueFactory<>("startDate"));

        TableColumn creationDae = new TableColumn("r_creation_date");
        creationDae.setCellValueFactory(new PropertyValueFactory<>("creationDae"));

        TableColumn activityId = new TableColumn("actid");
        activityId.setCellValueFactory(new PropertyValueFactory<>("activityId"));

        TableColumn processId = new TableColumn("pid");
        processId.setCellValueFactory(new PropertyValueFactory<>("processId"));

        TableColumn parentId = new TableColumn("parent_id");
        parentId.setCellValueFactory(new PropertyValueFactory<>("parentId"));

        TableColumn queueItemId = new TableColumn("qid");
        queueItemId.setCellValueFactory(new PropertyValueFactory<>("queueItemId"));

        tvResults.getColumns().addAll(workflowId, workflowName, workitemId, processName, activityName, activitySeqNo, runtimeState, performerName, supervisorName, event, workqueueName, startDate, creationDae, activityId, processId, parentId, queueItemId);
    }

    @FXML
    private void handleTodayFlows(ActionEvent actionEvent) {
        tvResults.getItems().clear();
        workflowService.setCurrentState(WorkflowService.ServiceStates.TODAY);
        tvResults.getItems().addAll(workflowService.getWorkflows());
    }

    @FXML
    private void handleAllFlows(ActionEvent actionEvent) {
        tvResults.getItems().clear();
        workflowService.setCurrentState(WorkflowService.ServiceStates.ALL);
        tvResults.getItems().addAll(workflowService.getWorkflows());
    }

    @FXML
    public void handlePausedFlows(ActionEvent actionEvent) {
        tvResults.getItems().clear();
        workflowService.setCurrentState(WorkflowService.ServiceStates.PAUSED);
        tvResults.getItems().addAll(workflowService.getWorkflows());
    }

    @FXML
    private void handleSupervisor(KeyEvent keyEvent) {
        tvResults.getItems().clear();
        workflowService.setCurrentSupervisor(txtSupervisor.getText() + keyEvent.getText());
        tvResults.getItems().addAll(workflowService.getWorkflows());
    }

    @FXML
    private void handleObject(KeyEvent keyEvent) {
        tvResults.getItems().clear();
        workflowService.setCurrentObject(txtObject.getText() + keyEvent.getText());
        tvResults.getItems().addAll(workflowService.getWorkflows());
    }
}
