package nl.bos.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import nl.bos.beans.WorkflowObject;
import nl.bos.services.WorkflowService;


public class WorkflowEditor {

    @FXML
    private TableView<WorkflowObject> tvResults;
    @FXML
    private TextArea txaErrorContents;


    private WorkflowService workflowService;

    public WorkflowEditor() {
        this.workflowService = new WorkflowService();
    }

    @FXML
    private void initialize() {
        initColumnsResultTableView();
        tvResults.getItems().addAll(workflowService.getAllWorkflows());
        tvResults.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue != null) {
                txaErrorContents.setText(newValue.getExecOsError());
            }
        });
    }

    private void initColumnsResultTableView() {
        TableColumn workflowId = new TableColumn("workflow_id");
        workflowId.setCellValueFactory(new PropertyValueFactory<>("workflowId"));

        TableColumn workitemId = new TableColumn("workitem_id");
        workitemId.setCellValueFactory(new PropertyValueFactory<>("workitemId"));

        TableColumn processName = new TableColumn("pname");
        processName.setCellValueFactory(new PropertyValueFactory<>("processName"));

        TableColumn activityName = new TableColumn("actname");
        activityName.setCellValueFactory(new PropertyValueFactory<>("activityName"));

        TableColumn activitySeqNo = new TableColumn("act_seqno");
        activitySeqNo.setCellValueFactory(new PropertyValueFactory<>("activitySeqNo"));

        TableColumn packageName = new TableColumn("pkgname");
        packageName.setCellValueFactory(new PropertyValueFactory<>("packageName"));

        TableColumn objectId = new TableColumn("objid");
        objectId.setCellValueFactory(new PropertyValueFactory<>("objectId"));

        TableColumn objectName = new TableColumn("object_name");
        objectName.setCellValueFactory(new PropertyValueFactory<>("objectName"));

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

        TableColumn packageId = new TableColumn("pkgid");
        packageId.setCellValueFactory(new PropertyValueFactory<>("packageId"));

        tvResults.getColumns().addAll(workflowId, workitemId, processName, activityName, activitySeqNo, packageName, objectId, objectName, runtimeState, performerName, supervisorName, event, workqueueName, startDate, creationDae, activityId, processId, parentId, queueItemId, packageId);
    }

    @FXML
    private void handleTodayFlows(ActionEvent actionEvent) {
        tvResults.getItems().clear();
        tvResults.getItems().addAll(workflowService.getTodayWorkflows());
    }

    @FXML
    private void handleAllFlows(ActionEvent actionEvent) {
        tvResults.getItems().clear();
        tvResults.getItems().addAll(workflowService.getAllWorkflows());
    }

    @FXML
    public void handlePausedFlows(ActionEvent actionEvent) {
        tvResults.getItems().clear();
        tvResults.getItems().addAll(workflowService.getPausedWorkflows());
    }
}
