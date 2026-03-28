package bank.ui;

import bank.exception.AuthenticationException;
import bank.model.Customer;
import bank.service.AuthService;

import javax.swing.*;
import java.awt.*;

public class RegisterDialog extends JDialog {
    private JTextField nameField, emailField, phoneField, nationalIdField, addressField;
    private JPasswordField pwField, confirmPwField;
    private JLabel msgLabel;
    private AuthService authService = new AuthService();

    public RegisterDialog(JFrame parent) {
        super(parent, "Create New Account", true);
        setSize(480, 580);
        setLocationRelativeTo(parent);
        setResizable(false);
        buildUI();
    }

    private void buildUI() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(UITheme.BG_MAIN);

        
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.PRIMARY);
        header.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        JLabel title = new JLabel("Register New Customer");
        title.setFont(UITheme.FONT_HEADING);
        title.setForeground(Color.WHITE);
        header.add(title);
        main.add(header, BorderLayout.NORTH);

        
        JPanel form = new JPanel(new GridLayout(0, 1, 0, 10));
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        nameField = UIComponents.createTextField("e.g. Nimal Silva");
        emailField = UIComponents.createTextField("e.g. nimal@email.com");
        phoneField = UIComponents.createTextField("e.g. 0712345678");
        nationalIdField = UIComponents.createTextField("NIC number");
        addressField = UIComponents.createTextField("Home address");
        pwField = UIComponents.createPasswordField();
        confirmPwField = UIComponents.createPasswordField();

        form.add(UIComponents.formRow("Full Name *", nameField));
        form.add(UIComponents.formRow("Email Address *", emailField));
        form.add(UIComponents.formRow("Phone Number *", phoneField));
        form.add(UIComponents.formRow("National ID *", nationalIdField));
        form.add(UIComponents.formRow("Address", addressField));
        form.add(UIComponents.formRow("Password *", pwField));
        form.add(UIComponents.formRow("Confirm Password *", confirmPwField));

        msgLabel = new JLabel(" ");
        msgLabel.setFont(UITheme.FONT_SMALL);
        msgLabel.setForeground(UITheme.DANGER);
        form.add(msgLabel);

        JScrollPane sp = new JScrollPane(form);
        sp.setBorder(null);
        main.add(sp, BorderLayout.CENTER);

        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        btnPanel.setBackground(UITheme.BG_MAIN);
        btnPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.BORDER));

        JButton cancelBtn = UIComponents.createButton("Cancel", UITheme.BG_MAIN, UITheme.TEXT_MUTED, 100, 36);
        cancelBtn.addActionListener(e -> dispose());

        JButton regBtn = UIComponents.createButton("Register", UITheme.PRIMARY, Color.WHITE, 120, 36);
        regBtn.addActionListener(e -> doRegister());

        btnPanel.add(cancelBtn);
        btnPanel.add(regBtn);
        main.add(btnPanel, BorderLayout.SOUTH);

        add(main);
    }

    private void doRegister() {
        String pw = new String(pwField.getPassword());
        String cpw = new String(confirmPwField.getPassword());
        if (!pw.equals(cpw)) { msgLabel.setText("Passwords do not match."); return; }

        try {
            Customer c = authService.registerCustomer(
                nameField.getText().trim(), emailField.getText().trim(),
                phoneField.getText().trim(), pw,
                nationalIdField.getText().trim(), addressField.getText().trim()
            );
            JOptionPane.showMessageDialog(this, "Account created successfully!\nYour ID: " + c.getUserId() +
                "\n\nYou can now log in.", "Registration Successful", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (AuthenticationException ex) {
            msgLabel.setText("<html>" + ex.getMessage() + "</html>");
        }
    }
}
