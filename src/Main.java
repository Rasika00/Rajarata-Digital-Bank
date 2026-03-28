import bank.ui.LoginFrame;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            throwable.printStackTrace();
            JOptionPane.showMessageDialog(
                null,
                "Application failed to start.\n\n" + throwable.getClass().getSimpleName() + ": " + throwable.getMessage(),
                "Rajarata Digital Bank",
                JOptionPane.ERROR_MESSAGE
            );
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
                    JOptionPane.showMessageDialog(
                        null,
                        "Application failed to start.\n\n" + t.getClass().getSimpleName() + ": " + t.getMessage(),
                        "Rajarata Digital Bank",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });
    }
}
