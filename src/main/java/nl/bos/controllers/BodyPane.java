package nl.bos.controllers;

import com.documentum.fc.common.DfException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.extern.java.Log;
import nl.bos.Repository;

import java.io.IOException;

@Log
public class BodyPane {
    @Getter
    private static Stage loginStage;

    @FXML
    private void handleConnect(ActionEvent actionEvent) throws IOException {
        log.info(String.valueOf(actionEvent.getSource()));

        loginStage = new Stage();
        loginStage.initModality(Modality.APPLICATION_MODAL);
        loginStage.setTitle("Documentum Login");
        VBox loginPane = FXMLLoader.load(getClass().getResource("/nl/bos/views/LoginPane.fxml"));
        loginStage.setScene(new Scene(loginPane));
        loginStage.showAndWait();
    }

    @FXML
    private void handleExit(ActionEvent actionEvent) throws DfException {
        log.info(String.valueOf(actionEvent.getSource()));
        Repository.disconnect();
        System.exit(0);
    }
}
