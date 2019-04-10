package nl.bos.menu.menuitem.action;

import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.common.DfException;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import nl.bos.Repository;
import nl.bos.utils.AppAlert;

import java.util.logging.Level;
import java.util.logging.Logger;

public class GetLastSQLAction {
    private static final Logger LOGGER = Logger.getLogger(GetLastSQLAction.class.getName());

    public GetLastSQLAction() {
        try {
            Repository repository = Repository.getInstance();
            IDfCollection lastSql = repository.query("EXECUTE get_last_sql");
            lastSql.next();

            GridPane gridPane = new GridPane();
            gridPane.setMaxWidth(Double.MAX_VALUE);
            TextArea textArea = new TextArea(lastSql.getString("result"));
            textArea.setEditable(false);
            textArea.setWrapText(true);
            gridPane.add(textArea, 0, 0);
            AppAlert.informationWithPanel("", gridPane);

            lastSql.close();
        } catch (DfException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}
