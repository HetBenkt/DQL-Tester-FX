package nl.bos.controllers;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import nl.bos.Repository;
import nl.bos.utils.Resources;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExecuteAPIScript {
    private static final Logger LOGGER = Logger.getLogger(ExecuteAPIScript.class.getName());
    private IDfSession session = Repository.getInstance().getSession();

    @FXML
    private Button btnExecute;
    @FXML
    private TextArea apiScriptView;
    @FXML
    private CheckBox ignoreErrors;
    @FXML
    private TextArea apiResultView;
    @FXML
    private TextField filepath;

    @FXML
    public void initialize() {
        apiScriptView.textProperty().addListener((observableValue, oldValue, newValue) -> btnExecute.setDisable(newValue.isEmpty()));
    }

    @FXML
    private void loadAPIScript() {
        File apiFileToLoad = Resources.createFileFromFileChooser("Select API Script to load");

        if (apiFileToLoad == null || !apiFileToLoad.exists() || !apiFileToLoad.canRead()) {
            return;
        }

        filepath.setText(apiFileToLoad.getPath());
        List<String> apiScriptToLoad = Resources.readLines(apiFileToLoad);

        if (!apiScriptToLoad.isEmpty()) {
            StringBuilder stringBuilder = new StringBuilder();
            apiScriptToLoad.forEach(x -> stringBuilder.append(x.trim()).append("\r\n"));

            apiScriptView.clear();
            apiScriptView.setText(stringBuilder.toString());
        }
    }

    @FXML
    private void executeScript() {
        if (apiScriptView.getText().trim().isEmpty()) {
            return;
        }

        Repository repository = Repository.getInstance();
        if (!repository.isConnected()) {
            LOGGER.log(Level.WARNING, "No repository connection to run API script!");
            return;
        }

        String[] commands = apiScriptView.getText().split("\\r?\\n");
        int caret = 0;

        for (int i = 0; i < commands.length; i++) {
            String command = commands[i].trim();

            if (command.isEmpty()) {
                continue;
            }

            apiScriptView.selectRange(caret, caret + command.length());
            caret += command.length() + 1;

            try {
                int commandType = getCommandType(command, session);

                String commandMethod = getCommandMethod(command);
                String commandArgs = getCommandArgs(command);
                String commandData = null;

                if (commandType == IDfSession.DM_SET) {
                    commandData = commands[++i].trim();
                    caret += commandData.length();
                }

                apiResultView.appendText("> " + command + "\r\n");

                String result = null;

                switch (commandType) {
                    case IDfSession.DM_GET:
                        result = session.apiGet(commandMethod, commandArgs);
                        break;

                    case IDfSession.DM_SET:
                        result = "" + session.apiSet(commandMethod, commandArgs, commandData);
                        break;

                    case IDfSession.DM_EXEC:
                        result = "" + session.apiExec(commandMethod, commandArgs);
                        break;
                }

                apiResultView.appendText(result + "\r\n");

            } catch (DfException e) {
                LOGGER.log(Level.WARNING, e.getMessage(), e);
                apiResultView.appendText(e.getStackTraceAsString());

                if (!ignoreErrors.isSelected()) {
                    return;
                }
            }
        }
    }

    private String getCommandMethod(String command) {
        return command.substring(0, command.indexOf(","));
    }

    private String getCommandArgs(String command) {
        String[] commandBits = command.split(",");
        String[] arguments = Arrays.copyOfRange(commandBits, 2, commandBits.length);

        return String.join(",", arguments);
    }

    private int getCommandType(String command, IDfSession session) throws DfException {
        IDfList apiDescription = session.apiDesc(command);
        return apiDescription.getInt(2);
    }

    @FXML
    private void saveAPIScript() {

    }

    @FXML
    private void clearScript() {
        apiScriptView.setText("");
    }

    @FXML
    private void exit() {
        Stage stage = (Stage) btnExecute.getScene().getWindow();
        stage.close();
    }

    public void injectData(String script) {
        apiScriptView.setText(script);
    }
}
