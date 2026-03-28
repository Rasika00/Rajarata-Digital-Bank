package bank.util;

import java.io.*;
import java.util.HashMap;
import java.util.Map;




public class FileHandler {
    private static final File DATA_DIR = resolveDataDirectory();

    static {
        if (!DATA_DIR.exists() && !DATA_DIR.mkdirs()) {
            System.err.println("Unable to create data directory: " + DATA_DIR.getAbsolutePath());
        }
    }

    private static File resolveDataDirectory() {
        String customDir = System.getProperty("rdb.data.dir");
        if (customDir != null && !customDir.trim().isEmpty()) {
            return new File(customDir.trim());
        }

        String appData = System.getenv("APPDATA");
        if (appData != null && !appData.trim().isEmpty()) {
            return new File(appData, "RajarataDigitalBank\\data");
        }

        return new File(System.getProperty("user.home"), ".RajarataDigitalBank/data");
    }

    public static String dataPath(String filename) {
        return new File(DATA_DIR, filename).getPath();
    }

    public static File dataDirectory() {
        return DATA_DIR;
    }

    public static void saveObject(String filename, Object obj) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(dataPath(filename)))) {
            oos.writeObject(obj);
        } catch (IOException e) {
            System.err.println("Error saving " + filename + ": " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T loadObject(String filename) {
        File f = new File(dataPath(filename));
        if (!f.exists()) return null;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            return (T) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading " + filename + ": " + e.getMessage());
            return null;
        }
    }

    public static void appendLog(String filename, String message) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(dataPath(filename), true))) {
            pw.println(message);
        } catch (IOException e) {
            System.err.println("Error writing log: " + e.getMessage());
        }
    }
}
