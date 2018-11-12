package nl.bos;

import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;
import javafx.scene.control.TreeItem;
import lombok.Data;
import lombok.extern.java.Log;

import java.util.ArrayList;
import java.util.List;

import static nl.bos.Constants.*;

@Data
@Log
public class MyTreeItem extends TreeItem<String> {

    private String type;
    private String name;
    private String path;
    private IDfPersistentObject object;
    private Repository repositoryCon = Repository.getInstance();

    public MyTreeItem(IDfPersistentObject object, String name, String type, String path) {
        this.object = object;
        this.name = name;
        this.type = type;
        this.path = path;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public boolean isDirectory() {
        return type.equals(TYPE_REPOSITORY) || type.equals(TYPE_CABINET) || type.equals(TYPE_FOLDER);
    }

    public List<MyTreeItem> listObjects(MyTreeItem parent) {
        List<MyTreeItem> children = new ArrayList<>();

        try {
            if (TYPE_REPOSITORY.equals(parent.getType())) {
                IDfCollection cabinets = repositoryCon.query("select r_object_id, object_name, is_private from dm_cabinet order by object_name");
                while (cabinets.next()) {
                    IDfPersistentObject cabinet = repositoryCon.getSession().getObject(new DfId(cabinets.getString(ATTR_R_OBJECT_ID)));
                    MyTreeItem child = new MyTreeItem(cabinet, cabinets.getString(ATTR_OBJECT_NAME), TYPE_CABINET, String.format(PATH_FORMAT, parent.getPath(), cabinets.getString(ATTR_OBJECT_NAME)));
                    children.add(child);
                }

            } else if (TYPE_CABINET.equals(parent.getType())) {
                addNodesToParent(parent, children, "cabinet");
            } else if (TYPE_FOLDER.equals(parent.getType())) {
                addNodesToParent(parent, children, "folder");
            }
        } catch (DfException e) {
            log.finest(e.getMessage());
        }

        return children;
    }

    private void addNodesToParent(MyTreeItem parent, List<MyTreeItem> children, String type) throws DfException {
        IDfCollection folders = repositoryCon.query(String.format("select r_object_id, object_name from dm_folder where %s('%s') order by object_name", type, parent.getPath()));
        while (folders.next()) {
            IDfPersistentObject folder = repositoryCon.getSession().getObject(new DfId(folders.getString(ATTR_R_OBJECT_ID)));
            MyTreeItem child = new MyTreeItem(folder, folders.getString(ATTR_OBJECT_NAME), TYPE_FOLDER, String.format(PATH_FORMAT, parent.getPath(), folders.getString(ATTR_OBJECT_NAME)));
            children.add(child);
        }
        addDocuments(children, parent);
    }

    private void addDocuments(List<MyTreeItem> children, MyTreeItem parent) throws DfException {
        IDfCollection documents = repositoryCon.query(String.format("select r_object_id, object_name from dm_sysobject where folder('%s') and r_object_type != 'dm_folder' order by object_name", parent.getPath()));
        while (documents.next()) {
            IDfPersistentObject document = repositoryCon.getSession().getObject(new DfId(documents.getString(ATTR_R_OBJECT_ID)));
            MyTreeItem child = new MyTreeItem(document, documents.getString(ATTR_OBJECT_NAME), TYPE_DOCUMENT, String.format(PATH_FORMAT, parent.getPath(), documents.getString(ATTR_OBJECT_NAME)));
            children.add(child);
        }
    }
}
