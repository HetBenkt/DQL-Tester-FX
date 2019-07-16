package nl.bos.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import nl.bos.beans.AttachmentObject;
import nl.bos.beans.PackageObject;
import nl.bos.beans.WorkflowObject;
import nl.bos.services.WorkflowService;

import java.util.List;
import java.util.logging.Logger;


public class WorkflowEditor {
    private static final Logger LOGGER = Logger.getLogger(WorkflowEditor.class.getName());

    @FXML
    private TableView<WorkflowObject> tvResults;
    @FXML
    private TableView<PackageObject> tvPackages;
    @FXML
    private TableView<AttachmentObject> tvAttachments;
    @FXML
    private TextArea txaErrorContents;
    @FXML
    private TextField txtSupervisor;
    @FXML
    private TextField txtObject;
    @FXML
    private CheckBox chbOneRowPerWflSeqNo;
    @FXML
    private CheckBox chbMonitor;
    @FXML
    private TextField txtMonitoringState;
    @FXML
    private TextField txtRowCount;

    private WorkflowService workflowService;

    public WorkflowEditor() {
        this.workflowService = new WorkflowService(WorkflowService.ServiceStates.ALL);
    }

    @FXML
    private void initialize() {
        initColumnsResultTableView();
        initColumnsProcessPackagesTableView();
        initColumnsProcessAttachmentsTableView();
        tvResults.getItems().addAll(workflowService.getWorkflows());
        txtRowCount.setText(String.format("Row count: %s", tvResults.getItems().size()));

        tvResults.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, workflowObject) -> {
            if (workflowObject != null) {
                txaErrorContents.setText(workflowObject.getExecOsError());

                List<PackageObject> packages = workflowService.getPackages(workflowObject.getWorkitemId());
                tvPackages.getItems().clear();
                tvPackages.getItems().addAll(packages);

                List<AttachmentObject> attachments = workflowService.getAttachments(workflowObject.getWorkitemId());
                tvAttachments.getItems().clear();
                tvAttachments.getItems().addAll(attachments);


            }
        });
    }

    private void initColumnsProcessAttachmentsTableView() {
        TableColumn componentType = new TableColumn("r_component_type");
        componentType.setCellValueFactory(new PropertyValueFactory<>("type"));

        TableColumn componentId = new TableColumn("r_component_id");
        componentId.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn componentName = new TableColumn("r_component_name");
        componentName.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn exists = new TableColumn("exists");
        exists.setCellValueFactory(new PropertyValueFactory<>("exists"));

        TableColumn locked = new TableColumn("locked");
        locked.setCellValueFactory(new PropertyValueFactory<>("locked"));

        tvAttachments.getColumns().addAll(componentType, componentId, componentName, exists, locked);
    }

    private void initColumnsProcessPackagesTableView() {
        TableColumn packageName = new TableColumn("r_package_name");
        packageName.setCellValueFactory(new PropertyValueFactory<>("packageName"));

        TableColumn packageType = new TableColumn("r_package_type");
        packageType.setCellValueFactory(new PropertyValueFactory<>("packageType"));

        TableColumn componentId = new TableColumn("r_component_id");
        componentId.setCellValueFactory(new PropertyValueFactory<>("componentId"));

        TableColumn componentName = new TableColumn("r_component_name");
        componentName.setCellValueFactory(new PropertyValueFactory<>("componentName"));

        TableColumn packageExists = new TableColumn("pkg_exists");
        packageExists.setCellValueFactory(new PropertyValueFactory<>("packageExists"));

        TableColumn packageIsLocked = new TableColumn("pkg_is_locked");
        packageIsLocked.setCellValueFactory(new PropertyValueFactory<>("packageIsLocked"));

        tvPackages.getColumns().addAll(packageName, packageType, componentId, componentName, packageExists, packageIsLocked);
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
        txtRowCount.setText(String.format("Row count: %s", tvResults.getItems().size()));
    }

    @FXML
    private void handleAllFlows(ActionEvent actionEvent) {
        tvResults.getItems().clear();
        workflowService.setCurrentState(WorkflowService.ServiceStates.ALL);
        tvResults.getItems().addAll(workflowService.getWorkflows());
        txtRowCount.setText(String.format("Row count: %s", tvResults.getItems().size()));
    }

    @FXML
    public void handlePausedFlows(ActionEvent actionEvent) {
        tvResults.getItems().clear();
        workflowService.setCurrentState(WorkflowService.ServiceStates.PAUSED);
        tvResults.getItems().addAll(workflowService.getWorkflows());
        txtRowCount.setText(String.format("Row count: %s", tvResults.getItems().size()));
    }

    @FXML
    private void handleSupervisor(KeyEvent keyEvent) {
        tvResults.getItems().clear();
        workflowService.setCurrentSupervisor(txtSupervisor.getText() + keyEvent.getText());
        tvResults.getItems().addAll(workflowService.getWorkflows());
        txtRowCount.setText(String.format("Row count: %s", tvResults.getItems().size()));
    }

    @FXML
    private void handleObject(KeyEvent keyEvent) {
        tvResults.getItems().clear();
        workflowService.setCurrentObject(txtObject.getText() + keyEvent.getText());
        tvResults.getItems().addAll(workflowService.getWorkflows());
        txtRowCount.setText(String.format("Row count: %s", tvResults.getItems().size()));
    }

    @FXML
    private void handleMonitor(ActionEvent actionEvent) {
        if (chbMonitor.isSelected()) {
            txtMonitoringState.setText("Monitoring ON");
        } else {
            txtMonitoringState.setText("Monitoring OFF");
        }
    }

    @FXML
    private void handleOneRowPerWflSeqNo(ActionEvent actionEvent) {
        tvResults.getItems().clear();
        workflowService.setOneRowPerWflSeqNo(chbOneRowPerWflSeqNo.isSelected());
        tvResults.getItems().addAll(workflowService.getWorkflows());
        txtRowCount.setText(String.format("Row count: %s", tvResults.getItems().size()));
    }
}
