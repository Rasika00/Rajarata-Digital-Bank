package bank.ui;

import bank.exception.BankingException;
import bank.model.*;
import bank.service.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class BillPaymentPanel extends JPanel {
    private User user;
    private MainFrame mainFrame;
    private BillService billService = new BillService();
    private AccountService accountService = new AccountService();
    private JComboBox<String> accountCombo, billTypeCombo;
    private JTextField providerField, refField, amountField;
    private JLabel msgLabel;
    private List<Account> accounts;

    public BillPaymentPanel(User user, MainFrame mainFrame) {
        this.user = user;
        this.mainFrame = mainFrame;
        setOpaque(false);
        setLayout(new BorderLayout(0, 20));
        buildUI();
    }

    private void buildUI() {
        add(UIComponents.sectionHeader("Bill Payments"), BorderLayout.NORTH);
        accounts = accountService.getCustomerAccounts(user.getUserId());

        JPanel split = new JPanel(new GridLayout(1, 2, 20, 0));
        split.setOpaque(false);

        
        UIComponents.RoundedPanel form = new UIComponents.RoundedPanel(UITheme.CARD_RADIUS, UITheme.BG_CARD);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        JLabel formTitle = UIComponents.label("New Bill Payment", UITheme.FONT_HEADING, UITheme.TEXT_PRIMARY);
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        String[] accOpts = accounts.stream().map(a -> a.getAccountNumber() + " - LKR " +
                String.format("%.2f", a.getBalance())).toArray(String[]::new);
        accountCombo = new JComboBox<>(accOpts);
        accountCombo.setFont(UITheme.FONT_BODY);
        accountCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        String[] billTypes = java.util.Arrays.stream(BillType.values()).map(Enum::toString).toArray(String[]::new);
        billTypeCombo = new JComboBox<>(billTypes);
        billTypeCombo.setFont(UITheme.FONT_BODY);
        billTypeCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        billTypeCombo.addActionListener(e -> updateProviderSuggestion());

        providerField = UIComponents.createTextField("e.g. Lanka Electricity Board");
        providerField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        refField = UIComponents.createTextField("Bill reference / account number");
        refField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        amountField = UIComponents.createTextField("Amount (LKR)");
        amountField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        msgLabel = new JLabel(" ");
        msgLabel.setFont(UITheme.FONT_SMALL);

        JButton payBtn = UIComponents.createButton("Pay Bill", UITheme.PRIMARY, Color.WHITE, -1, 44);
        payBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        payBtn.addActionListener(e -> doPayBill());

        form.add(formTitle);
        form.add(Box.createVerticalStrut(16));
        form.add(UIComponents.formRow("Account *", accountCombo));
        form.add(Box.createVerticalStrut(12));
        form.add(UIComponents.formRow("Bill Type *", billTypeCombo));
        form.add(Box.createVerticalStrut(12));
        form.add(UIComponents.formRow("Provider / Company *", providerField));
        form.add(Box.createVerticalStrut(12));
        form.add(UIComponents.formRow("Reference Number *", refField));
        form.add(Box.createVerticalStrut(12));
        form.add(UIComponents.formRow("Amount (LKR) *", amountField));
        form.add(Box.createVerticalStrut(16));
        form.add(payBtn);
        form.add(Box.createVerticalStrut(8));
        form.add(msgLabel);

        
        UIComponents.RoundedPanel history = new UIComponents.RoundedPanel(UITheme.CARD_RADIUS, UITheme.BG_CARD);
        history.setLayout(new BorderLayout(0, 10));
        history.setBorder(BorderFactory.createEmptyBorder(20, 16, 20, 16));
        history.add(UIComponents.sectionHeader("Payment History"), BorderLayout.NORTH);

        List<BillPayment> bills = billService.getCustomerBills(user.getUserId());
        String[] cols = {"Bill ID", "Type", "Provider", "Reference", "Amount", "Date", "Status"};
        Object[][] data = bills.stream().map(BillPayment::toTableRow).toArray(Object[][]::new);
        JTable table = UIComponents.createStyledTable(data, cols);
        history.add(UIComponents.scrollPane(table), BorderLayout.CENTER);

        split.add(form);
        split.add(history);
        add(split, BorderLayout.CENTER);
    }

    private void updateProviderSuggestion() {
        String type = (String) billTypeCombo.getSelectedItem();
        String suggestion = "";
        switch (type) {
            case "ELECTRICITY": suggestion = "Lanka Electricity Board"; break;
            case "WATER": suggestion = "National Water Supply & Drainage Board"; break;
            case "INTERNET": suggestion = "Sri Lanka Telecom (Fiber)"; break;
            case "TELEPHONE": suggestion = "Dialog Axiata PLC"; break;
            case "INSURANCE": suggestion = "Sri Lanka Insurance"; break;
        }
        if (!suggestion.isEmpty()) providerField.setText(suggestion);
    }

    private void doPayBill() {
        int accIdx = accountCombo.getSelectedIndex();
        if (accIdx < 0) { show("Select account.", UITheme.DANGER); return; }
        String provider = providerField.getText().trim();
        String ref = refField.getText().trim();
        String amtStr = amountField.getText().trim();
        if (provider.isEmpty() || ref.isEmpty() || amtStr.isEmpty()) {
            show("All fields are required.", UITheme.DANGER); return;
        }
        try {
            double amount = Double.parseDouble(amtStr);
            BillType type = BillType.valueOf((String) billTypeCombo.getSelectedItem());
            String accNum = accounts.get(accIdx).getAccountNumber();
            billService.payBill(user.getUserId(), accNum, type, provider, ref, amount);
            show("Bill payment successful.", UITheme.SUCCESS);
            amountField.setText("");
            refField.setText("");
            mainFrame.navigateTo("Bill Payments");
        } catch (NumberFormatException ex) { show("Invalid amount.", UITheme.DANGER);
        } catch (BankingException ex) { show(ex.getMessage(), UITheme.DANGER); }
    }

    private void show(String msg, Color color) { msgLabel.setForeground(color); msgLabel.setText(msg); }
}
