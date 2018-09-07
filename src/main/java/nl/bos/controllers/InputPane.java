package nl.bos.controllers;

import com.documentum.fc.common.DfException;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lombok.Getter;
import lombok.extern.java.Log;
import nl.bos.Repository;

import java.io.IOException;

@Log
public class InputPane implements EventHandler<WindowEvent> {
    @Getter
    private static Stage loginStage;
    @FXML
    private Label lblStatus;
    @FXML
    private Label lblUsernameOS;
    @FXML
    private Label lblUsernameDC;
    @FXML
    private Label lblDomainOS;
    @FXML
    private Label lblPrivileges;
    @FXML
    private Label lblServerVersion;
    private FXMLLoader fxmlLoader;

    @FXML
    private void handleConnect(ActionEvent actionEvent) throws IOException {
        log.info(String.valueOf(actionEvent.getSource()));

        loginStage = new Stage();
        loginStage.initModality(Modality.APPLICATION_MODAL);
        loginStage.setTitle("Documentum Login");
        fxmlLoader = new FXMLLoader(getClass().getResource("/nl/bos/views/LoginPane.fxml"));
        VBox loginPane = fxmlLoader.load();
        loginStage.setScene(new Scene(loginPane));
        loginStage.setOnCloseRequest(this);
        loginStage.showAndWait();
    }

    @FXML
    private void handleExit(ActionEvent actionEvent) throws DfException {
        log.info(String.valueOf(actionEvent.getSource()));
        Repository.disconnect();
        System.exit(0);
    }

    public void handle(WindowEvent event) {
        log.info(String.valueOf(event.getSource()));
        LoginPane loginPaneController = fxmlLoader.getController();
        lblStatus.setText(String.valueOf(loginPaneController.getChbRepository().getValue()));
        lblUsernameOS.setText("dummy");
        lblUsernameDC.setText(loginPaneController.getTxtUsername().getText());
        lblDomainOS.setText(loginPaneController.getHostName());
        lblPrivileges.setText("dummy");
        lblServerVersion.setText("dummy");
    }
}
