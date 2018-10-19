package nl.bos;

import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.common.DfException;
import javafx.scene.control.TreeItem;
import lombok.Data;
import lombok.extern.java.Log;

import java.util.ArrayList;
import java.util.List;

@Data
@Log
public class MyTreeItem extends TreeItem<String> {

    private String type;
    private String name;
    private String path;
    private Repository repositoryCon = Repository.getInstance();

    public MyTreeItem(String name, String type, String path) {
        this.name = name;
        this.type = type;
        this.path = path;
    }

    @Override
    public String toString() {
        return this.name;
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
                    MyTreeItem child = new MyTreeItem(cabinets.getString("object_name"), "cabinet", String.format("%s/%s", parent.getPath(), cabinets.getString("object_name")));
                    children.add(child);
                }
            } else if (parent.getType().equals("cabinet")) {
                IDfCollection folders = repositoryCon.query(String.format("select r_object_id, object_name from dm_folder where cabinet('%s') order by object_name", parent.getPath()));
                while (folders.next()) {
                    MyTreeItem child = new MyTreeItem(folders.getString("object_name"), "folder", String.format("%s/%s", parent.getPath(), folders.getString("object_name")));
                    children.add(child);
                }
                addDocuments(children, parent);
            } else if (parent.getType().equals("folder")) {
                IDfCollection folders = repositoryCon.query(String.format("select r_object_id, object_name from dm_folder where folder('%s') order by object_name", parent.getPath()));
                while (folders.next()) {
                    MyTreeItem child = new MyTreeItem(folders.getString("object_name"), "folder", String.format("%s/%s", parent.getPath(), folders.getString("object_name")));
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
            MyTreeItem child = new MyTreeItem(documents.getString("object_name"), "document", String.format("%s/%s", parent.getPath(), documents.getString("object_name")));
            children.add(child);
        }
    }
}
