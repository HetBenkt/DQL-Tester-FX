package nl.bos.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;
import nl.bos.Repository;
import nl.bos.menu.menuitem.action.*;
import nl.bos.utils.Controllers;

public class Menu {
    @FXML
    private MenuBar menubar;

    private final DescribeObjectAction describeObjectAction;

    public Menu() {
        Controllers.put(this.getClass().getSimpleName(), this);

        describeObjectAction = new DescribeObjectAction();
    }

    @FXML
    private void initialize() {
        if (Repository.getInstance().isConnected()) {
            menubar.setDisable(false);
        }
    }

    MenuBar getMenubar() {
        return menubar;
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
}