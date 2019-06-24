package nl.bos.services;

import nl.bos.Repository;
import nl.bos.beans.WorkflowObject;

import java.util.List;

public class WorkflowService {

    private Repository repository = Repository.getInstance();

    public List<WorkflowObject> getAllWorkflows() {
        return repository.getAllWorkflows();
    }
}
