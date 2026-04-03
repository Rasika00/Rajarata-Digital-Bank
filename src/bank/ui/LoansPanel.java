package bank.ui;

import bank.exception.BankingException;
import bank.model.*;
import bank.service.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;

public class LoansPanel extends JPanel {
    private User user;
    private MainFrame mainFrame;
    private LoanService loanService = new LoanService();
    private AccountService accountService = new AccountService();
    private DataStore store = DataStore.getInstance();

    public LoansPanel(User user, MainFrame mainFrame) {
        this.user = user;
        this.mainFrame = mainFrame;
        setOpaque(false);
        setLayout(new BorderLayout(0, 16));
        buildUI();
    }

    private void buildUI() {
        add(UIComponents.sectionHeader(user.getRole() == UserRole.CUSTOMER ? "My Loans" : "Loan Management"), BorderLayout.NORTH);

        List<Loan> loans = user.getRole() == UserRole.CUSTOMER
            ? loanService.getCustomerLoans(user.getUserId())
            : new ArrayList<>(store.getAllLoans());

        String[] cols = {"Loan ID", "Type", "Principal", "Rate", "Term", "Monthly EMI", "Remaining", "Status", "Next Due"};
        Object[][] data = loans.stream().map(Loan::toTableRow).toArray(Object[][]::new);

        JTable table = UIComponents.createStyledTable(data, cols);

        JPanel center = new JPanel(new BorderLayout(0, 10));
        center.setOpaque(false);
        center.add(UIComponents.scrollPane(table), BorderLayout.CENTER);

        
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actions.setOpaque(false);

        if (user.getRole() == UserRole.CUSTOMER) {
            JButton applyBtn = UIComponents.createButton("Apply for Loan", UITheme.PRIMARY, Color.WHITE, 160, 36);
            applyBtn.addActionListener(e -> showApplyDialog());
            actions.add(applyBtn);

            JButton repayBtn = UIComponents.createButton("Make Repayment", UITheme.SUCCESS, Color.WHITE, 160, 36);
            repayBtn.addActionListener(e -> showRepayDialog(loans, table));
            actions.add(repayBtn);
        } else {
            JButton approveBtn = UIComponents.createButton("Approve", UITheme.SUCCESS, Color.WHITE, 100, 36);
            approveBtn.addActionListener(e -> handleLoanAction(loans, table, true));
            actions.add(approveBtn);

            JButton rejectBtn = UIComponents.createButton("Reject", UITheme.DANGER, Color.WHITE, 100, 36);
            rejectBtn.addActionListener(e -> handleLoanAction(loans, table, false));
            actions.add(rejectBtn);
        }

        center.add(actions, BorderLayout.SOUTH);
        add(center, BorderLayout.CENTER);
    }

    private void showApplyDialog() {
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Apply for Loan", true);
        dlg.setSize(420, 520);
        dlg.setLocationRelativeTo(this);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));
        form.setBackground(Color.WHITE);

        List<Account> accounts = accountService.getCustomerAccounts(user.getUserId());
        String[] accOpts = accounts.stream().map(a -> a.getAccountNumber() + " - " + a.getAccountType()).toArray(String[]::new);
        JComboBox<String> accCombo = new JComboBox<>(accOpts);

        JComboBox<LoanType> typeCombo = new JComboBox<>(LoanType.values());
        JTextField amtField = UIComponents.createTextField("e.g. 100000");
        JTextField rateField = UIComponents.createTextField("e.g. 12.5");
        JTextField termField = UIComponents.createTextField("e.g. 24 (months)");
        JTextField purposeField = UIComponents.createTextField("Purpose of loan");
        JLabel msgLbl = new JLabel(" ");
        msgLbl.setFont(UITheme.FONT_SMALL);
        msgLbl.setForeground(UITheme.DANGER);

        form.add(UIComponents.formRow("Account *", accCombo));
        form.add(Box.createVerticalStrut(10));
        form.add(UIComponents.formRow("Loan Type *", typeCombo));
        form.add(Box.createVerticalStrut(10));
        form.add(UIComponents.formRow("Amount (LKR) *", amtField));
        form.add(Box.createVerticalStrut(10));
        form.add(UIComponents.formRow("Interest Rate (% p.a.) *", rateField));
        form.add(Box.createVerticalStrut(10));
        form.add(UIComponents.formRow("Term (months) *", termField));
        form.add(Box.createVerticalStrut(10));
        form.add(UIComponents.formRow("Purpose *", purposeField));
        form.add(Box.createVerticalStrut(8));
        form.add(msgLbl);

        JButton submitBtn = UIComponents.createButton("Submit Application", UITheme.PRIMARY, Color.WHITE, -1, 40);
        submitBtn.addActionListener(e -> {
            try {
                int accIdx = accCombo.getSelectedIndex();
                if (accIdx < 0 || accounts.isEmpty()) { msgLbl.setText("Select account."); return; }
                double amt = Double.parseDouble(amtField.getText().trim());
                double rate = Double.parseDouble(rateField.getText().trim());
                int term = Integer.parseInt(termField.getText().trim());
                String purpose = purposeField.getText().trim();
                if (purpose.isEmpty()) { msgLbl.setText("Purpose is required."); return; }
                loanService.applyForLoan(user.getUserId(), accounts.get(accIdx).getAccountNumber(),
                        amt, rate, term, (LoanType) typeCombo.getSelectedItem(), purpose);
                JOptionPane.showMessageDialog(dlg, "Loan application submitted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dlg.dispose();
                mainFrame.navigateTo("Loans");
            } catch (NumberFormatException ex) { msgLbl.setText("Invalid number format.");
            } catch (BankingException ex) { msgLbl.setText(ex.getMessage()); }
        });
        form.add(Box.createVerticalStrut(12));
        form.add(submitBtn);

        JScrollPane scrollPane = UIComponents.scrollPane(form);
        dlg.add(scrollPane);
        dlg.setVisible(true);
    }

    private void showRepayDialog(List<Loan> loans, JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Please select a loan to repay."); return; }
        Loan loan = loans.get(row);
        if (loan.getStatus() != LoanStatus.ACTIVE) {
            JOptionPane.showMessageDialog(this, "Only active loans can be repaid."); return;
        }

        String input = JOptionPane.showInputDialog(this,
            "Loan: " + loan.getLoanId() + "\nEMI: LKR " + String.format("%.2f", loan.getMonthlyInstallment()) +
            "\nRemaining: LKR " + String.format("%.2f", loan.getRemainingBalance()) + "\n\nEnter repayment amount:");
        if (input == null || input.trim().isEmpty()) return;
        try {
            double amt = Double.parseDouble(input.trim());
            loanService.repayLoan(loan.getLoanId(), amt);
            JOptionPane.showMessageDialog(this, "Repayment successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            mainFrame.navigateTo("Loans");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleLoanAction(List<Loan> loans, JTable table, boolean approve) {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Please select a loan."); return; }
        Loan loan = loans.get(row);
        try {
            if (approve) loanService.approveLoan(loan.getLoanId(), user.getUserId());
            else loanService.rejectLoan(loan.getLoanId(), user.getUserId());
            JOptionPane.showMessageDialog(this, "Loan " + (approve ? "approved" : "rejected") + " successfully.");
            mainFrame.navigateTo("Loan Approvals");
        } catch (BankingException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
