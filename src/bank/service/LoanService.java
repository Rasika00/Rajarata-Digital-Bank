package bank.service;

import bank.exception.BankingException;
import bank.model.*;
import bank.util.IDGenerator;

import java.util.List;




public class LoanService {
    private final DataStore store = DataStore.getInstance();
    private final NotificationService notifService = new NotificationService();

    public Loan applyForLoan(String customerId, String accountNumber, double amount,
                             double interestRate, int termMonths, LoanType type, String purpose)
            throws BankingException {
        if (amount <= 0) throw new BankingException("Loan amount must be positive.");
        if (termMonths < 1 || termMonths > 360) throw new BankingException("Invalid loan term.");

        
        long activeLoans = store.getLoansByCustomer(customerId).stream()
                .filter(l -> l.getStatus() == LoanStatus.ACTIVE || l.getStatus() == LoanStatus.APPROVED).count();
        if (activeLoans >= 3) throw new BankingException("Maximum 3 active loans allowed per customer.");

        String loanId = IDGenerator.generateLoanId();
        Loan loan = new Loan(loanId, customerId, accountNumber, amount, interestRate, termMonths, type, purpose);
        store.putLoan(loan);
        store.persist();

        notifService.sendNotification(customerId,
                "Loan application " + loanId + " submitted for LKR " + String.format("%.2f", amount) + ". Pending approval.",
                NotificationType.INFO);
        store.addAuditLog("LOAN_APPLICATION | " + loanId + " | Customer: " + customerId);
        return loan;
    }

    public void approveLoan(String loanId, String staffId) throws BankingException {
        Loan loan = store.getLoan(loanId);
        if (loan == null) throw new BankingException("Loan not found.");
        if (loan.getStatus() != LoanStatus.PENDING) throw new BankingException("Loan is not in pending state.");

        loan.approve();
        loan.activate();

        
        Account account = store.getAccount(loan.getAccountNumber());
        if (account != null) {
            account.deposit(loan.getPrincipal());
            Transaction t = new Transaction(IDGenerator.generateTransactionId(), account.getAccountNumber(),
                    TransactionType.LOAN_DISBURSEMENT, loan.getPrincipal(), account.getBalance(),
                    "Loan disbursement: " + loanId);
            account.addTransaction(t);
        }
        store.persist();

        notifService.sendNotification(loan.getCustomerId(),
                "Loan " + loanId + " approved! LKR " + String.format("%.2f", loan.getPrincipal()) + " disbursed.",
                NotificationType.SUCCESS);
        store.addAuditLog("LOAN_APPROVED | " + loanId + " | Staff: " + staffId);
    }

    public void rejectLoan(String loanId, String staffId) throws BankingException {
        Loan loan = store.getLoan(loanId);
        if (loan == null) throw new BankingException("Loan not found.");
        loan.reject();
        store.persist();
        notifService.sendNotification(loan.getCustomerId(), "Loan application " + loanId + " has been rejected.", NotificationType.ALERT);
        store.addAuditLog("LOAN_REJECTED | " + loanId + " | Staff: " + staffId);
    }

    public void repayLoan(String loanId, double amount) throws BankingException {
        Loan loan = store.getLoan(loanId);
        if (loan == null) throw new BankingException("Loan not found.");
        if (loan.getStatus() != LoanStatus.ACTIVE) throw new BankingException("Loan is not active.");

        Account account = store.getAccount(loan.getAccountNumber());
        if (account == null) throw new BankingException("Account not found.");

        
        double penalty = 0;
        java.time.LocalDate today = java.time.LocalDate.now();
        if (today.isAfter(loan.getNextDueDate())) {
            long daysLate = java.time.temporal.ChronoUnit.DAYS.between(loan.getNextDueDate(), today);
            penalty = loan.getMonthlyInstallment() * 0.02 * ((daysLate / 30) + 1); 
            store.addAuditLog("LATE_PAYMENT_PENALTY | " + loanId + " | Days late: " + daysLate + " | Penalty: " + penalty);
            notifService.sendNotification(loan.getCustomerId(),
                "Late payment penalty of LKR " + String.format("%.2f", penalty) +
                " applied to loan " + loanId + " (" + daysLate + " days overdue).",
                NotificationType.ALERT);
        }

        double totalDue = amount + penalty;
        if (account.getBalance() < totalDue)
            throw new BankingException(String.format(
                "Insufficient funds. Repayment: LKR %.2f + Penalty: LKR %.2f = Total: LKR %.2f",
                amount, penalty, totalDue));

        account.withdraw(totalDue);
        loan.makePayment(amount);

        
        Transaction t = new Transaction(IDGenerator.generateTransactionId(), account.getAccountNumber(),
                TransactionType.LOAN_REPAYMENT, amount, account.getBalance(),
                "Loan repayment: " + loanId);
        account.addTransaction(t);

        
        if (penalty > 0) {
            Transaction penaltyTxn = new Transaction(IDGenerator.generateTransactionId(),
                account.getAccountNumber(), TransactionType.PENALTY, penalty, account.getBalance(),
                "Late payment penalty: " + loanId);
            account.addTransaction(penaltyTxn);
        }

        store.persist();
        notifService.sendNotification(loan.getCustomerId(),
                "Loan repayment of LKR " + String.format("%.2f", amount) + " made. Remaining: LKR " +
                String.format("%.2f", loan.getRemainingBalance()) +
                (penalty > 0 ? " | Late penalty: LKR " + String.format("%.2f", penalty) : ""),
                NotificationType.SUCCESS);
    }

    



    public void sendUpcomingInstallmentAlerts() {
        java.time.LocalDate today = java.time.LocalDate.now();
        for (Loan loan : store.getAllLoans()) {
            if (loan.getStatus() != LoanStatus.ACTIVE) continue;
            long daysUntilDue = java.time.temporal.ChronoUnit.DAYS.between(today, loan.getNextDueDate());
            if (daysUntilDue >= 0 && daysUntilDue <= 3) {
                notifService.sendNotification(loan.getCustomerId(),
                    "Upcoming loan installment: LKR " + String.format("%.2f", loan.getMonthlyInstallment()) +
                    " due on " + loan.getNextDueDate() + " for loan " + loan.getLoanId() +
                    " (" + daysUntilDue + " day(s) remaining).",
                    NotificationType.WARNING);
            } else if (daysUntilDue < 0) {
                notifService.sendNotification(loan.getCustomerId(),
                    "OVERDUE: Loan " + loan.getLoanId() + " installment of LKR " +
                    String.format("%.2f", loan.getMonthlyInstallment()) +
                    " was due on " + loan.getNextDueDate() + ". Late penalties will apply.",
                    NotificationType.ALERT);
            }
        }
    }

    public List<Loan> getCustomerLoans(String customerId) {
        return store.getLoansByCustomer(customerId);
    }
}
