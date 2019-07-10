package nl.bos.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Class that contains UI util methods
 */
public final class UIUtils {
    public static final String INVALID_STYLE_STRING = "-fx-border-color: red;-fx-border-style: solid;";

    /**
     * Private constructor that hides the default public constructor
     */
    private UIUtils() {
        //Do nothing, just hide the default constructor
    }

    /**
     * Shows an alert with an expendable box to show the stacktrace of the exception
     *
     * @param title   Shown in the title bar of the alert
     * @param header  Shown in the header bar of the alert
     * @param content The normal content (summary of what happened)
     * @param ex      The exception to show
     */
    public static void showExpendableExceptionAlert(String title, String header, String content, Exception ex) {
        //Create the alert
        final Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        // Get the stacktrace of the exception
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        final String exceptionText = sw.toString();

        //Create the expendable stacktrace box
        final Label label = new Label("The exception stacktrace was:");

        final TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        final GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        //Add expandable stacktrace box to the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait();
    }

    /**
     * Loads and opens a new window. This method should only be used when you explicitly want to open a new window.
     * When you want to open a new window and close the current one, use the loadAndShowInCurrentWindow method.
     *
     * @param fxmlPath    The path to the fxml file with the resource directory as root
     * @param windowTitle The title to be used for the window
     * @param modality    The modality to initialize the stage with. Owner should be set when the modality is
     *                    WINDOW_MODAL
     * @param owner       The owner window of the window to create. This is only needed when setting the modality to
     *                    WINDOW_MODAL
     * @throws IOException Thrown if something goes wrong while loading the fxml window
     */
    public static void loadAndShowNewWindow(String fxmlPath, String windowTitle, Modality modality,
                                            Window owner) throws IOException {
        final FXMLLoader fxmlLoader = new FXMLLoader(Thread.currentThread().getContextClassLoader().getResource(fxmlPath));
        final Parent root = fxmlLoader.load();
        final Stage stage = new Stage();
        stage.initOwner(owner);
        stage.initModality(modality);
        stage.setTitle(windowTitle);
        stage.setScene(new Scene(root));
        stage.show();
    }

    /**
     * Loads and opens a new window. This method should only be used when you explicitly want to open a new window.
     * When you want to open a new window and close the current one, use the loadAndShowInCurrentWindow method.
     *
     * @param fxmlPath    The path to the fxml file with the resource directory as root
     * @param windowTitle The title to be used for the window
     * @param modality    The modality to initialize the stage with. Owner should be set when the modality is
     *                    WINDOW_MODAL, else it will be treated if set to NONE
     * @throws IOException Thrown if something goes wrong while loading the fxml window
     */
    public static void loadAndShowNewWindow(String fxmlPath, String windowTitle, Modality modality) throws IOException {
        loadAndShowNewWindow(fxmlPath, windowTitle, modality, null);
    }

    /**
     * Loads and opens a new window. This method should only be used when you explicitly want to open a new window.
     * When you want to open a new window and close the current one, use the loadAndShowInCurrentWindow method.
     *
     * @param fxmlPath    The path to the fxml file with the resource directory as root
     * @param windowTitle The title to be used for the window
     * @throws IOException Thrown if something goes wrong while loading the fxml window
     */
    public static void loadAndShowNewWindow(String fxmlPath, String windowTitle) throws IOException {
        loadAndShowNewWindow(fxmlPath, windowTitle, Modality.NONE, null);
    }

    /**
     * Loads the given fxml in the window in which the node resides
     *
     * @param node     The node that is in the window that should be used to load the next window in
     * @param fxmlPath The path to the FXML that should be used
     */
    public static void loadAndShowInCurrentWindow(Node node, String fxmlPath) throws IOException {
        final FXMLLoader fxmlLoader = new FXMLLoader(Thread.currentThread().getContextClassLoader().getResource(fxmlPath));
        final Stage stage = (Stage) node.getScene().getWindow();
        final double width = stage.getWidth();
        final double height = stage.getHeight();
        stage.setScene(new Scene(fxmlLoader.load()));
        stage.setWidth(width);
        stage.setHeight(height);
    }

    /**
     * Checks whether or not a textfield is filled. If it is not, a style element is added to it.
     *
     * @param textField The textfield from which to validate the input.
     * @return Whether or not the input is valid
     */
    public static boolean validateTextInput(TextField textField) {
        if (textField.getText() == null || textField.getText().isEmpty()) {
            addInvalidInputStyle(textField);
            return false;
        } else {
            removeInvalidInputStyle(textField);
            return true;
        }
    }


    /**
     * Checks whether or not a choice is made in the choicebox. If it is not, a style element to show it isn't will
     * be added.
     *
     * @param choiceBox The choicebox to validate the input from
     * @return Whether the input is valid or not
     */
    public static boolean validateChoiceBoxInput(ChoiceBox choiceBox) {
        if (choiceBox.getSelectionModel().isEmpty()) {
            addInvalidInputStyle(choiceBox);
            return false;
        } else {
            removeInvalidInputStyle(choiceBox);
            return true;
        }
    }

    /**
     * Checks whether or not a choice is made in the choicebox. If it is not, a style element to show it isn't will
     * be added.
     *
     * @param comboBox The comboBox to validate the input from
     * @return Whether the input is valid or not
     */
    public static boolean validateComboBoxInput(ComboBox comboBox) {
        if (comboBox.getSelectionModel().isEmpty()) {
            addInvalidInputStyle(comboBox);
            return false;
        } else {
            removeInvalidInputStyle(comboBox);
            return true;
        }
    }

    /**
     * Checks whether or not a textarea is filled. If it is not, a style element is added to it.
     *
     * @param textArea The textarea to validate the input of.
     * @return Wether the input is valid or not
     */
    public static boolean validateTextAreaInput(TextArea textArea) {
        if (textArea.getText().isEmpty()) {
            addInvalidInputStyle(textArea);
            return false;
        } else {
            removeInvalidInputStyle(textArea);
            return true;
        }
    }

    /**
     * Adds a invalid input style element to the control, making it have a red border.
     *
     * @param control The control to add the style elements to
     */
    public static void addInvalidInputStyle(Control control) {
        String style = control.getStyle();
        if (!style.isEmpty() && !style.endsWith(";")) {
            style = style + ';';
        }
        style = style + INVALID_STYLE_STRING;
        control.setStyle(style);
    }

    /**
     * Removes the invalid input style element to the control, making it have a red border.
     *
     * @param control The control to remove the style elements from
     */
    public static void removeInvalidInputStyle(Control control) {
        String style = control.getStyle();
        style = style.replace(INVALID_STYLE_STRING, "");
        control.setStyle(style);
    }
}
