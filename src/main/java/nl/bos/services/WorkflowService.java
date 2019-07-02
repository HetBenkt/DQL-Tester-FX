package nl.bos.services;

import nl.bos.Repository;
import nl.bos.beans.WorkflowObject;

import java.util.List;
import java.util.logging.Logger;

public class WorkflowService {
    private static final Logger LOGGER = Logger.getLogger(WorkflowService.class.getName());
    private ServiceStates currentState;

    private Repository repository = Repository.getInstance();
    private String currentSupervisor;
    private String currentObject;

    public WorkflowService(ServiceStates currentState) {
        this.currentState = currentState;
        this.currentSupervisor = "";
        this.currentObject = "";
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
                return repository.getAllWorkflows(currentSupervisor, currentObject);
            case TODAY:
                return repository.getTodayWorkflows(currentSupervisor, currentObject);
            case PAUSED:
                return repository.getPausedWorkflows(currentSupervisor, currentObject);
            default:
                LOGGER.info("No valid input");
                return null;
        }
    }

    public enum ServiceStates {TODAY, ALL, PAUSED}
}
