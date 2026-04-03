package bank.ui;

import bank.model.*;
import bank.service.*;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class AccountsPanel extends JPanel {
    private User user;
    private MainFrame mainFrame;
    private AccountService accountService = new AccountService();
    private DataStore store = DataStore.getInstance();

    public AccountsPanel(User user, MainFrame mainFrame) {
        this.user = user;
        this.mainFrame = mainFrame;
        setOpaque(false);
        setLayout(new BorderLayout(0, 16));
        buildUI();
    }

    private void buildUI() {
        add(UIComponents.sectionHeader("Bank Accounts"), BorderLayout.NORTH);

        List<Account> accounts = user.getRole() == UserRole.CUSTOMER
            ? accountService.getCustomerAccounts(user.getUserId())
            : new ArrayList<>(store.getAllAccounts());

        if (accounts.isEmpty()) {
            JLabel empty = UIComponents.label("No accounts found. Click 'Open Account' to create one.",
                    UITheme.FONT_BODY, UITheme.TEXT_MUTED);
            add(empty, BorderLayout.CENTER);
            return;
        }

        
        if (user.getRole() == UserRole.CUSTOMER) {
            JPanel grid = new JPanel(new GridLayout(0, 2, 16, 16));
            grid.setOpaque(false);
            for (Account a : accounts) grid.add(buildAccountCard(a));
            JScrollPane sp = new JScrollPane(grid);
            sp.setBorder(null);
            sp.setOpaque(false);
            sp.getViewport().setOpaque(false);
            add(sp, BorderLayout.CENTER);
        } else {
            String[] cols = {"Account No.", "Type", "Customer ID", "Balance", "Currency", "Status", "Opened"};
            Object[][] data = new Object[accounts.size()][7];
            for (int i = 0; i < accounts.size(); i++) {
                Account a = accounts.get(i);
                data[i] = new Object[]{
                    a.getAccountNumber(), a.getAccountType(), a.getCustomerId(),
                    String.format("LKR %.2f", a.getBalance()), a.getCurrency(),
                    a.isActive() ? "Active" : "Inactive",
                    a.getCreatedAt().toLocalDate().toString()
                };
            }
            JTable table = UIComponents.createStyledTable(data, cols);
            add(UIComponents.scrollPane(table), BorderLayout.CENTER);
        }
    }

    private JPanel buildAccountCard(Account account) {
        UIComponents.RoundedPanel card = new UIComponents.RoundedPanel(UITheme.CARD_RADIUS, UITheme.BG_CARD);
        card.setLayout(new BorderLayout(0, 8));
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        Color typeColor = getAccountTypeColor(account.getAccountType());
        JLabel typeLbl = new JLabel(account.getAccountType().toString().replace("_", " "));
        typeLbl.setFont(UITheme.FONT_SUBHEAD);
        typeLbl.setForeground(typeColor);

        JLabel statusLbl = new JLabel(account.isActive() ? "● Active" : "● Inactive");
        statusLbl.setFont(UITheme.FONT_SMALL);
        statusLbl.setForeground(account.isActive() ? UITheme.SUCCESS : UITheme.DANGER);

        header.add(typeLbl, BorderLayout.WEST);
        header.add(statusLbl, BorderLayout.EAST);

        
        JLabel accNum = UIComponents.label(account.getAccountNumber(), UITheme.FONT_MONO, UITheme.TEXT_MUTED);

        
        JLabel balLbl = new JLabel(String.format("LKR %.2f", account.getBalance()));
        balLbl.setFont(new Font("Segoe UI", Font.BOLD, 26));
        balLbl.setForeground(UITheme.TEXT_PRIMARY);

        
        JLabel descLbl = UIComponents.label(account.getAccountDescription(), UITheme.FONT_SMALL, UITheme.TEXT_MUTED);
        descLbl.setText("<html>" + account.getAccountDescription() + "</html>");

        
        JLabel txnLbl = UIComponents.label(account.getTransactions().size() + " transactions",
                UITheme.FONT_SMALL, UITheme.TEXT_MUTED);

        JPanel body = new JPanel(new GridLayout(0, 1, 0, 6));
        body.setOpaque(false);
        body.add(header);
        body.add(accNum);
        body.add(balLbl);
        body.add(descLbl);
        body.add(txnLbl);

        card.add(body, BorderLayout.CENTER);
        return card;
    }

    private Color getAccountTypeColor(AccountType type) {
        switch (type) {
            case SAVINGS: return UITheme.PRIMARY;
            case CHECKING: return UITheme.ACCENT;
            case STUDENT: return UITheme.SUCCESS;
            case FIXED_DEPOSIT: return UITheme.WARNING;
            default: return UITheme.TEXT_MUTED;
        }
    }
}
