package nl.bos;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

public class MyJobObject {
    private static final Logger log = Logger.getLogger(MyJobObject.class.getName());

    private final String objectId;
    private final String objectName;
    private final String category;
    private final boolean isActive;
    private final boolean isRunning;
    private Map<String, String> updates = new HashMap<>();

    public MyJobObject(String objectId, String objectName, String category, boolean isActive, String isRunning) {
        this.objectId = objectId;
        this.objectName = objectName;
        this.category = category;
        this.isActive = isActive;

        this.isRunning = isRunning.equals("STARTED");
    }

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

    public void updateChanges(String key, String value) {
        updates.put(key, value);
    }

    public String getUpdateList() {
        StringBuilder updateList = new StringBuilder();

        Iterator it = updates.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            log.info(pair.getKey() + " = " + pair.getValue());
            updateList.append(String.format("%s ", pair.getValue()));
            it.remove();
        }

        return updateList.toString();
    }
}
