package nl.bos;

import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;
import lombok.Data;

@Data
public class MyTreeItem extends TreeItem<String> {

    private String type;

    public MyTreeItem(String name, String type, ImageView imageView) {
        super(name, imageView);
        this.type = type;
    }

    public boolean isDirectory() {
        return type.equals("repository") || type.equals("cabinet") || type.equals("folder");
    }

    public String[] getFolders() {
        String[] result = {"test", "test2"};
        return result;
    }
}
