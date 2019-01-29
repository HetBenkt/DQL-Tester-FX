package nl.bos;

import javafx.scene.control.TreeItem;

public class DescribeObjectTreeItem extends TreeItem {
    private String value;

    public DescribeObjectTreeItem(String value) {
        this.value = value;
        this.setValue(value);
    }

    @Override
    public String toString() {
        return this.value;
    }
}
