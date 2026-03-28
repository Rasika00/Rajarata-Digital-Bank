package bank.model;

import java.io.Serializable;
import java.time.LocalDateTime;





public abstract class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private String userId;
    private String fullName;
    private String email;
    private String phone;
    private String passwordHash;
    private UserRole role;
    private boolean active;
    private LocalDateTime createdAt;
    private int failedLoginAttempts;
    private boolean locked;

    public User(String userId, String fullName, String email, String phone,
                String passwordHash, UserRole role) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.passwordHash = passwordHash;
        this.role = role;
        this.active = true;
        this.createdAt = LocalDateTime.now();
        this.failedLoginAttempts = 0;
        this.locked = false;
    }

    
    public abstract String getDashboardTitle();

    
    public String getUserId() { return userId; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public UserRole getRole() { return role; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public int getFailedLoginAttempts() { return failedLoginAttempts; }
    public void incrementFailedLogins() { this.failedLoginAttempts++; }
    public void resetFailedLogins() { this.failedLoginAttempts = 0; }
    public boolean isLocked() { return locked; }
    public void setLocked(boolean locked) { this.locked = locked; }

    @Override
    public String toString() {
        return fullName + " (" + userId + ") - " + role;
    }
}
