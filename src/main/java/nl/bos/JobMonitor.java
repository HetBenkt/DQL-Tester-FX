package nl.bos;

import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;
import javafx.application.Platform;
import javafx.concurrent.Task;
import lombok.extern.java.Log;
import nl.bos.controllers.JobEditorPane;

@Log
public class JobMonitor extends Task<Void> {
    private final JobEditorPane jobEditorPane;
    private final MyJobObject currentJob;
    private Repository repository = Repository.getInstance();
    private volatile boolean running;

    public JobMonitor(MyJobObject currentJob, JobEditorPane jobEditorPane) {
        this.running = true;
        this.currentJob = currentJob;
        this.jobEditorPane = jobEditorPane;
    }

    public synchronized void stop() {
        running = false;
    }

    @Override
    protected Void call() throws Exception {
        while (running) {
            log.finest("Monitor...");

            Platform.runLater(() -> {
                try {
                    IDfPersistentObject job = repository.getSession().getObject(new DfId(currentJob.getObjectId()));
                    jobEditorPane.updateFields(job);
                } catch (DfException e) {
                    log.finest(e.getMessage());
                }
            });

            Thread.sleep(2000);
        }
        return null;
    }
}
