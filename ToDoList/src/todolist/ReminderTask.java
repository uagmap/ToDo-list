package todolist;

import java.awt.*;
import java.util.Date;
import java.awt.TrayIcon.MessageType;
import java.net.URL;

public class ReminderTask extends Task {
    // fields specific to the ReminderTask
    private Date reminderTime; // store the exact time for the reminder
    private boolean reminderSent = false; //flag for marking sent reminders

    public ReminderTask(String name, Date dueDate, boolean isUrgent, boolean hasReminder, String recurrence, Date reminderTime) {
        super(name, dueDate, isUrgent, hasReminder, recurrence);
        this.reminderTime = reminderTime;
    }

    // Getter and setter for reminderTime
    public Date getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(Date reminderTime) {
        this.reminderTime = reminderTime;
    }

    // need to send a desktop notification at specified time
    public void sendDesktopNotification(String message) {
        // Check if SystemTray is supported
        if (SystemTray.isSupported()) {
            SystemTray tray = SystemTray.getSystemTray(); //create tray object
            URL imageUrl = getClass().getResource("/resources/icon.png");
            Image image = Toolkit.getDefaultToolkit().getImage(imageUrl); // Placeholder for an icon image
            TrayIcon trayIcon = new TrayIcon(image, "Reminder");
            trayIcon.setImageAutoSize(true);
            trayIcon.setToolTip("Task Reminder");
            try {
                tray.add(trayIcon);
                trayIcon.displayMessage("Task Reminder", message, MessageType.INFO);
            } catch (AWTException e) {
                System.err.println("TrayIcon could not be added.");
            }
        } else {
            System.err.println("System tray not supported!");
        }
    }

    public void checkAndSendReminder() {
        Date now = new Date();
        // Simple check: if the current time is after or equal to the reminder time, send the notification
        if (now.equals(reminderTime) || now.after(reminderTime) && !reminderSent) {
            sendDesktopNotification("Reminder for task: " + getName());
            reminderSent = true;
        }
    }
}