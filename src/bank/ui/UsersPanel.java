package bank.ui;

import bank.model.*;
import bank.service.DataStore;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class UsersPanel extends JPanel {
    private User user;
    private DataStore store = DataStore.getInstance();

    public UsersPanel(User user, MainFrame mainFrame) {
        this.user = user;
        setOpaque(false);
        setLayout(new BorderLayout(0, 16));
        buildUI();
    }

    private void buildUI() {
        add(UIComponents.sectionHeader("User Management"), BorderLayout.NORTH);

        Collection<User> allUsers = store.getAllUsers();
        List<User> users = allUsers.stream()
            .filter(u -> user.getRole() == UserRole.ADMIN || u.getRole() == UserRole.CUSTOMER)
            .sorted(Comparator.comparing(User::getRole).thenComparing(User::getFullName))
            .collect(Collectors.toList());

        String[] cols = {"User ID", "Full Name", "Email", "Phone", "Role", "Status", "Registered"};
        Object[][] data = users.stream().map(u -> new Object[]{
            u.getUserId(), u.getFullName(), u.getEmail(), u.getPhone(),
            u.getRole(), u.isActive() ? "Active" : "Inactive", u.getCreatedAt().toLocalDate()
        }).toArray(Object[][]::new);

        JTable table = UIComponents.createStyledTable(data, cols);

        
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actions.setOpaque(false);

        if (user.getRole() == UserRole.ADMIN) {
            JButton toggleBtn = UIComponents.createButton("Toggle Active/Inactive", UITheme.WARNING, Color.WHITE, 190, 36);
            toggleBtn.addActionListener(e -> {
                int row = table.getSelectedRow();
                if (row < 0) { JOptionPane.showMessageDialog(this, "Select a user."); return; }
                User selected = users.get(row);
                if (selected.getUserId().equals(user.getUserId())) {
                    JOptionPane.showMessageDialog(this, "Cannot deactivate your own account.");
                    return;
                }
                selected.setActive(!selected.isActive());
                store.persist();
                table.setValueAt(selected.isActive() ? "Active" : "Inactive", row, 5);
                table.repaint();
                JOptionPane.showMessageDialog(this, selected.getFullName() + " is now " + (selected.isActive() ? "Active" : "Inactive"));
            });
            actions.add(toggleBtn);
        }

        JPanel center = new JPanel(new BorderLayout(0, 10));
        center.setOpaque(false);
        center.add(UIComponents.scrollPane(table), BorderLayout.CENTER);
        center.add(actions, BorderLayout.SOUTH);
        add(center, BorderLayout.CENTER);
        add(UIComponents.label("Total: " + users.size() + " users", UITheme.FONT_SMALL, UITheme.TEXT_MUTED), BorderLayout.SOUTH);
    }
}
