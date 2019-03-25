package nl.bos.controllers;

import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.commands.admin.IDfAdminCommand;
import com.documentum.fc.commands.admin.impl.AdminApplyCommand;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfList;
import com.documentum.fc.common.IDfList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import nl.bos.Main;
import nl.bos.Repository;
import nl.bos.utils.AppAlert;
import nl.bos.utils.TableResultUtils;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Menu implements EventHandler<WindowEvent> {
    private static final Logger LOGGER = Logger.getLogger(Menu.class.getName());

    private final Repository repositoryCon = Repository.getInstance();

    private final static Stage describeObjectStage = new Stage();
    private final FXMLLoader fxmlLoader;

    @FXML
    private MenuBar menubar;

    static Stage getDescribeObjectStage() {
        return describeObjectStage;
    }

    public MenuBar getMenubar() {
        return menubar;
    }

    /**
     * @noinspection WeakerAccess
     */
    public Menu() {
        describeObjectStage.setTitle("Describe object");
        fxmlLoader = new FXMLLoader(getClass().getResource("/nl/bos/views/DescribeObject.fxml"));
        try {
            HBox describeObject = fxmlLoader.load();
            describeObjectStage.setScene(new Scene(describeObject));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        describeObjectStage.setOnCloseRequest(this);
    }

    /**
     * @noinspection EmptyMethod
     */
    @FXML
    private void initialize() {
        //No implementation needed
    }

    @Override
    public void handle(WindowEvent windowEvent) {
        DescribeObject describeObjectController = fxmlLoader.getController();
        String currentSelected = (String) describeObjectController.getCurrentSelected().getValue();
        String type = describeObjectController.getCurrentSelected().getType();
        String message = MessageFormat.format("Selected item ''{0}'' of type ''{1}''", currentSelected, type);
        LOGGER.info(message);
        new TableResultUtils().updateTable(type, currentSelected);
    }

    @FXML
    private void browseRepository(ActionEvent actionEvent) {
        Stage browseRepositoryStage = new Stage();
        browseRepositoryStage.initModality(Modality.APPLICATION_MODAL);
        browseRepositoryStage.setTitle(String.format("Repository Browser - %s (%s)", repositoryCon.getRepositoryName(), repositoryCon.getUserName()));
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/nl/bos/views/RepositoryBrowser.fxml"));
        try {
            VBox repositoryBrowser = fxmlLoader.load();
            browseRepositoryStage.setScene(new Scene(repositoryBrowser));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        browseRepositoryStage.showAndWait();
    }

    @FXML
    private void manageJobs(ActionEvent actionEvent) {
        Stage jobEditorStage = new Stage();
        jobEditorStage.setTitle(String.format("Job Editor - %s", repositoryCon.getRepositoryName()));
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/nl/bos/views/JobEditor.fxml"));
        try {
            VBox jobEditor = fxmlLoader.load();
            jobEditorStage.setScene(new Scene(jobEditor));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        jobEditorStage.showAndWait();
    }

    @FXML
    private void describeObject(ActionEvent actionEvent) {
        DescribeObject describeObjectController = fxmlLoader.getController();
        describeObjectController.initialize();
        describeObjectStage.showAndWait();
    }

    @FXML
    private void getLastSQL(ActionEvent actionEvent) {
        try {
            IDfCollection lastSql = repositoryCon.query("EXECUTE get_last_sql");
            lastSql.next();

            GridPane gridPane = new GridPane();
            gridPane.setMaxWidth(Double.MAX_VALUE);
            TextArea textArea = new TextArea(lastSql.getString("result"));
            textArea.setEditable(false);
            textArea.setWrapText(true);
            gridPane.add(textArea, 0, 0);
            AppAlert.infoWithPanel("", gridPane);

            lastSql.close();
        } catch (DfException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @FXML
    private void enableSQLTrace(ActionEvent actionEvent) {
        try {
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
                AppAlert.info("SQL Trace is enabled", "Please check the repository log for the enabled trace");
            }
            setOptions.close();
        } catch (DfException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @FXML
    private void disableSQLTrace(ActionEvent actionEvent) {
        try {
            IDfAdminCommand command = AdminApplyCommand.getCommand(IDfAdminCommand.APPLY_SET_OPTIONS);
            command.setString("OPTION", "sqltrace");
            command.setBoolean("VALUE", false);
            IDfCollection execute = command.execute(repositoryCon.getSession());
            execute.next();
            if (execute.getBoolean("result")) {
                AppAlert.info("SQL Trace is disabled", "Please check the repository log for the disabled trace");
            }
            execute.close();
        } catch (DfException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @FXML
    private void showCurrentSessions(ActionEvent actionEvent) {
        try {
            IDfCollection showSessions = repositoryCon.query("EXECUTE show_sessions");
            QueryWithResult queryWithResultController = Main.getInstance().getBodyPaneLoader().getController();
            queryWithResultController.updateResultTable(showSessions);
            showSessions.close();
        } catch (DfException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}
