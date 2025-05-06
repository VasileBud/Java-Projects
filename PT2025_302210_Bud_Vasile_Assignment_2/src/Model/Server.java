package Model;

import BusinessLogic.SimulationManager;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Server implements Runnable {
    public BlockingQueue<Task> tasks;
    public AtomicInteger waitingPeriod;
    private volatile boolean running = true;
    private volatile Task currentTask;

    public Server() {
        this.tasks = new LinkedBlockingQueue<>();
        this.waitingPeriod = new AtomicInteger(0);
    }

    public void addTask(Task newTask) {
        tasks.add(newTask);
        waitingPeriod.addAndGet(newTask.getServiceTime());
    }

    public BlockingQueue<Task> getTasks() {
        return tasks;
    }

    public Task getCurrentTask() {
        return currentTask;
    }

    public AtomicInteger getWaitingPeriod() {
        return waitingPeriod;
    }

    public void stop() {
        this.running = false;
    }

    public int getTotalTaskCount() {
        return tasks.size() + (currentTask != null ? 1 : 0);
    }

    @Override
    public void run() {
        while (running) {
            try {
                currentTask = tasks.take();
                int waitTime = SimulationManager.getCurrentGlobalTime() - currentTask.getArrivalTime();
                SimulationManager.addTaskStats(waitTime,currentTask.getServiceTime());
                while (currentTask.getServiceTime() > 0) {
                    Thread.sleep(1000);
                    currentTask.setServiceTime(currentTask.getServiceTime() - 1);
                    waitingPeriod.decrementAndGet();
                }
                currentTask = null;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    @Override
    public String toString() {
        return "Server{" +
                "tasks=" + tasks.toString() +
                '}';
    }
}
