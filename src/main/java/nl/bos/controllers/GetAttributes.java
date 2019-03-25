package nl.bos.controllers;

import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.IDfAttr;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import nl.bos.Repository;
import nl.bos.utils.AppAlert;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetAttributes {
    private static final Logger LOGGER = Logger.getLogger(GetAttributes.class.getName());

    private final Repository repositoryCon = Repository.getInstance();

    private final List<IDfAttr> userAttributes = new ArrayList<>();
    private final List<IDfAttr> systemAttributes = new ArrayList<>();
    private final List<IDfAttr> applicationAttributes = new ArrayList<>();
    private final List<IDfAttr> internalAttributes = new ArrayList<>();
    private final StringBuilder text = new StringBuilder();
    private final List<Integer> previousIndexes = new ArrayList<>();

    private int lastIndex;

    @FXML
    private TextField txtObjectId;
    @FXML
    private Button btnExit;
    @FXML
    private TextArea txaAttributes;
    @FXML
    private TextField txtSearch;
    @FXML
    private CheckBox chkCaseSensitive;

    /**
     * @noinspection EmptyMethod
     */
    @FXML
    private void initialize() {
        //No implementation needed
    }

    public void initTextArea(IDfPersistentObject object) throws DfException {
        appendTextToStringBuilder(object);

        txaAttributes.setText(String.valueOf(text));
        txtObjectId.setText(object.getObjectId().getId());
    }

    private void appendTextToStringBuilder(IDfPersistentObject object) throws DfException {
        for (int i = 0; i <= object.getAttrCount(); i++) {
            IDfAttr attr = object.getAttr(i);
            if (attr.getName().startsWith("r_"))
                systemAttributes.add(attr);
            else if (attr.getName().startsWith("a_"))
                applicationAttributes.add(attr);
            else if (attr.getName().startsWith("i_"))
                internalAttributes.add(attr);
            else
                userAttributes.add(attr);
        }

        appendAttributes("USER ATTRIBUTES\n\n", object, text, userAttributes);
        appendAttributes("\nSYSTEM ATTRIBUTES\n\n", object, text, systemAttributes);
        appendAttributes("\nAPPLICATION ATTRIBUTES\n\n", object, text, applicationAttributes);
        appendAttributes("\nINTERNAL ATTRIBUTES\n\n", object, text, internalAttributes);
    }

    private void appendAttributes(String title, IDfPersistentObject object, StringBuilder text, List<IDfAttr> attributes) throws DfException {
        text.append(title);
        for (IDfAttr attr : attributes) {
            if (attr.isRepeating()) {
                if (object.getValueCount(attr.getName()) == 0) {
                    text.append(String.format("\t%s []: <none>", attr.getName()));
                    text.append("\n");
                } else {
                    appendRepeatedAttribute(object, text, attr);
                }
            } else {
                text.append(String.format("\t%s: %s", attr.getName(), object.getValue(attr.getName()).asString()));
                text.append("\n");
            }
        }
    }

    private void appendRepeatedAttribute(IDfPersistentObject object, StringBuilder text, IDfAttr attr) throws DfException {
        for (int i = 0; i < object.getValueCount(attr.getName()); i++) {
            if (i == 0)
                text.append(String.format("\t%s [%s]: %s", attr.getName(), i, object.getRepeatingValue(attr.getName(), i).asString()));
            else
                text.append(String.format("\t\t [%s]: %s", i, object.getRepeatingValue(attr.getName(), i).asString()));
            text.append("\n");
        }
    }

    @FXML
    private void handleExit(ActionEvent actionEvent) {
        LOGGER.info(String.valueOf(actionEvent.getSource()));
        Stage stage = (Stage) btnExit.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleFind(ActionEvent actionEvent) {
        previousIndexes.clear();
        if (!chkCaseSensitive.isSelected()) {
            searchFind(txtSearch.getText().toUpperCase(), text.toString().replace("\n", " ").toUpperCase());
        } else {
            searchFind(txtSearch.getText(), text.toString().replace("\n", " "));
        }
    }

    private void searchFind(String patternText, String matchText) {
        Pattern pattern = Pattern.compile(patternText);
        Matcher matcher = pattern.matcher(matchText);
        if (matcher.find(0)) {
            previousIndexes.add(0);
            lastIndex = matcher.end();
            txaAttributes.selectRange(matcher.start(), lastIndex);
        } else {
            AppAlert.warn("Information Dialog", String.format("String not found: %s", patternText));
        }
    }

    @FXML
    private void handleFindPrevious(ActionEvent actionEvent) {
        if (!chkCaseSensitive.isSelected()) {
            searchFindPrevious(txtSearch.getText().toUpperCase(), text.toString().replace("\n", " ").toUpperCase());
        } else {
            searchFindPrevious(txtSearch.getText(), text.toString().replace("\n", " "));
        }
    }

    private void searchFindPrevious(String patternText, String matchText) {
        Pattern pattern = Pattern.compile(patternText);
        Matcher matcher = pattern.matcher(matchText);
        if (!previousIndexes.isEmpty()) {
            if (matcher.find(previousIndexes.get(previousIndexes.size() - 1))) {
                if (previousIndexes.size() > 1)
                    previousIndexes.remove(previousIndexes.size() - 1);
                lastIndex = matcher.end();
                txaAttributes.selectRange(matcher.start(), lastIndex);
            } else {
                AppAlert.warn("Information Dialog", String.format("String not found: %s", patternText));
            }
        }
    }

    @FXML
    private void handleFindNext(ActionEvent actionEvent) {
        if (!chkCaseSensitive.isSelected()) {
            searchFindNext(txtSearch.getText().toUpperCase(), text.toString().replace("\n", " ").toUpperCase());
        } else {
            searchFindNext(txtSearch.getText(), text.toString().replace("\n", " "));
        }
    }

    private void searchFindNext(String patternText, String matchText) {
        Pattern pattern = Pattern.compile(patternText);
        Matcher matcher = pattern.matcher(matchText);
        if (matcher.find(lastIndex)) {
            int index = lastIndex - txtSearch.getText().length();
            if (index < 0)
                index = 0;
            previousIndexes.add(index);
            lastIndex = matcher.end();
            txaAttributes.selectRange(matcher.start(), lastIndex);
        } else {
            AppAlert.warn("Information Dialog", "EOF reached!");
        }
    }

    @FXML
    private void handleDump(ActionEvent actionEvent) {
        try {
            IDfPersistentObject object = repositoryCon.getSession().getObject(new DfId(txtObjectId.getText()));
            text.setLength(0);
            userAttributes.clear();
            applicationAttributes.clear();
            internalAttributes.clear();
            systemAttributes.clear();
            appendTextToStringBuilder(object);
            txaAttributes.setText(String.valueOf(text));
        } catch (DfException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            AppAlert.error("Information Dialog", e.getMessage());
        }
    }

    @FXML
    private void handleCaseSensitive(ActionEvent actionEvent) {
        lastIndex = 0;
        previousIndexes.clear();
        previousIndexes.add(lastIndex);
    }
}
