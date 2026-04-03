package bank.service;

import bank.exception.BankingException;
import bank.model.*;
import bank.util.FileHandler;
import bank.util.PasswordUtil;




public class DataInitializer {
    public static void initialize() {
        DataStore store = DataStore.getInstance();
        System.out.println("Data directory: " + FileHandler.dataDirectory().getAbsolutePath());

        
        boolean hasAdmin = store.getAllUsers().stream().anyMatch(u -> u.getRole() == UserRole.ADMIN);
        if (!hasAdmin) {
            Admin admin = new Admin("ADMIN001", "System Administrator", "admin@rajaratabank.lk",
                    "0771234567", PasswordUtil.hash("Admin@1234"), "SUPER");
            store.putUser(admin);

            Staff staff = new Staff("STAFF001", "Kamal Perera", "kamal@rajaratabank.lk",
                    "0777654321", PasswordUtil.hash("Staff@1234"), "Loans", "STF001");
            store.putUser(staff);

            store.persist();
            System.out.println("Default data initialized.");
            System.out.println("Admin: admin@rajaratabank.lk / Admin@1234");
            System.out.println("Staff: kamal@rajaratabank.lk / Staff@1234");
        }

        
        new LoanService().sendUpcomingInstallmentAlerts();
        store.persist();
    }
}
