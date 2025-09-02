package todolist;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String name;
    private String email;
    private List<Task> userTasks;

    public User(String name, String email) {
        this.name = name;
        this.email = email;
        this.userTasks = new ArrayList<>();
    }

    // Add a task to the user's list
    public void addTask(Task task) {
        userTasks.add(task);
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public List<Task> getUserTasks() {
        return userTasks;
    }
}