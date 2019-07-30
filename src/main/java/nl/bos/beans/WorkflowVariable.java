package nl.bos.beans;

public class WorkflowVariable {
    private final String name;
    private final String value;

    public WorkflowVariable(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
