//ToDoList GUI

package todolist;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;
import java.text.ParseException;
import java.util.TimerTask;

public class ToDoList extends JFrame {

    private JTabbedPane tabbedPane;
    private List<Category> categories;
    private JButton deleteCategoryButton; //delete button is non-static

    public ToDoList() {
        categories = new ArrayList<>(); //list of all categories
        setTitle("To-Do List Application");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        tabbedPane = new JTabbedPane();

        // Add initial category tabs
        addCategoryTab(new Category("Urgent"));
        addCategoryTab(new Category("Personal"));
        addCategoryTab(new Category("Work"));

        //add category button listener
        //creates new category with given name
        JButton addCategoryButton = new JButton("Add Category");
        addCategoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String categoryName = JOptionPane.showInputDialog("Enter category name:");
                if (categoryName != null && !categoryName.isEmpty()) {
                    addCategoryTab(new Category(categoryName));
                }
            }
        });
                
        //delete button change listener
        //when pressed calls delete category method
        deleteCategoryButton = new JButton("Delete Category");
        deleteCategoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSelectedCategoryTab();
            }
        });
        
        //add listener to tabbedPane
        //on changes to tabbedPane update delete button visibility
        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                updateDeleteButtonVisibility();
            }
        });
        
        //initial set button visibility
        updateDeleteButtonVisibility();

        //add elements to the GUI
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addCategoryButton);
        buttonPanel.add(deleteCategoryButton);
        
        add(tabbedPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
        
        startReminderChecker();
    }
    
    //this method checks if the conditions for deleting a category are met
    //and if so sets the button to visible.
    //This needs to be called whenever any changes are made in the GUI
    private void updateDeleteButtonVisibility() {
        int selectedTabIndex = tabbedPane.getSelectedIndex();
        
        if (selectedTabIndex != -1) { // Check if there is a selected tab
            Category selectedCategory = categories.get(selectedTabIndex);
            boolean isCategoryEmpty = selectedCategory.getTasks().isEmpty();
            boolean isCategoryNotUrgent = !selectedCategory.getName().equals("Urgent");
            deleteCategoryButton.setVisible(isCategoryEmpty && isCategoryNotUrgent);
        } else {
            deleteCategoryButton.setVisible(false);
        }
    }

    //delete selected category
    private void deleteSelectedCategoryTab() {
        int selectedTabIndex = tabbedPane.getSelectedIndex();
        //delete category
        categories.remove(selectedTabIndex);
        //delete tab in GUI
        tabbedPane.removeTabAt(selectedTabIndex);
    }
            
    //add category, return the panel to be accessed by another method
    private JPanel addCategoryTab(Category category) {
        categories.add(category);
        
        //this panel holds the tasks list panel and the button panel for nice orientation
        JPanel categoryPanelHolder = new JPanel(new BorderLayout());
        
        //this panel holds only tasks
        JPanel categoryPanel = new JPanel();
        //categoryPanel.setLayout(new BoxLayout(categoryPanel, BoxLayout.Y_AXIS));
        
        //this panel holds only one button aligned to center at the bottom border of tab
        JPanel addTaskButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        JButton addTaskButton = new JButton("Add Task");
        addTaskButton.addActionListener(e -> createTaskDialog(categoryPanel, category));

        categoryPanelHolder.add(addTaskButtonPanel, BorderLayout.SOUTH);
        categoryPanelHolder.add(categoryPanel, BorderLayout.CENTER);
        addTaskButtonPanel.add(addTaskButton);
        
        category.setCategoryPanel(categoryPanel);
        
        tabbedPane.addTab(category.getName(), categoryPanelHolder);
        
        return categoryPanel;
    }

    //dialog window for creating tasks
    private void createTaskDialog(JPanel categoryPanel, Category category) {
        JDialog dialog = new JDialog(this, "Add Task", true);
        dialog.setLayout(new BorderLayout());
        
        //all dialog elements
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(6, 2));
        
        inputPanel.add(new JLabel("Task Name:"));
        JTextField taskNameField = new JTextField();
        inputPanel.add(taskNameField);
        
        inputPanel.add(new JLabel("Due Date:"));
        JTextField dueDateField = new JTextField(new SimpleDateFormat("MM/dd/yyyy").format(new Date()));
        inputPanel.add(dueDateField);
        
        inputPanel.add(new JLabel("Urgent Task:"));
        JCheckBox urgentCheckBox = new JCheckBox();
        inputPanel.add(urgentCheckBox);
        
        inputPanel.add(new JLabel("Set Reminder:"));
        JCheckBox reminderCheckBox = new JCheckBox();
        inputPanel.add(reminderCheckBox);
        
        inputPanel.add(new JLabel("Recurrence:"));
        JComboBox<String> recurrenceComboBox = new JComboBox<>(new String[]{"None", "Every day", "Every week", "Every 3 weeks", "Every month", "Every year"});
        inputPanel.add(recurrenceComboBox);
        
        inputPanel.add(new JLabel("Reminder Time (HH:mm):"));
        JTextField reminderTimeField = new JTextField(new SimpleDateFormat("HH:mm").format(new Date()));
        inputPanel.add(reminderTimeField);

        //the add task button that parses data from the fields above
        //and sorts the created task accordingly
        JButton addTaskButton = new JButton("Add Task");
        addTaskButton.addActionListener(e -> {
            String taskName = taskNameField.getText();
            
            //parse the date from the text field
            SimpleDateFormat dueDateFormat = new SimpleDateFormat("MM/dd/yyyy");
            Date dueDate;
            try {
                dueDate = dueDateFormat.parse(dueDateField.getText());
            } catch (ParseException ex) {
                dueDate = new Date(); //revert to today's date
                ex.printStackTrace();
            }
            
            boolean isUrgent = urgentCheckBox.isSelected();
            boolean hasReminder = reminderCheckBox.isSelected();
            String recurrence = recurrenceComboBox.getSelectedItem().toString();
            
            //
            Date reminderTime = null;
            try {
                reminderTime = new SimpleDateFormat("HH:mm").parse(reminderTimeField.getText());
            } catch (ParseException ex) {
                ex.printStackTrace();
            }

            //if task is reminder and urgent, create a reminder task but also put it in urgent category
            if (hasReminder && isUrgent) {
                //combine dueDate and reminderTime into one date datatype
                    //due date
                Calendar dueDateCalendar = Calendar.getInstance();
                dueDateCalendar.setTime(dueDate);
                
                    //reminder time
                Calendar reminderTimeCalendar = Calendar.getInstance();
                reminderTimeCalendar.setTime(reminderTime);
                
                    //combine
                dueDateCalendar.set(Calendar.HOUR_OF_DAY, reminderTimeCalendar.get(Calendar.HOUR_OF_DAY));
                dueDateCalendar.set(Calendar.MINUTE, reminderTimeCalendar.get(Calendar.MINUTE));
                
                Date combinedReminderTime = dueDateCalendar.getTime();
                
                //create reminder task
                ReminderTask newTask = new ReminderTask(taskName, dueDate, isUrgent, hasReminder, recurrence, combinedReminderTime);
                
                //check if category is already urgent to avoid duplicates
                if (category.getName().equals("Urgent")) {
                category.addTask(newTask);
                addTaskToPanel(categoryPanel, newTask, category);
                } else {
                    if (newTask.isUrgent()) {
                    // Assuming 'categories' is accessible and contains the "Urgent" category
                    categories.stream()
                        .filter(category -> category.getName().equals("Urgent"))
                        .findFirst()
                        .ifPresent(urgentCategory -> urgentCategory.addTask(newTask));
                }

                    category.addTask(newTask);
                    addTaskToPanel(categoryPanel, newTask, category);
                    refreshTabs();
                }
            }
            //if task has reminder create reminder task
            else if (hasReminder && reminderTime != null) {
                //combine dueDate and reminderTime into one date datatype
                    //due date
                Calendar dueDateCalendar = Calendar.getInstance();
                dueDateCalendar.setTime(dueDate);
                
                    //reminder time
                Calendar reminderTimeCalendar = Calendar.getInstance();
                reminderTimeCalendar.setTime(reminderTime);
                
                    //combine
                dueDateCalendar.set(Calendar.HOUR_OF_DAY, reminderTimeCalendar.get(Calendar.HOUR_OF_DAY));
                dueDateCalendar.set(Calendar.MINUTE, reminderTimeCalendar.get(Calendar.MINUTE));
                
                Date combinedReminderTime = dueDateCalendar.getTime();
                
                //create reminder task
                ReminderTask newTask = new ReminderTask(taskName, dueDate, isUrgent, hasReminder, recurrence, combinedReminderTime);
                category.addTask(newTask);
                addTaskToPanel(categoryPanel, newTask, category);
            }
            
            //If task is urgent create urgent task
            else if (isUrgent) {
                Task newTask = new UrgentTask(taskName, dueDate, isUrgent, hasReminder, recurrence, true);
                
                //check if category is already urgent to avoid duplicates
                if (category.getName().equals("Urgent")) {
                category.addTask(newTask);
                addTaskToPanel(categoryPanel, newTask, category);
                } else {
                    if (newTask.isUrgent()) {
                    // Assuming 'categories' is accessible and contains the "Urgent" category
                    categories.stream()
                        .filter(category -> category.getName().equals("Urgent"))
                        .findFirst()
                        .ifPresent(urgentCategory -> urgentCategory.addTask(newTask));
                }

                    category.addTask(newTask);
                    addTaskToPanel(categoryPanel, newTask, category);
                    refreshTabs();
                }
            } 
            //create regular task if none of the above
            else if (!hasReminder && !isUrgent) {
                Task newTask = new Task(taskName, dueDate, isUrgent, hasReminder, recurrence);
                category.addTask(newTask);
                addTaskToPanel(categoryPanel, newTask, category);
            }
            
            updateDeleteButtonVisibility();
            dialog.dispose();
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(addTaskButton);

        dialog.add(inputPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setSize(300, 200);
        dialog.setVisible(true);
}

    
    private void addTaskToPanel(JPanel categoryPanel, Task task, Category category) {
    JPanel taskPanel = new JPanel();
    taskPanel.setLayout(new BoxLayout(taskPanel, BoxLayout.Y_AXIS));
    
    //display task
    JLabel taskLabel = new JLabel(task.getName());
    taskPanel.add(taskLabel);
    taskLabel.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked (MouseEvent e) {
            // Custom dialog for comment
            final JDialog commentDialog = new JDialog();
            commentDialog.setTitle("Comment for task:");
            commentDialog.setLayout(new BorderLayout());

            // JTextArea for multiline comment input
            JTextArea commentTextArea = new JTextArea(5, 20); // Adjust rows and columns as needed
            commentTextArea.setLineWrap(true);
            commentTextArea.setWrapStyleWord(true);
            if (task.getComment() != null) {
                commentTextArea.setText(task.getComment().getText());
            }
            commentDialog.add(commentTextArea, BorderLayout.CENTER);

            // Button to confirm comment entry
            JButton confirmButton = new JButton("OK");
            confirmButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String commentText = commentTextArea.getText();
                    if (commentText != null) {
                        task.setComment(new Comment(commentText));
                    }
                    commentDialog.dispose();
                }
            });

            JPanel buttonPanel = new JPanel();
            buttonPanel.add(confirmButton);
            commentDialog.add(buttonPanel, BorderLayout.SOUTH);

            commentDialog.pack();
            commentDialog.setLocationRelativeTo(null); // Center on screen
            commentDialog.setVisible(true);
        }
    });

    //display task due date
    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
    JLabel dueDateLabel = new JLabel("Due: " + dateFormat.format(task.getDueDate())); 
    taskPanel.add(dueDateLabel);

    //display task recurrence if any
    if (task.getRecurrence() != "None") {
        JLabel recurrenceLabel = new JLabel("Repeat: " + task.getRecurrence());
        taskPanel.add(recurrenceLabel);
    }
    
    //check if task is a reminder task
    if (task instanceof ReminderTask) {
        //cast to ReminderTask to access the method
        ReminderTask reminderTask = (ReminderTask) task;
        
        //convert to time only because date was not passed to the class
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        JLabel reminderLabel = new JLabel("Remind at " + timeFormat.format(reminderTask.getReminderTime()));
        taskPanel.add(reminderLabel);
    }
    
    
    //create checkbox now but place it in GUI after "Complete"
    JCheckBox preventRecurrence = new JCheckBox("Stop recurrence");
    //only display if recurrence present
    preventRecurrence.setVisible(!task.getRecurrence().equals("None"));
        
    JCheckBox checkBox = new JCheckBox("Complete");
    checkBox.addActionListener(e -> {
        if (checkBox.isSelected()) {
            //this timer is the GUI timer and is different from the scheduler timer
            Timer timer = new Timer(3000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    categoryPanel.remove(taskPanel);
                    categoryPanel.remove(checkBox);
                    categoryPanel.revalidate();
                    categoryPanel.repaint();
                    
                    //remove from the category list
                    category.removeTask(task);
                    // If the task is urgent, also remove it from the original category if this is the "Urgent" category
                    if(task.isUrgent()) {
                        
                        if (category.getName().equals("Urgent")) {
                            categories.stream()
                                .filter(c -> c.getTasks().contains(task))
                                .forEach(c -> c.removeTask(task));
                        }
                        categories.stream()
                            .filter(c -> c.getName().equals("Urgent"))
                            .findFirst()
                            .ifPresent(urgentCategory -> urgentCategory.removeTask(task));
                    }
                    
                    //if the task is recurrent, check for recurrence and create a new due date
                    if (!task.getRecurrence().equals("None") && !preventRecurrence.isSelected()) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(task.getDueDate());
                        
                        switch (task.getRecurrence()) {
                            case "Every day":
                                calendar.add(Calendar.DAY_OF_MONTH, 1); // Add one day
                                break;
                            case "Every week":
                                calendar.add(Calendar.WEEK_OF_YEAR, 1); // Add one week
                                break;
                            case "Every 3 weeks":
                                calendar.add(Calendar.WEEK_OF_YEAR, 3); // Add three weeks
                                break;
                            case "Every month":
                                calendar.add(Calendar.MONTH, 1); // Add one month
                                break;
                            case "Every year":
                                calendar.add(Calendar.YEAR, 1); // Add one year
                                break;
                        } 
                        
                        Date newDueDate = calendar.getTime();
                        Task newTask = new Task(task.getName(), newDueDate, task.isUrgent(), task.hasReminder(), task.getRecurrence());
                        category.addTask(newTask);
                        addTaskToPanel(categoryPanel, newTask, category);
                    } else {
                        //intentionally blank, don't create anything
                    }
                    

                    refreshTabs();
                    }
            });
            timer.setRepeats(false);
            timer.start();
        }
    });

    categoryPanel.add(taskPanel);
    taskPanel.add(checkBox);
    taskPanel.add(preventRecurrence);
    categoryPanel.revalidate();
    categoryPanel.repaint();
    taskPanel.revalidate();
    taskPanel.repaint();
}
    
    //refresh all tabs when urgent task is added
    //the only difference between this method and addCategoryTabs is 
    //that here the category is not added to the categories list
    //everything else is virtually the same.
    //basically this re-creates the whole GUI for existing categories and tasks
    private void refreshTabs() {
        //remember the last selected index before refreshing
        int selectedIndex = tabbedPane.getSelectedIndex();
        printCategories(); //debug
        tabbedPane.removeAll(); // Remove all tabs
        
        //re-create all category tabs
        for (Category category : categories) {
            //this panel holds the tasks list panel and the button panel for nice orientation
            JPanel categoryPanelHolder = new JPanel(new BorderLayout());

            //this panel holds only tasks
            JPanel categoryPanel = new JPanel();

            //this panel holds only one button aligned to center at the bottom border of tab
            JPanel addTaskButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

            JButton addTaskButton = new JButton("Add Task");
            addTaskButton.addActionListener(e -> createTaskDialog(categoryPanel, category));

            categoryPanelHolder.add(addTaskButtonPanel, BorderLayout.SOUTH);
            categoryPanelHolder.add(categoryPanel, BorderLayout.CENTER);
            addTaskButtonPanel.add(addTaskButton);

            // Add tasks to the category panel
            for (Task task : category.getTasks()) {
                addTaskToPanel(categoryPanel, task, category);
            }

            //re-create tab for each category
            tabbedPane.addTab(category.getName(), categoryPanelHolder);
                             
            tabbedPane.validate();
            tabbedPane.repaint();
        }
        
        // Set the selected index to the last active
        //after re-adding all the tabs
        tabbedPane.setSelectedIndex(selectedIndex); 
    }
    
    //FOR DEBUGGING
    private void printCategories() {
    if (categories.isEmpty()) {
        System.out.println("The categories list is empty.");
    } else {
        System.out.println("Current categories:");
        for (Category category : categories) {
        System.out.println("Category: " + category.getName());
            for (Task task : category.getTasks()) {
                System.out.println("\tTask: " + task.getName() + ", Due Date: " + new SimpleDateFormat("MM/dd/yyyy").format(task.getDueDate()) + ", Urgent: " + task.isUrgent() + ", Reminder: " + task.hasReminder() + ", Recurrence: " + task.getRecurrence());
            }
        }
    }
    }
    
    public void startReminderChecker() {
        java.util.Timer timer = new java.util.Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                for (Category category : categories) {
                    for (Task task : category.getTasks()) {
                        if (task instanceof ReminderTask) {
                            ((ReminderTask) task).checkAndSendReminder();
                        }
                    }
                }
            }
        }, 0, 60000); //checks every minute
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ToDoList());
    }
}

/*
TODO:
- desktop notifications for urgent and setReminder KINDA DONE? idk if need for urgent
- task comments??
- when creating an urgent and reminder task it gets duplicated.
*/