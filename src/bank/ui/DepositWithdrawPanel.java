package bank.ui;

import bank.exception.BankingException;
import bank.model.*;
import bank.service.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class DepositWithdrawPanel extends JPanel {
    private User user;
    private MainFrame mainFrame;
    private boolean isDeposit;
    private AccountService accountService = new AccountService();
    private JComboBox<String> accountCombo;
    private JTextField amountField, descField;
    private JLabel msgLabel, balanceLabel;

    public DepositWithdrawPanel(User user, MainFrame mainFrame, boolean isDeposit) {
        this.user = user;
        this.mainFrame = mainFrame;
        this.isDeposit = isDeposit;
        setOpaque(false);
        setLayout(new BorderLayout(0, 20));
        buildUI();
    }

    private void buildUI() {
        String title = isDeposit ? "Deposit Funds" : "Withdraw Funds";
        add(UIComponents.sectionHeader(title), BorderLayout.NORTH);

        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);

        UIComponents.RoundedPanel card = new UIComponents.RoundedPanel(UITheme.CARD_RADIUS, UITheme.BG_CARD);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(32, 40, 32, 40));
        card.setMaximumSize(new Dimension(520, 460));
        card.setPreferredSize(new Dimension(480, 420));

        
        List<Account> accounts = accountService.getCustomerAccounts(user.getUserId());
        String[] accOptions = accounts.stream().map(a -> a.getAccountNumber() + " - " + a.getAccountType() +
                " (LKR " + String.format("%.2f", a.getBalance()) + ")").toArray(String[]::new);
        accountCombo = new JComboBox<>(accOptions);
        accountCombo.setFont(UITheme.FONT_BODY);
        accountCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        accountCombo.addActionListener(e -> updateBalance(accounts));

        balanceLabel = UIComponents.label("", UITheme.FONT_BODY, UITheme.TEXT_MUTED);

        amountField = UIComponents.createTextField("Enter amount (LKR)");
        amountField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        descField = UIComponents.createTextField("Description (optional)");
        descField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        msgLabel = new JLabel(" ");
        msgLabel.setFont(UITheme.FONT_BODY);
        msgLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        Color btnColor = isDeposit ? UITheme.SUCCESS : UITheme.DANGER;
        JButton actionBtn = UIComponents.createButton(title, btnColor, Color.WHITE, -1, 46);
        actionBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        actionBtn.addActionListener(e -> doTransaction(accounts));

        card.add(UIComponents.formRow("Select Account", accountCombo));
        card.add(Box.createVerticalStrut(4));
        card.add(balanceLabel);
        card.add(Box.createVerticalStrut(16));
        card.add(UIComponents.formRow("Amount (LKR) *", amountField));
        card.add(Box.createVerticalStrut(14));
        card.add(UIComponents.formRow("Description", descField));
        card.add(Box.createVerticalStrut(20));
        card.add(actionBtn);
        card.add(Box.createVerticalStrut(12));
        card.add(msgLabel);

        if (!accounts.isEmpty()) updateBalance(accounts);

        center.add(card);
        add(center, BorderLayout.CENTER);
    }

    private void updateBalance(List<Account> accounts) {
        int idx = accountCombo.getSelectedIndex();
        if (idx >= 0 && idx < accounts.size()) {
            Account a = accounts.get(idx);
            balanceLabel.setText("Current Balance: LKR " + String.format("%.2f", a.getBalance()) +
                    " | Min Required: LKR " + String.format("%.2f", a.getMinimumBalance()));
        }
    }

    private void doTransaction(List<Account> accounts) {
        int idx = accountCombo.getSelectedIndex();
        if (idx < 0) { showMsg("Please select an account.", UITheme.DANGER); return; }

        String amtStr = amountField.getText().trim();
        if (amtStr.isEmpty()) { showMsg("Please enter an amount.", UITheme.DANGER); return; }

        try {
            double amount = Double.parseDouble(amtStr);
            String desc = descField.getText().trim();
            if (desc.isEmpty()) desc = isDeposit ? "Cash deposit" : "Cash withdrawal";
            String accNum = accounts.get(idx).getAccountNumber();

            Transaction t = isDeposit
                ? accountService.deposit(accNum, amount, desc)
                : accountService.withdraw(accNum, amount, desc);

                showMsg((isDeposit ? "Deposited" : "Withdrawn") + " LKR " + String.format("%.2f", amount) +
                    " | New Balance: LKR " + String.format("%.2f", t.getBalanceAfter()), UITheme.SUCCESS);
            amountField.setText("");
            descField.setText("");

            
            SwingUtilities.invokeLater(() -> mainFrame.navigateTo(isDeposit ? "Deposit" : "Withdraw"));
        } catch (NumberFormatException ex) {
            showMsg("Invalid amount. Please enter a number.", UITheme.DANGER);
        } catch (BankingException ex) {
            showMsg(ex.getMessage(), UITheme.DANGER);
        }
    }

    private void showMsg(String msg, Color color) {
        msgLabel.setForeground(color);
        msgLabel.setText("<html>" + msg + "</html>");
    }
}
