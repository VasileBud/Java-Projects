package GraphicalUserInterface;

import BusinessLogic.TasksManagement;
import BusinessLogic.Utility;
import DataAccess.SerializationOperations;
import DataModel.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GUI extends JFrame implements ActionListener {
    private static final long serialVersionUID = 1L;

    private JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
    private JPanel inputPanel = new JPanel(new GridBagLayout());
    private JPanel taskPanel = new JPanel(new GridBagLayout());
    private JPanel tablePanel = new JPanel(new BorderLayout());
    private JPanel statusPanel = new JPanel();

    private JTextField employeeIdField = new JTextField(10);
    private JTextField employeeNameField = new JTextField(10);
    private JButton addEmployeeButton = new JButton("Add Employee");

    private JTextField employeeIdField2 = new JTextField(10);
    private JTextField taskIdField = new JTextField(10);
    private JComboBox<String> statusComboBox;
    private JCheckBox subTaskCheckBox;
    private JTextField startHourField = new JTextField(5);
    private JTextField endHourField = new JTextField(5);
    private JComboBox<String> taskTypeComboBox;
    private JButton addTaskButton = new JButton("Assign Task");
    private JButton modifyTaskButton = new JButton("Modify Task");
    private JButton workDurationButton = new JButton("Work Duration");
    private JButton taskStatsButton = new JButton("Task Stats");
    private JButton emplyeeWithMoreThen40HoursButton = new JButton("More Then 40 Hours");
    private JButton removeTaskButton = new JButton("Remove Task");
    private JTextField employeeIdFieldAction, taskIdFieldAction;
    private DefaultListModel<String> statsListModel;
    private JList<String> statsList;

    private JTable taskTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel = new JLabel("Status: Ready", SwingConstants.CENTER);

    private TasksManagement tasksManagement;
    private Utility utility = new Utility();

    public GUI(String name) {
        super(name);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(900, 750);
        this.setLocationRelativeTo(null);

        tasksManagement = SerializationOperations.loadTasksManagement();

        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        setupInputPanel();
        setupTaskPanel();
        setupTablePanel();
        setupStatusPanel();

        mainPanel.add(inputPanel);
        mainPanel.add(taskPanel);
        mainPanel.add(tablePanel);
        mainPanel.add(statusPanel);

        this.add(mainPanel);
        updateTable();
        this.setVisible(true);
    }

    public void setupInputPanel() {
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.HORIZONTAL;

        inputPanel.setBorder(BorderFactory.createTitledBorder("Employee Management"));

        c.gridx = 0;
        c.gridy = 0;
        inputPanel.add(new JLabel("Employee ID:"), c);
        c.gridx = 1;
        inputPanel.add(employeeIdField, c);

        c.gridx = 0;
        c.gridy = 1;
        inputPanel.add(new JLabel("Employee Name:"), c);
        c.gridx = 1;
        inputPanel.add(employeeNameField, c);

        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 2;
        inputPanel.add(addEmployeeButton, c);

        addEmployeeButton.addActionListener(this);
    }

    public void setupTaskPanel() {
        taskPanel.setPreferredSize(new Dimension(900, 300));
        taskPanel.setBorder(BorderFactory.createTitledBorder("Task Management"));
        taskPanel.setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.LEFT);

        tabbedPane.addTab("Assign Task", createAssignTaskPanel());
        tabbedPane.addTab("Task Actions", createTaskActionsPanel());
        tabbedPane.addTab("Statistics", createTaskStatsPanel());

        taskPanel.add(tabbedPane, BorderLayout.CENTER);
    }

    public JPanel createAssignTaskPanel() {
        JPanel assignTaskPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0;
        c.gridy = 0;
        assignTaskPanel.add(new JLabel("Employee ID:"), c);
        c.gridx = 1;
        employeeIdField2 = new JTextField(10);
        assignTaskPanel.add(employeeIdField2, c);

        c.gridx = 0;
        c.gridy = 1;
        assignTaskPanel.add(new JLabel("Task ID:"), c);
        c.gridx = 1;
        taskIdField = new JTextField(10);
        assignTaskPanel.add(taskIdField, c);

        c.gridx = 0;
        c.gridy = 2;
        assignTaskPanel.add(new JLabel("Status:"), c);
        c.gridx = 1;
        statusComboBox = new JComboBox<>(new String[]{"Uncompleted", "Completed"});
        assignTaskPanel.add(statusComboBox, c);

        c.gridx = 0;
        c.gridy = 3;
        assignTaskPanel.add(new JLabel("Start Hour:"), c);
        c.gridx = 1;
        startHourField = new JTextField(10);
        assignTaskPanel.add(startHourField, c);

        c.gridx = 0;
        c.gridy = 4;
        assignTaskPanel.add(new JLabel("End Hour:"), c);
        c.gridx = 1;
        endHourField = new JTextField(10);
        assignTaskPanel.add(endHourField, c);

        c.gridx = 0;
        c.gridy = 5;
        assignTaskPanel.add(new JLabel("Task Type:"), c);
        c.gridx = 1;
        taskTypeComboBox = new JComboBox<>(new String[]{"Simple", "Complex"});
        assignTaskPanel.add(taskTypeComboBox, c);

        c.gridx = 0;
        c.gridy = 6;
        assignTaskPanel.add(new JLabel("Sub Task:"), c);
        c.gridx = 1;
        subTaskCheckBox = new JCheckBox();
        assignTaskPanel.add(subTaskCheckBox, c);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonPanel.add(addTaskButton);

        c.gridx = 0;
        c.gridy = 7;
        c.gridwidth = 2;
        assignTaskPanel.add(buttonPanel, c);

        addTaskButton.addActionListener(this);

        return assignTaskPanel;
    }

    public JPanel createTaskActionsPanel() {
        JPanel taskActionsPanel = new JPanel(new BorderLayout());
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0;
        c.gridy = 0;
        inputPanel.add(new JLabel("Employee ID:"), c);
        c.gridx = 1;
        employeeIdFieldAction = new JTextField(10);
        inputPanel.add(employeeIdFieldAction, c);

        c.gridx = 0;
        c.gridy = 1;
        inputPanel.add(new JLabel("Task ID:"), c);
        c.gridx = 1;
        taskIdFieldAction = new JTextField(10);
        inputPanel.add(taskIdFieldAction, c);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonPanel.add(modifyTaskButton);
        buttonPanel.add(workDurationButton);
        buttonPanel.add(removeTaskButton);

        modifyTaskButton.addActionListener(this);
        workDurationButton.addActionListener(this);
        removeTaskButton.addActionListener(this);

        taskActionsPanel.add(inputPanel, BorderLayout.NORTH);
        taskActionsPanel.add(buttonPanel, BorderLayout.CENTER);

        return taskActionsPanel;
    }

    public JPanel createTaskStatsPanel() {
        JPanel taskStatsPanel = new JPanel(new BorderLayout());

        statsListModel = new DefaultListModel<>();
        statsList = new JList<>(statsListModel);
        JScrollPane scrollPane = new JScrollPane(statsList);

        scrollPane.setPreferredSize(new Dimension(100, 100));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonPanel.add(taskStatsButton);
        buttonPanel.add(emplyeeWithMoreThen40HoursButton);

        taskStatsButton.addActionListener(this);
        emplyeeWithMoreThen40HoursButton.addActionListener(this);

        taskStatsPanel.add(buttonPanel, BorderLayout.NORTH);
        taskStatsPanel.add(scrollPane, BorderLayout.CENTER);

        return taskStatsPanel;
    }

    public void setupTablePanel() {
        String[] columns = {"Employee ID", "Name", "Task ID", "Type", "Duration", "Status"};
        tableModel = new DefaultTableModel(columns, 0);
        taskTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(taskTable);

        taskTable.getColumnModel().getColumn(0).setPreferredWidth(80);  // Employee ID
        taskTable.getColumnModel().getColumn(1).setPreferredWidth(120); // Name
        taskTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Task ID
        taskTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Type
        taskTable.getColumnModel().getColumn(4).setPreferredWidth(80);  // Duration
        taskTable.getColumnModel().getColumn(5).setPreferredWidth(150); // Status

        scrollPane.setPreferredSize(new Dimension(900, 200));

        tablePanel.setLayout(new BorderLayout());
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        tablePanel.setBorder(BorderFactory.createTitledBorder("Task List"));
    }

    public void setupStatusPanel() {
        statusPanel.setLayout(new BorderLayout());
        statusPanel.add(statusLabel, BorderLayout.CENTER);
        statusPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        statusPanel.setPreferredSize(new Dimension(900, 30));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if (e.getSource() == addEmployeeButton) {
                handleAddEmployee();
            } else if (e.getSource() == addTaskButton) {
                handleAddTask();
            } else if (e.getSource() == modifyTaskButton) {
                handleModifyTask();
            } else if (e.getSource() == workDurationButton) {
                handleWorkDuration();
            } else if (e.getSource() == removeTaskButton) {
                handleRemoveTask();
            } else if (e.getSource() == taskStatsButton) {
                handleTaskStatistics();
            } else if (e.getSource() == emplyeeWithMoreThen40HoursButton) {
                handleEmployeeWithMoreThen40Hours();
            }
        } catch (NumberFormatException ex) {
            updateStatus("Error: Please enter valid data.");
        } catch (Exception ex) {
            updateStatus("Error: " + ex.getMessage());
        }
    }

    public void handleAddEmployee() {
        int id = Integer.parseInt(employeeIdField.getText());
        String name = employeeNameField.getText().trim();

        if (name.isEmpty()) {
            updateStatus("Error: Employee name cannot be empty!");
            return;
        }

        Employee emp = new Employee(id, name);
        boolean added = tasksManagement.addEmployee(emp);

        if (added) {
            saveAndUpdate("Employee added.");
        } else {
            updateStatus("Error: Employee ID already exists!");
        }
    }

    public void handleAddTask() {
        try {
            int empId = Integer.parseInt(employeeIdField2.getText());
            int taskId = Integer.parseInt(taskIdField.getText());
            String status = statusComboBox.getSelectedItem().toString();
            String taskType = taskTypeComboBox.getSelectedItem().toString();

            int startHour = startHourField.getText().isEmpty() ? 0 : Integer.parseInt(startHourField.getText());
            int endHour = endHourField.getText().isEmpty() ? 0 : Integer.parseInt(endHourField.getText());

            Task newTask;
            if (taskType.equals("Simple")) {
                newTask = new SimpleTask(taskId, status, startHour, endHour);
            } else {
                newTask = new ComplexTask(taskId, status, new ArrayList<>());
            }

            if (!subTaskCheckBox.isSelected()) {
                tasksManagement.assignTaskToEmployee(empId, newTask);
                saveAndUpdate("Task assigned.");
            } else {
                assignSubTask(empId, newTask);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid input. Please enter valid data.",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void assignSubTask(int empId, Task newTask) {
        tasksManagement = SerializationOperations.loadTasksManagement();

        String complexTaskIdStr = JOptionPane.showInputDialog(this,
                "Enter Complex Task ID to assign this sub-task:", "Assign Sub-Task", JOptionPane.QUESTION_MESSAGE);

        if (complexTaskIdStr != null && !complexTaskIdStr.trim().isEmpty()) {
            int complexTaskId = Integer.parseInt(complexTaskIdStr.trim());

            Employee employee = tasksManagement.getEmployeeById(empId);
            if (employee != null) {
                for (Task task : tasksManagement.getMap().get(employee)) {
                    if (task instanceof ComplexTask complexTask) {
                        if (addSubTaskRecursively(complexTask, complexTaskId, newTask)) {
                            saveAndUpdate("Sub-task added to Complex Task ID: " + complexTaskId);
                            return;
                        }
                    }
                }
            }
            JOptionPane.showMessageDialog(this,
                    "Error: Complex Task ID " + complexTaskId + " not found.",
                    "Task Assignment Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean addSubTaskRecursively(ComplexTask parentTask, int targetComplexTaskId, Task newTask) {
        if (parentTask.getIdTask() == targetComplexTaskId) {
            parentTask.addTask(newTask);
            return true;
        }
        for (Task subTask : parentTask.getComplexTasks()) {
            if (subTask instanceof ComplexTask subComplexTask) {
                if (addSubTaskRecursively(subComplexTask, targetComplexTaskId, newTask)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void handleModifyTask() {
        try {
            int empId = Integer.parseInt(employeeIdFieldAction.getText());
            int taskId = Integer.parseInt(taskIdFieldAction.getText());

            tasksManagement.modifyTaskStatus(empId, taskId);
            SerializationOperations.saveTasksManagement(tasksManagement);
            tasksManagement = SerializationOperations.loadTasksManagement();
            updateTable();

            updateStatus("Task ID " + taskId + " modified successfully!");
        } catch (NumberFormatException ex) {
            updateStatus("Error: Please enter valid data.");
        }
    }

    public void handleWorkDuration() {
        StringBuilder workDurationInfo = new StringBuilder();

        for (Employee emp : tasksManagement.getMap().keySet()) {
            int duration = tasksManagement.calculateEmployeeWorkDuration(emp.getIdEmployee());
            workDurationInfo.append(emp.getName()).append(": ").append(duration).append("h\n");
        }

        if (workDurationInfo.isEmpty()) {
            workDurationInfo.append("No work duration data available.");
        }

        JOptionPane.showMessageDialog(this, workDurationInfo.toString(),
                "Employee Work Duration", JOptionPane.INFORMATION_MESSAGE);

        updateTable();
    }

    public void handleRemoveTask() {
        try {
            tasksManagement = SerializationOperations.loadTasksManagement();

            int empId = Integer.parseInt(employeeIdFieldAction.getText());
            int taskId = Integer.parseInt(taskIdFieldAction.getText());

            Employee employee = tasksManagement.getEmployeeById(empId);
            if (employee == null) {
                JOptionPane.showMessageDialog(this, "Employee not found!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            List<Task> employeeTasks = tasksManagement.getMap().get(employee);
            if (employeeTasks == null || employeeTasks.isEmpty()) {
                JOptionPane.showMessageDialog(this, "This employee has no tasks.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Task taskToRemove = null;
            for (Task task : employeeTasks) {
                if (task.getIdTask() == taskId) {
                    taskToRemove = task;
                    break;
                }
            }

            if (taskToRemove != null) {
                employeeTasks.remove(taskToRemove);
                saveAndUpdate("Task removed successfully.");
            } else {
                for (Task task : employeeTasks) {
                    if (task instanceof ComplexTask complexTask) {
                        complexTask.deleteTask(new SimpleTask(taskId, "", 0, 0));
                        saveAndUpdate("Sub-task removed successfully.");
                        return;
                    }
                }
                JOptionPane.showMessageDialog(this, "Task ID not found!", "Error", JOptionPane.ERROR_MESSAGE);
            }
            tasksManagement = SerializationOperations.loadTasksManagement();
            updateTable();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid data.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void handleTaskStatistics() {
        statsListModel.clear();

        Map<String, Map<String, Integer>> stats = utility.getTaskCompletionStats(tasksManagement);
        if (stats.isEmpty()) {
            statsListModel.addElement("No task statistics available.");
            return;
        }

        for (Map.Entry<String, Map<String, Integer>> entry : stats.entrySet()) {
            String line = entry.getKey() + " -> Completed: " + entry.getValue().get("Completed") +
                    ", Uncompleted: " + entry.getValue().get("Uncompleted");
            statsListModel.addElement(line);
        }
    }

    public void handleEmployeeWithMoreThen40Hours() {
        statsListModel.clear();

        List<Employee> employeesWithMoreThen40Hour = utility.getEmployeesWithMoreThen40Hour(tasksManagement);
        if (employeesWithMoreThen40Hour.isEmpty()) {
            statsListModel.addElement("No employeesWithMoreThen40Hour employees.");
            return;
        }

        statsListModel.addElement("Overworked Employees:");
        for (Employee emp : employeesWithMoreThen40Hour) {
            statsListModel.addElement(emp.getName());
        }
    }

    public void saveAndUpdate(String message) {
        SerializationOperations.saveTasksManagement(tasksManagement);
        updateStatus(message);
        updateTable();
    }

    public void updateTable() {
        tasksManagement = SerializationOperations.loadTasksManagement();
        tableModel.setRowCount(0);

        for (Map.Entry<Employee, List<Task>> entry : tasksManagement.getMap().entrySet()) {
            Employee employee = entry.getKey();
            List<Task> tasks = entry.getValue();

            if (tasks.isEmpty()) {
                tableModel.addRow(new Object[]{employee.getIdEmployee(), employee.getName(), "No tasks", "N/A", "N/A", "N/A"});
            } else {
                for (Task task : tasks) {
                    if (task instanceof ComplexTask complexTask) {
                        tableModel.addRow(new Object[]{
                                employee.getIdEmployee(),
                                employee.getName(),
                                + task.getIdTask(),
                                "Complex",
                                task.estimateDuration() + "h",
                                task.getStatusTask()
                        });
                        displaySubTasks(complexTask, "> ");
                    } else {
                        tableModel.addRow(new Object[]{
                                employee.getIdEmployee(),
                                employee.getName(),
                                + task.getIdTask(),
                                "Simple",
                                task.estimateDuration() + "h",
                                task.getStatusTask()
                        });
                    }
                }
            }
        }

        tablePanel.revalidate();
        tablePanel.repaint();
    }

    public void displaySubTasks(ComplexTask parentTask, String prefix) {
        for (Task subTask : parentTask.getComplexTasks()) {
            String subTaskType = (subTask instanceof SimpleTask) ? "Simple" : "Complex";

            tableModel.addRow(new Object[]{
                    "", "", prefix + subTask.getIdTask(),
                    subTaskType,
                    subTask.estimateDuration() + "h",
                    subTask.getStatusTask()
            });

            if (subTask instanceof ComplexTask subComplexTask) {
                displaySubTasks(subComplexTask, prefix + "> ");
            }
        }
    }

    public void updateStatus(String message) {
        statusLabel.setText("Status: " + message);
    }

    public static void main(String[] args) {
        new GUI("Task Management System");
    }
}
