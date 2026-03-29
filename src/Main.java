import bank.ui.LoginFrame;
import javax.swing.*;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;

public class Main {
    private static void logStartupError(Throwable throwable) {
        try {
            String base = System.getenv("LOCALAPPDATA");
            if (base == null || base.trim().isEmpty()) {
                base = System.getProperty("user.home");
            }
            File dir = new File(base, "RajarataDigitalBank/logs");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File logFile = new File(dir, "startup-error.log");
            try (PrintWriter pw = new PrintWriter(new FileWriter(logFile, true))) {
                pw.println("==== " + LocalDateTime.now() + " ====");
                throwable.printStackTrace(pw);
                pw.println();
            }
        } catch (Exception ignored) {
        }
    }

    private static void showStartupErrorDialog(Throwable throwable) {
        if (GraphicsEnvironment.isHeadless()) return;
        try {
            JOptionPane.showMessageDialog(
                null,
                "Application failed to start.\n\n" + throwable.getClass().getSimpleName() + ": " + throwable.getMessage(),
                "Rajarata Digital Bank",
                JOptionPane.ERROR_MESSAGE
            );
        } catch (Throwable ignored) {
        }
    }

    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            throwable.printStackTrace();
            logStartupError(throwable);
            showStartupErrorDialog(throwable);
        });

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        UIManager.put("Button.font", new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));
        UIManager.put("Label.font", new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));
        UIManager.put("TextField.font", new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));
        UIManager.put("ComboBox.font", new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));
        UIManager.put("Table.font", new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));
        UIManager.put("OptionPane.messageFont", new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    LoginFrame frame = new LoginFrame();
                    frame.setVisible(true);
                } catch (Throwable t) {
                    t.printStackTrace();
                    logStartupError(t);
                    showStartupErrorDialog(t);
                }
            }
        });
    }
}
