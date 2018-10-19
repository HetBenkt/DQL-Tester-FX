package nl.bos;

import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.common.DfException;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.Data;
import lombok.extern.java.Log;

import java.util.ArrayList;
import java.util.List;

@Data
@Log
public class MyTreeItem extends TreeItem<String> {

    private String type;
    private String path;
    private Repository repositoryCon = Repository.getInstance();

    public MyTreeItem(String name, String type, ImageView imageView, String path) {
        super(name, imageView);
        this.type = type;
        this.path = path;
    }

    public boolean isDirectory() {
        return type.equals("repository") || type.equals("cabinet") || type.equals("folder");
    }

    public List<MyTreeItem> listObjects(MyTreeItem parent) {
        List<MyTreeItem> children = new ArrayList();

        try {
            if (parent.getType().equals("repository")) {
                IDfCollection cabinets = repositoryCon.query("select r_object_id, object_name, is_private from dm_cabinet order by object_name");
                while (cabinets.next()) {
                    MyTreeItem child = new MyTreeItem(cabinets.getString("object_name"), "cabinet", new ImageView(
                            new Image(getClass().getClassLoader().getResourceAsStream("cabinet_16.png"))), String.format("%s/%s", parent.getPath(), cabinets.getString("object_name")));
                    children.add(child);
                }
            } else if (parent.getType().equals("cabinet")) {
                IDfCollection folders = repositoryCon.query(String.format("select r_object_id, object_name from dm_folder where cabinet('%s') order by object_name", parent.getPath()));
                while (folders.next()) {
                    MyTreeItem child = new MyTreeItem(folders.getString("object_name"), "folder", new ImageView(
                            new Image(getClass().getClassLoader().getResourceAsStream("folder_16.png"))), String.format("%s/%s", parent.getPath(), folders.getString("object_name")));
                    children.add(child);
                }
                addDocuments(children, parent);
            } else if (parent.getType().equals("folder")) {
                IDfCollection folders = repositoryCon.query(String.format("select r_object_id, object_name from dm_folder where folder('%s') order by object_name", parent.getPath()));
                while (folders.next()) {
                    MyTreeItem child = new MyTreeItem(folders.getString("object_name"), "folder", new ImageView(
                            new Image(getClass().getClassLoader().getResourceAsStream("folder_16.png"))), String.format("%s/%s", parent.getPath(), folders.getString("object_name")));
                    children.add(child);
                }
                addDocuments(children, parent);
            }
        } catch (DfException e) {
            log.finest(e.getMessage());
        }

        return children;
    }

    private void addDocuments(List<MyTreeItem> children, MyTreeItem parent) throws DfException {
        IDfCollection documents = repositoryCon.query(String.format("select r_object_id, object_name from dm_sysobject where folder('%s') and r_object_type != 'dm_folder' order by object_name", parent.getPath()));
        while (documents.next()) {
            MyTreeItem child = new MyTreeItem(documents.getString("object_name"), "document", new ImageView(
                    new Image(getClass().getClassLoader().getResourceAsStream("document_16.png"))), String.format("%s/%s", parent.getPath(), documents.getString("object_name")));
            children.add(child);
        }
    }
}
