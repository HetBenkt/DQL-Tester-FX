package nl.bos.controllers;

import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfAttr;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.extern.java.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log
public class GetAttributesPane {
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

    private List<IDfAttr> userAttributes = new ArrayList<>();
    private List<IDfAttr> systemAttributes = new ArrayList<>();
    private List<IDfAttr> appicationAttributes = new ArrayList<>();
    private List<IDfAttr> internalAttributes = new ArrayList<>();
    private StringBuilder text = new StringBuilder();
    private int lastIndex;
    private List<Integer> previousIndexes = new ArrayList();

    @FXML
    private void handleExit(ActionEvent actionEvent) {
        log.info(String.valueOf(actionEvent.getSource()));
        Stage stage = (Stage) btnExit.getScene().getWindow();
        stage.close();
    }

    public void initTextArea(IDfPersistentObject object) throws DfException {
        for (int i = 0; i <= object.getAttrCount(); i++) {
            IDfAttr attr = object.getAttr(i);
            if (attr.getName().startsWith("r_"))
                systemAttributes.add(attr);
            else if (attr.getName().startsWith("a_"))
                appicationAttributes.add(attr);
            else if (attr.getName().startsWith("i_"))
                internalAttributes.add(attr);
            else
                userAttributes.add(attr);
        }

        appendAttributes("USER ATTRIBUTES\n\n", object, text, userAttributes);
        appendAttributes("\nSYSTEM ATTRIBUTES\n\n", object, text, systemAttributes);
        appendAttributes("\nAPPLICATION ATTRIBUTES\n\n", object, text, appicationAttributes);
        appendAttributes("\nINTERNAL ATTRIBUTES\n\n", object, text, internalAttributes);

        txaAttributes.setText(String.valueOf(text));
        txtObjectId.setText(object.getObjectId().getId());
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
        if (matcher.find(previousIndexes.get(previousIndexes.size() - 1))) {
            if (previousIndexes.size() > 1)
                previousIndexes.remove(previousIndexes.size() - 1);
            lastIndex = matcher.end();
            txaAttributes.selectRange(matcher.start(), lastIndex);
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
        }
    }

    @FXML
    private void handleDump(ActionEvent actionEvent) {

    }

    @FXML
    private void handleCaseSensitive(ActionEvent actionEvent) {
        lastIndex = 0;
        previousIndexes.clear();
        previousIndexes.add(lastIndex);
    }
}
