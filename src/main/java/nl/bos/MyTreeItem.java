package nl.bos;

import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;
import javafx.scene.control.TreeItem;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static nl.bos.Constants.*;

public class MyTreeItem extends TreeItem<String> {
    private static final Logger log = Logger.getLogger(MyTreeItem.class.getName());

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

    public List<MyTreeItem> listObjects(MyTreeItem parent, boolean showAllCabinets, boolean showAllVersions) {
        List<MyTreeItem> children = new ArrayList<>();

        try {
            if (TYPE_REPOSITORY.equals(parent.getType())) {
                IDfCollection cabinets;
                if (showAllCabinets)
                    cabinets = repositoryCon.query("select r_object_id, object_name, is_private from dm_cabinet order by object_name");
                else
                    cabinets = repositoryCon.query("select r_object_id, object_name, is_private from dm_cabinet where is_private = 0 order by object_name");
                while (cabinets.next()) {
                    IDfPersistentObject cabinet = repositoryCon.getSession().getObject(new DfId(cabinets.getString(ATTR_R_OBJECT_ID)));
                    MyTreeItem child = new MyTreeItem(cabinet, cabinets.getString(ATTR_OBJECT_NAME), TYPE_CABINET, String.format(PATH_FORMAT, parent.getPath(), cabinets.getString(ATTR_OBJECT_NAME)));
                    children.add(child);
                }
                cabinets.close();
            } else if (TYPE_CABINET.equals(parent.getType())) {
                addNodesToParent(parent, children, "cabinet", showAllVersions);
            } else if (TYPE_FOLDER.equals(parent.getType())) {
                addNodesToParent(parent, children, "folder", showAllVersions);
            }
        } catch (DfException e) {
            log.finest(e.getMessage());
        }

        return children;
    }

    private String getPath() {
        return path;
    }

    public String getType() {
        return type;
    }

    private void addNodesToParent(MyTreeItem parent, List<MyTreeItem> children, String function, boolean showAllVersions) throws DfException {
        IDfCollection folders = repositoryCon.query(String.format("select r_object_id, object_name from dm_folder where %s('%s') order by object_name", function, parent.getPath()));
        while (folders.next()) {
            IDfPersistentObject folder = repositoryCon.getSession().getObject(new DfId(folders.getString(ATTR_R_OBJECT_ID)));
            MyTreeItem child = new MyTreeItem(folder, folders.getString(ATTR_OBJECT_NAME), TYPE_FOLDER, String.format(PATH_FORMAT, parent.getPath(), folders.getString(ATTR_OBJECT_NAME)));
            children.add(child);
        }
        folders.close();
        addDocuments(children, parent, showAllVersions);
    }

    private void addDocuments(List<MyTreeItem> children, MyTreeItem parent, boolean showAllVersions) throws DfException {
        IDfCollection documents;
        if (showAllVersions)
            documents = repositoryCon.query(String.format("select r_object_id, object_name from dm_sysobject (ALL) where folder('%s') and r_object_type != 'dm_folder' and r_object_type not in (select name from dm_type where super_name = 'dm_folder') order by object_name", parent.getPath()));
        else
            documents = repositoryCon.query(String.format("select r_object_id, object_name from dm_sysobject where folder('%s') and r_object_type != 'dm_folder' and r_object_type not in (select name from dm_type where super_name = 'dm_folder') order by object_name", parent.getPath()));
        while (documents.next()) {
            IDfPersistentObject document = repositoryCon.getSession().getObject(new DfId(documents.getString(ATTR_R_OBJECT_ID)));
            MyTreeItem child = new MyTreeItem(document, documents.getString(ATTR_OBJECT_NAME), TYPE_DOCUMENT, String.format(PATH_FORMAT, parent.getPath(), documents.getString(ATTR_OBJECT_NAME)));
            children.add(child);
        }
        documents.close();
    }

    public IDfPersistentObject getObject() {
        return object;
    }

    public String getName() {
        return name;
    }
}
