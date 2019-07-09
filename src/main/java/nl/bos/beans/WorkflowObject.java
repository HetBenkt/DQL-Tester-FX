package nl.bos.beans;

public class WorkflowObject {
    private String workflowId;
    private String workflowName;
    private String workitemId;
    private String processName;
    private String activityName;
    private String activitySeqNo;
    private String runtimeState;
    private String performerName;
    private String supervisorName;
    private String event;
    private String workqueueName;
    private String startDate;
    private String creationDae;
    private String activityId;
    private String processId;
    private String parentId;
    private String queueItemId;
    private String execOsError;

    public String getWorkflowName() {
        return workflowName;
    }

    public void setWorkflowName(String workflowName) {
        this.workflowName = workflowName;
    }

    public String getExecOsError() {
        return execOsError;
    }

    public void setExecOsError(String execOsError) {
        this.execOsError = execOsError;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getActivitySeqNo() {
        return activitySeqNo;
    }

    public void setActivitySeqNo(String activitySeqNo) {
        this.activitySeqNo = activitySeqNo;
    }

    public String getRuntimeState() {
        return runtimeState;
    }

    public void setRuntimeState(String runtimeState) {
        this.runtimeState = runtimeState;
    }

    public String getPerformerName() {
        return performerName;
    }

    public void setPerformerName(String performerName) {
        this.performerName = performerName;
    }

    public String getSupervisorName() {
        return supervisorName;
    }

    public void setSupervisorName(String supervisorName) {
        this.supervisorName = supervisorName;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getWorkqueueName() {
        return workqueueName;
    }

    public void setWorkqueueName(String workqueueName) {
        this.workqueueName = workqueueName;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getCreationDae() {
        return creationDae;
    }

    public void setCreationDae(String creationDae) {
        this.creationDae = creationDae;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getQueueItemId() {
        return queueItemId;
    }

    public void setQueueItemId(String queueItemId) {
        this.queueItemId = queueItemId;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    public String getWorkitemId() {
        return workitemId;
    }

    public void setWorkitemId(String workitemId) {
        this.workitemId = workitemId;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }
}
