package nl.bos.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import nl.bos.beans.WorkflowObject;
import nl.bos.services.WorkflowService;


public class WorkflowEditor {

    @FXML
    private TableView<WorkflowObject> tvResults;
    private WorkflowService workflowService;

    public WorkflowEditor() {
        this.workflowService = new WorkflowService();
    }

    @FXML
    private void initialize() {
        initColumnsResultTableView();
        tvResults.getItems().addAll(workflowService.getAllWorkflows());
    }

    private void initColumnsResultTableView() {
        TableColumn workflowId = new TableColumn("workflow_id");
        workflowId.setCellValueFactory(new PropertyValueFactory<>("workflowId"));
        TableColumn workitemId = new TableColumn("workitem_id");
        workitemId.setCellValueFactory(new PropertyValueFactory<>("workitemId"));
        TableColumn processName = new TableColumn("pname");
        processName.setCellValueFactory(new PropertyValueFactory<>("processName"));

        tvResults.getColumns().addAll(workflowId, workitemId, processName);
    }
}
