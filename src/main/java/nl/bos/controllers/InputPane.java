package nl.bos.controllers;

import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfUser;
import com.documentum.fc.common.DfException;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import nl.bos.Main;
import nl.bos.Repository;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class InputPane implements Initializable, EventHandler<WindowEvent> {
    private static final Logger log = Logger.getLogger(InputPane.class.getName());

    private final static Stage loginStage = new Stage();

    public Button getBtnFlushCache() {
        return btnFlushCache;
    }

    public Button getBtnReadQuery() {
        return btnReadQuery;
    }

    public Button getBtnConnect() {
        return btnConnect;
    }

    public Button getBtnDisconnect() {
        return btnDisconnect;
    }

    private final FXMLLoader fxmlLoader;
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
    @FXML
    private Button btnFlushCache;
    @FXML
    private Button btnReadQuery;
    @FXML
    private Button btnConnect;
    @FXML
    private Button btnDisconnect;
    private final Repository repositoryCon = Repository.getInstance();
    private final Main main = Main.getInstance();

    static Stage getLoginStage() {
        return loginStage;
    }

    public InputPane() throws IOException {
        loginStage.initModality(Modality.APPLICATION_MODAL);
        loginStage.setTitle("Documentum Login");
        fxmlLoader = new FXMLLoader(getClass().getResource("/nl/bos/views/LoginPane.fxml"));
        VBox loginPane = fxmlLoader.load();
        loginStage.setScene(new Scene(loginPane));
        loginStage.setOnCloseRequest(this);
    }

    @FXML
    private void handleConnect(ActionEvent actionEvent) throws DfException {
        log.info(String.valueOf(actionEvent.getSource()));
        repositoryCon.setClient();
        LoginPane loginPaneController = fxmlLoader.getController();
        loginPaneController.initialize(null, null);
        loginStage.showAndWait();
    }

    @FXML
    private void handleExit(ActionEvent actionEvent) {
        log.info(String.valueOf(actionEvent.getSource()));

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Quit the application...");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            repositoryCon.disconnect();
            System.exit(0);
        } else {
            alert.close();
        }
    }

    public void handle(WindowEvent event) {
        IDfSession session = repositoryCon.getSession();
        if (session != null && session.isConnected()) {
            try {
                updateNodes(session);
            } catch (DfException e) {
                log.finest(e.getMessage());
            }
        } else {
            updateNodes("Offline", "OS Username", "DC Username", "OS Domain", "Privileges", "Server Version", false);
        }
    }

    private void updateNodes(IDfSession session) throws DfException {
        IDfUser user = session.getUser(session.getLoginUserName());
        lblStatus.setText(session.getDocbaseName());
        lblUsernameOS.setText(user.getUserOSName());
        lblUsernameDC.setText(session.getLoginUserName());
        lblDomainOS.setText(user.getUserOSDomain());
        lblPrivileges.setText(String.format("%s (%d)", getUserPrivilegesLabel(user.getUserPrivileges()), user.getUserPrivileges()));
        lblServerVersion.setText(session.getServerVersion());

        btnReadQuery.setDisable(!session.isConnected());
        btnFlushCache.setDisable(!session.isConnected());

        btnDisconnect.managedProperty().bindBidirectional(btnDisconnect.visibleProperty());
        btnDisconnect.setManaged(session.isConnected());

        btnConnect.managedProperty().bindBidirectional(btnConnect.visibleProperty());
        btnConnect.setManaged(!session.isConnected());

        RootPane rootPaneLoaderController = main.getRootPaneLoader().getController();
        rootPaneLoaderController.getMenubar().setDisable(!session.isConnected());
    }

    private String getUserPrivilegesLabel(int userPrivileges) {
        String userPrivilegesLabel = "";

        switch (userPrivileges) {
            case 0:
                userPrivilegesLabel = "None";
                break;
            case 1:
                userPrivilegesLabel = "Create Type";
                break;
            case 2:
                userPrivilegesLabel = "Create Cabinet";
                break;
            case 4:
                userPrivilegesLabel = "Create Group";
                break;
            case 8:
                userPrivilegesLabel = "Sysadmin";
                break;
            case 16:
                userPrivilegesLabel = "Superuser";
                break;
        }

        return userPrivilegesLabel;
    }

    private void updateNodes(String status, String usernameOS, String usernameDC, String domainOS, String privileges, String serverVersion, boolean isConnected) {
        lblStatus.setText(status);
        lblUsernameOS.setText(usernameOS);
        lblUsernameDC.setText(usernameDC);
        lblDomainOS.setText(domainOS);
        lblPrivileges.setText(privileges);
        lblServerVersion.setText(serverVersion);

        btnReadQuery.setDisable(!isConnected);
        btnFlushCache.setDisable(!isConnected);

        btnDisconnect.managedProperty().bindBidirectional(btnDisconnect.visibleProperty());
        btnDisconnect.setManaged(isConnected);

        btnConnect.managedProperty().bindBidirectional(btnConnect.visibleProperty());
        btnConnect.setManaged(!isConnected);

        RootPane rootPaneLoaderController = main.getRootPaneLoader().getController();
        rootPaneLoaderController.getMenubar().setDisable(!isConnected);
    }

    @FXML
    private void handleReadQuery(ActionEvent actionEvent) throws DfException {
        log.info(String.valueOf(actionEvent.getSource()));

        BodyPane bodyPaneController = main.getBodyPaneLoader().getController();
        String statement = bodyPaneController.getTaStatement().getText();
        JSONObject jsonObject = bodyPaneController.getJsonObject();

        IDfCollection result = null;
        try {
            result = repositoryCon.query(statement);
        } catch (DfException dfe) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Information Dialog");
            alert.setHeaderText(null);
            alert.setContentText(dfe.getMessage());
            alert.showAndWait();
        } finally {
            if (result != null) {
                bodyPaneController.updateResultTable(result);
                result.close();
            }
        }

        if (result != null) {
            ChoiceBox<Object> cmbHistory = bodyPaneController.getCmbHistory();
            ObservableList<Object> items = cmbHistory.getItems();
            if (statementNotExists(items, statement)) {
                items.add(0, statement);
                cmbHistory.setValue(statement);
                JSONArray queries = (JSONArray) jsonObject.get("queries");
                queries.add(0, statement);
                try (FileWriter file = new FileWriter("history.json")) {
                    file.write(jsonObject.toJSONString());
                    file.flush();
                } catch (IOException e) {
                    log.finest(e.getMessage());
                }
                cmbHistory.setItems(items);
            }
        }
    }

    private boolean statementNotExists(ObservableList items, String statement) {
        for (Object item : items) {
            String historyStatement = (String) item;
            if (historyStatement.equalsIgnoreCase(statement))
                return false;
        }
        return true;
    }

    @FXML
    private void handleClearQuery(ActionEvent actionEvent) {
        BodyPane bodyPaneController = main.getBodyPaneLoader().getController();
        bodyPaneController.getTaStatement().clear();
    }

    @FXML
    private void handleDisconnect(ActionEvent actionEvent) {
        LoginPane loginPaneController = fxmlLoader.getController();
        loginPaneController.initialize(null, null);
        loginStage.showAndWait();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnDisconnect.managedProperty().bindBidirectional(btnDisconnect.visibleProperty());
        btnDisconnect.setManaged(false);
    }
}