package nl.bos.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.extern.java.Log;
import nl.bos.Repository;

import java.io.IOException;

@Log
public class RootPane {
    @FXML
    @Getter
    private MenuBar menubar;

    public void browseRepository(ActionEvent actionEvent) throws IOException {
        log.info(String.valueOf(actionEvent.getSource()));
        Repository repositoryCon = Repository.getInstance();
        Stage browseRepositoryStage = new Stage();
        browseRepositoryStage.initModality(Modality.APPLICATION_MODAL);
        browseRepositoryStage.setTitle(String.format("Repository Browser - %s (%s)", repositoryCon.getRepositoryName(), repositoryCon.getUserName()));
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/nl/bos/views/RepositoryBrowser.fxml"));
        VBox repositoryBrowser = fxmlLoader.load();
        browseRepositoryStage.setScene(new Scene(repositoryBrowser));

        browseRepositoryStage.showAndWait();
    }
}
