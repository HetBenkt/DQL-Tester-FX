package nl.bos.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import nl.bos.Repository;
import nl.bos.contextmenu.menuitem.action.MenuItemExportToCsvAction;
import nl.bos.menu.menuitem.action.*;
import nl.bos.utils.Controllers;
import nl.bos.utils.Resources;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static nl.bos.Constants.ROOT_SCENE_CSS;

public class Menu {
    private static final Logger LOGGER = Logger.getLogger(Menu.class.getName());

    @FXML
    private javafx.scene.control.Menu menuTools;
    @FXML
    private javafx.scene.control.Menu menuAdmin;
    @FXML
    private javafx.scene.control.Menu menuInfo;
    @FXML
    private javafx.scene.control.Menu menuSpecial;
    @FXML
    private javafx.scene.control.MenuItem menuItemHelp;
    @FXML
    private javafx.scene.control.MenuItem menuShortcutKeys;

    private final Repository repository = Repository.getInstance();
    private Resources resources = new Resources();

    private final DescribeObjectAction describeObjectAction;
    @FXML
    private MenuItem miExportResults;

    public Menu() {
        Controllers.put(this.getClass().getSimpleName(), this);

        describeObjectAction = new DescribeObjectAction();
    }

    @FXML
    private void initialize() {
        if (repository.isConnected()) {
            menuTools.setDisable(true);
            menuAdmin.setDisable(true);
            menuInfo.setDisable(true);
            menuSpecial.setDisable(true);
        }
    }

    MenuItem getMiExportResults() {
        return miExportResults;
    }

    @FXML
    private void browseRepository() {
        new BrowseRepositoryAction();
    }

    @FXML
    private void manageJobs() {
        new ManageJobsAction();
    }

    @FXML
    private void describeObject() {
        describeObjectAction.execute();
    }

    @FXML
    private void getLastSQL() {
        new GetLastSQLAction();
    }

    @FXML
    private void enableSQLTrace() {
        new EnableSQLTraceAction();
    }

    @FXML
    private void disableSQLTrace() {
        new DisableSQLTraceAction();
    }

    @FXML
    private void showCurrentSessions() {
        new ShowCurrentSessionsAction();
    }

    @FXML
    private void executeAPIScript() {
        new ExecuteAPIScriptAction("");
    }

    @FXML
    private void executeDQLScript() {
        new ExecuteDQLScriptAction();
    }

    @FXML
    private void getAttributes() {
        Stage getAttributesStage = new Stage();
        getAttributesStage.setTitle(String.format("Attributes List - %s (%s)", "ID", repository.getRepositoryName()));
        VBox getAttributesPane = (VBox) resources.loadFXML("/nl/bos/views/GetAttributes.fxml");
        getAttributesStage.setScene(new Scene(getAttributesPane));
        getAttributesStage.getScene().getStylesheets()
                .addAll(ROOT_SCENE_CSS);

        getAttributesStage.showAndWait();
    }

    @FXML
    private void exportResults() {
        QueryWithResult queryWithResult = (QueryWithResult) Controllers.get(QueryWithResult.class.getSimpleName());
        new MenuItemExportToCsvAction(miExportResults, queryWithResult.getResult()).handle(null);
    }

    @FXML
    private void manageUsers(ActionEvent actionEvent) {
		new ManageUsersAction();
	}

    @FXML
    private void manageWorkflows(ActionEvent actionEvent) {
        new ManageWorkflowsAction();
    }

    public void about(ActionEvent actionEvent) {
        Stage aboutStage = new Stage();
        aboutStage.setTitle("About DQL Tester FX");
        BorderPane aboutPane = (BorderPane) resources.loadFXML("/nl/bos/views/About.fxml");
        aboutStage.setScene(new Scene(aboutPane));
        aboutStage.getScene().getStylesheets().addAll(ROOT_SCENE_CSS);
        aboutStage.showAndWait();
    }

    public javafx.scene.control.Menu getMenuTools() {
        return menuTools;
    }

    public javafx.scene.control.Menu getMenuAdmin() {
        return menuAdmin;
    }

    public javafx.scene.control.Menu getMenuInfo() {
        return menuInfo;
    }

    public javafx.scene.control.Menu getMenuSpecial() {
        return menuSpecial;
    }

    public void help() {
        showLink("url.help");
    }

    public void shortcutKeys() {
        showLink("url.shortcut_keys");
    }

    private void showLink(String url) {
        try {
            Desktop.getDesktop().browse(new URI(resources.getProjectProperty(url)));
        } catch (IOException | URISyntaxException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}