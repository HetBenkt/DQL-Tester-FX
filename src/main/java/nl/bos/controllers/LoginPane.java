package nl.bos.controllers;

import com.documentum.fc.client.IDfDocbaseMap;
import com.documentum.fc.client.IDfTypedObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfAttr;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lombok.Getter;
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
    @Getter
    private ChoiceBox chbRepository;
    @FXML
    private Button btnConnect;
    @FXML
    private Button btnDisconnect;
    @FXML
    @Getter
    private TextField txtUsername;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private TextField txtDomain;
    @Getter
    private String hostName;
    @FXML
    private CheckBox chkSaveLoginData;
    @FXML
    private CheckBox chkUseWindowsLogin;

    private Repository repositoryCon = Repository.getRepositoryCon();

    @FXML
    private void handleConnect(ActionEvent actionEvent) throws DfException {
        log.info(String.valueOf(actionEvent.getSource()));
        String selectedRepository = chbRepository.getValue().toString();
        lblServer.setText(String.format("Connection to '%s'", selectedRepository));
        repositoryCon.setCredentials(selectedRepository, txtUsername.getText(), txtPassword.getText(), txtDomain.getText());
        repositoryCon.createSessionManager();
        if (repositoryCon.isConnectionValid()) {
            Stage loginStage = InputPane.getLoginStage();
            loginStage.fireEvent(new WindowEvent(loginStage, WindowEvent.WINDOW_CLOSE_REQUEST));
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Information Dialog");
            alert.setHeaderText(null);
            alert.setContentText(repositoryCon.getErrorMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void handleCancel(ActionEvent actionEvent) {
        log.info(String.valueOf(actionEvent.getSource()));
        Stage loginStage = InputPane.getLoginStage();
        loginStage.fireEvent(new WindowEvent(loginStage, WindowEvent.WINDOW_CLOSE_REQUEST));
    }

    @FXML
    private void handleServerMap(ActionEvent actionEvent) throws DfException {
        log.info(String.valueOf(actionEvent.getSource()));

        String selectedRepository = chbRepository.getValue().toString();
        IDfTypedObject serverMap = repositoryCon.obtainServerMap(selectedRepository);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(String.format("Server Map for Repository >> %s <<", selectedRepository));
        alert.getDialogPane().setContent(formatContent(serverMap));
        alert.showAndWait();
    }

    private HBox formatContent(IDfTypedObject typedObject) throws DfException {
        HBox hBox = new HBox();
        hBox.setSpacing(10);
        VBox labels = new VBox();
        VBox values = new VBox();

        for (int i = 0; i < typedObject.getAttrCount(); i++) {
            IDfAttr attr = typedObject.getAttr(i);
            labels.getChildren().add(new Label(String.format("%s:", attr.getName())));

            Label label = new Label();
            switch (attr.getDataType()) {
                case IDfAttr.DM_BOOLEAN:
                    label.setText(String.valueOf(typedObject.getBoolean(attr.getName())));
                    break;
                case IDfAttr.DM_DOUBLE:
                    label.setText(String.valueOf(typedObject.getDouble(attr.getName())));
                    break;
                case IDfAttr.DM_ID:
                    label.setText(String.valueOf(typedObject.getId(attr.getName())));
                    break;
                case IDfAttr.DM_INTEGER:
                    label.setText(String.valueOf(typedObject.getInt(attr.getName())));
                    break;
                case IDfAttr.DM_STRING:
                    label.setText(String.valueOf(typedObject.getString(attr.getName())));
                    break;
                case IDfAttr.DM_TIME:
                    label.setText(String.valueOf(typedObject.getTime(attr.getName())));
                    break;
                default:
                    log.finest("Error occurred while displaying the results.");
                    break;
            }
            values.getChildren().add(label);
        }
        hBox.getChildren().addAll(labels, values);
        return hBox;
    }

    @FXML
    private void handleConnectionBrokerMap(ActionEvent actionEvent) throws DfException {
        log.info(String.valueOf(actionEvent.getSource()));

        IDfDocbaseMap repositoryMap = repositoryCon.obtainRepositoryMap();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText("Connection Broker Map");
        alert.getDialogPane().setContent(formatContent(repositoryMap));
        alert.showAndWait();
    }

    public void initialize(URL location, ResourceBundle resources) {
        try {
            if (repositoryCon.getSession() != null && repositoryCon.getSession().isConnected()) {
                btnDisconnect.managedProperty().bindBidirectional(btnDisconnect.visibleProperty());
                btnDisconnect.setManaged(true);

                btnConnect.managedProperty().bindBidirectional(btnConnect.visibleProperty());
                btnConnect.setManaged(false);

                chbRepository.setDisable(true);
                txtUsername.setDisable(true);
                txtPassword.setDisable(true);
                txtDomain.setDisable(true);
                chkSaveLoginData.setDisable(true);
                chkUseWindowsLogin.setDisable(true);
            } else {
                btnDisconnect.managedProperty().bindBidirectional(btnDisconnect.visibleProperty());
                btnDisconnect.setManaged(false);
            }

            lblVersion.setText(getProjectVersion());

            IDfDocbaseMap repositoryMap = repositoryCon.obtainRepositoryMap();
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

    @FXML
    private void handleConnectButton(KeyEvent keyEvent) {
        if (txtUsername.getText().length() > 0 && txtPassword.getText().length() > 0)
            btnConnect.setDisable(false);
        else
            btnConnect.setDisable(true);
    }

    @FXML
    private void handleDisconnect(ActionEvent actionEvent) {
        log.info(String.valueOf(actionEvent.getSource()));
        repositoryCon.disconnect();
        Stage loginStage = InputPane.getLoginStage();
        loginStage.fireEvent(new WindowEvent(loginStage, WindowEvent.WINDOW_CLOSE_REQUEST));
    }
}
