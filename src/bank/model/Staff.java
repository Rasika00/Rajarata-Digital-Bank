package bank.model;




public class Staff extends User {
    private static final long serialVersionUID = 1L;
    private String department;
    private String staffCode;

    public Staff(String userId, String fullName, String email, String phone,
                 String passwordHash, String department, String staffCode) {
        super(userId, fullName, email, phone, passwordHash, UserRole.STAFF);
        this.department = department;
        this.staffCode = staffCode;
    }

    @Override
    public String getDashboardTitle() {
        return "Staff Dashboard - " + getFullName() + " | " + department;
    }

    public String getDepartment() { return department; }
    public String getStaffCode() { return staffCode; }
}
