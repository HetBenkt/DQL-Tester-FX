package nl.bos.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import lombok.extern.java.Log;

import java.net.URL;
import java.util.ResourceBundle;

@Log
public class LoginPane implements Initializable {
    @FXML
    private Label lblVersion;

    @FXML
    private void handleConnect(ActionEvent actionEvent) {
        log.info(String.valueOf(actionEvent.getSource()));
    }

    @FXML
    private void handleCancel(ActionEvent actionEvent) {
        log.info(String.valueOf(actionEvent.getSource()));
        BodyPane.getLoginStage().close();
    }

    @FXML
    private void handleServerMap(ActionEvent actionEvent) {
        log.info(String.valueOf(actionEvent.getSource()));
    }

    @FXML
    private void handleConnectionBrokerMap(ActionEvent actionEvent) {
        log.info(String.valueOf(actionEvent.getSource()));
    }

    public void initialize(URL location, ResourceBundle resources) {
        log.info(String.valueOf(location));
        lblVersion.setText("1.0");

    }
}
