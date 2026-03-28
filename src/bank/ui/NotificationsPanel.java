package bank.ui;

import bank.model.*;
import bank.service.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class NotificationsPanel extends JPanel {
    private User user;
    private NotificationService notifService = new NotificationService();

    public NotificationsPanel(User user, MainFrame mainFrame) {
        this.user = user;
        setOpaque(false);
        setLayout(new BorderLayout(0, 16));
        buildUI(mainFrame);
    }

    private void buildUI(MainFrame mainFrame) {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(UIComponents.sectionHeader("Notifications"), BorderLayout.WEST);
        JButton markAllBtn = UIComponents.createButton("Mark All Read", UITheme.TEXT_MUTED, Color.WHITE, 130, 32);
        markAllBtn.addActionListener(e -> {
            notifService.markAllRead(user.getUserId());
            mainFrame.navigateTo("Notifications");
            mainFrame.refreshNotifBadge();
        });
        JPanel btnP = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnP.setOpaque(false);
        btnP.add(markAllBtn);
        header.add(btnP, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        List<Notification> notifications = notifService.getNotifications(user.getUserId());

        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setBackground(UITheme.BG_MAIN);

        if (notifications.isEmpty()) {
            list.add(UIComponents.label("No notifications yet.", UITheme.FONT_BODY, UITheme.TEXT_MUTED));
        } else {
            for (Notification n : notifications) {
                list.add(buildNotifCard(n));
                list.add(Box.createVerticalStrut(8));
            }
        }

        JScrollPane sp = new JScrollPane(list);
        sp.setBorder(null);
        sp.setOpaque(false);
        sp.getViewport().setBackground(UITheme.BG_MAIN);
        add(sp, BorderLayout.CENTER);
    }

    private JPanel buildNotifCard(Notification n) {
        Color accent;
        switch (n.getType()) {
            case SUCCESS: accent = UITheme.SUCCESS; break;
            case WARNING: accent = UITheme.WARNING; break;
            case ALERT:
            case FRAUD_ALERT: accent = UITheme.DANGER; break;
            default: accent = UITheme.INFO; break;
        }
        UIComponents.RoundedPanel card = new UIComponents.RoundedPanel(8, n.isRead() ? UITheme.BG_CARD : new Color(240, 248, 255));
        card.setLayout(new BorderLayout(12, 0));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        card.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        JPanel bar = new JPanel();
        bar.setBackground(accent);
        bar.setPreferredSize(new Dimension(4, 0));
        card.add(bar, BorderLayout.WEST);

        JPanel content = new JPanel(new BorderLayout());
        content.setOpaque(false);
        JLabel msg = new JLabel("<html>" + n.getMessage() + "</html>");
        msg.setFont(n.isRead() ? UITheme.FONT_BODY : UITheme.FONT_SUBHEAD);
        msg.setForeground(UITheme.TEXT_PRIMARY);
        JLabel time = UIComponents.label(n.getTimestamp().toLocalDate() + " " + n.getTimestamp().toLocalTime().toString().substring(0,5), UITheme.FONT_SMALL, UITheme.TEXT_MUTED);
        content.add(msg, BorderLayout.CENTER);
        content.add(time, BorderLayout.EAST);
        card.add(content, BorderLayout.CENTER);
        return card;
    }
}
