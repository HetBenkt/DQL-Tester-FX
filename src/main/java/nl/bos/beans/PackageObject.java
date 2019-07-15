package nl.bos.beans;

public class PackageObject {
    private String packageName;
    private String packageType;
    private String componentId;
    private String componentName;
    private boolean packageExists;
    private boolean packageIsLocked;

    public boolean isPackageExists() {
        return packageExists;
    }

    public void setPackageExists(boolean packageExists) {
        this.packageExists = packageExists;
    }

    public boolean isPackageIsLocked() {
        return packageIsLocked;
    }

    public void setPackageIsLocked(boolean packageIsLocked) {
        this.packageIsLocked = packageIsLocked;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getPackageType() {
        return packageType;
    }

    public void setPackageType(String packageType) {
        this.packageType = packageType;
    }

    public String getComponentId() {
        return componentId;
    }

    public void setComponentId(String componentId) {
        this.componentId = componentId;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }
}
