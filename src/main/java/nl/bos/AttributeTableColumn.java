package nl.bos;


import com.documentum.fc.common.IDfAttr;
import javafx.scene.control.TableColumn;

public class AttributeTableColumn extends TableColumn {
    private IDfAttr attr;

    public AttributeTableColumn(String name) {
        super(name);
    }

    IDfAttr getAttr() {
        return attr;
    }

    public void setAttr(IDfAttr attr) {
        this.attr = attr;
    }
}
