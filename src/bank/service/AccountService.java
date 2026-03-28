package bank.service;

import bank.exception.BankingException;
import bank.exception.InsufficientFundsException;
import bank.model.*;
import bank.util.IDGenerator;

import java.util.List;




public class AccountService {
    private final DataStore store = DataStore.getInstance();
    private final NotificationService notifService = new NotificationService();

    public Account openAccount(Customer customer, AccountType type, double initialDeposit, Object... extras)
            throws BankingException {
        String accountNumber = IDGenerator.generateAccountNumber();
        Account account = AccountFactory.createAccount(type, accountNumber, customer.getUserId(), initialDeposit, extras);
        customer.addAccountId(accountNumber);
        store.putAccount(account);
        store.putUser(customer);

        
        Transaction t = new Transaction(IDGenerator.generateTransactionId(), accountNumber,
                TransactionType.DEPOSIT, initialDeposit, initialDeposit, "Initial deposit");
        account.addTransaction(t);

        store.addAuditLog("ACCOUNT_OPENED | " + accountNumber + " | Customer: " + customer.getUserId());
        store.persist();
        notifService.sendNotification(customer.getUserId(),
                "Your " + type + " account " + accountNumber + " has been opened successfully.",
                NotificationType.SUCCESS);
        return account;
    }

    public Transaction deposit(String accountNumber, double amount, String description) throws BankingException {
        Account account = getAccountOrThrow(accountNumber);
        account.deposit(amount);
        Transaction t = new Transaction(IDGenerator.generateTransactionId(), accountNumber,
                TransactionType.DEPOSIT, amount, account.getBalance(), description);
        account.addTransaction(t);
        store.persist();

        
        if (account.getBalance() < account.getMinimumBalance() * 1.5) {
            notifService.sendNotification(account.getCustomerId(),
                    "Low balance alert on account " + accountNumber + ". Balance: LKR " + String.format("%.2f", account.getBalance()),
                    NotificationType.WARNING);
        }
        store.addAuditLog("DEPOSIT | " + accountNumber + " | Amount: " + amount);
        return t;
    }

    public Transaction withdraw(String accountNumber, double amount, String description) throws BankingException {
        Account account = getAccountOrThrow(accountNumber);
        if (account.getBalance() < amount)
            throw new InsufficientFundsException(account.getBalance(), amount);
        account.withdraw(amount);
        Transaction t = new Transaction(IDGenerator.generateTransactionId(), accountNumber,
                TransactionType.WITHDRAWAL, amount, account.getBalance(), description);
        account.addTransaction(t);
        store.persist();

        if (account.getBalance() < account.getMinimumBalance() * 1.5 + 500) {
            notifService.sendNotification(account.getCustomerId(),
                    "Low balance warning: LKR " + String.format("%.2f", account.getBalance()) + " in account " + accountNumber,
                    NotificationType.WARNING);
        }
        store.addAuditLog("WITHDRAWAL | " + accountNumber + " | Amount: " + amount);
        return t;
    }

    public void transfer(String fromAccount, String toAccount, double amount) throws BankingException {
        Account from = getAccountOrThrow(fromAccount);
        Account to = getAccountOrThrow(toAccount);

        if (from.getBalance() < amount)
            throw new InsufficientFundsException(from.getBalance(), amount);

        
        String fromCurrency = from.getCurrency();
        String toCurrency   = to.getCurrency();
        double convertedAmount = amount;
        String conversionNote = "";

        if (!fromCurrency.equals(toCurrency)) {
            convertedAmount = CurrencyConverter.convert(amount, fromCurrency, toCurrency);
            conversionNote = String.format(" [Converted: %s %.2f → %s %.2f | Rate: %s]",
                    fromCurrency, amount, toCurrency, convertedAmount,
                    CurrencyConverter.getRateDescription(fromCurrency, toCurrency));
        }

        from.withdraw(amount);
        to.deposit(convertedAmount);

        Transaction tOut = new Transaction(IDGenerator.generateTransactionId(), fromAccount,
                TransactionType.TRANSFER_OUT, amount, from.getBalance(),
                "Transfer to " + toAccount + conversionNote);
        tOut.setRelatedAccountNumber(toAccount);
        from.addTransaction(tOut);

        Transaction tIn = new Transaction(IDGenerator.generateTransactionId(), toAccount,
                TransactionType.TRANSFER_IN, convertedAmount, to.getBalance(),
                "Transfer from " + fromAccount + conversionNote);
        tIn.setRelatedAccountNumber(fromAccount);
        to.addTransaction(tIn);

        store.persist();

        notifService.sendNotification(from.getCustomerId(),
                "Transferred " + fromCurrency + " " + String.format("%.2f", amount) + " to " + toAccount +
                (conversionNote.isEmpty() ? "" : " (" + toCurrency + " " + String.format("%.2f", convertedAmount) + " received)"),
                NotificationType.SUCCESS);
        notifService.sendNotification(to.getCustomerId(),
                "Received " + toCurrency + " " + String.format("%.2f", convertedAmount) + " from " + fromAccount,
                NotificationType.SUCCESS);
        store.addAuditLog("TRANSFER | " + fromAccount + " -> " + toAccount + " | " + fromCurrency + " " + amount +
                (conversionNote.isEmpty() ? "" : " | Converted to " + toCurrency + " " + convertedAmount));
    }

    public void applyMonthlyInterest(String accountNumber) throws BankingException {
        Account account = getAccountOrThrow(accountNumber);
        double interest = account.calculateInterest();
        if (interest > 0) {
            account.deposit(interest);
            Transaction t = new Transaction(IDGenerator.generateTransactionId(), accountNumber,
                    TransactionType.INTEREST, interest, account.getBalance(), "Monthly interest credit");
            account.addTransaction(t);
            store.persist();
        }
    }

    public List<Account> getCustomerAccounts(String customerId) {
        return store.getAccountsByCustomer(customerId);
    }

    private Account getAccountOrThrow(String accountNumber) throws BankingException {
        Account a = store.getAccount(accountNumber);
        if (a == null) throw new BankingException("Account not found: " + accountNumber);
        if (!a.isActive()) throw new BankingException("Account " + accountNumber + " is inactive.");
        return a;
    }
}
