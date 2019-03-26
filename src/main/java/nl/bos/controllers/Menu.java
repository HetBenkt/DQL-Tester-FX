package nl.bos.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;
import nl.bos.menu.menuitem.action.*;

public class Menu {
    @FXML
    private MenuBar menubar;

    private DescribeObjectAction describeObjectAction;

    public Menu() {
        describeObjectAction = new DescribeObjectAction();
    }

    DescribeObjectAction getDescribeObjectAction() {
        return describeObjectAction;
    }

    public MenuBar getMenubar() {
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