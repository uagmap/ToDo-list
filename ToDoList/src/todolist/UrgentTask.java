package todolist;

import java.util.Date;

public class UrgentTask extends Task {
   
    private boolean requiresImmediateNotification;

    public UrgentTask(String name, Date dueDate, boolean isUrgent, boolean hasReminder, String recurrence, boolean requiresImmediateNotification) {
        super(name, dueDate, isUrgent, hasReminder, recurrence);
        this.requiresImmediateNotification = requiresImmediateNotification;
    }

    
    public void sendImmediateNotification() {
        if (requiresImmediateNotification) {
            System.out.println("Sending immediate notification for urgent task: " + getName());
            // urgent task was supposed to be sending recurrent notification reminders 
            // but I thought this functionality is not really necessary
            // so urgent task is left a separate class for future implementation
        }
    }

    public boolean requiresImmediateNotification() {
        return requiresImmediateNotification;
    }

    public void setRequiresImmediateNotification(boolean requiresImmediateNotification) {
        this.requiresImmediateNotification = requiresImmediateNotification;
    }
}

//Urgent task is a separate class for future implemeentation and flexibility