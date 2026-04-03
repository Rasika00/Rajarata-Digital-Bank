package bank.ui;

import bank.exception.AuthenticationException;
import bank.model.User;
import bank.service.*;

import javax.swing.*;
import java.awt.*;

public class SettingsPanel extends JPanel {
    private User user;
    private MainFrame mainFrame;
    private AuthService authService = new AuthService();
    private DataStore store = DataStore.getInstance();

    public SettingsPanel(User user, MainFrame mainFrame) {
        this.user = user;
        this.mainFrame = mainFrame;
        setOpaque(false);
        setLayout(new BorderLayout(0, 20));
        buildUI();
    }

    private void buildUI() {
        add(UIComponents.sectionHeader("Settings & Profile"), BorderLayout.NORTH);

        JPanel split = new JPanel(new GridLayout(1, 2, 20, 0));
        split.setOpaque(false);

        split.add(buildProfileCard());
        split.add(buildPasswordCard());

        add(split, BorderLayout.CENTER);
    }

    private JPanel buildProfileCard() {
        UIComponents.RoundedPanel card = new UIComponents.RoundedPanel(UITheme.CARD_RADIUS, UITheme.BG_CARD);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        JLabel title = UIComponents.label("Profile Information", UITheme.FONT_HEADING, UITheme.TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField nameField = UIComponents.createTextField("");
        nameField.setText(user.getFullName());
        nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        JTextField emailField = UIComponents.createTextField("");
        emailField.setText(user.getEmail());
        emailField.setEditable(false);
        emailField.setBackground(new Color(245, 245, 245));
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        JTextField phoneField = UIComponents.createTextField("");
        phoneField.setText(user.getPhone());
        phoneField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        JLabel msgLbl = new JLabel(" ");
        msgLbl.setFont(UITheme.FONT_SMALL);

        JButton saveBtn = UIComponents.createButton("Save Changes", UITheme.PRIMARY, Color.WHITE, -1, 40);
        saveBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        saveBtn.addActionListener(e -> {
            user.setFullName(nameField.getText().trim());
            user.setPhone(phoneField.getText().trim());
            store.putUser(user);
            store.persist();
            msgLbl.setForeground(UITheme.SUCCESS);
            msgLbl.setText(UIComponents.wrapHtml("Profile updated successfully.", 340));
        });

        card.add(title);
        card.add(Box.createVerticalStrut(16));
        card.add(UIComponents.formRow("Full Name", nameField));
        card.add(Box.createVerticalStrut(12));
        card.add(UIComponents.formRow("Email (read-only)", emailField));
        card.add(Box.createVerticalStrut(12));
        card.add(UIComponents.formRow("Phone Number", phoneField));
        card.add(Box.createVerticalStrut(16));
        card.add(UIComponents.label("User ID: " + user.getUserId(), UITheme.FONT_SMALL, UITheme.TEXT_MUTED));
        card.add(UIComponents.label("Role: " + user.getRole(), UITheme.FONT_SMALL, UITheme.TEXT_MUTED));
        card.add(UIComponents.label("Registered: " + user.getCreatedAt().toLocalDate(), UITheme.FONT_SMALL, UITheme.TEXT_MUTED));
        card.add(Box.createVerticalStrut(16));
        card.add(saveBtn);
        card.add(Box.createVerticalStrut(8));
        card.add(msgLbl);

        return card;
    }

    private JPanel buildPasswordCard() {
        UIComponents.RoundedPanel card = new UIComponents.RoundedPanel(UITheme.CARD_RADIUS, UITheme.BG_CARD);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        JLabel title = UIComponents.label("Change Password", UITheme.FONT_HEADING, UITheme.TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPasswordField oldPw = UIComponents.createPasswordField();
        oldPw.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        JPasswordField newPw = UIComponents.createPasswordField();
        newPw.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        JPasswordField confirmPw = UIComponents.createPasswordField();
        confirmPw.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        JLabel msgLbl = new JLabel(" ");
        msgLbl.setFont(UITheme.FONT_SMALL);

        JButton changeBtn = UIComponents.createButton("Change Password", UITheme.DANGER, Color.WHITE, -1, 40);
        changeBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        changeBtn.addActionListener(e -> {
            String np = new String(newPw.getPassword());
            String cp = new String(confirmPw.getPassword());
            if (!np.equals(cp)) {
                msgLbl.setForeground(UITheme.DANGER);
                msgLbl.setText(UIComponents.wrapHtml("Passwords don't match.", 340));
                return;
            }
            try {
                authService.changePassword(user, new String(oldPw.getPassword()), np);
                msgLbl.setForeground(UITheme.SUCCESS);
                msgLbl.setText(UIComponents.wrapHtml("Password changed successfully.", 340));
                oldPw.setText(""); newPw.setText(""); confirmPw.setText("");
            } catch (AuthenticationException ex) {
                msgLbl.setForeground(UITheme.DANGER);
                msgLbl.setText(UIComponents.wrapHtml(ex.getMessage(), 340));
            }
        });

        JLabel hint = UIComponents.wrappedLabel(
                "Password must have: 8+ chars, uppercase, lowercase, digit, and special character.",
                UITheme.FONT_SMALL,
                UITheme.TEXT_MUTED,
                340
        );

        card.add(title);
        card.add(Box.createVerticalStrut(16));
        card.add(UIComponents.formRow("Current Password", oldPw));
        card.add(Box.createVerticalStrut(12));
        card.add(UIComponents.formRow("New Password", newPw));
        card.add(Box.createVerticalStrut(12));
        card.add(UIComponents.formRow("Confirm New Password", confirmPw));
        card.add(Box.createVerticalStrut(8));
        card.add(hint);
        card.add(Box.createVerticalStrut(16));
        card.add(changeBtn);
        card.add(Box.createVerticalStrut(8));
        card.add(msgLbl);

        return card;
    }
}
