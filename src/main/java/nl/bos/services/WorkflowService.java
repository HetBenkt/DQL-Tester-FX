package nl.bos.services;

import javafx.scene.control.TreeItem;
import nl.bos.Repository;
import nl.bos.beans.AttachmentObject;
import nl.bos.beans.PackageObject;
import nl.bos.beans.WorkflowObject;

import java.util.List;
import java.util.logging.Logger;

public class WorkflowService {
    private static final Logger LOGGER = Logger.getLogger(WorkflowService.class.getName());
    private ServiceStates currentState;

    private Repository repository = Repository.getInstance();
    private String currentSupervisor;
    private String currentObject;
    private boolean oneRowPerWflSeqNo;

    public WorkflowService(ServiceStates currentState) {
        this.currentState = currentState;
        this.currentSupervisor = "";
        this.currentObject = "";
        this.oneRowPerWflSeqNo = true;
    }

    public void setOneRowPerWflSeqNo(boolean oneRowPerWflSeqNo) {
        this.oneRowPerWflSeqNo = oneRowPerWflSeqNo;
    }

    public void setCurrentSupervisor(String currentSupervisor) {
        this.currentSupervisor = currentSupervisor.trim();
    }

    public void setCurrentObject(String currentObject) {
        this.currentObject = currentObject.trim();
    }

    public void setCurrentState(ServiceStates currentState) {
        this.currentState = currentState;
    }

    public List<WorkflowObject> getWorkflows() {
        switch (currentState) {
            case ALL:
                return repository.getAllWorkflows(currentSupervisor, currentObject, oneRowPerWflSeqNo);
            case TODAY:
                return repository.getTodayWorkflows(currentSupervisor, currentObject, oneRowPerWflSeqNo);
            case PAUSED:
                return repository.getPausedWorkflows(currentSupervisor, currentObject, oneRowPerWflSeqNo);
            default:
                LOGGER.info("No valid input");
                return null;
        }
    }

    public List<PackageObject> getPackages(String workitemId) {
        return repository.getPackages(workitemId);
    }

    public List<AttachmentObject> getAttachments(String workitemId) {
        return repository.getAttachments(workitemId);
    }

    public TreeItem getVariables(String workflowId, String processName) {
        return repository.getVariables(workflowId, processName);
    }

    public enum ServiceStates {TODAY, ALL, PAUSED}
}
