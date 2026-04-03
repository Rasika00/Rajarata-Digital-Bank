package bank.ui;

import bank.model.*;
import bank.service.*;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.List;

public class AuditLogPanel extends JPanel {
    private DataStore store = DataStore.getInstance();

    public AuditLogPanel(User user, MainFrame mainFrame) {
        setOpaque(false);
        setLayout(new BorderLayout(0, 16));
        buildUI();
    }

    private void buildUI() {
        add(UIComponents.sectionHeader("System Audit Log"), BorderLayout.NORTH);

        List<String> log = store.getAuditLog();
        Collections.reverse(log); 

        JTextArea area = new JTextArea();
        area.setFont(UITheme.FONT_MONO);
        area.setEditable(false);
        area.setBackground(new Color(20, 25, 35));
        area.setForeground(new Color(0, 220, 130));
        area.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));

        StringBuilder sb = new StringBuilder();
        for (String entry : log) sb.append(entry).append("\n");
        if (log.isEmpty()) sb.append("No audit entries yet.");
        area.setText(sb.toString());
        area.setCaretPosition(0);

        JScrollPane sp = new JScrollPane(area);
        sp.setBorder(new UIComponents.RoundBorder(UITheme.PRIMARY_DARK, 8));
        sp.getVerticalScrollBar().setUnitIncrement(16);
        add(sp, BorderLayout.CENTER);
        add(UIComponents.label("Total entries: " + log.size(), UITheme.FONT_SMALL, UITheme.TEXT_MUTED), BorderLayout.SOUTH);
    }
}
