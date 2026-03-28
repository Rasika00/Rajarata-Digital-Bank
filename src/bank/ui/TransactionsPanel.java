package bank.ui;

import bank.exception.BankingException;
import bank.model.*;
import bank.service.*;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class TransactionsPanel extends JPanel {
    private User user;
    private AccountService accountService = new AccountService();
    private DataStore store = DataStore.getInstance();

    public TransactionsPanel(User user, MainFrame mainFrame) {
        this.user = user;
        setOpaque(false);
        setLayout(new BorderLayout(0, 16));
        buildUI();
    }

    private void buildUI() {
        add(UIComponents.sectionHeader("Transaction History"), BorderLayout.NORTH);

        List<Transaction> all = new ArrayList<>();
        if (user.getRole() == UserRole.CUSTOMER) {
            accountService.getCustomerAccounts(user.getUserId()).forEach(a -> all.addAll(a.getTransactions()));
        } else {
            store.getAllAccounts().forEach(a -> all.addAll(a.getTransactions()));
        }
        all.sort((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()));

        String[] cols = {"Transaction ID", "Date & Time", "Type", "Amount", "Balance After", "Description", "Status"};
        Object[][] data = all.stream().map(Transaction::toTableRow).toArray(Object[][]::new);

        JTable table = UIComponents.createStyledTable(data, cols);
        table.getColumnModel().getColumn(0).setPreferredWidth(140);
        table.getColumnModel().getColumn(1).setPreferredWidth(140);
        add(UIComponents.scrollPane(table), BorderLayout.CENTER);

        JLabel countLbl = UIComponents.label("Total: " + all.size() + " transactions", UITheme.FONT_SMALL, UITheme.TEXT_MUTED);
        add(countLbl, BorderLayout.SOUTH);
    }
}
