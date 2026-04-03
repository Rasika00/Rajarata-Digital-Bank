package bank.model;

import java.util.ArrayList;
import java.util.List;




public class Customer extends User {
    private static final long serialVersionUID = 1L;

    private String nationalId;
    private String address;
    private List<String> accountIds;

    public Customer(String userId, String fullName, String email, String phone,
                    String passwordHash, String nationalId, String address) {
        super(userId, fullName, email, phone, passwordHash, UserRole.CUSTOMER);
        this.nationalId = nationalId;
        this.address = address;
        this.accountIds = new ArrayList<>();
    }

    @Override
    public String getDashboardTitle() {
        return "Customer Dashboard - Welcome, " + getFullName();
    }

    public String getNationalId() { return nationalId; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public List<String> getAccountIds() { return accountIds; }
    public void addAccountId(String accountId) { accountIds.add(accountId); }
    public void removeAccountId(String accountId) { accountIds.remove(accountId); }
}
