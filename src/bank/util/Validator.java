package bank.util;

public class Validator {
    public static boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }
    public static boolean isValidPhone(String phone) {
        return phone != null && phone.matches("^0[0-9]{9}$");
    }
    public static boolean isValidNationalId(String id) {
        return id != null && (id.matches("^[0-9]{9}[VvXx]$") || id.matches("^[0-9]{12}$"));
    }
    public static boolean isPositiveAmount(double amount) { return amount > 0; }
    public static boolean isNotEmpty(String s) { return s != null && !s.trim().isEmpty(); }
}
