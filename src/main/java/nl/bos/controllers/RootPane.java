package nl.bos.controllers;

import com.documentum.fc.common.DfException;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import nl.bos.Main;
import nl.bos.Repository;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.logging.Logger;

import static nl.bos.Constants.TABLE;
import static nl.bos.Constants.TYPE;

public class RootPane implements EventHandler<WindowEvent> {
    private static final Logger log = Logger.getLogger(RootPane.class.getName());

    public MenuBar getMenubar() {
        return menubar;
    }

    private final static Stage describeObjectStage = new Stage();
    private final Repository repositoryCon = Repository.getInstance();
    private final Main main = Main.getInstance();

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
        try {
            switch (type) {
                case TYPE:
                    updateTableWithTypeInfo(currentSelected);
                    break;
                case TABLE:
                    updateTableWithTableInfo(currentSelected);
                    break;
                default:
                    log.info("Do nothing");
                    break;
            }
        } catch (DfException e) {
            log.finest(e.getMessage());
        }
    }

    private void updateTableWithTableInfo(String currentSelected) throws DfException {
        BodyPane bodyPaneController = main.getBodyPaneLoader().getController();
        String tableDesciption = repositoryCon.getSession().describe(TABLE, "dm_dbo." + currentSelected);
        bodyPaneController.updateResultTableWithStringInput(tableDesciption, Arrays.asList("Column", "Data Type", "Primary Key"));
    }

    private void updateTableWithTypeInfo(String currentSelected) throws DfException {
        BodyPane bodyPaneController = main.getBodyPaneLoader().getController();
        String typeDesciption = repositoryCon.getSession().describe(TYPE, currentSelected);
        bodyPaneController.updateResultTableWithStringInput(typeDesciption, Arrays.asList("Attribute", "Data Type", "Repeating"));
    }
}
