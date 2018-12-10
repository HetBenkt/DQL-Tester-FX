package nl.bos;

import lombok.Data;

@Data
public class MyJobObject {
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
}
