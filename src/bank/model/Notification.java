package bank.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Notification implements Serializable {
    private static final long serialVersionUID = 1L;
    private String notificationId;
    private String userId;
    private String message;
    private NotificationType type;
    private boolean read;
    private LocalDateTime timestamp;

    public Notification(String notificationId, String userId, String message, NotificationType type) {
        this.notificationId = notificationId;
        this.userId = userId;
        this.message = message;
        this.type = type;
        this.read = false;
        this.timestamp = LocalDateTime.now();
    }

    public String getNotificationId() { return notificationId; }
    public String getUserId() { return userId; }
    public String getMessage() { return message; }
    public NotificationType getType() { return type; }
    public boolean isRead() { return read; }
    public void markRead() { this.read = true; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
