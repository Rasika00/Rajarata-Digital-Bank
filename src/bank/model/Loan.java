package bank.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;




public class Loan implements Serializable {
    private static final long serialVersionUID = 1L;

    private String loanId;
    private String customerId;
    private String accountNumber;
    private double principal;
    private double interestRate;
    private int termMonths;
    private double monthlyInstallment;
    private double remainingBalance;
    private LoanStatus status;
    private LocalDate startDate;
    private LocalDate nextDueDate;
    private List<LoanPayment> payments;
    private LoanType loanType;
    private String purpose;

    public Loan(String loanId, String customerId, String accountNumber, double principal,
                double interestRate, int termMonths, LoanType loanType, String purpose) {
        this.loanId = loanId;
        this.customerId = customerId;
        this.accountNumber = accountNumber;
        this.principal = principal;
        this.interestRate = interestRate;
        this.termMonths = termMonths;
        this.loanType = loanType;
        this.purpose = purpose;
        this.status = LoanStatus.PENDING;
        this.startDate = LocalDate.now();
        this.nextDueDate = LocalDate.now().plusMonths(1);
        this.payments = new ArrayList<>();
        this.remainingBalance = principal;
        
        double r = interestRate / 100 / 12;
        this.monthlyInstallment = principal * r * Math.pow(1 + r, termMonths) / (Math.pow(1 + r, termMonths) - 1);
    }

    public void approve() { this.status = LoanStatus.APPROVED; }
    public void reject() { this.status = LoanStatus.REJECTED; }
    public void activate() { this.status = LoanStatus.ACTIVE; }

    public void makePayment(double amount) {
        double payment = Math.min(amount, remainingBalance);
        remainingBalance -= payment;
        payments.add(new LoanPayment(payment, LocalDate.now()));
        nextDueDate = nextDueDate.plusMonths(1);
        if (remainingBalance <= 0) status = LoanStatus.CLOSED;
    }

    public String getLoanId() { return loanId; }
    public String getCustomerId() { return customerId; }
    public String getAccountNumber() { return accountNumber; }
    public double getPrincipal() { return principal; }
    public double getInterestRate() { return interestRate; }
    public int getTermMonths() { return termMonths; }
    public double getMonthlyInstallment() { return monthlyInstallment; }
    public double getRemainingBalance() { return remainingBalance; }
    public LoanStatus getStatus() { return status; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getNextDueDate() { return nextDueDate; }
    public List<LoanPayment> getPayments() { return payments; }
    public LoanType getLoanType() { return loanType; }
    public String getPurpose() { return purpose; }

    public Object[] toTableRow() {
        return new Object[]{
            loanId, loanType, String.format("LKR %.2f", principal),
            String.format("%.1f%%", interestRate), termMonths + " months",
            String.format("LKR %.2f", monthlyInstallment),
            String.format("LKR %.2f", remainingBalance),
            status, nextDueDate.toString()
        };
    }

    
    public static class LoanPayment implements Serializable {
        private static final long serialVersionUID = 1L;
        double amount;
        LocalDate date;
        public LoanPayment(double amount, LocalDate date) { this.amount = amount; this.date = date; }
        public double getAmount() { return amount; }
        public LocalDate getDate() { return date; }
    }
}
