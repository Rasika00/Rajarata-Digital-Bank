package bank.ui;

import bank.model.*;
import bank.service.*;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DashboardPanel extends JPanel {
    private final User user;
    private final MainFrame mainFrame;
    private final AccountService accountService = new AccountService();
    private final LoanService loanService = new LoanService();
    private final NotificationService notifService = new NotificationService();

    public DashboardPanel(User user, MainFrame mainFrame) {
        this.user = user;
        this.mainFrame = mainFrame;
        setOpaque(false);
        setLayout(new BorderLayout(0, UITheme.SPACE_2));
        setBorder(BorderFactory.createEmptyBorder(UITheme.SPACE_2, UITheme.SPACE_2, UITheme.SPACE_2, UITheme.SPACE_2));
        buildUI();
    }

    private void buildUI() {
        add(buildHeader(), BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        if (user.getRole() == UserRole.CUSTOMER) {
            content.add(buildOpenAccountHero());
            content.add(Box.createVerticalStrut(UITheme.SPACE_2));
        }

        JPanel cards = new JPanel(new GridLayout(1, 4, UITheme.SPACE_2, 0));
        cards.setOpaque(false);
        populateStatCards(cards);

        JPanel body = new JPanel(new GridLayout(1, 2, UITheme.SPACE_2, 0));
        body.setOpaque(false);
        body.add(buildTransactionsCard());
        body.add(buildQuickActionsCard());

        content.add(cards);
        content.add(Box.createVerticalStrut(UITheme.SPACE_2));
        content.add(body);

        add(content, BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        JLabel title = new JLabel(user.getDashboardTitle());
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Digital banking overview");
        subtitle.setFont(UITheme.FONT_BODY);
        subtitle.setForeground(UITheme.TEXT_MUTED);

        left.add(title);
        left.add(Box.createVerticalStrut(2));
        left.add(subtitle);

        JLabel date = new JLabel(LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
        date.setFont(UITheme.FONT_BODY);
        date.setForeground(UITheme.TEXT_MUTED);

        header.add(left, BorderLayout.WEST);
        header.add(date, BorderLayout.EAST);
        return header;
    }

    private void populateStatCards(JPanel container) {
        if (user.getRole() == UserRole.CUSTOMER) {
            List<Account> accounts = accountService.getCustomerAccounts(user.getUserId());
            double totalBalance = accounts.stream().mapToDouble(Account::getBalance).sum();
            long activeLoans = loanService.getCustomerLoans(user.getUserId()).stream()
                .filter(l -> l.getStatus() == LoanStatus.ACTIVE).count();
            long unread = notifService.getUnreadCount(user.getUserId());

            container.add(createStatCard("Total Balance", String.format("LKR %,.2f", totalBalance), true));
            container.add(createStatCard("Accounts", String.valueOf(accounts.size()), false));
            container.add(createStatCard("Active Loans", String.valueOf(activeLoans), false));
            container.add(createStatCard("Notifications", String.valueOf(unread), false));
            return;
        }

        DataStore store = DataStore.getInstance();
        long customers = store.getAllUsers().stream().filter(u -> u.getRole() == UserRole.CUSTOMER).count();
        long pendingLoans = store.getAllLoans().stream().filter(l -> l.getStatus() == LoanStatus.PENDING).count();
        long allAccounts = store.getAllAccounts().size();
        double totalDeposits = store.getAllAccounts().stream().mapToDouble(Account::getBalance).sum();

        container.add(createStatCard("Total Customers", String.valueOf(customers), true));
        container.add(createStatCard("Total Deposits", String.format("LKR %,.0f", totalDeposits), false));
        container.add(createStatCard("Pending Loans", String.valueOf(pendingLoans), false));
        container.add(createStatCard("All Accounts", String.valueOf(allAccounts), false));
    }

    private JPanel createStatCard(String label, String value, boolean highlighted) {
        JPanel card = new SurfaceCard(14, highlighted ? UITheme.PRIMARY : UITheme.BG_CARD, highlighted ? UITheme.PRIMARY_DARK : UITheme.BORDER);
        card.setLayout(new BorderLayout(0, 8));
        card.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        JLabel labelText = new JLabel(label.toUpperCase());
        labelText.setFont(UITheme.FONT_SMALL);
        labelText.setForeground(highlighted ? new Color(232, 236, 255) : UITheme.TEXT_MUTED);

        JLabel valueText = new JLabel(value);
        valueText.setFont(new Font(UITheme.FONT_FAMILY, Font.BOLD, 21));
        valueText.setForeground(highlighted ? Color.WHITE : UITheme.TEXT_PRIMARY);

        card.add(labelText, BorderLayout.NORTH);
        card.add(valueText, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildTransactionsCard() {
        JPanel card = new SurfaceCard(14, UITheme.BG_CARD, UITheme.BORDER);
        card.setLayout(new BorderLayout(0, UITheme.SPACE_1));
        card.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        card.add(sectionHeader("Recent Transactions"), BorderLayout.NORTH);
        card.add(UIComponents.scrollPane(buildTransactionsTable()), BorderLayout.CENTER);
        return card;
    }

    private JTable buildTransactionsTable() {
        String[] cols = {"Date", "Type", "Amount", "Status"};
        List<Transaction> tx = getRecentTransactions();
        int limit = Math.min(10, tx.size());

        Object[][] data = new Object[limit][4];
        for (int i = 0; i < limit; i++) {
            Transaction t = tx.get(i);
            data[i][0] = t.getTimestamp().toLocalDate().toString();
            data[i][1] = transactionLabel(t.getType());
            data[i][2] = String.format("LKR %,.2f", t.getAmount());
            data[i][3] = t.getStatus().name();
        }

        DefaultTableModel model = new DefaultTableModel(data, cols) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        JTable table = new JTable(model);
        UIComponents.styleTable(table);
        table.setRowHeight(36);
        table.getColumnModel().getColumn(3).setCellRenderer(new StatusRenderer());

        table.addMouseMotionListener(new MouseAdapter() {
            @Override public void mouseMoved(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                if (row >= 0) table.setRowSelectionInterval(row, row);
            }
        });
        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseExited(MouseEvent e) {
                table.clearSelection();
            }
        });

        return table;
    }

    private JPanel buildQuickActionsCard() {
        JPanel card = new SurfaceCard(14, UITheme.BG_CARD, UITheme.BORDER);
        card.setLayout(new BorderLayout(0, UITheme.SPACE_1));
        card.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        card.add(sectionHeader("Quick Actions"), BorderLayout.NORTH);

        int actionColumns = 3;
        JPanel actions = new JPanel(new GridLayout(0, actionColumns, UITheme.SPACE_1, UITheme.SPACE_1));
        actions.setOpaque(false);

        if (user.getRole() == UserRole.CUSTOMER) {
            actions.add(actionButton("\uE8C8", new Color(22, 163, 74), new Color(16, 185, 129), "Deposit", "Deposit"));
            actions.add(actionButton("\uE71D", new Color(249, 115, 22), new Color(245, 158, 11), "Withdraw", "Withdraw"));
            actions.add(actionButton("\uE8AB", new Color(37, 99, 235), new Color(59, 130, 246), "Transfer", "Transfer"));
            actions.add(actionButton("\uE8A1", new Color(14, 165, 233), new Color(56, 189, 248), "Pay Bills", "Bill Payments"));
            actions.add(actionButton("\uE8B7", new Color(67, 56, 202), new Color(79, 70, 229), "Statements", "Statements"));
            actions.add(actionButton("\uE774", new Color(13, 148, 136), new Color(20, 184, 166), "Loans", "Loans"));
        } else {
            actions.add(actionButton("\uE8B7", new Color(22, 163, 74), new Color(16, 185, 129), "Accounts", "All Accounts"));
            actions.add(actionButton("\uE774", new Color(249, 115, 22), new Color(245, 158, 11), "Loans", "Loan Approvals"));
            actions.add(actionButton("\uE716", new Color(37, 99, 235), new Color(59, 130, 246), "Users", "Customers"));
            actions.add(actionButton("\uE9D2", new Color(14, 165, 233), new Color(56, 189, 248), "Reports", "Reports"));
            actions.add(actionButton("\uE8A1", new Color(67, 56, 202), new Color(79, 70, 229), "Alerts", "Notifications"));
            actions.add(actionButton("\uE713", new Color(13, 148, 136), new Color(20, 184, 166), "Settings", "Settings"));
        }

        card.add(actions, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildOpenAccountHero() {
        OpenAccountHeroCard hero = new OpenAccountHeroCard();
        hero.setLayout(new BorderLayout());
        hero.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.X_AXIS));

        JLabel icon = new JLabel("\uE710");
        icon.setFont(new Font("Segoe MDL2 Assets", Font.PLAIN, 34));
        icon.setForeground(Color.WHITE);
        icon.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Open New Account");
        title.setFont(new Font(UITheme.FONT_FAMILY, Font.BOLD, 22));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Start now and choose Savings, Current, or Fixed Deposit in seconds.");
        subtitle.setFont(UITheme.FONT_BODY);
        subtitle.setForeground(new Color(226, 232, 255));

        text.add(title);
        text.add(Box.createVerticalStrut(3));
        text.add(subtitle);

        left.add(icon);
        left.add(text);

        hero.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                mainFrame.navigateTo("Open Account");
            }
        });

        hero.add(left, BorderLayout.WEST);
        return hero;
    }

    private JButton actionButton(String icon, Color start, Color end, String tooltip, String destination) {
        JButton btn = new GlassActionButton(icon, start, end);
        btn.setToolTipText(tooltip);
        btn.setFont(new Font("Segoe MDL2 Assets", Font.PLAIN, 14));
        btn.addActionListener(e -> mainFrame.navigateTo(destination));
        return btn;
    }

    private JPanel sectionHeader(String text) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));

        JLabel title = new JLabel(text);
        title.setFont(UITheme.FONT_HEADING);
        title.setForeground(UITheme.TEXT_PRIMARY);

        p.add(title, BorderLayout.WEST);
        return p;
    }

    private List<Transaction> getRecentTransactions() {
        List<Transaction> all = new ArrayList<>();
        if (user.getRole() == UserRole.CUSTOMER) {
            List<Account> accounts = accountService.getCustomerAccounts(user.getUserId());
            for (Account a : accounts) all.addAll(a.getTransactions());
        } else {
            DataStore.getInstance().getAllAccounts().forEach(a -> all.addAll(a.getTransactions()));
        }
        all.sort(Comparator.comparing(Transaction::getTimestamp).reversed());
        return all;
    }

    private String transactionLabel(TransactionType type) {
        switch (type) {
            case DEPOSIT: return "Deposit";
            case WITHDRAWAL: return "Withdraw";
            case TRANSFER_IN:
            case TRANSFER_OUT: return "Transfer";
            case BILL_PAYMENT: return "Bill";
            case LOAN_REPAYMENT: return "Loan";
            default: return "General";
        }
    }

    private static class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean selected,
                                                       boolean focus, int row, int col) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, selected, focus, row, col);
            String status = value == null ? "" : value.toString();
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));

            if ("SUCCESS".equals(status)) label.setForeground(UITheme.SUCCESS);
            else if ("PENDING".equals(status)) label.setForeground(UITheme.WARNING);
            else label.setForeground(UITheme.DANGER);

            if (!selected) label.setBackground(row % 2 == 0 ? UITheme.BG_CARD : UITheme.TABLE_ALT);
            return label;
        }
    }

    private static class SurfaceCard extends JPanel {
        private final int radius;
        private final Color fill;
        private final Color border;

        private SurfaceCard(int radius, Color fill, Color border) {
            this.radius = radius;
            this.fill = fill;
            this.border = border;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(new Color(15, 23, 42, 12));
            g2.fillRoundRect(3, 4, getWidth() - 6, getHeight() - 6, radius, radius);

            g2.setColor(fill);
            g2.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 4, radius, radius);

            g2.setColor(border);
            g2.drawRoundRect(0, 0, getWidth() - 5, getHeight() - 5, radius, radius);
            g2.dispose();

            super.paintComponent(g);
        }
    }

    private static class GlassActionButton extends JButton {
        private final String icon;
        private final Color gradientStart;
        private final Color gradientEnd;

        private GlassActionButton(String icon, Color gradientStart, Color gradientEnd) {
            this.icon = icon;
            this.gradientStart = gradientStart;
            this.gradientEnd = gradientEnd;
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(92, 92);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int radius = 16;
            int w = getWidth() - 1;
            int h = getHeight() - 1;

            g2.setColor(new Color(255, 255, 255, 26));
            g2.fillRoundRect(2, 4, w - 4, h - 4, radius, radius);

            GradientPaint gp = new GradientPaint(0, 0, gradientStart, w, h, gradientEnd);
            g2.setPaint(gp);
            g2.fillRoundRect(0, 0, w - 2, h - 2, radius, radius);

            g2.setColor(new Color(255, 255, 255, 42));
            g2.fillRoundRect(10, 10, w - 24, (h / 2) - 12, 10, 10);

            if (getModel().isRollover()) {
                g2.setColor(new Color(255, 255, 255, 26));
                g2.fillRoundRect(0, 0, w - 2, h - 2, radius, radius);
            }

            g2.setColor(new Color(255, 255, 255, 128));
            g2.drawRoundRect(0, 0, w - 3, h - 3, radius, radius);

            g2.setFont(new Font("Segoe MDL2 Assets", Font.PLAIN, 34));
            g2.setColor(new Color(20, 28, 45, 46));
            FontMetrics fm = g2.getFontMetrics();
            int textX = (w - fm.stringWidth(icon)) / 2;
            int textY = (h + fm.getAscent() - fm.getDescent()) / 2;
            g2.drawString(icon, textX + 1, textY + 1);

            g2.setColor(Color.WHITE);
            g2.drawString(icon, textX, textY);
            g2.dispose();
        }
    }

    private static class OpenAccountHeroCard extends JPanel {
        private float pulsePhase = 0f;
        private boolean hovered = false;

        private OpenAccountHeroCard() {
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            Timer timer = new Timer(45, e -> {
                pulsePhase += 0.09f;
                repaint();
            });
            timer.start();

            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) {
                    hovered = true;
                    repaint();
                }

                @Override public void mouseExited(MouseEvent e) {
                    hovered = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth() - 1;
            int h = getHeight() - 1;
            int radius = 20;

            int glowAlpha = hovered ? 78 : 58;
            int pulseAlpha = (int) (24 + (12 * (0.5 + 0.5 * Math.sin(pulsePhase))));

            g2.setColor(new Color(59, 130, 246, glowAlpha));
            g2.fillRoundRect(3, 6, w - 6, h - 4, radius, radius);

            GradientPaint gp = new GradientPaint(
                0, 0,
                hovered ? new Color(59, 130, 246) : new Color(37, 99, 235),
                w, h,
                hovered ? new Color(79, 70, 229) : new Color(67, 56, 202)
            );
            g2.setPaint(gp);
            g2.fillRoundRect(0, 0, w - 2, h - 2, radius, radius);

            g2.setColor(new Color(255, 255, 255, pulseAlpha));
            g2.fillRoundRect(8, 8, (w / 2), h - 18, 14, 14);

            g2.setColor(new Color(255, 255, 255, hovered ? 148 : 110));
            g2.drawRoundRect(0, 0, w - 3, h - 3, radius, radius);

            g2.dispose();
            super.paintComponent(g);
        }
    }

}
