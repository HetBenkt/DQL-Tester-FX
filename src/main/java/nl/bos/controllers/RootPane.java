package nl.bos.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import nl.bos.Repository;

import java.io.IOException;

public class RootPane {
    public MenuBar getMenubar() {
        return menubar;
    }

    private final Repository repositoryCon = Repository.getInstance();

    @FXML
    private MenuBar menubar;

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
    private void describeObject(ActionEvent actionEvent) throws IOException {
        Stage describeObjectStage = new Stage();
        describeObjectStage.setTitle("Describe object");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/nl/bos/views/DescribeObjectPane.fxml"));
        HBox describeObject = fxmlLoader.load();
        describeObjectStage.setScene(new Scene(describeObject));

        describeObjectStage.showAndWait();
    }
}
