package nl.bos.utils;

import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;

import java.util.Optional;

public class AppAlert {
    public static void information(String title, String message) {
        Alert alert = getAlert(title, message, Alert.AlertType.INFORMATION);
        alert.showAndWait();
    }

    public static void error(String title, String message) {
        Alert alert = getAlert(title, message, Alert.AlertType.ERROR);
        alert.showAndWait();
    }

    public static void confirmation(String title, String message) {
        Alert alert = getAlert(title, message, Alert.AlertType.CONFIRMATION);
        alert.showAndWait();
    }

    public static void warning(String title, String message) {
        Alert alert = getAlert(title, message, Alert.AlertType.WARNING);
        alert.showAndWait();
    }

    public static Optional<ButtonType> confirmationWithResponse(String title, String message) {
        Alert alert = getAlert(title, message, Alert.AlertType.CONFIRMATION);
        return alert.showAndWait();
    }

    public static Optional<String> confirmationWithPanelAndResponse(String title, String text) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(title);
        dialog.setHeaderText(null);
        dialog.setContentText(text);
        return dialog.showAndWait();
    }

    private static Alert getAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert;
    }

    public static void informationWithPanel(String title, Node node) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.getDialogPane().setContent(node);
        alert.showAndWait();
    }

    public static Optional<ButtonType> warningWithResponse(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        ButtonType btnYes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType btnNo = new ButtonType("No", ButtonBar.ButtonData.NO);
        alert.getButtonTypes().setAll(btnYes, btnNo);
        return alert.showAndWait();
    }
}
