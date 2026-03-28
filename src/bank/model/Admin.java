package bank.model;

public class Admin extends User {
    private static final long serialVersionUID = 1L;
    private String adminLevel;

    public Admin(String userId, String fullName, String email, String phone,
                 String passwordHash, String adminLevel) {
        super(userId, fullName, email, phone, passwordHash, UserRole.ADMIN);
        this.adminLevel = adminLevel;
    }

    @Override
    public String getDashboardTitle() {
        return "Administrator Dashboard - " + getFullName();
    }

    public String getAdminLevel() { return adminLevel; }
}
