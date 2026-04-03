package bank.ui;

import bank.model.*;
import bank.service.NotificationService;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;




public class MainFrame extends JFrame {
    private User currentUser;
    private JPanel contentArea;
    private JPanel sidebar;
    private JLabel titleLabel;
    private AlertBadge notifBadge;
    private NotificationService notifService = new NotificationService();
    private Map<String, JButton> navButtons = new LinkedHashMap<>();

    public MainFrame(User user) {
        this.currentUser = user;
        setTitle("Rajarata Digital Bank - " + user.getFullName());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1280, 820);
        setMinimumSize(new Dimension(1100, 700));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        buildUI();
        navigateTo("Dashboard");
    }

    private void buildUI() {
        JPanel topBar = buildTopBar();
        add(topBar, BorderLayout.NORTH);

        sidebar = buildSidebar();
        add(sidebar, BorderLayout.WEST);

        contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(UITheme.BG_MAIN);
        contentArea.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));
        add(contentArea, BorderLayout.CENTER);
        getContentPane().setBackground(UITheme.BG_MAIN);
    }

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint base = new GradientPaint(0, 0, new Color(12, 25, 64), getWidth(), 0, new Color(45, 61, 142));
                g2.setPaint(base);
                g2.fillRect(0, 0, getWidth(), getHeight());

                GradientPaint layer = new GradientPaint(0, 0, new Color(93, 115, 222, 38), 0, getHeight(), new Color(21, 163, 186, 22));
                g2.setPaint(layer);
                g2.fillRect(0, 0, getWidth(), getHeight());

                g2.setColor(new Color(255, 255, 255, 18));
                g2.fillOval(getWidth() - 420, -130, 320, 250);
                g2.setColor(new Color(131, 197, 255, 20));
                g2.fillOval(220, -90, 280, 200);
                g2.setColor(new Color(57, 95, 217, 24));
                g2.fillOval(-130, 18, 290, 160);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        bar.setOpaque(false);
        bar.setPreferredSize(new Dimension(0, 84));
        bar.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.X_AXIS));
        left.setOpaque(false);

        JComponent logo = new GlowingLogo(UIComponents.createBrandMark("RB", 56));
        JPanel brandCopy = new JPanel();
        brandCopy.setOpaque(false);
        brandCopy.setLayout(new BoxLayout(brandCopy, BoxLayout.Y_AXIS));
        JLabel bankName = new JLabel("Rajarata Digital Bank");
        bankName.setFont(new Font(UITheme.FONT_FAMILY, Font.BOLD, 14));
        bankName.setForeground(Color.WHITE);
        JLabel bankSub = new JLabel("Digital operations dashboard");
        bankSub.setFont(new Font(UITheme.FONT_FAMILY, Font.PLAIN, 11));
        bankSub.setForeground(new Color(176, 210, 255));
        logo.setAlignmentY(Component.CENTER_ALIGNMENT);
        brandCopy.setAlignmentY(Component.CENTER_ALIGNMENT);
        left.add(logo);
        left.add(Box.createHorizontalStrut(8));
        brandCopy.add(bankName);
        brandCopy.add(bankSub);
        left.add(brandCopy);

        titleLabel = new JLabel("Dashboard");
        titleLabel.setFont(new Font(UITheme.FONT_FAMILY, Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);

        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);
        center.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        c.anchor = GridBagConstraints.WEST;
        center.add(titleLabel, c);

        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.X_AXIS));
        right.setOpaque(false);

        notifBadge = new AlertBadge();
        notifBadge.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        notifBadge.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) { navigateTo("Notifications"); }
        });
        notifBadge.setAlignmentY(Component.CENTER_ALIGNMENT);
        refreshNotifBadge();
        right.add(notifBadge);
        right.add(Box.createHorizontalStrut(10));

        String fullUserInfo = currentUser.getFullName() + "  |  " + currentUser.getRole().toString().toUpperCase();
        String shortUserInfo = fullUserInfo.length() > 34 ? fullUserInfo.substring(0, 31) + "..." : fullUserInfo;
        JLabel userInfo = new JLabel(shortUserInfo);
        userInfo.setFont(UITheme.FONT_BODY);
        userInfo.setForeground(UITheme.TEXT_LIGHT);
        userInfo.setToolTipText(fullUserInfo);
        userInfo.setAlignmentY(Component.CENTER_ALIGNMENT);
        right.add(userInfo);
        right.add(Box.createHorizontalStrut(10));

        JButton logoutBtn = new RubyLogoutButton();
        logoutBtn.setAlignmentY(Component.CENTER_ALIGNMENT);
        logoutBtn.addActionListener(e -> logout());
        right.add(logoutBtn);

        bar.add(left, BorderLayout.WEST);
        bar.add(center, BorderLayout.CENTER);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    private JPanel buildSidebar() {
        JPanel sb = new JPanel();
        sb.setBackground(UITheme.BG_SIDEBAR);
        sb.setPreferredSize(new Dimension(UITheme.SIDEBAR_WIDTH, 0));
        sb.setLayout(new BoxLayout(sb, BoxLayout.Y_AXIS));
        sb.setBorder(BorderFactory.createEmptyBorder(20, 12, 20, 12));

        UIComponents.RoundedPanel identityCard = new UIComponents.RoundedPanel(14, new Color(255, 255, 255, 20), new Color(255, 255, 255, 24));
        identityCard.setLayout(new BoxLayout(identityCard, BoxLayout.Y_AXIS));
        identityCard.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        JLabel roleLabel = new JLabel(currentUser.getRole().toString());
        roleLabel.setFont(UITheme.FONT_SMALL);
        roleLabel.setForeground(new Color(171, 194, 220));
        JLabel nameLabel = new JLabel(currentUser.getFullName());
        nameLabel.setFont(UITheme.FONT_SUBHEAD);
        nameLabel.setForeground(Color.WHITE);
        identityCard.add(roleLabel);
        identityCard.add(Box.createVerticalStrut(6));
        identityCard.add(nameLabel);
        sb.add(identityCard);
        sb.add(Box.createVerticalStrut(14));

        JPanel menuHeader = new JPanel(new BorderLayout());
        menuHeader.setOpaque(false);
        menuHeader.setBorder(BorderFactory.createEmptyBorder(0, 6, 8, 6));
        JLabel menuTitle = new JLabel("MENU");
        menuTitle.setFont(new Font(UITheme.FONT_FAMILY, Font.BOLD, 11));
        menuTitle.setForeground(new Color(170, 198, 233));
        JSeparator menuSep = new JSeparator();
        menuSep.setForeground(new Color(146, 171, 201, 90));
        menuSep.setBackground(new Color(146, 171, 201, 90));
        menuHeader.add(menuTitle, BorderLayout.WEST);
        menuHeader.add(menuSep, BorderLayout.SOUTH);
        sb.add(menuHeader);
        sb.add(Box.createVerticalStrut(4));

        addNavItem(sb, "Dashboard");
        addNavItem(sb, "Notifications");

        if (currentUser.getRole() == UserRole.CUSTOMER) {
            addNavItem(sb, "My Accounts");
            addNavItem(sb, "Balance Inquiry");
            addNavItem(sb, "Deposit");
            addNavItem(sb, "Withdraw");
            addNavItem(sb, "Transfer");
            addNavItem(sb, "Transactions");
            addNavItem(sb, "Statements");
            addNavItem(sb, "Loans");
            addNavItem(sb, "Bill Payments");
            addNavItem(sb, "Currency Converter");
            addNavItem(sb, "Open Account");
        }

        if (currentUser.getRole() == UserRole.STAFF) {
            addNavItem(sb, "All Accounts");
            addNavItem(sb, "Loan Approvals");
            addNavItem(sb, "Customers");
            addNavItem(sb, "Transactions");
            addNavItem(sb, "Statements");
        }

        if (currentUser.getRole() == UserRole.ADMIN) {
            addNavItem(sb, "All Accounts");
            addNavItem(sb, "All Users");
            addNavItem(sb, "Loan Approvals");
            addNavItem(sb, "All Transactions");
            addNavItem(sb, "Statements");
            addNavItem(sb, "Audit Log");
            addNavItem(sb, "Reports");
        }

        sb.add(Box.createVerticalGlue());

        addNavItem(sb, "Settings");

        return sb;
    }

    private void addNavItem(JPanel sidebar, String name) {
        JButton btn = UIComponents.createNavButton(name, iconFor(name));
        btn.addActionListener(e -> navigateTo(name));
        navButtons.put(name, btn);
        sidebar.add(btn);
        sidebar.add(Box.createVerticalStrut(6));
    }

    private String iconFor(String item) {
        switch (item) {
            case "Dashboard": return "\uE80F";
            case "Notifications": return "\uE7ED";
            case "My Accounts":
            case "All Accounts": return "\uE8B7";
            case "Balance Inquiry": return "\uEAFD";
            case "Deposit": return "\uE8C8";
            case "Withdraw": return "\uE71D";
            case "Transfer": return "\uE8AB";
            case "Transactions":
            case "All Transactions": return "\uE9D2";
            case "Statements": return "\uE8A5";
            case "Loans":
            case "Loan Approvals": return "\uE774";
            case "Bill Payments": return "\uE8A1";
            case "Currency Converter": return "\uE909";
            case "Open Account": return "\uECC8";
            case "All Users":
            case "Customers": return "\uE716";
            case "Audit Log": return "\uE9D9";
            case "Reports": return "\uE9D2";
            case "Settings": return "\uE713";
            default: return "\uE946";
        }
    }

    public void navigateTo(String screen) {
        navButtons.forEach((name, btn) -> {
            btn.setForeground(name.equals(screen) ? Color.WHITE : UITheme.TEXT_LIGHT);
            UIComponents.setNavButtonSelected(btn, name.equals(screen));
        });
        titleLabel.setText(screen);

        contentArea.removeAll();
        JPanel panel;
        switch (screen) {
            case "Dashboard": panel = new DashboardPanel(currentUser, this); break;
            case "My Accounts":
            case "All Accounts": panel = new AccountsPanel(currentUser, this); break;
            case "Balance Inquiry": panel = new BalanceInquiryPanel(currentUser, this); break;
            case "Deposit": panel = new DepositWithdrawPanel(currentUser, this, true); break;
            case "Withdraw": panel = new DepositWithdrawPanel(currentUser, this, false); break;
            case "Transfer": panel = new TransferPanel(currentUser, this); break;
            case "Transactions":
            case "All Transactions": panel = new TransactionsPanel(currentUser, this); break;
            case "Statements": panel = new StatementPanel(currentUser, this); break;
            case "Loans":
            case "Loan Approvals": panel = new LoansPanel(currentUser, this); break;
            case "Bill Payments": panel = new BillPaymentPanel(currentUser, this); break;
            case "Currency Converter": panel = new CurrencyConverterPanel(currentUser, this); break;
            case "Open Account": panel = new OpenAccountPanel(currentUser, this); break;
            case "Notifications": panel = new NotificationsPanel(currentUser, this); break;
            case "All Users":
            case "Customers": panel = new UsersPanel(currentUser, this); break;
            case "Audit Log": panel = new AuditLogPanel(currentUser, this); break;
            case "Reports": panel = new ReportsPanel(currentUser, this); break;
            case "Settings": panel = new SettingsPanel(currentUser, this); break;
            default: panel = new DashboardPanel(currentUser, this); break;
        }

        contentArea.add(panel, BorderLayout.CENTER);
        contentArea.revalidate();
        contentArea.repaint();
        refreshNotifBadge();
    }

    public void refreshNotifBadge() {
        long count = notifService.getUnreadCount(currentUser.getUserId());
        if (notifBadge != null) notifBadge.setCount((int) count);
    }

    private static class GlowingLogo extends JComponent {
        private final JComponent inner;

        private GlowingLogo(JComponent inner) {
            this.inner = inner;
            setLayout(new BorderLayout());
            add(inner, BorderLayout.CENTER);
            setOpaque(false);
        }

        @Override
        public Dimension getPreferredSize() {
            Dimension d = inner.getPreferredSize();
            return new Dimension(d.width + 16, d.height + 16);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int size = Math.min(getWidth(), getHeight()) - 6;
            int x = (getWidth() - size) / 2;
            int y = (getHeight() - size) / 2;

            g2.setColor(new Color(242, 191, 96, 80));
            g2.fillOval(x - 3, y - 3, size + 6, size + 6);

            GradientPaint ring = new GradientPaint(x, y, new Color(207, 162, 75), x + size, y + size, new Color(16, 140, 101));
            g2.setPaint(ring);
            g2.setStroke(new BasicStroke(2.5f));
            g2.drawOval(x, y, size, size);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class AlertBadge extends JComponent {
        private int count;
        private float ringPhase;

        private AlertBadge() {
            setOpaque(false);
            Timer pulse = new Timer(90, e -> {
                ringPhase += 0.08f;
                if (ringPhase > 1f) ringPhase = 0f;
                repaint();
            });
            pulse.start();
        }

        private void setCount(int count) {
            this.count = count;
            repaint();
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(86, 32);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth() - 1;
            int h = getHeight() - 1;
            g2.setColor(new Color(255, 255, 255, 28));
            g2.fillRoundRect(0, 0, w, h, 14, 14);
            g2.setColor(new Color(255, 255, 255, 60));
            g2.drawRoundRect(0, 0, w - 1, h - 1, 14, 14);

            int cx = 14;
            int cy = h / 2;
            int pulse = (int) (8 + ringPhase * 5);
            int alpha = 130 - (int) (ringPhase * 90);
            alpha = Math.max(20, alpha);
            g2.setColor(new Color(244, 194, 76, alpha));
            g2.setStroke(new BasicStroke(2f));
            g2.drawOval(cx - pulse / 2, cy - pulse / 2, pulse, pulse);

            g2.setColor(new Color(247, 204, 102));
            g2.fillOval(cx - 3, cy - 3, 6, 6);

            g2.setColor(Color.WHITE);
            g2.setFont(UITheme.FONT_BODY);
            String txt = count > 0 ? "Alerts " + count : "Alerts";
            g2.drawString(txt, 24, cy + 5);
            g2.dispose();
        }
    }

    private static class RubyLogoutButton extends JButton {
        private RubyLogoutButton() {
            super("Logout");
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setForeground(Color.WHITE);
            setFont(new Font(UITheme.FONT_FAMILY, Font.BOLD, 12));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(86, 32));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth() - 1;
            int h = getHeight() - 1;
            GradientPaint ruby = new GradientPaint(0, 0, new Color(224, 72, 72), w, h, new Color(168, 28, 28));
            g2.setPaint(ruby);
            g2.fillRoundRect(0, 0, w - 1, h - 1, 11, 11);

            g2.setColor(new Color(255, 255, 255, 34));
            g2.fillRoundRect(8, 5, w - 16, 10, 8, 8);
            g2.setColor(new Color(255, 255, 255, 70));
            g2.drawRoundRect(0, 0, w - 2, h - 2, 11, 11);

            if (getModel().isPressed()) {
                g2.setColor(new Color(0, 0, 0, 28));
                g2.fillRoundRect(0, 0, w - 1, h - 1, 11, 11);
            }

            g2.setColor(Color.WHITE);
            FontMetrics fm = g2.getFontMetrics();
            int x = (w - fm.stringWidth(getText())) / 2;
            int y = (h + fm.getAscent() - fm.getDescent()) / 2;
            g2.drawString(getText(), x, y);
            g2.dispose();
        }
    }

    private void logout() {
        int result = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?",
                "Confirm Logout", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            new LoginFrame().setVisible(true);
            dispose();
        }
    }

    public User getCurrentUser() { return currentUser; }
}
