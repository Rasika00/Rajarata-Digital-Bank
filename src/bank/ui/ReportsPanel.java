package bank.ui;

import bank.model.*;
import bank.service.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportsPanel extends JPanel {
    private DataStore store = DataStore.getInstance();

    public ReportsPanel(User user, MainFrame mainFrame) {
        setOpaque(false);
        setLayout(new BorderLayout(0, 20));
        buildUI();
    }

    private void buildUI() {
        add(UIComponents.sectionHeader("Reports & Analytics"), BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(2, 2, 16, 16));
        grid.setOpaque(false);

        grid.add(buildAccountSummaryReport());
        grid.add(buildLoanSummaryReport());
        grid.add(buildTransactionSummaryReport());
        grid.add(buildUserSummaryReport());

        add(grid, BorderLayout.CENTER);
    }

    private JPanel buildAccountSummaryReport() {
        UIComponents.RoundedPanel card = new UIComponents.RoundedPanel(UITheme.CARD_RADIUS, UITheme.BG_CARD);
        card.setLayout(new BorderLayout(0, 8));
        card.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        card.add(UIComponents.sectionHeader("Account Summary"), BorderLayout.NORTH);

        Collection<Account> accounts = store.getAllAccounts();
        Map<AccountType, Long> countByType = accounts.stream().collect(Collectors.groupingBy(Account::getAccountType, Collectors.counting()));
        Map<AccountType, Double> balByType = accounts.stream().collect(Collectors.groupingBy(Account::getAccountType, Collectors.summingDouble(Account::getBalance)));

        String[] cols = {"Account Type", "Count", "Total Balance"};
        Object[][] data = Arrays.stream(AccountType.values()).map(t -> new Object[]{
            t.toString(), countByType.getOrDefault(t, 0L),
            String.format("LKR %.2f", balByType.getOrDefault(t, 0.0))
        }).toArray(Object[][]::new);

        JTable table = UIComponents.createStyledTable(data, cols);
        card.add(UIComponents.scrollPane(table), BorderLayout.CENTER);
        return card;
    }

    private JPanel buildLoanSummaryReport() {
        UIComponents.RoundedPanel card = new UIComponents.RoundedPanel(UITheme.CARD_RADIUS, UITheme.BG_CARD);
        card.setLayout(new BorderLayout(0, 8));
        card.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        card.add(UIComponents.sectionHeader("Loan Summary"), BorderLayout.NORTH);

        Collection<Loan> loans = store.getAllLoans();
        Map<LoanStatus, Long> byStatus = loans.stream().collect(Collectors.groupingBy(Loan::getStatus, Collectors.counting()));
        double totalPrincipal = loans.stream().mapToDouble(Loan::getPrincipal).sum();
        double totalRemaining = loans.stream().mapToDouble(Loan::getRemainingBalance).sum();

        String[] cols = {"Status", "Count"};
        Object[][] data = Arrays.stream(LoanStatus.values()).map(s -> new Object[]{
            s.toString(), byStatus.getOrDefault(s, 0L)
        }).toArray(Object[][]::new);

        JTable table = UIComponents.createStyledTable(data, cols);
        card.add(UIComponents.scrollPane(table), BorderLayout.CENTER);

        JLabel totals = UIComponents.label(String.format("Total Principal: LKR %.2f | Remaining: LKR %.2f", totalPrincipal, totalRemaining),
                UITheme.FONT_SMALL, UITheme.TEXT_MUTED);
        card.add(totals, BorderLayout.SOUTH);
        return card;
    }

    private JPanel buildTransactionSummaryReport() {
        UIComponents.RoundedPanel card = new UIComponents.RoundedPanel(UITheme.CARD_RADIUS, UITheme.BG_CARD);
        card.setLayout(new BorderLayout(0, 8));
        card.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        card.add(UIComponents.sectionHeader("Transaction Summary"), BorderLayout.NORTH);

        List<Transaction> allTxn = new ArrayList<>();
        store.getAllAccounts().forEach(a -> allTxn.addAll(a.getTransactions()));
        Map<TransactionType, Long> byType = allTxn.stream().collect(Collectors.groupingBy(Transaction::getType, Collectors.counting()));
        Map<TransactionType, Double> amtByType = allTxn.stream().collect(Collectors.groupingBy(Transaction::getType, Collectors.summingDouble(Transaction::getAmount)));

        String[] cols = {"Type", "Count", "Total Volume"};
        List<Object[]> rows = byType.entrySet().stream().map(e -> new Object[]{
            e.getKey().toString(), e.getValue(),
            String.format("LKR %.2f", amtByType.getOrDefault(e.getKey(), 0.0))
        }).collect(Collectors.toList());

        JTable table = UIComponents.createStyledTable(rows.toArray(Object[][]::new), cols);
        card.add(UIComponents.scrollPane(table), BorderLayout.CENTER);
        card.add(UIComponents.label("Total transactions: " + allTxn.size(), UITheme.FONT_SMALL, UITheme.TEXT_MUTED), BorderLayout.SOUTH);
        return card;
    }

    private JPanel buildUserSummaryReport() {
        UIComponents.RoundedPanel card = new UIComponents.RoundedPanel(UITheme.CARD_RADIUS, UITheme.BG_CARD);
        card.setLayout(new BorderLayout(0, 8));
        card.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        card.add(UIComponents.sectionHeader("User Summary"), BorderLayout.NORTH);

        Collection<User> users = store.getAllUsers();
        long customers = users.stream().filter(u -> u.getRole() == UserRole.CUSTOMER).count();
        long staff = users.stream().filter(u -> u.getRole() == UserRole.STAFF).count();
        long admins = users.stream().filter(u -> u.getRole() == UserRole.ADMIN).count();
        long active = users.stream().filter(User::isActive).count();
        long locked = users.stream().filter(User::isLocked).count();

        String[] cols = {"Metric", "Count"};
        Object[][] data = {
            {"Total Users", users.size()}, {"Customers", customers},
            {"Staff", staff}, {"Admins", admins},
            {"Active", active}, {"Locked", locked}
        };
        JTable table = UIComponents.createStyledTable(data, cols);
        card.add(UIComponents.scrollPane(table), BorderLayout.CENTER);
        return card;
    }
}
