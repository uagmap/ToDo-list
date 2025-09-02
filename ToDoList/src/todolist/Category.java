package todolist;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;

public class Category {
    private String name;
    private  List<Task> tasks;
    private JPanel categoryPanel; //panel associated with this category

    public Category(String name) {
        this.name = name;
        this.tasks = new ArrayList<>();
    }

    // Getter and setter for the category name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Method to add a task to the category
    public void addTask(Task task) {
        tasks.add(task);
    }

    // Getter for the tasks list
    public List<Task> getTasks() {
        return tasks;
    }

    public void removeTask(Task task) {
        tasks.remove(task);
    }
    
    public void setCategoryPanel(JPanel categoryPanel) {
        this.categoryPanel = categoryPanel;
    }
    
    public JPanel getCategoryPanel() {
        return this.categoryPanel;
    }
}