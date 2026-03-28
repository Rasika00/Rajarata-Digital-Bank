package bank.service;

import bank.exception.BankingException;
import bank.model.*;
import bank.util.PasswordUtil;




public class DataInitializer {
    public static void initialize() {
        DataStore store = DataStore.getInstance();

        
        boolean hasAdmin = store.getAllUsers().stream().anyMatch(u -> u.getRole() == UserRole.ADMIN);
        if (!hasAdmin) {
            Admin admin = new Admin("ADMIN001", "System Administrator", "admin@rajaratabank.lk",
                    "0771234567", PasswordUtil.hash("Admin@1234"), "SUPER");
            store.putUser(admin);

            Staff staff = new Staff("STAFF001", "Kamal Perera", "kamal@rajaratabank.lk",
                    "0777654321", PasswordUtil.hash("Staff@1234"), "Loans", "STF001");
            store.putUser(staff);

            
            try {
                AuthService auth = new AuthService();
                Customer customer = auth.registerCustomer("Nimal Silva", "nimal@gmail.com",
                        "0712345678", "Customer@123", "199012345678", "123 Main St, Colombo");

                AccountService accountService = new AccountService();
                
                accountService.openAccount(customer, AccountType.SAVINGS, 5000.0);
                
                accountService.openAccount(customer, AccountType.CHECKING, 2000.0);
                
                accountService.openAccount(customer, AccountType.SAVINGS, 100.0, "USD");
            } catch (BankingException e) {
                System.err.println("Sample data error: " + e.getMessage());
            }

            store.persist();
            System.out.println("Default data initialized.");
            System.out.println("Admin: admin@rajaratabank.lk / Admin@1234");
            System.out.println("Staff: kamal@rajaratabank.lk / Staff@1234");
            System.out.println("Customer: nimal@gmail.com / Customer@123");
        }

        
        new LoanService().sendUpcomingInstallmentAlerts();
        store.persist();
    }
}
