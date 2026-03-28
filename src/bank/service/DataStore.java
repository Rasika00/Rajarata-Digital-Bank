package bank.service;

import bank.model.*;
import bank.util.FileHandler;
import java.util.*;





public class DataStore {
    private static DataStore instance;

    private Map<String, User> users;
    private Map<String, Account> accounts;
    private Map<String, Loan> loans;
    private Map<String, BillPayment> billPayments;
    private Map<String, Notification> notifications;
    private List<String> auditLog;

    @SuppressWarnings("unchecked")
    private DataStore() {
        
        Map<String, User> loadedUsers = FileHandler.loadObject("users.dat");
        users = loadedUsers != null ? loadedUsers : new HashMap<>();

        Map<String, Account> loadedAccounts = FileHandler.loadObject("accounts.dat");
        accounts = loadedAccounts != null ? loadedAccounts : new HashMap<>();

        Map<String, Loan> loadedLoans = FileHandler.loadObject("loans.dat");
        loans = loadedLoans != null ? loadedLoans : new HashMap<>();

        Map<String, BillPayment> loadedBills = FileHandler.loadObject("bills.dat");
        billPayments = loadedBills != null ? loadedBills : new HashMap<>();

        Map<String, Notification> loadedNotif = FileHandler.loadObject("notifications.dat");
        notifications = loadedNotif != null ? loadedNotif : new HashMap<>();

        List<String> loadedLog = FileHandler.loadObject("auditlog.dat");
        auditLog = loadedLog != null ? loadedLog : new ArrayList<>();
    }

    public static DataStore getInstance() {
        if (instance == null) instance = new DataStore();
        return instance;
    }

    public void persist() {
        FileHandler.saveObject("users.dat", users);
        FileHandler.saveObject("accounts.dat", accounts);
        FileHandler.saveObject("loans.dat", loans);
        FileHandler.saveObject("bills.dat", billPayments);
        FileHandler.saveObject("notifications.dat", notifications);
        FileHandler.saveObject("auditlog.dat", auditLog);
    }

    
    public void putUser(User u) { users.put(u.getUserId(), u); }
    public User getUser(String id) { return users.get(id); }
    public User getUserByEmail(String email) {
        return users.values().stream().filter(u -> u.getEmail().equalsIgnoreCase(email)).findFirst().orElse(null);
    }
    public Collection<User> getAllUsers() { return users.values(); }

    
    public void putAccount(Account a) { accounts.put(a.getAccountNumber(), a); }
    public Account getAccount(String num) { return accounts.get(num); }
    public Collection<Account> getAllAccounts() { return accounts.values(); }
    public List<Account> getAccountsByCustomer(String customerId) {
        List<Account> list = new ArrayList<>();
        for (Account a : accounts.values()) if (a.getCustomerId().equals(customerId)) list.add(a);
        return list;
    }

    
    public void putLoan(Loan l) { loans.put(l.getLoanId(), l); }
    public Loan getLoan(String id) { return loans.get(id); }
    public Collection<Loan> getAllLoans() { return loans.values(); }
    public List<Loan> getLoansByCustomer(String customerId) {
        List<Loan> list = new ArrayList<>();
        for (Loan l : loans.values()) if (l.getCustomerId().equals(customerId)) list.add(l);
        return list;
    }

    
    public void putBill(BillPayment b) { billPayments.put(b.getBillId(), b); }
    public Collection<BillPayment> getAllBills() { return billPayments.values(); }
    public List<BillPayment> getBillsByCustomer(String customerId) {
        List<BillPayment> list = new ArrayList<>();
        for (BillPayment b : billPayments.values()) if (b.getCustomerId().equals(customerId)) list.add(b);
        return list;
    }

    
    public void putNotification(Notification n) { notifications.put(n.getNotificationId(), n); }
    public List<Notification> getNotificationsForUser(String userId) {
        List<Notification> list = new ArrayList<>();
        for (Notification n : notifications.values()) if (n.getUserId().equals(userId)) list.add(n);
        list.sort((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()));
        return list;
    }

    
    public void addAuditLog(String entry) {
        auditLog.add(java.time.LocalDateTime.now() + " | " + entry);
        if (auditLog.size() > 10000) auditLog.remove(0);
    }
    public List<String> getAuditLog() { return auditLog; }
}
