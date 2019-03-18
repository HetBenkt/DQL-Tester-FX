package nl.bos;

import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;
import javafx.application.Platform;
import javafx.concurrent.Task;
import nl.bos.controllers.JobEditor;

import java.util.logging.Level;
import java.util.logging.Logger;

public class JobMonitor extends Task<Void> {
    private static final Logger LOGGER = Logger.getLogger(JobMonitor.class.getName());

    private final Repository repository = Repository.getInstance();

    private final JobEditor jobEditor;
    private final JobObject currentJob;
    private volatile boolean running;

    public JobMonitor(JobObject currentJob, JobEditor jobEditor) {
        this.running = true;
        this.currentJob = currentJob;
        this.jobEditor = jobEditor;
    }

    public synchronized void stop() {
        running = false;
    }

    @Override
    protected Void call() {
        while (running) {
            LOGGER.finest("Monitor...");

            Platform.runLater(() -> {
                try {
                    IDfPersistentObject job = repository.getSession().getObject(new DfId(currentJob.getObjectId()));
                    jobEditor.updateFields(job);
                } catch (DfException e) {
                    LOGGER.log(Level.SEVERE, e.getMessage(), e);
                }
            });

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        }
        return null;
    }
}
