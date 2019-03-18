package nl.bos;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

public class JobObject {
    private static final Logger LOGGER = Logger.getLogger(JobObject.class.getName());

    private final String objectId;
    private final String objectName;
    private final boolean isActive;
    private final boolean isRunning;
    private final Map<String, String> updates = new HashMap<>();

    public String getObjectId() {
        return objectId;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public boolean isActive() {
        return isActive;
    }

    public String getObjectName() {
        return objectName;
    }

    public JobObject(String objectId, String objectName, boolean isActive, String isRunning) {
        this.objectId = objectId;
        this.objectName = objectName;
        this.isActive = isActive;

        this.isRunning = isRunning.equals("STARTED");
    }

    public void updateChanges(String key, String value) {
        String message = MessageFormat.format("key: {0}; value: {1}", key, value);
        LOGGER.info(message);
        updates.put(key, value);
    }

    public String getUpdateList() {
        StringBuilder updateList = new StringBuilder();

        Iterator it = updates.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            LOGGER.info(pair.getKey() + " = " + pair.getValue());
            updateList.append(String.format("%s ", pair.getValue()));
            it.remove();
        }

        return updateList.toString();
    }
}
