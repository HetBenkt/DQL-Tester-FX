package nl.bos.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.*;
import java.io.*;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import static nl.bos.Constants.*;

public class Resources {
    private static final Logger LOGGER = Logger.getLogger(Resources.class.getName());

    private static Properties settings = null;

    private static File exportPath = null;

    private static Boolean isBrowserAllCabinet = null;
    private static File checkoutFile;
    private FXMLLoader fxmlLoader;

    public static File createFileFromFileChooser(String title) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        return fileChooser.showOpenDialog(null);
    }

    public static File selectFileFromFileChooser(String title, File path, Window owner) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(path);
        fileChooser.setTitle(title);
        return fileChooser.showOpenDialog(owner);
    }

    public static List<String> readLines(File file) {
        try {
            return Files.readAllLines(file.toPath());
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return new ArrayList<>();
    }

    public static void initHistoryFile() {
        File historyFile = new File(HISTORY_JSON);
        try {
            if (historyFile.createNewFile()) {
                JSONArray list = new JSONArray();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(QUERIES, list);

                writeJsonToFile(jsonObject, historyFile);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    private static void initCheckoutFile() {
        checkoutFile = new File(CHECKOUT_JSON);
        try {
            if (checkoutFile.createNewFile()) {
                LOGGER.log(Level.INFO, "New Checkout file");
                JSONObject jsonObject = new JSONObject();
                writeJsonToFile(jsonObject, checkoutFile);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public static byte[] readHistoryJsonBytes() {
        try {
            return Files.readAllBytes(Paths.get(HISTORY_JSON));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return new byte[0];
    }

    public static void writeJsonDataToJsonHistoryFile(JSONObject jsonObject) {
        writeJsonToFile(jsonObject, new File(HISTORY_JSON));
    }

    private static void writeJsonToFile(JSONObject jsonObject, File jsonFile) {
        try (FileWriter file = new FileWriter(jsonFile)) {
            file.write(jsonObject.toString());
            file.flush();
        } catch (IOException ioe) {
            LOGGER.log(Level.SEVERE, ioe.getMessage(), ioe);
        }
    }

    private static JSONObject readCheckoutFile() {
        try {
            if (checkoutFile == null || !checkoutFile.exists()) {
                initCheckoutFile();
            }
            return new JSONObject(new String(Files.readAllBytes(Paths.get(CHECKOUT_JSON)), StandardCharsets.UTF_8));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
    }

    public static void putContentPathToCheckoutFile(String objectId, String path) {
        JSONObject currentJson = readCheckoutFile();
        Objects.requireNonNull(currentJson).put(objectId, path);
        writeJsonToFile(currentJson, checkoutFile);
    }

    public static void removeContentPathFromCheckoutFile(String objectId) {
        JSONObject currentJson = readCheckoutFile();
        Objects.requireNonNull(currentJson).remove(objectId);
        writeJsonToFile(currentJson, checkoutFile);
    }

    public static String getContentPathFromCheckoutFile(String objectId) {
        JSONObject currentJson = readCheckoutFile();
        String contentPath = null;
        try {
            contentPath = Objects.requireNonNull(currentJson).getString(objectId);
        } catch (JSONException je) {
            LOGGER.warning("ID not found");
        }
        return contentPath;
    }

    public static File createTempFile(String prefix, String suffic) {
        try {
            return File.createTempFile(prefix, suffic);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return null;
    }

    public static File exportStringToFile(File tempFile, String tableResult) {
        try (InputStream tableResultContent = new ByteArrayInputStream(tableResult.getBytes(StandardCharsets.UTF_8));
             ReadableByteChannel readableByteChannel = Channels.newChannel(tableResultContent);
             FileOutputStream fileOutputStream = new FileOutputStream(tempFile);
             FileChannel fileChannel = fileOutputStream.getChannel()) {
            fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        return tempFile;
    }

    public static File exportStreamToFile(File tempFile, ByteArrayInputStream jobLogContent) {
        int n = jobLogContent.available();
        byte[] bytes = new byte[n];
        String tableResult = new String(bytes, StandardCharsets.UTF_8);

        return exportStringToFile(tempFile, tableResult);
    }

    public static void openFile(File tempFile) {
        Desktop desktop = Desktop.getDesktop();
        if (tempFile.exists()) {
            try {
                desktop.open(tempFile);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

    private static String getSettingProperty(String property, String defaultValue) {
        return getSettings().getProperty(property, defaultValue);
    }

    private static void setSettingProperty(String property, String value) {
        getSettings().setProperty(property, value);

        File settingsFile = new File("config", "settings.properties");
        try {
            if (!settingsFile.exists() || !settingsFile.isFile()) {
                if (settingsFile.createNewFile())
                    LOGGER.info("New settings.properties file created");
            }
            settings.store(new FileOutputStream(settingsFile), "");
        } catch (FileNotFoundException e) {
            LOGGER.warning("Could not find settings.properties");
        } catch (IOException e) {
            LOGGER.warning("Could not read settings.properties");
        }
    }

    /**
     * Get the application settings properties as a singleton
     */
    private static Properties getSettings() {
        if (settings == null) {
            File settingsFile = new File("config", "settings.properties");

            try {
                if (!settingsFile.exists() || !settingsFile.isFile()) {
                    if (settingsFile.createNewFile()) {
                        LOGGER.info("New settings.properties file created");
                    }
                }
                settings = new Properties();
                settings.load(new FileInputStream(settingsFile));
            } catch (FileNotFoundException e) {
                LOGGER.warning("Could not find settings.properties");
            } catch (IOException e) {
                LOGGER.warning("Could not read settings.properties");
            }
        }
        return settings;
    }

    public static File getExportPath() {
        if (exportPath == null) {
            exportPath = new File(getSettingProperty("export.path", System.getenv("TEMP")));
        }
        return exportPath;
    }

    public static void setExportPath(String path) {
        setSettingProperty("export.path", path);
    }

    public static boolean isBrowserAllCabinet() {
        if (isBrowserAllCabinet == null) {
            isBrowserAllCabinet = Boolean.valueOf(getSettingProperty("browser.allcabinet", "false"));
        }
        return isBrowserAllCabinet;
    }

    public static void setBrowserAllCabinet(boolean allCabinet) {
        isBrowserAllCabinet = Boolean.valueOf(allCabinet);
        setSettingProperty("browser.allcabinet", isBrowserAllCabinet.toString());
    }

    public FXMLLoader getFxmlLoader() {
        return fxmlLoader;
    }

    public String getProjectProperty(String property) {
        try {
            final Properties properties = new Properties();
            properties.load(getClass().getClassLoader().getResourceAsStream("project.properties"));
            return properties.getProperty(property);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return "";
    }

    public InputStream getResourceStream(String name) {
        return getClass().getClassLoader().getResourceAsStream(name);
    }

    public String getResourceExternalForm(String name) {
        return getClass().getResource(name).toExternalForm();
    }

    public Pane loadFXML(String fxml) {
        fxmlLoader = new FXMLLoader(getClass().getResource(fxml));
        Pane pane = null;
        try {
            pane = fxmlLoader.load();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return pane;
    }
}