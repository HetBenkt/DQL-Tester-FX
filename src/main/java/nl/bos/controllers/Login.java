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
import nl.bos.utils.AppAlert;
import nl.bos.utils.Resources;
import nl.bos.utils.UIUtils;

import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import static nl.bos.Constants.ERROR_TITLE;
import static nl.bos.Constants.MSG_TITLE_INFO_DIALOG;

public class Login {
    private static final Logger LOGGER = Logger.getLogger(Login.class.getName());

    private final Repository repository = Repository.getInstance();

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
    private Tooltip ttUsername;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private Tooltip ttPassword;
    @FXML
    private TextField txtDomain;
    @FXML
    private Tooltip ttDomain;
    @FXML
    private ChoiceBox<String> chbSecureMode;
    @FXML
    private Tooltip ttSecureMode;
    @FXML
    private CheckBox chkSaveLoginData;

    @FXML
    void initialize() {
        try {

            ObservableList<String> secureModes = FXCollections.observableArrayList();
            secureModes.addAll("default", "native", "secure", "try_native_first", "try_secure_first");
            chbSecureMode.setItems(secureModes);
            chbSecureMode.setValue(chbSecureMode.getItems().get(0));

            setFieldsConnect(repository.isConnected());
            lblVersion.setText(new Resources().getProjectProperty("version"));

            ttUsername.setText("Enter a user name");
            ttPassword.setText("Enter the user password");
            ttDomain.setText("Enter the user OS domain");
            ttSecureMode.setText("Defines whether to establish a secure or native (insecure) connection.\nDefault uses the settings from dfc.properties or the Server Config Object.");

            if (repository.getClient() != null) {
                IDfDocbaseMap repositoryMap = repository.obtainRepositoryMap();
                if (repositoryMap != null) {
                    //noinspection deprecation
                    String hostName = repositoryMap.getHostName();
                    lblServer.setText(hostName);

                    LOGGER.info(MessageFormat.format("Repositories for Connection Broker: {0}", hostName));
                    LOGGER.info(MessageFormat.format("Total number of Repositories: {0}", repositoryMap.getDocbaseCount()));

                    chbRepository.getItems().clear();
                    for (int i = 0; i < repositoryMap.getDocbaseCount(); i++) {
                        LOGGER.info(MessageFormat.format("Repository {0}: {1}", i + 1, repositoryMap.getDocbaseName(i)));
                        chbRepository.getItems().add(repositoryMap.getDocbaseName(i));
                    }

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
        chbSecureMode.setDisable(connected);
        chkSaveLoginData.setDisable(connected);
    }


    @FXML
    private void handleLogin(ActionEvent actionEvent) {
        LOGGER.info(String.valueOf(actionEvent.getSource()));
        String selectedRepository = chbRepository.getValue();
        String selectedSecureMode = chbSecureMode.getValue();
        lblServer.setText(String.format("Connection to '%s'", selectedRepository));
        repository.setCredentials(selectedRepository, txtUsername.getText(), txtPassword.getText(), txtDomain.getText(), selectedSecureMode);
        repository.createSessionManager();
        repository.createSession();

        if (repository.isConnected()) {
            Stage loginStage = ConnectionWithStatus.getLoginStage();
            loginStage.fireEvent(new WindowEvent(loginStage, WindowEvent.WINDOW_CLOSE_REQUEST));
        } else {
            AppAlert.warning("Information Dialog", repository.getErrorMessage());
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
            IDfTypedObject serverMap = repository.obtainServerMap(selectedRepository);
            if (serverMap == null) {
                AppAlert.information(MSG_TITLE_INFO_DIALOG, "No servermap found!");
            } else {
                AppAlert.informationWithPanel("Info", formatContent(serverMap));
            }
        } catch (DfException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            UIUtils.showExpendableExceptionAlert(ERROR_TITLE,
                    "Something went wrong fetching the server map.", e.getMessage(), e);
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
            IDfDocbaseMap repositoryMap = repository.obtainRepositoryMap();
            if (repositoryMap == null) {
                AppAlert.information(MSG_TITLE_INFO_DIALOG, "No connection broker map found!");
            } else {
                AppAlert.informationWithPanel("Info", formatContent(repositoryMap));
            }
        } catch (DfException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            UIUtils.showExpendableExceptionAlert(ERROR_TITLE,
                    "Something went wrong fetching the connection broker map.", e.getMessage(), e);
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
        repository.disconnect();
        Stage loginStage = ConnectionWithStatus.getLoginStage();
        loginStage.fireEvent(new WindowEvent(loginStage, WindowEvent.WINDOW_CLOSE_REQUEST));
    }
}
