package bank.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;




public abstract class Account implements Serializable {
    private static final long serialVersionUID = 1L;

    private String accountNumber;
    private String customerId;
    private double balance;
    private String currency;
    private boolean active;
    private LocalDateTime createdAt;
    private List<Transaction> transactions;

    public Account(String accountNumber, String customerId, double initialBalance, String currency) {
        this.accountNumber = accountNumber;
        this.customerId = customerId;
        this.balance = initialBalance;
        this.currency = currency;
        this.active = true;
        this.createdAt = LocalDateTime.now();
        this.transactions = new ArrayList<>();
    }

    
    public abstract AccountType getAccountType();
    public abstract double calculateInterest();
    public abstract double getWithdrawalLimit();
    public abstract double getMinimumBalance();
    public abstract String getAccountDescription();

    
    public void deposit(double amount) throws bank.exception.BankingException {
        if (amount <= 0) throw new bank.exception.BankingException("Deposit amount must be positive.");
        this.balance += amount;
    }

    
    public void withdraw(double amount) throws bank.exception.BankingException {
        if (amount <= 0) throw new bank.exception.BankingException("Withdrawal amount must be positive.");
        if (amount > getWithdrawalLimit()) throw new bank.exception.BankingException("Exceeds withdrawal limit of " + getWithdrawalLimit());
        if ((balance - amount) < getMinimumBalance()) throw new bank.exception.BankingException("Balance would fall below minimum balance of " + getMinimumBalance());
        this.balance -= amount;
    }

    public void addTransaction(Transaction t) { transactions.add(t); }
    public List<Transaction> getTransactions() { return transactions; }

    
    public String getAccountNumber() { return accountNumber; }
    public String getCustomerId() { return customerId; }
    public double getBalance() { return balance; }
    protected void setBalance(double balance) { this.balance = balance; }
    public String getCurrency() { return currency; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    @Override
    public String toString() {
        return getAccountType() + " - " + accountNumber + " | Balance: " + String.format("%.2f", balance) + " " + currency;
    }
}
