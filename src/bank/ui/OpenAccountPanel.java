package bank.ui;

import bank.exception.BankingException;
import bank.model.*;
import bank.service.AccountService;
import bank.service.CurrencyConverter;

import javax.swing.*;
import java.awt.*;

public class OpenAccountPanel extends JPanel {
    private User user;
    private MainFrame mainFrame;
    private AccountService accountService = new AccountService();
    private JComboBox<AccountType> typeCombo;
    private JComboBox<String> currencyCombo;
    private JTextField amountField, uniField, studentIdField, termField;
    private JLabel msgLabel;
    private JPanel extraFields;

    public OpenAccountPanel(User user, MainFrame mainFrame) {
        this.user = user;
        this.mainFrame = mainFrame;
        setOpaque(false);
        setLayout(new BorderLayout(0, 20));
        buildUI();
    }

    private void buildUI() {
        add(UIComponents.sectionHeader("Open New Account"), BorderLayout.NORTH);

        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);

        UIComponents.RoundedPanel card = new UIComponents.RoundedPanel(UITheme.CARD_RADIUS, UITheme.BG_CARD);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(32, 40, 32, 40));
        card.setPreferredSize(new Dimension(520, 560));

        typeCombo = new JComboBox<>(AccountType.values());
        typeCombo.setFont(UITheme.FONT_BODY);
        typeCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        typeCombo.addActionListener(e -> updateExtraFields());

        
        currencyCombo = new JComboBox<>(CurrencyConverter.getSupportedCurrencies());
        currencyCombo.setSelectedItem("LKR");
        currencyCombo.setFont(UITheme.FONT_BODY);
        currencyCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        amountField = UIComponents.createTextField("Initial deposit amount");
        amountField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        extraFields = new JPanel(new GridLayout(0, 1, 0, 10));
        extraFields.setOpaque(false);
        extraFields.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        uniField = UIComponents.createTextField("University name");
        studentIdField = UIComponents.createTextField("Student ID number");
        termField = UIComponents.createTextField("Term in months (e.g. 12)");

        msgLabel = new JLabel(" ");
        msgLabel.setFont(UITheme.FONT_SMALL);

        JButton openBtn = UIComponents.createButton("Open Account", UITheme.PRIMARY, Color.WHITE, -1, 46);
        openBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        openBtn.addActionListener(e -> doOpenAccount());

        
        JPanel infoPanel = buildInfoPanel();

        card.add(UIComponents.formRow("Account Type *", typeCombo));
        card.add(Box.createVerticalStrut(12));
        card.add(UIComponents.formRow("Currency (BONUS: Multi-Currency) *", currencyCombo));
        card.add(Box.createVerticalStrut(12));
        card.add(infoPanel);
        card.add(Box.createVerticalStrut(12));
        card.add(UIComponents.formRow("Initial Deposit *", amountField));
        card.add(Box.createVerticalStrut(12));
        card.add(extraFields);
        card.add(Box.createVerticalStrut(20));
        card.add(openBtn);
        card.add(Box.createVerticalStrut(10));
        card.add(msgLabel);

        updateExtraFields();
        center.add(card);
        add(center, BorderLayout.CENTER);
    }

    private JPanel buildInfoPanel() {
        UIComponents.RoundedPanel info = new UIComponents.RoundedPanel(6, new Color(235, 245, 255));
        info.setLayout(new BorderLayout());
        info.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        info.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        JLabel infoLbl = UIComponents.wrappedLabel(
            "Requirements: Savings: Min LKR 1,000 equiv. | Checking: Any | Student: Any | Fixed Deposit: Min LKR 10,000 equiv.",
            UITheme.FONT_SMALL,
            UITheme.PRIMARY,
            420
        );
        info.add(infoLbl);
        return info;
    }

    private void updateExtraFields() {
        extraFields.removeAll();
        AccountType type = (AccountType) typeCombo.getSelectedItem();
        if (type == AccountType.STUDENT) {
            uniField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
            studentIdField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
            extraFields.add(UIComponents.formRow("University Name", uniField));
            extraFields.add(UIComponents.formRow("Student ID", studentIdField));
        } else if (type == AccountType.FIXED_DEPOSIT) {
            termField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
            extraFields.add(UIComponents.formRow("Fixed Deposit Term (months)", termField));
        }
        extraFields.revalidate();
        extraFields.repaint();
    }

    private void doOpenAccount() {
        AccountType type = (AccountType) typeCombo.getSelectedItem();
        String currency = (String) currencyCombo.getSelectedItem();
        String amtStr = amountField.getText().trim();
        if (amtStr.isEmpty()) { show("Enter initial deposit.", UITheme.DANGER); return; }
        try {
            double amount = Double.parseDouble(amtStr);
            Account acc;
            if (type == AccountType.STUDENT) {
                acc = accountService.openAccount((Customer) user, type, amount,
                        uniField.getText().trim(), studentIdField.getText().trim(), currency);
            } else if (type == AccountType.FIXED_DEPOSIT) {
                int term = termField.getText().trim().isEmpty() ? 12 : Integer.parseInt(termField.getText().trim());
                acc = accountService.openAccount((Customer) user, type, amount, term, currency);
            } else {
                acc = accountService.openAccount((Customer) user, type, amount, currency);
            }
            JOptionPane.showMessageDialog(this,
                "Account opened successfully!\nAccount Number: " + acc.getAccountNumber() +
                "\nCurrency: " + acc.getCurrency(),
                "Account Created", JOptionPane.INFORMATION_MESSAGE);
            mainFrame.navigateTo("My Accounts");
        } catch (NumberFormatException ex) { show("Invalid number format.", UITheme.DANGER);
        } catch (BankingException ex) { show(ex.getMessage(), UITheme.DANGER); }
    }

    private void show(String msg, Color color) { msgLabel.setForeground(color); msgLabel.setText("<html>" + msg + "</html>"); }
}
