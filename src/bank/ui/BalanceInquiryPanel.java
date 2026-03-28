package bank.ui;

import bank.model.*;
import bank.service.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;




public class BalanceInquiryPanel extends JPanel {
    private User user;
    private AccountService accountService = new AccountService();

    public BalanceInquiryPanel(User user, MainFrame mainFrame) {
        this.user = user;
        setOpaque(false);
        setLayout(new BorderLayout(0, 16));
        buildUI();
    }

    private void buildUI() {
        add(UIComponents.sectionHeader("Balance Inquiry"), BorderLayout.NORTH);

        List<Account> accounts = accountService.getCustomerAccounts(user.getUserId());
        if (accounts.isEmpty()) {
            add(UIComponents.label("No accounts found.", UITheme.FONT_BODY, UITheme.TEXT_MUTED), BorderLayout.CENTER);
            return;
        }

        JPanel grid = new JPanel(new GridLayout(0, 1, 0, 16));
        grid.setOpaque(false);

        for (Account a : accounts) {
            grid.add(buildDetailCard(a));
        }

        JScrollPane sp = new JScrollPane(grid);
        sp.setBorder(null);
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        add(sp, BorderLayout.CENTER);
    }

    private JPanel buildDetailCard(Account account) {
        UIComponents.RoundedPanel card = new UIComponents.RoundedPanel(UITheme.CARD_RADIUS, UITheme.BG_CARD);
        card.setLayout(new BorderLayout(0, 0));
        card.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        
        JPanel header = new JPanel(new BorderLayout());
        Color typeColor = getTypeColor(account.getAccountType());
        header.setBackground(typeColor);
        header.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        JLabel accLabel = new JLabel(account.getAccountType().toString().replace("_"," ") + " — " + account.getAccountNumber());
        accLabel.setFont(UITheme.FONT_SUBHEAD);
        accLabel.setForeground(Color.WHITE);

        JLabel currLabel = new JLabel(account.getCurrency());
        currLabel.setFont(UITheme.FONT_BODY);
        currLabel.setForeground(new Color(220,240,255));

        header.add(accLabel, BorderLayout.WEST);
        header.add(currLabel, BorderLayout.EAST);
        card.add(header, BorderLayout.NORTH);

        
        JPanel details = new JPanel(new GridLayout(2, 3, 20, 10));
        details.setBackground(Color.WHITE);
        details.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));

        double interest = account.calculateInterest();
        double annualInterest = interest * 12;
        double lkrBalance = account.getCurrency().equals("LKR")
            ? account.getBalance()
            : CurrencyConverter.convert(account.getBalance(), account.getCurrency(), "LKR");

        details.add(buildDetailItem("Current Balance",
            CurrencyConverter.format(account.getBalance(), account.getCurrency()), typeColor));
        details.add(buildDetailItem("LKR Equivalent",
            String.format("LKR %.2f", lkrBalance), UITheme.PRIMARY));
        details.add(buildDetailItem("Monthly Interest",
            CurrencyConverter.format(interest, account.getCurrency()), UITheme.SUCCESS));
        details.add(buildDetailItem("Annual Interest (est.)",
            CurrencyConverter.format(annualInterest, account.getCurrency()), UITheme.SUCCESS));
        details.add(buildDetailItem("Min. Balance",
            CurrencyConverter.format(account.getMinimumBalance(), account.getCurrency()), UITheme.WARNING));
        details.add(buildDetailItem("Transactions",
            account.getTransactions().size() + " total", UITheme.INFO));

        card.add(details, BorderLayout.CENTER);

        
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(new Color(248, 250, 255));
        footer.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        JLabel desc = UIComponents.label(account.getAccountDescription(), UITheme.FONT_SMALL, UITheme.TEXT_MUTED);
        footer.add(desc);
        card.add(footer, BorderLayout.SOUTH);

        return card;
    }

    private JPanel buildDetailItem(String label, String value, Color accent) {
        JPanel p = new JPanel(new GridLayout(2, 1, 0, 3));
        p.setOpaque(false);
        JLabel lbl = UIComponents.label(label, UITheme.FONT_SMALL, UITheme.TEXT_MUTED);
        JLabel val = UIComponents.label(value, UITheme.FONT_SUBHEAD, accent);
        p.add(lbl);
        p.add(val);
        return p;
    }

    private Color getTypeColor(AccountType type) {
        switch (type) {
            case SAVINGS: return UITheme.PRIMARY;
            case CHECKING: return UITheme.ACCENT;
            case STUDENT: return UITheme.SUCCESS;
            case FIXED_DEPOSIT: return UITheme.WARNING;
            default: return UITheme.TEXT_MUTED;
        }
    }
}
