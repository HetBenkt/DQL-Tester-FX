package nl.bos.controllers;

import com.documentum.fc.client.IDfDocbaseMap;
import com.documentum.fc.client.IDfTypedObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfAttr;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import nl.bos.Repository;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Login {
    private static final Logger LOGGER = Logger.getLogger(Login.class.getName());

    private final Repository repositoryCon = Repository.getInstance();

    @FXML
    private Label lblVersion;
    @FXML
    private Label lblServer;
    @FXML
    private ChoiceBox<String> chbRepository;
    @FXML
    private Button btnLogin;
    @FXML
    private Button btnLogout;
    @FXML
    private TextField txtUsername;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private TextField txtDomain;
    @FXML
    private CheckBox chkSaveLoginData;
    @FXML
    private CheckBox chkUseWindowsLogin;
    private String projectVersion = null;

    @FXML
    void initialize() {
        try {
            if (repositoryCon.getSession() != null && repositoryCon.getSession().isConnected()) {
                setFieldsConnect(true);
            } else {
                setFieldsConnect(false);
            }

            lblVersion.setText(getProjectVersion());

            if (repositoryCon.getClient() != null) {
                IDfDocbaseMap repositoryMap = repositoryCon.obtainRepositoryMap();
                //noinspection deprecation
                String hostName = repositoryMap.getHostName();
                lblServer.setText(hostName);

                LOGGER.info(MessageFormat.format("Repositories for Connection Broker: {0}", hostName));
                LOGGER.info(MessageFormat.format("Total number of Repositories: {0}", repositoryMap.getDocbaseCount()));
                for (int i = 0; i < repositoryMap.getDocbaseCount(); i++) {
                    LOGGER.info(MessageFormat.format("Repository {0}", (i + 1) + ": " + repositoryMap.getDocbaseName(i)));
                    ObservableList<String> repositories = FXCollections.observableArrayList();
                    repositories.add(repositoryMap.getDocbaseName(i));
                    chbRepository.setItems(repositories);
                    chbRepository.setValue(chbRepository.getItems().get(0));
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    private void setFieldsConnect(boolean connected) {
        btnLogout.managedProperty().bindBidirectional(btnLogout.visibleProperty());
        btnLogout.setManaged(connected);

        btnLogin.managedProperty().bindBidirectional(btnLogin.visibleProperty());
        btnLogin.setManaged(!connected);

        chbRepository.setDisable(connected);
        txtUsername.setDisable(connected);
        txtPassword.setDisable(connected);
        txtDomain.setDisable(connected);
        chkSaveLoginData.setDisable(connected);
        chkUseWindowsLogin.setDisable(connected);
    }

    private String getProjectVersion() {
        if (projectVersion == null) {
            readProjectVersionFromFile();
        }

        return projectVersion;
    }

    private void readProjectVersionFromFile() {
        try {
            final Properties properties = new Properties();
            properties.load(this.getClass().getClassLoader().getResourceAsStream("project.properties"));
            projectVersion = properties.getProperty("version");

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @FXML
    private void handleLogin(ActionEvent actionEvent) {
        LOGGER.info(String.valueOf(actionEvent.getSource()));
        String selectedRepository = chbRepository.getValue();
        lblServer.setText(String.format("Connection to '%s'", selectedRepository));
        repositoryCon.setCredentials(selectedRepository, txtUsername.getText(), txtPassword.getText(), txtDomain.getText());
        repositoryCon.createSessionManager();
        if (repositoryCon.isConnectionValid()) {
            Stage loginStage = ConnectionWithStatus.getLoginStage();
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
        LOGGER.info(String.valueOf(actionEvent.getSource()));
        Stage loginStage = ConnectionWithStatus.getLoginStage();
        loginStage.fireEvent(new WindowEvent(loginStage, WindowEvent.WINDOW_CLOSE_REQUEST));
    }

    @FXML
    private void handleServerMap(ActionEvent actionEvent) {
        LOGGER.info(String.valueOf(actionEvent.getSource()));

        String selectedRepository = chbRepository.getValue();
        try {
            IDfTypedObject serverMap = repositoryCon.obtainServerMap(selectedRepository);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Info");
            alert.setHeaderText(String.format("Server Map for Repository >> %s <<", selectedRepository));
            alert.getDialogPane().setContent(formatContent(serverMap));
            alert.showAndWait();
        } catch (DfException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
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
                    LOGGER.finest("Error occurred while displaying the results.");
                    break;
            }
            values.getChildren().add(label);
        }
        hBox.getChildren().addAll(labels, values);
        return hBox;
    }

    @FXML
    private void handleConnectionBrokerMap(ActionEvent actionEvent) {
        LOGGER.info(String.valueOf(actionEvent.getSource()));

        try {
            IDfDocbaseMap repositoryMap = repositoryCon.obtainRepositoryMap();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Info");
            alert.setHeaderText("Connection Broker Map");
            alert.getDialogPane().setContent(formatContent(repositoryMap));
            alert.showAndWait();
        } catch (DfException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @FXML
    private void handleConnectButton(KeyEvent keyEvent) {
        boolean hasRequiredInputs = hasRequiredInputs();
        btnLogin.setDisable(!hasRequiredInputs);

        if (hasRequiredInputs && keyEvent.getCode() == KeyCode.ENTER) {
            btnLogin.fire();
        }
    }

    private boolean hasRequiredInputs() {
        return txtUsername.getText().length() > 0 && txtPassword.getText().length() > 0;
    }

    @FXML
    private void handleLogout(ActionEvent actionEvent) {
        LOGGER.info(String.valueOf(actionEvent.getSource()));
        repositoryCon.disconnect();
        Stage loginStage = ConnectionWithStatus.getLoginStage();
        loginStage.fireEvent(new WindowEvent(loginStage, WindowEvent.WINDOW_CLOSE_REQUEST));
    }
}
