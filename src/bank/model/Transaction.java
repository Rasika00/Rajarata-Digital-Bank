package bank.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;




public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private String transactionId;
    private String accountNumber;
    private TransactionType type;
    private double amount;
    private double balanceAfter;
    private String description;
    private LocalDateTime timestamp;
    private String relatedAccountNumber; 
    private TransactionStatus status;

    public Transaction(String transactionId, String accountNumber, TransactionType type,
                       double amount, double balanceAfter, String description) {
        this.transactionId = transactionId;
        this.accountNumber = accountNumber;
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.description = description;
        this.timestamp = LocalDateTime.now();
        this.status = TransactionStatus.SUCCESS;
    }

    public String getTransactionId() { return transactionId; }
    public String getAccountNumber() { return accountNumber; }
    public TransactionType getType() { return type; }
    public double getAmount() { return amount; }
    public double getBalanceAfter() { return balanceAfter; }
    public String getDescription() { return description; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getRelatedAccountNumber() { return relatedAccountNumber; }
    public void setRelatedAccountNumber(String r) { this.relatedAccountNumber = r; }
    public TransactionStatus getStatus() { return status; }
    public void setStatus(TransactionStatus s) { this.status = s; }

    public String getFormattedTimestamp() { return timestamp.format(FMT); }

    public Object[] toTableRow() {
        return new Object[]{
            transactionId,
            getFormattedTimestamp(),
            type.toString(),
            String.format("LKR %.2f", amount),
            String.format("LKR %.2f", balanceAfter),
            description,
            status.toString()
        };
    }
}
