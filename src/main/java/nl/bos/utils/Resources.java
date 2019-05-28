package nl.bos.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import org.json.JSONArray;
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
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import static nl.bos.Constants.HISTORY_JSON;
import static nl.bos.Constants.QUERIES;

public class Resources {
    private static final Logger LOGGER = Logger.getLogger(Resources.class.getName());

    private FXMLLoader fxmlLoader;

    public static File createFileFromFileChooser(String title) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        return fileChooser.showOpenDialog(null);
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
                writePreparedJsonDataToFile();
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    private static void writePreparedJsonDataToFile() {
        JSONArray list = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(QUERIES, list);

        writeJsonDataToJsonHistoryFile(jsonObject);
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

        try (FileWriter file = new FileWriter(HISTORY_JSON)) {
            file.write(jsonObject.toString());
            file.flush();
        } catch (IOException ioe) {
            LOGGER.log(Level.SEVERE, ioe.getMessage(), ioe);
        }
    }

    public static File createTempFile(String prefix, String suffic) {
        try {
            return File.createTempFile(prefix, suffic);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return null;
    }

    public FXMLLoader getFxmlLoader() {
        return fxmlLoader;
    }

    public String getProjectVersion() {
        try {
            final Properties properties = new Properties();
            properties.load(getClass().getClassLoader().getResourceAsStream("project.properties"));
            return properties.getProperty("version");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return "";
    }

    public static File exportStringToFile(File tempFile, String tableResult) {
        try (InputStream tableResultContent = new ByteArrayInputStream(tableResult.getBytes(StandardCharsets.UTF_8));
             ReadableByteChannel readableByteChannel = Channels.newChannel(tableResultContent);
             FileOutputStream fileOutputStream = new FileOutputStream(tempFile);
             FileChannel fileChannel = fileOutputStream.getChannel()
        ) {
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

    public static void openCSV(File tempFile) {
        Desktop desktop = Desktop.getDesktop();
        if (tempFile.exists()) {
            try {
                desktop.open(tempFile);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

    public InputStream getResourceStream(String name) {
        return getClass().getClassLoader().getResourceAsStream(name);
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