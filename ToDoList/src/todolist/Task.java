//Task.java
package todolist;

import java.util.Date;

public class Task {
    private String name;
    private Date dueDate;
    private boolean isUrgent;
    private boolean hasReminder;
    private String recurrence;
    //private User taskOwner;
    private Comment comment;


    public Task(String name, Date dueDate, boolean isUrgent, boolean hasReminder, String recurrence) {
        this.name = name;
        this.dueDate = dueDate;
        this.isUrgent = isUrgent;
        this.hasReminder = hasReminder;
        this.recurrence = recurrence;
        //this.taskOwner = taskOwner;
        this.comment = null;
    }

    // Getters and setters for the Task attributes
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isUrgent() {
        return isUrgent;
    }

    public void setUrgent(boolean urgent) {
        this.isUrgent = urgent;
    }
    
    public boolean hasReminder() {
        return hasReminder;
    }
    
    public void setReminder(boolean reminder) {
        this.hasReminder = reminder;
    }
    
    public String getRecurrence() {
        return recurrence;
    }

    public void setRecurrence(String recurrence) {
        this.recurrence = recurrence;
    }
    
  /*  public User getTaskOwner() {
        return taskOwner;
    }*/
    
    public Comment getComment() {
        return comment;
    }
    
    public void setComment(Comment comment){
        this.comment = comment;
    }
    

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", dueDate=" + dueDate +
                ", isUrgent=" + isUrgent +
                '}';
    }
}