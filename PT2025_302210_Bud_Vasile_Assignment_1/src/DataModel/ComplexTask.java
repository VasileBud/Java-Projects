package DataModel;

import java.util.ArrayList;
import java.util.Iterator;

public non-sealed class ComplexTask extends Task {
    private static final long serialVersionUID = 1L;

    private ArrayList<Task> complexTasks;

    public ComplexTask(int idTask, String statusTask, ArrayList<Task> complexTasks) {
        super(idTask, statusTask);
        this.complexTasks = complexTasks;
    }

    @Override
    public int estimateDuration() {
        int totalDuration = 0;

        for (Task subTask : complexTasks) {
            totalDuration += subTask.estimateDuration();
        }

        return totalDuration;
    }

    public void addTask(Task task) {
        complexTasks.add(task);
    }

    public void deleteTask(Task task) {
        Iterator<Task> iterator = complexTasks.iterator();
        while (iterator.hasNext()) {
            Task subTask = iterator.next();
            if (subTask.getIdTask() == task.getIdTask()) {
                iterator.remove();
                return;
            }
            if (subTask instanceof ComplexTask subComplexTask) {
                subComplexTask.deleteTask(task);
            }
        }
    }

    public ArrayList<Task> getComplexTasks() {
        return complexTasks;
    }

    public void setComplexTasks(ArrayList<Task> complexTasks) {
        this.complexTasks = complexTasks;
    }
}
