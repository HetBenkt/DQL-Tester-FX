package nl.bos.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import nl.bos.utils.Resources;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class About {
    private static final Logger LOGGER = Logger.getLogger(About.class.getName());

    @FXML
    private TextField txtVersion;
    @FXML
    private Hyperlink hlWhatsNew;
    @FXML
    private Hyperlink hlFeedback;
    @FXML
    private Hyperlink hlContribute;
    @FXML
    private Hyperlink hlLicense;


    @FXML
    private void initialize() {
        Resources resources = new Resources();
        txtVersion.setText(resources.getProjectProperty("version"));
        hlWhatsNew.setAccessibleText(resources.getProjectProperty("url.whats_new"));
        hlFeedback.setAccessibleText(resources.getProjectProperty("url.feeback"));
        hlContribute.setAccessibleText(resources.getProjectProperty("url.contribute"));
        hlLicense.setAccessibleText(resources.getProjectProperty("url.license"));

        List<Hyperlink> links = new ArrayList<>();

        links.add(hlContribute);
        links.add(hlFeedback);
        links.add(hlLicense);
        links.add(hlWhatsNew);

        for (final Hyperlink hyperlink : links) {
            hyperlink.setOnAction(t -> {
                try {
                    Desktop.getDesktop().browse(new URI(hyperlink.getAccessibleText()));
                } catch (IOException | URISyntaxException e) {
                    LOGGER.log(Level.SEVERE, e.getMessage(), e);
                }
            });
        }
    }
}
