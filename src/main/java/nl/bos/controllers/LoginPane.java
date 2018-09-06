package nl.bos.controllers;

import com.documentum.fc.client.IDfDocbaseMap;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import lombok.extern.java.Log;
import nl.bos.Repository;

import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Properties;
import java.util.ResourceBundle;

@Log
public class LoginPane implements Initializable {
    @FXML
    private Label lblVersion;
    @FXML
    private Label lblServer;
    @FXML
    private ChoiceBox chbRepository;
    @FXML
    private Button btnConnect;
    @FXML
    private TextField txtUsername;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private TextField txtDomain;
    private String hostName;

    @FXML
    private void handleConnect(ActionEvent actionEvent) {
        log.info(String.valueOf(actionEvent.getSource()));
        Repository.setCredentials(chbRepository.getValue().toString(), txtUsername.getText(), txtPassword.getText(), txtDomain.getText());
        InputPane.getLoginStage().close();
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
        lblVersion.setText(getProjectVersion());
        try {
            IDfDocbaseMap repositoryMap = Repository.obtainRepositoryMap();
            //noinspection deprecation
            hostName = repositoryMap.getHostName();
            lblServer.setText(hostName);

            log.info(MessageFormat.format("Repositories for Connection Broker: {0}", hostName));
            log.info(MessageFormat.format("Total number of Repostories: {0}", repositoryMap.getDocbaseCount()));
            for (int i = 0; i < repositoryMap.getDocbaseCount(); i++) {
                log.info(MessageFormat.format("Repository {0}", (i + 1) + ": " + repositoryMap.getDocbaseName(i)));
                ObservableList repositories = FXCollections.observableArrayList();
                repositories.add(repositoryMap.getDocbaseName(i));
                chbRepository.setItems(repositories);
                chbRepository.setValue(chbRepository.getItems().get(0));
            }
        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }

    private String getProjectVersion() {
        final Properties properties = new Properties();
        try {
            properties.load(this.getClass().getClassLoader().getResourceAsStream("project.properties"));
            return properties.getProperty("version");
        } catch (IOException e) {
            log.info(e.getMessage());
        }
        return "";
    }

    public void handleConnectButton(KeyEvent keyEvent) {
        if (txtUsername.getText().length() > 0 && txtPassword.getText().length() > 0)
            btnConnect.setDisable(false);
        else
            btnConnect.setDisable(true);
    }
}
