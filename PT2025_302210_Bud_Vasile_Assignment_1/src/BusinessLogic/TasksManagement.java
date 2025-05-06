package BusinessLogic;

import java.io.Serializable;
import java.util.*;
import DataModel.*;

public class TasksManagement implements Serializable {
    private static final long serialVersionUID = 1L;
    private Map<Employee, List<Task>> map ;

    public TasksManagement() {
        this.map = new HashMap<Employee, List<Task>>();;
    }

    public Map<Employee, List<Task>> getMap() {
        return map;
    }

    public void setMap(Map<Employee, List<Task>> map) {
        this.map = map;
    }

    public boolean addEmployee(Employee employee) {
        for (Employee existingEmployee : map.keySet()) {
            if (existingEmployee.getIdEmployee() == employee.getIdEmployee()) {
                return false;
            }
        }
        map.put(employee, new ArrayList<>());
        return true;
    }

    public void assignTaskToEmployee(int idEmployee, Task task) {
        Employee employee = getEmployeeById(idEmployee);
        if (employee == null) {
            return;
        }
        if (!map.containsKey(employee)) {
            map.put(employee, new ArrayList<>());
        }
        for (Task existingTask : map.get(employee)) {
            if (existingTask.getIdTask() == task.getIdTask()) {
                return;
            }
        }
        map.get(employee).add(task);
    }

    public int calculateEmployeeWorkDuration(int idEmployee) {
        Employee employee = getEmployeeById(idEmployee);
        if (employee == null) return 0;

        int totalDuration = 0;
        for (Task task : map.get(employee)) {
            totalDuration += calculateCompletedTaskDuration(task, false);
        }
        return totalDuration;
    }

    private int calculateCompletedTaskDuration(Task task, boolean parentCompleted) {
        int duration = 0;

        if (parentCompleted) {
            return 0;
        }

        if ("Completed".equals(task.getStatusTask())) {
            duration += task.estimateDuration();
            if (task instanceof ComplexTask) {
                return duration;
            }
        }

        if (task instanceof ComplexTask complexTask) {
            for (Task subTask : complexTask.getComplexTasks()) {
                duration += calculateCompletedTaskDuration(subTask, "Completed".equals(task.getStatusTask()));
            }
        }
        return duration;
    }

    public void modifyTaskStatus(int idEmployee, int idTask) {
        Employee employee = getEmployeeById(idEmployee);
        if (employee == null) {
            return;
        }

        for (Task task : map.get(employee)) {
            if (task.getIdTask() == idTask) {
                String newStatus = "Uncompleted".equals(task.getStatusTask()) ? "Completed" : "Uncompleted";

                if ("Completed".equals(newStatus)) {
                    if (task instanceof ComplexTask complexTask && !areAllSubTasksCompleted(complexTask)) {
                        return;
                    }
                }
                task.setStatusTask(newStatus);
                return;
            }

            if (task instanceof ComplexTask complexTask) {
                boolean updated = modifySubTaskStatus(complexTask, idTask);
                if (updated) return;
            }
        }
    }

    private boolean modifySubTaskStatus(ComplexTask parentTask, int targetTaskId) {
        for (Task subTask : parentTask.getComplexTasks()) {
            if (subTask.getIdTask() == targetTaskId) {
                String newStatus = "Uncompleted".equals(subTask.getStatusTask()) ? "Completed" : "Uncompleted";
                subTask.setStatusTask(newStatus);
                return true;
            }

            if (subTask instanceof ComplexTask subComplexTask) {
                boolean updated = modifySubTaskStatus(subComplexTask, targetTaskId);
                if (updated) return true;
            }
        }
        return false;
    }
    private boolean areAllSubTasksCompleted(ComplexTask complexTask) {
        for (Task subTask : complexTask.getComplexTasks()) {
            if (!"Completed".equals(subTask.getStatusTask())) {
                return false;
            }
            if (subTask instanceof ComplexTask subComplexTask) {
                if (!areAllSubTasksCompleted(subComplexTask)) {
                    return false;
                }
            }
        }
        return true;
    }

    public Employee getEmployeeById(int idEmployee) {
        for (Employee employee : map.keySet()) {
            if (employee.getIdEmployee() == idEmployee) {
                return employee;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "TasksManagement{" +
                "map=" + map +
                '}';
    }
}
