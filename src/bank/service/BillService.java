package bank.service;

import bank.exception.BankingException;
import bank.model.*;
import bank.util.IDGenerator;

import java.util.List;

public class BillService {
    private final DataStore store = DataStore.getInstance();
    private final NotificationService notifService = new NotificationService();

    public BillPayment payBill(String customerId, String accountNumber, BillType type,
                               String provider, String referenceNumber, double amount)
            throws BankingException {
        Account account = store.getAccount(accountNumber);
        if (account == null) throw new BankingException("Account not found.");
        if (account.getBalance() < amount) throw new BankingException("Insufficient funds.");

        account.withdraw(amount);

        String billId = IDGenerator.generateBillId();
        BillPayment bill = new BillPayment(billId, customerId, accountNumber, type, provider, referenceNumber, amount);
        bill.setStatus(BillStatus.PAID);

        Transaction t = new Transaction(IDGenerator.generateTransactionId(), accountNumber,
                TransactionType.BILL_PAYMENT, amount, account.getBalance(),
                type + " bill payment - " + provider + " | Ref: " + referenceNumber);
        account.addTransaction(t);

        store.putBill(bill);
        store.persist();
        notifService.sendNotification(customerId,
                type + " bill of LKR " + String.format("%.2f", amount) + " paid to " + provider, NotificationType.SUCCESS);
        store.addAuditLog("BILL_PAYMENT | " + billId + " | Account: " + accountNumber);
        return bill;
    }

    public List<BillPayment> getCustomerBills(String customerId) {
        return store.getBillsByCustomer(customerId);
    }
}
