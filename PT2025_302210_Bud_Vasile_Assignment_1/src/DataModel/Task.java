package DataModel;

import java.io.*;

public sealed abstract class Task implements Serializable permits ComplexTask, SimpleTask{
    private static final long serialVersionUID = 1L;
    private int idTask;
    private String statusTask;

    public Task(int idTask, String statusTask) {
        this.idTask = idTask;
        this.statusTask = statusTask;
    }

    public int getIdTask() {
        return idTask;
    }

    public void setIdTask(int idTask) {
        this.idTask = idTask;
    }

    public String getStatusTask() {
        return statusTask;
    }

    public void setStatusTask(String statusTask) {
        this.statusTask = statusTask;
    }

    public abstract int estimateDuration();

    @Override
    public String toString() {
        return "Task{" +
                "idTask=" + idTask +
                ", statusTask='" + statusTask + '\'' +
                '}';
    }
}
