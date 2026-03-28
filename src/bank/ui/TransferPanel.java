package bank.ui;

import bank.exception.BankingException;
import bank.model.*;
import bank.service.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TransferPanel extends JPanel {
    private User user;
    private MainFrame mainFrame;
    private AccountService accountService = new AccountService();
    private JComboBox<String> fromCombo;
    private JTextField toField, amountField, descField;
    private JLabel msgLabel;
    private List<Account> myAccounts;

    public TransferPanel(User user, MainFrame mainFrame) {
        this.user = user;
        this.mainFrame = mainFrame;
        setOpaque(false);
        setLayout(new BorderLayout(0, 20));
        buildUI();
    }

    private void buildUI() {
        add(UIComponents.sectionHeader("Fund Transfer"), BorderLayout.NORTH);
        myAccounts = accountService.getCustomerAccounts(user.getUserId());

        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);

        UIComponents.RoundedPanel card = new UIComponents.RoundedPanel(UITheme.CARD_RADIUS, UITheme.BG_CARD);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(32, 40, 32, 40));
        card.setPreferredSize(new Dimension(500, 420));

        String[] fromOpts = myAccounts.stream().map(a -> a.getAccountNumber() + " - " + a.getAccountType() +
                " (LKR " + String.format("%.2f", a.getBalance()) + ")").toArray(String[]::new);
        fromCombo = new JComboBox<>(fromOpts);
        fromCombo.setFont(UITheme.FONT_BODY);
        fromCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        toField = UIComponents.createTextField("Destination account number");
        toField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        amountField = UIComponents.createTextField("Enter amount (LKR)");
        amountField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        descField = UIComponents.createTextField("Transfer description");
        descField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        msgLabel = new JLabel(" ");
        msgLabel.setFont(UITheme.FONT_BODY);
        msgLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton transferBtn = UIComponents.createButton("Transfer Funds", UITheme.PRIMARY, Color.WHITE, -1, 46);
        transferBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        transferBtn.addActionListener(e -> doTransfer());

        JLabel warningLbl = UIComponents.wrappedLabel(
            "Please verify the destination account number carefully before transferring.",
            UITheme.FONT_SMALL,
            UITheme.WARNING,
            390
        );

        card.add(UIComponents.formRow("From Account *", fromCombo));
        card.add(Box.createVerticalStrut(14));
        card.add(UIComponents.formRow("To Account Number *", toField));
        card.add(Box.createVerticalStrut(14));
        card.add(UIComponents.formRow("Amount (LKR) *", amountField));
        card.add(Box.createVerticalStrut(14));
        card.add(UIComponents.formRow("Description", descField));
        card.add(Box.createVerticalStrut(8));
        card.add(warningLbl);
        card.add(Box.createVerticalStrut(20));
        card.add(transferBtn);
        card.add(Box.createVerticalStrut(12));
        card.add(msgLabel);

        center.add(card);
        add(center, BorderLayout.CENTER);
    }

    private void doTransfer() {
        int idx = fromCombo.getSelectedIndex();
        if (idx < 0) { show("Select source account.", UITheme.DANGER); return; }

        String toAccNum = toField.getText().trim();
        String amtStr = amountField.getText().trim();

        if (toAccNum.isEmpty() || amtStr.isEmpty()) {
            show("All fields are required.", UITheme.DANGER); return;
        }

        try {
            double amount = Double.parseDouble(amtStr);
            String fromAccNum = myAccounts.get(idx).getAccountNumber();

            if (fromAccNum.equals(toAccNum)) { show("Cannot transfer to the same account.", UITheme.DANGER); return; }

            
            int confirm = JOptionPane.showConfirmDialog(this,
                String.format("Transfer LKR %.2f from %s to %s?", amount, fromAccNum, toAccNum),
                "Confirm Transfer", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;

            accountService.transfer(fromAccNum, toAccNum, amount);
            show("Transfer successful. LKR " + String.format("%.2f", amount) + " sent to " + toAccNum, UITheme.SUCCESS);
            amountField.setText("");
            toField.setText("");
        } catch (NumberFormatException ex) {
            show("Invalid amount.", UITheme.DANGER);
        } catch (BankingException ex) {
            show(ex.getMessage(), UITheme.DANGER);
        }
    }

    private void show(String msg, Color color) {
        msgLabel.setForeground(color);
        msgLabel.setText(UIComponents.wrapHtml(msg, 390));
    }
}
