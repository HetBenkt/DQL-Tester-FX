package nl.bos.beans;

public class PackageObject {
    private final String packageName;
    private final String packageType;
    private final String componentId;
    private final String componentName;

    public PackageObject(String packageName, String packageType, String componentId, String componentName) {
        this.packageName = packageName;
        this.packageType = packageType;
        this.componentId = componentId;
        this.componentName = componentName;
    }
}
