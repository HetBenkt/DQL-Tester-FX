package nl.bos.controllers;

import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.common.DfException;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lombok.Getter;
import lombok.extern.java.Log;
import nl.bos.Main;
import nl.bos.Repository;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.UUID;

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
    @FXML
    private Button btnFlushCache;
    @FXML
    private Button btnReadQuery;

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
        lblUsernameOS.setText("dummyUserNameOS");
        lblUsernameDC.setText(loginPaneController.getTxtUsername().getText());
        lblDomainOS.setText(loginPaneController.getHostName());
        lblPrivileges.setText("dummyPrivileges");
        lblServerVersion.setText("dummyServerVersion");

        btnReadQuery.setDisable(false);
        btnFlushCache.setDisable(false);
    }

    @FXML
    private void handleReadQuery(ActionEvent actionEvent) throws DfException {
        log.info(String.valueOf(actionEvent.getSource()));

        BodyPane bodyPaneController = Main.getBodyPaneLoader().getController();
        String statement = bodyPaneController.getTaStatement().getText();
        Properties history = bodyPaneController.getHistory();

        Repository repository = Repository.getRepositoryCon();
        IDfCollection query = repository.query(statement);
        while (query.next()) {
            log.info(query.getString("r_object_id"));
        }

        ObservableList items = bodyPaneController.getCmbHistory().getItems();
        if (statementNotExists(items, statement)) {
            items.add(0, statement);
            history.put("q." + UUID.randomUUID(), statement);
            try {
                history.store(new FileOutputStream("history.properties"), null);
            } catch (IOException e) {
                log.info(e.getMessage());
            }
            bodyPaneController.getCmbHistory().setItems(items);
        }
    }

    private boolean statementNotExists(ObservableList items, String statement) {
        for (int i = 0; i < items.size(); i++) {
            String historyStatement = (String) items.get(i);
            if (historyStatement.equalsIgnoreCase(statement))
                return false;
        }
        return true;
    }

    @FXML
    private void handleClearQuery(ActionEvent actionEvent) {
        BodyPane bodyPaneController = Main.getBodyPaneLoader().getController();
        bodyPaneController.getTaStatement().clear();
    }
}