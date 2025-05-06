package BusinessLogic;

import java.util.*;
import DataModel.*;

public class Utility {
    private static final long serialVersionUID = 1L;

    public List<Employee> getEmployeesWithMoreThen40Hour(TasksManagement tasksManagement) {
        List<Employee> employeesWithMoreThen40Hours = new ArrayList<>();

        for (Map.Entry<Employee, List<Task>> entry : tasksManagement.getMap().entrySet()) {
            Employee employee = entry.getKey();
            int workDuration = tasksManagement.calculateEmployeeWorkDuration(employee.getIdEmployee());

            if (workDuration > 40) {
                employeesWithMoreThen40Hours.add(employee);
            }
        }

        employeesWithMoreThen40Hours.sort(Comparator.comparingInt(e -> tasksManagement.calculateEmployeeWorkDuration(e.getIdEmployee())));

//        System.out.println("Angajații care au lucrat mai mult de 40 de ore:");
//        for (Employee emp : employeesWithMoreThen40Hours) {
//            System.out.println(emp.getName() + " - " + tasksManagement.calculateEmployeeWorkDuration(emp.getIdEmployee()) + " ore");
//        }
        return employeesWithMoreThen40Hours;
    }

    public Map<String, Map<String, Integer>> getTaskCompletionStats(TasksManagement tasksManagement) {
        Map<String, Map<String, Integer>> stats = new HashMap<>();

        for (Map.Entry<Employee, List<Task>> entry : tasksManagement.getMap().entrySet()) {
            Employee employee = entry.getKey();
            List<Task> tasks = entry.getValue();

            int completedTasks = 0;
            int uncompletedTasks = 0;

            for (Task task : tasks) {
                if (task instanceof ComplexTask complexTask) {
                    Map<String, Integer> subTaskStats = countSubTasks(complexTask);
                    completedTasks += subTaskStats.get("Completed");
                    uncompletedTasks += subTaskStats.get("Uncompleted");
                }

                if ("Completed".equals(task.getStatusTask())) {
                    completedTasks++;
                } else {
                    uncompletedTasks++;
                }
            }

            Map<String, Integer> taskStats = new HashMap<>();
            taskStats.put("Completed", completedTasks);
            taskStats.put("Uncompleted", uncompletedTasks);

            stats.put(employee.getName(), taskStats);
        }
        return stats;
    }

    private Map<String, Integer> countSubTasks(ComplexTask complexTask) {
        int completed = 0;
        int uncompleted = 0;

        for (Task subTask : complexTask.getComplexTasks()) {
            if (subTask instanceof ComplexTask subComplexTask) {
                Map<String, Integer> subTaskStats = countSubTasks(subComplexTask);
                completed += subTaskStats.get("Completed");
                uncompleted += subTaskStats.get("Uncompleted");
            }

            if ("Completed".equals(subTask.getStatusTask())) {
                completed++;
            } else {
                uncompleted++;
            }
        }

        Map<String, Integer> result = new HashMap<>();
        result.put("Completed", completed);
        result.put("Uncompleted", uncompleted);
        return result;
    }
}
