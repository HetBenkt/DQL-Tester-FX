package nl.bos.utils;

import org.json.JSONObject;

import java.awt.*;
import java.io.*;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

import static nl.bos.Constants.HISTORY_JSON;

public class Resources {
    private static final Logger LOGGER = Logger.getLogger(Resources.class.getName());

    public static boolean writeJsonDataToJsonHistoryFile(JSONObject jsonObject) {
        boolean result = false;

        try (FileWriter file = new FileWriter(HISTORY_JSON)) {
            file.write(jsonObject.toString());
            file.flush();
            result = true;
        } catch (IOException ioe) {
            LOGGER.log(Level.SEVERE, ioe.getMessage(), ioe);
        }

        return result;
    }


    public static File createTempCSV() {
        File result = null;

        try {
            result = File.createTempFile("tmp_", ".csv");
            LOGGER.info(result.getPath());
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        return result;
    }

    public static File exportToCSV(File tempFile, String tableResult) {
        try (InputStream tableResultContent = new ByteArrayInputStream(tableResult.getBytes(Charset.forName("UTF-8")));
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
}