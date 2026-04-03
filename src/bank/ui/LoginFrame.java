package bank.ui;

import bank.exception.AuthenticationException;
import bank.model.*;
import bank.service.AuthService;
import bank.service.DataInitializer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;




public class LoginFrame extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JLabel messageLabel;
    private AuthService authService = new AuthService();

    public LoginFrame() {
        DataInitializer.initialize();
        setTitle("Rajarata Digital Bank");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1120, 700);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(980, 640));
        setLayout(new BorderLayout());
        buildUI();
    }

    private void buildUI() {
        JPanel left = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint gp = new GradientPaint(0, 0, UITheme.BG_DARK, getWidth(), getHeight(), UITheme.PRIMARY_DARK);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(255, 255, 255, 22));
                g2.fillOval(-80, -40, 260, 260);
                g2.setColor(new Color(86, 170, 216, 45));
                g2.fillOval(getWidth() - 260, 80, 300, 300);
                g2.setColor(new Color(255, 255, 255, 10));
                g2.fillRoundRect(56, getHeight() - 220, getWidth() - 112, 120, 28, 28);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        left.setOpaque(false);
        left.setPreferredSize(new Dimension(470, 700));
        left.setLayout(new GridBagLayout());

        JPanel brandPanel = new JPanel();
        brandPanel.setOpaque(false);
        brandPanel.setLayout(new BoxLayout(brandPanel, BoxLayout.Y_AXIS));
        brandPanel.setBorder(BorderFactory.createEmptyBorder(32, 44, 32, 44));

        JComponent bankIcon = UIComponents.createBrandMark("RB", 148);
        bankIcon.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel eyebrow = new JLabel("DIGITAL BANKING PLATFORM");
        eyebrow.setFont(UITheme.FONT_SMALL);
        eyebrow.setForeground(new Color(150, 193, 232));
        eyebrow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel bankName = new JLabel("Rajarata Digital Bank");
        bankName.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 32));
        bankName.setForeground(Color.WHITE);
        bankName.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel tagline = UIComponents.wrappedLabel(
            "A cleaner, faster banking workspace for customers, staff, and administrators.",
            new Font("Segoe UI", Font.PLAIN, 15),
            new Color(192, 214, 236),
            332
        );
        tagline.setAlignmentX(Component.LEFT_ALIGNMENT);

        brandPanel.add(bankIcon);
        brandPanel.add(Box.createVerticalStrut(28));
        brandPanel.add(eyebrow);
        brandPanel.add(Box.createVerticalStrut(14));
        brandPanel.add(bankName);
        brandPanel.add(Box.createVerticalStrut(10));
        brandPanel.add(tagline);
        brandPanel.add(Box.createVerticalStrut(38));

        String[] features = {"Protected access and password validation", "Multi-account management from one profile", "Transfers, statements, and bill payments", "Loan processing with role-based control"};
        for (String f : features) {
            JPanel row = new JPanel(new BorderLayout(8, 0));
            row.setOpaque(false);
            JLabel badge = UIComponents.createPillLabel("OK", new Color(255, 255, 255, 28), Color.WHITE);
            JLabel fl = UIComponents.wrappedLabel(f, UITheme.FONT_BODY, new Color(212, 227, 242), 300);
            row.add(badge, BorderLayout.WEST);
            row.add(fl, BorderLayout.CENTER);
            row.setAlignmentX(Component.LEFT_ALIGNMENT);
            brandPanel.add(row);
            brandPanel.add(Box.createVerticalStrut(12));
        }

        UIComponents.RoundedPanel credentials = new UIComponents.RoundedPanel(22, new Color(255, 255, 255, 18), new Color(255, 255, 255, 30));
        credentials.setLayout(new BoxLayout(credentials, BoxLayout.Y_AXIS));
        credentials.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        credentials.setAlignmentX(Component.LEFT_ALIGNMENT);
        credentials.add(UIComponents.label("Demo Access", UITheme.FONT_SUBHEAD, Color.WHITE));
        credentials.add(Box.createVerticalStrut(8));
        credentials.add(UIComponents.label("Admin    admin@rajaratabank.lk", UITheme.FONT_BODY, new Color(214, 226, 239)));
        credentials.add(UIComponents.label("Staff    kamal@rajaratabank.lk", UITheme.FONT_BODY, new Color(214, 226, 239)));
        credentials.add(Box.createVerticalStrut(4));
        credentials.add(UIComponents.wrappedLabel(
            "Use seeded admin/staff passwords from the README or startup console output.",
            UITheme.FONT_SMALL,
            new Color(175, 194, 214),
            300
        ));
        brandPanel.add(Box.createVerticalStrut(18));
        brandPanel.add(credentials);

        left.add(brandPanel);

        JPanel right = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint gp = new GradientPaint(0, 0, UITheme.BG_MAIN, getWidth(), getHeight(), new Color(229, 239, 247));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        right.setOpaque(false);

        UIComponents.RoundedPanel form = new UIComponents.RoundedPanel(28, Color.WHITE);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(BorderFactory.createEmptyBorder(42, 44, 40, 44));
        form.setMaximumSize(new Dimension(430, 580));
        form.setPreferredSize(new Dimension(430, 540));

        JLabel title = new JLabel("Welcome Back");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = UIComponents.wrappedLabel(
            "Sign in to continue to your banking workspace.",
            UITheme.FONT_BODY,
            UITheme.TEXT_MUTED,
            320
        );
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        emailField = UIComponents.createTextField("Email address");
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        passwordField = UIComponents.createPasswordField();
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        JButton loginBtn = UIComponents.createButton("Sign In", UITheme.PRIMARY, Color.WHITE, -1, 46);
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        loginBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginBtn.addActionListener(e -> doLogin());

        JButton registerBtn = UIComponents.createButton("Create Account", UITheme.BG_SOFT, UITheme.TEXT_PRIMARY, -1, 46);
        registerBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        registerBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        registerBtn.addActionListener(e -> openRegister());

        messageLabel = new JLabel(" ");
        messageLabel.setFont(UITheme.FONT_SMALL);
        messageLabel.setForeground(UITheme.DANGER);
        messageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel hint = UIComponents.wrappedLabel(
            "Secure login with seeded demo data and persistent local storage.",
            UITheme.FONT_SMALL,
            UITheme.TEXT_MUTED,
            320
        );
        hint.setAlignmentX(Component.LEFT_ALIGNMENT);

        passwordField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) { if (e.getKeyCode() == KeyEvent.VK_ENTER) doLogin(); }
        });
        getRootPane().setDefaultButton(loginBtn);

        form.add(title);
        form.add(Box.createVerticalStrut(6));
        form.add(sub);
        form.add(Box.createVerticalStrut(30));
        JPanel emailRow = UIComponents.formRow("Email Address", emailField);
        emailRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(emailRow);
        form.add(Box.createVerticalStrut(16));
        JPanel passwordRow = UIComponents.formRow("Password", passwordField);
        passwordRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(passwordRow);
        form.add(Box.createVerticalStrut(10));
        form.add(messageLabel);
        form.add(Box.createVerticalStrut(18));
        form.add(loginBtn);
        form.add(Box.createVerticalStrut(12));
        form.add(registerBtn);
        form.add(Box.createVerticalStrut(18));
        form.add(hint);

        right.add(form);

        add(left, BorderLayout.WEST);
        add(right, BorderLayout.CENTER);
    }

    private void doLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        try {
            User user = authService.login(email, password);
            messageLabel.setForeground(UITheme.SUCCESS);
            messageLabel.setText(UIComponents.wrapHtml("Login successful! Loading dashboard...", 320));
            SwingUtilities.invokeLater(() -> {
                MainFrame main = new MainFrame(user);
                main.setVisible(true);
                dispose();
            });
        } catch (AuthenticationException ex) {
            messageLabel.setForeground(UITheme.DANGER);
            messageLabel.setText(UIComponents.wrapHtml(ex.getMessage(), 320));
        }
    }

    private void openRegister() {
        new RegisterDialog(this).setVisible(true);
    }
}
