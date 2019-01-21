package nl.bos;


import com.documentum.fc.common.IDfAttr;
import javafx.scene.control.TableColumn;

public class MyTableColumn extends TableColumn {
    private IDfAttr attr;

    public MyTableColumn(String name) {
        super(name);
    }

    public IDfAttr getAttr() {
        return attr;
    }

    public void setAttr(IDfAttr attr) {
        this.attr = attr;
    }
}
