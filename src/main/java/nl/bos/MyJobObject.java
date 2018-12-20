package nl.bos;

import java.util.logging.Logger;

public class MyJobObject {
    private static final Logger log = Logger.getLogger(MyJobObject.class.getName());

    private final String objectId;
    private final String objectName;
    private final String category;
    private final boolean isActive;
    private final boolean isRunning;

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
}
