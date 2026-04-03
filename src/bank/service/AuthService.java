package bank.service;

import bank.exception.AuthenticationException;
import bank.model.*;
import bank.util.IDGenerator;
import bank.util.PasswordUtil;
import bank.util.Validator;




public class AuthService {
    private static final int MAX_FAILED_ATTEMPTS = 3;
    private final DataStore store = DataStore.getInstance();

    public User login(String email, String password) throws AuthenticationException {
        if (!Validator.isNotEmpty(email) || !Validator.isNotEmpty(password))
            throw new AuthenticationException("Email and password are required.");

        User user = store.getUserByEmail(email);
        if (user == null) throw new AuthenticationException("No account found with that email.");
        if (!user.isActive()) throw new AuthenticationException("This account has been deactivated.");
        if (user.isLocked()) throw new AuthenticationException("Account locked due to too many failed attempts. Contact support.");

        if (!PasswordUtil.verify(password, user.getPasswordHash())) {
            user.incrementFailedLogins();
            if (user.getFailedLoginAttempts() >= MAX_FAILED_ATTEMPTS) {
                user.setLocked(true);
                store.addAuditLog("ACCOUNT_LOCKED | " + email);
                store.persist();
                throw new AuthenticationException("Account locked after " + MAX_FAILED_ATTEMPTS + " failed attempts.");
            }
            store.addAuditLog("FAILED_LOGIN | " + email + " | Attempt " + user.getFailedLoginAttempts());
            store.persist();
            int remaining = MAX_FAILED_ATTEMPTS - user.getFailedLoginAttempts();
            throw new AuthenticationException("Incorrect password. " + remaining + " attempt(s) remaining.");
        }

        user.resetFailedLogins();
        store.addAuditLog("LOGIN_SUCCESS | " + email + " | Role: " + user.getRole());
        store.persist();
        return user;
    }

    public Customer registerCustomer(String fullName, String email, String phone,
                                     String password, String nationalId, String address)
            throws AuthenticationException {
        
        if (!Validator.isValidEmail(email)) throw new AuthenticationException("Invalid email address.");
        if (!Validator.isValidPhone(phone)) throw new AuthenticationException("Invalid phone number (must start with 0 and be 10 digits).");
        if (!Validator.isNotEmpty(fullName)) throw new AuthenticationException("Full name is required.");
        if (!Validator.isValidNationalId(nationalId)) throw new AuthenticationException("Invalid National ID.");
        if (!PasswordUtil.isStrong(password)) throw new AuthenticationException(
                "Password must be at least 8 characters with uppercase, lowercase, digit, and special character.");
        if (store.getUserByEmail(email) != null) throw new AuthenticationException("Email already registered.");

        String userId = IDGenerator.generateUserId();
        Customer customer = new Customer(userId, fullName, email, phone,
                PasswordUtil.hash(password), nationalId, address);
        store.putUser(customer);
        store.addAuditLog("REGISTER | " + email + " | " + userId);
        store.persist();
        return customer;
    }

    public void changePassword(User user, String oldPassword, String newPassword) throws AuthenticationException {
        if (!PasswordUtil.verify(oldPassword, user.getPasswordHash()))
            throw new AuthenticationException("Current password is incorrect.");
        if (!PasswordUtil.isStrong(newPassword))
            throw new AuthenticationException("New password does not meet strength requirements.");
        user.setPasswordHash(PasswordUtil.hash(newPassword));
        store.persist();
        store.addAuditLog("PASSWORD_CHANGED | " + user.getUserId());
    }
}
