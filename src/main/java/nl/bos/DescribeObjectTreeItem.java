package nl.bos;

import javafx.scene.control.TreeItem;

public class DescribeObjectTreeItem extends TreeItem {
    private String value;
    private String type;

    public DescribeObjectTreeItem(String value, String type) {
        this.type = type;
        this.value = value;
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
