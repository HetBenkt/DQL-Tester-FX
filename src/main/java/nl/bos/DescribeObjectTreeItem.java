package nl.bos;

import javafx.collections.FXCollections;
import javafx.scene.control.TreeItem;
import nl.bos.utils.AlphanumComparator;

import java.util.Comparator;

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

    public void sortTree() {
        sortTree(this);
    }

    private void sortTree(TreeItem<? extends DescribeObjectTreeItem> root) {
        if (!root.isLeaf()) {
            FXCollections.sort(root.getChildren(), alphabetical);
            root.getChildren().forEach(this::sortTree);
        }
    }
    private static final Comparator<TreeItem<? extends DescribeObjectTreeItem>> alphabetical
            = Comparator.comparing(item -> item.toString(), AlphanumComparator.INSTANCE);

}
