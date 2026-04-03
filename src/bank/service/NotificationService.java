package bank.service;

import bank.model.Notification;
import bank.model.NotificationType;
import bank.util.IDGenerator;

import java.util.List;

public class NotificationService {
    private final DataStore store = DataStore.getInstance();

    public void sendNotification(String userId, String message, NotificationType type) {
        Notification n = new Notification(IDGenerator.generateNotificationId(), userId, message, type);
        store.putNotification(n);
    }

    public List<Notification> getNotifications(String userId) {
        return store.getNotificationsForUser(userId);
    }

    public long getUnreadCount(String userId) {
        return getNotifications(userId).stream().filter(n -> !n.isRead()).count();
    }

    public void markAllRead(String userId) {
        getNotifications(userId).forEach(Notification::markRead);
    }
}
