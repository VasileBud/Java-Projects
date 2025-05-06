package Model;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class Task {
    private static final AtomicInteger idGenerator = new AtomicInteger(1);
    private int id;
    private int arrivalTime;
    private int serviceTime;

    public Task(int arrivalTime, int serviceTime) {
        this.id = idGenerator.getAndIncrement();
        this.arrivalTime = arrivalTime;
        this.serviceTime = serviceTime;
    }

    public int getId() {
        return id;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getServiceTime() {
        return serviceTime;
    }

    public void setServiceTime(int serviceTime) {
        this.serviceTime = serviceTime;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Task task)) return false;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Task{" + "id=" + id + ", arrivalTime=" + arrivalTime + ", serviceTime=" + serviceTime + '}';
    }
}
