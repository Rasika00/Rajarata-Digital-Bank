package bank.ui;

import bank.model.*;
import bank.service.*;

import javax.swing.*;
import java.awt.*;
import java.time.Month;
import java.util.List;




public class StatementPanel extends JPanel {
    private User user;
    private MainFrame mainFrame;
    private AccountService accountService = new AccountService();
    private StatementService statementService = new StatementService();
    private DataStore store = DataStore.getInstance();

    private JComboBox<String> accountCombo;
    private JComboBox<String> monthCombo;
    private JComboBox<Integer> yearCombo;
    private JTextArea statementArea;
    private List<Account> accounts;

    public StatementPanel(User user, MainFrame mainFrame) {
        this.user = user;
        this.mainFrame = mainFrame;
        setOpaque(false);
        setLayout(new BorderLayout(0, 16));
        buildUI();
    }

    private void buildUI() {
        add(UIComponents.sectionHeader("Monthly Account Statements"), BorderLayout.NORTH);

        
        UIComponents.RoundedPanel controls = new UIComponents.RoundedPanel(UITheme.CARD_RADIUS, UITheme.BG_CARD);
        controls.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 10));
        controls.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        
        if (user.getRole() == UserRole.CUSTOMER) {
            accounts = accountService.getCustomerAccounts(user.getUserId());
        } else {
            accounts = new java.util.ArrayList<>(store.getAllAccounts());
        }

        String[] accOpts = accounts.stream()
            .map(a -> a.getAccountNumber() + " - " + a.getAccountType() + " (" + a.getCurrency() + ")")
            .toArray(String[]::new);
        accountCombo = new JComboBox<>(accOpts);
        accountCombo.setFont(UITheme.FONT_BODY);
        accountCombo.setPreferredSize(new Dimension(280, 36));

        String[] months = {"January","February","March","April","May","June",
                           "July","August","September","October","November","December"};
        monthCombo = new JComboBox<>(months);
        monthCombo.setSelectedIndex(java.time.LocalDate.now().getMonthValue() - 1);
        monthCombo.setFont(UITheme.FONT_BODY);
        monthCombo.setPreferredSize(new Dimension(130, 36));

        Integer[] years = new Integer[5];
        int currentYear = java.time.LocalDate.now().getYear();
        for (int i = 0; i < 5; i++) years[i] = currentYear - i;
        yearCombo = new JComboBox<>(years);
        yearCombo.setFont(UITheme.FONT_BODY);
        yearCombo.setPreferredSize(new Dimension(90, 36));

        JButton generateBtn = UIComponents.createButton("Generate Statement", UITheme.PRIMARY, Color.WHITE, 170, 36);
        generateBtn.addActionListener(e -> generateStatement());

        JButton saveBtn = UIComponents.createButton("Save to File", UITheme.ACCENT, Color.WHITE, 120, 36);
        saveBtn.addActionListener(e -> saveStatement());

        JLabel accLbl = UIComponents.label("Account:", UITheme.FONT_BODY, UITheme.TEXT_MUTED);
        JLabel monthLbl = UIComponents.label("Month:", UITheme.FONT_BODY, UITheme.TEXT_MUTED);
        JLabel yearLbl = UIComponents.label("Year:", UITheme.FONT_BODY, UITheme.TEXT_MUTED);

        controls.add(accLbl);
        controls.add(accountCombo);
        controls.add(monthLbl);
        controls.add(monthCombo);
        controls.add(yearLbl);
        controls.add(yearCombo);
        controls.add(generateBtn);
        controls.add(saveBtn);

        add(controls, BorderLayout.NORTH);

        
        statementArea = new JTextArea();
        statementArea.setFont(UITheme.FONT_MONO);
        statementArea.setEditable(false);
        statementArea.setBackground(new Color(252, 253, 255));
        statementArea.setForeground(UITheme.TEXT_PRIMARY);
        statementArea.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        statementArea.setText(
            "Select an account, month, and year above, then click 'Generate Statement'\n" +
            "to view a comprehensive monthly statement with all transaction details,\n" +
            "interest earned, and account summary."
        );

        JScrollPane sp = new JScrollPane(statementArea);
        sp.setBorder(new UIComponents.RoundBorder(UITheme.BORDER, 8));
        sp.getVerticalScrollBar().setUnitIncrement(16);
        add(sp, BorderLayout.CENTER);
    }

    private void generateStatement() {
        int accIdx = accountCombo.getSelectedIndex();
        if (accIdx < 0 || accounts.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select an account.");
            return;
        }

        Account account = accounts.get(accIdx);
        int monthIdx = monthCombo.getSelectedIndex() + 1; 
        int year = (Integer) yearCombo.getSelectedItem();
        Month month = Month.of(monthIdx);

        
        User customer = store.getUser(account.getCustomerId());
        if (customer == null) customer = user;

        String statement = statementService.generateMonthlyStatement(account, customer, year, month);
        statementArea.setText(statement);
        statementArea.setCaretPosition(0);
    }

    private void saveStatement() {
        String text = statementArea.getText();
        if (text.startsWith("Select an account")) {
            JOptionPane.showMessageDialog(this, "Please generate a statement first.");
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File("statement.txt"));
        int result = chooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try (java.io.PrintWriter pw = new java.io.PrintWriter(chooser.getSelectedFile())) {
                pw.print(text);
                JOptionPane.showMessageDialog(this, "Statement saved successfully!", "Saved", JOptionPane.INFORMATION_MESSAGE);
            } catch (java.io.IOException ex) {
                JOptionPane.showMessageDialog(this, "Error saving: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
