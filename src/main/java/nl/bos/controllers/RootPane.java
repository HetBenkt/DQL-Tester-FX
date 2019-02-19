package nl.bos.controllers;

import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.commands.admin.IDfAdminCommand;
import com.documentum.fc.commands.admin.impl.AdminApplyCommand;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfList;
import com.documentum.fc.common.IDfAttr;
import com.documentum.fc.common.IDfList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import nl.bos.Repository;
import nl.bos.utils.TableResultUtils;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.logging.Logger;

public class RootPane implements EventHandler<WindowEvent> {
    private static final Logger log = Logger.getLogger(RootPane.class.getName());

    public MenuBar getMenubar() {
        return menubar;
    }

    private final static Stage describeObjectStage = new Stage();
    private final Repository repositoryCon = Repository.getInstance();

    @FXML
    private MenuBar menubar;

    private FXMLLoader fxmlLoader;

    public RootPane() throws IOException {
        describeObjectStage.setTitle("Describe object");
        fxmlLoader = new FXMLLoader(getClass().getResource("/nl/bos/views/DescribeObjectPane.fxml"));
        HBox describeObject = fxmlLoader.load();
        describeObjectStage.setScene(new Scene(describeObject));
        describeObjectStage.setOnCloseRequest(this);
    }

    static Stage getDescribeObjectStage() {
        return describeObjectStage;
    }

    @FXML
    private void browseRepository(ActionEvent actionEvent) throws IOException {
        Stage browseRepositoryStage = new Stage();
        browseRepositoryStage.initModality(Modality.APPLICATION_MODAL);
        browseRepositoryStage.setTitle(String.format("Repository Browser - %s (%s)", repositoryCon.getRepositoryName(), repositoryCon.getUserName()));
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/nl/bos/views/RepositoryBrowserPane.fxml"));
        VBox repositoryBrowser = fxmlLoader.load();
        browseRepositoryStage.setScene(new Scene(repositoryBrowser));

        browseRepositoryStage.showAndWait();
    }

    @FXML
    private void manageJobs(ActionEvent actionEvent) throws IOException {
        Stage jobEditorStage = new Stage();
        jobEditorStage.setTitle(String.format("Job Editor - %s", repositoryCon.getRepositoryName()));
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/nl/bos/views/JobEditorPane.fxml"));
        VBox jobEditor = fxmlLoader.load();
        jobEditorStage.setScene(new Scene(jobEditor));

        jobEditorStage.showAndWait();
    }

    @FXML
    private void describeObject(ActionEvent actionEvent) {
        DescribeObjectPane describeObjectPaneController = fxmlLoader.getController();
        describeObjectPaneController.initialize(null, null);
        describeObjectStage.showAndWait();
    }

    @Override
    public void handle(WindowEvent windowEvent) {
        DescribeObjectPane describeObjectPaneController = fxmlLoader.getController();
        String currentSelected = (String) describeObjectPaneController.getCurrentSelected().getValue();
        String type = describeObjectPaneController.getCurrentSelected().getType();
        String message = MessageFormat.format("Selected item ''{0}'' of type ''{1}''", currentSelected, type);
        log.info(message);
        new TableResultUtils().updateTable(type, currentSelected);
    }

    @FXML
    private void getLastSQL(ActionEvent actionEvent) throws DfException {
        IDfCollection lastSql = repositoryCon.query("EXECUTE get_last_sql");
        lastSql.next();

        Alert confirmation = new Alert(Alert.AlertType.INFORMATION);
        confirmation.setTitle("Last executed SQL");
        confirmation.setHeaderText(null);
        GridPane gridPane = new GridPane();
        gridPane.setMaxWidth(Double.MAX_VALUE);
        TextArea textArea = new TextArea(lastSql.getString("result"));
        textArea.setEditable(false);
        textArea.setWrapText(true);
        gridPane.add(textArea, 0, 0);
        confirmation.getDialogPane().setContent(gridPane);
        confirmation.showAndWait();

        lastSql.close();
    }

    @FXML
    private void enableSQLTrace(ActionEvent actionEvent) throws DfException {
        IDfList args = new DfList();
        args.append("OPTION");
        args.append("VALUE");
        IDfList dataType = new DfList();
        dataType.append("S");
        dataType.append("B");
        IDfList values = new DfList();
        values.append("sqltrace");
        values.append("T");
        IDfCollection setOptions = repositoryCon.getSession().apply(null, "SET_OPTIONS", args, dataType, values);
        setOptions.next();
        if (setOptions.getBoolean("result")) {
            Alert confirmation = new Alert(Alert.AlertType.INFORMATION);
            confirmation.setTitle("SQL Trace is enabled");
            confirmation.setHeaderText(null);
            confirmation.setContentText("Please check the repository log for the enabled trace");
            confirmation.showAndWait();
        }
        setOptions.close();
    }

    @FXML
    private void disbleSQLTrace(ActionEvent actionEvent) throws DfException {
        IDfAdminCommand command = AdminApplyCommand.getCommand(IDfAdminCommand.APPLY_SET_OPTIONS);
        command.setString("OPTION", "sqltrace");
        command.setBoolean("VALUE", false);
        IDfCollection execute = command.execute(repositoryCon.getSession());
        execute.next();
        if (execute.getBoolean("result")) {
            Alert confirmation = new Alert(Alert.AlertType.INFORMATION);
            confirmation.setTitle("SQL Trace is disabled");
            confirmation.setHeaderText(null);
            confirmation.setContentText("Please check the repository log for the disabled trace");
            confirmation.showAndWait();
        }
        execute.close();
    }

    public void showCurrentSessions(ActionEvent actionEvent) throws DfException {
        IDfCollection showSessions = repositoryCon.query("EXECUTE show_sessions");
        while (showSessions.next()) {
            for (int i = 0; i < showSessions.getAttrCount(); i++) {
                IDfAttr attr = showSessions.getAttr(i);
                log.info(attr.getName());
            }
        }
        showSessions.close();
    }
}
