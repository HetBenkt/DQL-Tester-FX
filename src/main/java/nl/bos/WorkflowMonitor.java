package nl.bos;

import javafx.application.Platform;
import javafx.concurrent.Task;
import nl.bos.controllers.WorkflowEditor;

import java.util.logging.Level;
import java.util.logging.Logger;

public class WorkflowMonitor extends Task<Void> {
    private static final Logger LOGGER = Logger.getLogger(JobMonitor.class.getName());

    private WorkflowEditor workflowEditor;
    private boolean running;

    public WorkflowMonitor(WorkflowEditor workflowEditor) {
        this.running = true;
        this.workflowEditor = workflowEditor;
    }

    @Override
    protected Void call() {
        while (running) {
            LOGGER.finest("Monitor...");

            Platform.runLater(() -> workflowEditor.updateFields());

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        }
        return null;
    }

    public synchronized void stop() {
        running = false;
    }
}
