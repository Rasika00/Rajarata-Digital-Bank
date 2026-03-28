package bank.service;

import bank.model.*;

import java.util.List;





public class FraudDetectionService {
    private static final double LARGE_TRANSACTION_THRESHOLD = 500000.0;
    private static final int RAPID_TRANSACTIONS = 5;

    private final DataStore store = DataStore.getInstance();
    private final NotificationService notifService = new NotificationService();

    public boolean checkTransaction(Account account, double amount, TransactionType type) {
        boolean suspicious = false;

        
        if (amount > LARGE_TRANSACTION_THRESHOLD) {
            flagAlert(account, "Large transaction detected: LKR " + String.format("%.2f", amount));
            suspicious = true;
        }

        
        List<Transaction> recent = account.getTransactions();
        if (recent.size() >= RAPID_TRANSACTIONS) {
            long count = recent.stream()
                    .filter(t -> t.getTimestamp().isAfter(java.time.LocalDateTime.now().minusMinutes(5)))
                    .count();
            if (count >= RAPID_TRANSACTIONS) {
                flagAlert(account, "Rapid transactions detected on account " + account.getAccountNumber());
                suspicious = true;
            }
        }

        
        if (type == TransactionType.WITHDRAWAL && account.getBalance() - amount < account.getMinimumBalance()) {
            store.addAuditLog("OVERDRAFT_ATTEMPT | " + account.getAccountNumber() + " | Amount: " + amount);
        }

        return suspicious;
    }

    private void flagAlert(Account account, String message) {
        notifService.sendNotification(account.getCustomerId(), "FRAUD ALERT: " + message, NotificationType.FRAUD_ALERT);

        
        store.getAllUsers().stream()
                .filter(u -> u.getRole() == UserRole.ADMIN || u.getRole() == UserRole.STAFF)
                .forEach(admin -> notifService.sendNotification(admin.getUserId(),
                        "FRAUD ALERT on account " + account.getAccountNumber() + ": " + message,
                        NotificationType.FRAUD_ALERT));
        store.addAuditLog("FRAUD_FLAG | " + account.getAccountNumber() + " | " + message);
    }
}
