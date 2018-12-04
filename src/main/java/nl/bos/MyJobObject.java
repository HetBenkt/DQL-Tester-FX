package nl.bos;

import lombok.Data;

@Data
public class MyJobObject {
    private final String objectId;
    private final String objectName;
    private final String category;

    public MyJobObject(String objectId, String objectName, String category) {
        this.objectId = objectId;
        this.objectName = objectName;
        this.category = category;
    }

    @Override
    public String toString() {
        return this.objectName;
    }
}
