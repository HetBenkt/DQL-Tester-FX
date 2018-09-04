package nl.bos.controllers;

import com.documentum.fc.client.IDfDocbaseMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import lombok.extern.java.Log;
import nl.bos.Repository;

import java.net.URL;
import java.text.MessageFormat;
import java.util.ResourceBundle;

@Log
public class LoginPane implements Initializable {
    @FXML
    private Label lblVersion;
    @FXML
    private Label lblServer;

    @FXML
    private void handleConnect(ActionEvent actionEvent) {
        log.info(String.valueOf(actionEvent.getSource()));
    }

    @FXML
    private void handleCancel(ActionEvent actionEvent) {
        log.info(String.valueOf(actionEvent.getSource()));
        InputPane.getLoginStage().close();
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
        lblVersion.setText("1.0");
        try {
            IDfDocbaseMap repositoryMap = Repository.obtainRepositoryMap();
            //noinspection deprecation
            String hostName = repositoryMap.getHostName();
            lblServer.setText(hostName);

            log.info(MessageFormat.format("Repositories for Connection Broker: {0}", hostName));
            log.info(MessageFormat.format("Total number of Repostories: {0}", repositoryMap.getDocbaseCount()));
            for (int i = 0; i < repositoryMap.getDocbaseCount(); i++) {
                log.info(MessageFormat.format("Repository {0}", (i + 1) + ": " + repositoryMap.getDocbaseName(i)));
            }
        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }
}
