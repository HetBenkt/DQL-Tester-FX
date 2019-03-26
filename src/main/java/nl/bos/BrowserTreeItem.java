package nl.bos;

import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;
import javafx.scene.control.TreeItem;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static nl.bos.Constants.*;

public class BrowserTreeItem extends TreeItem<String> {
    private static final Logger LOGGER = Logger.getLogger(BrowserTreeItem.class.getName());

    private final Repository repository = Repository.getInstance();

    private final String type;
    private final String name;
    private final String path;
    private final IDfPersistentObject object;

    public BrowserTreeItem(IDfPersistentObject object, String name, String type, String path) {
        this.object = object;
        this.name = name;
        this.type = type;
        this.path = path;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public String getType() {
        return type;
    }

    public IDfPersistentObject getObject() {
        return object;
    }

    public String getName() {
        return name;
    }

    public boolean isDirectory() {
        return type.equals(TYPE_REPOSITORY) || type.equals(TYPE_CABINET) || type.equals(TYPE_FOLDER);
    }

    private String getPath() {
        return path;
    }

    public List<BrowserTreeItem> listObjects(BrowserTreeItem parent, boolean showAllCabinets, boolean showAllVersions) {
        List<BrowserTreeItem> children = new ArrayList<>();

        try {
            if (TYPE_REPOSITORY.equals(parent.getType())) {
                IDfCollection cabinets;
                if (showAllCabinets)
                    cabinets = repository.query("select r_object_id, object_name, is_private from dm_cabinet order by object_name");
                else
                    cabinets = repository.query("select r_object_id, object_name, is_private from dm_cabinet where is_private = 0 order by object_name");
                while (cabinets.next()) {
                    IDfPersistentObject cabinet = repository.getSession().getObject(new DfId(cabinets.getString(ATTR_R_OBJECT_ID)));
                    BrowserTreeItem child = new BrowserTreeItem(cabinet, cabinets.getString(ATTR_OBJECT_NAME), TYPE_CABINET, String.format(PATH_FORMAT, parent.getPath(), cabinets.getString(ATTR_OBJECT_NAME)));
                    children.add(child);
                }
                cabinets.close();
            } else if (TYPE_CABINET.equals(parent.getType())) {
                addNodesToParent(parent, children, "cabinet", showAllVersions);
            } else if (TYPE_FOLDER.equals(parent.getType())) {
                addNodesToParent(parent, children, "folder", showAllVersions);
            }
        } catch (DfException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        return children;
    }

    private void addNodesToParent(BrowserTreeItem parent, List<BrowserTreeItem> children, String function, boolean showAllVersions) throws DfException {
        IDfCollection folders = repository.query(String.format("select r_object_id, object_name from dm_folder where %s('%s') order by object_name", function, parent.getPath()));
        while (folders.next()) {
            IDfPersistentObject folder = repository.getSession().getObject(new DfId(folders.getString(ATTR_R_OBJECT_ID)));
            BrowserTreeItem child = new BrowserTreeItem(folder, folders.getString(ATTR_OBJECT_NAME), TYPE_FOLDER, String.format(PATH_FORMAT, parent.getPath(), folders.getString(ATTR_OBJECT_NAME)));
            children.add(child);
        }
        folders.close();
        addDocuments(children, parent, showAllVersions);
    }

    private void addDocuments(List<BrowserTreeItem> children, BrowserTreeItem parent, boolean showAllVersions) throws DfException {
        IDfCollection documents;
        if (showAllVersions)
            documents = repository.query(String.format("select r_object_id, object_name from dm_sysobject (ALL) where folder('%s') and r_object_type != 'dm_folder' and r_object_type not in (select name from dm_type where super_name = 'dm_folder') order by object_name", parent.getPath()));
        else
            documents = repository.query(String.format("select r_object_id, object_name from dm_sysobject where folder('%s') and r_object_type != 'dm_folder' and r_object_type not in (select name from dm_type where super_name = 'dm_folder') order by object_name", parent.getPath()));
        while (documents.next()) {
            IDfPersistentObject document = repository.getSession().getObject(new DfId(documents.getString(ATTR_R_OBJECT_ID)));
            BrowserTreeItem child = new BrowserTreeItem(document, documents.getString(ATTR_OBJECT_NAME), TYPE_DOCUMENT, String.format(PATH_FORMAT, parent.getPath(), documents.getString(ATTR_OBJECT_NAME)));
            children.add(child);
        }
        documents.close();
    }
}
