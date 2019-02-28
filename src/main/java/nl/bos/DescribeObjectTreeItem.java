package nl.bos;

import javafx.scene.control.TreeItem;

public class DescribeObjectTreeItem extends TreeItem {
    private final String value;
    private String type;

    public DescribeObjectTreeItem(String value, String type) {
        this.type = type;
        this.value = value;
        //noinspection unchecked
        this.setValue(value);
    }

    public DescribeObjectTreeItem(String value) {
        this.value = value;
        //noinspection unchecked
        this.setValue(value);
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
