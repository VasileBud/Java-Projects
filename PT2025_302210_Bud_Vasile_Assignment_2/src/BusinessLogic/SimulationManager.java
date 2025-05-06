package BusinessLogic;

import GUI.GUI;
import Model.Server;
import Model.Task;
import Utils.TextFileWriter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class SimulationManager implements Runnable {

    private int timeLimit;
    private int maxArrivalTime;
    private int minArrivalTime;
    private int maxProcessingTime;
    private int minProcessingTime;
    private int numberOfServers;
    private int numberOfClients;
    private SelectionPolicy selectionPolicy;
    private static int totalWaitingTime = 0;
    private static int totalServiceTime = 0;
    private static int clientsServed = 0;
    private int peakHour = 0;
    private int maxClientsInSystem = 0;

    private static int currentGlobalTime = 0;
    private GUI gui;
    private Scheduler scheduler;
    private List<Task> generatedTasks;
    private TextFileWriter fileWriter;

    public SimulationManager(int timeLimit, int maxArrivalTime, int minArrivalTime, int maxProcessingTime, int minProcessingTime, int numberOfServers, int numberOfClients, SelectionPolicy policy, GUI gui) {
        this.timeLimit = timeLimit;
        this.maxArrivalTime = maxArrivalTime;
        this.minArrivalTime = minArrivalTime;
        this.maxProcessingTime = maxProcessingTime;
        this.minProcessingTime = minProcessingTime;
        this.numberOfServers = numberOfServers;
        this.numberOfClients = numberOfClients;
        this.gui = gui;

        this.scheduler = new Scheduler(numberOfServers, 999, policy);

        generateNRandomTasks();
    }

    public void generateNRandomTasks() {
        generatedTasks = new ArrayList<>();
        Random rand = new Random();

        for (int i = 1; i <= numberOfClients; i++) {
            int arrivalTime = rand.nextInt(maxArrivalTime - minArrivalTime + 1) + minArrivalTime;
            int serviceTime = rand.nextInt(maxProcessingTime - minProcessingTime + 1) + minProcessingTime;
            Task t = new Task(arrivalTime, serviceTime);
            generatedTasks.add(t);
        }

    }

    public boolean allServersEmpty() {
        for (Server server : scheduler.getServers()) {
            if (!server.getTasks().isEmpty()) {
                return false;
            }
            if (server.getTotalTaskCount() != 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void run() {
        fileWriter = new TextFileWriter();
        currentGlobalTime = 0;

        while (currentGlobalTime < timeLimit && (!generatedTasks.isEmpty() || !allServersEmpty())) {
            dispatchArrivedTasks(currentGlobalTime);
            logCurrentState();
            updatePeakHour();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            currentGlobalTime++;
        }

        finalizeSimulation();
        scheduler.shutdown();
    }

    public void logCurrentState() {
        String log = createLogString(currentGlobalTime);
        fileWriter.write(log);
        if (gui != null) gui.updateLog(log);
    }

    public void updatePeakHour() {
        int totalClientsNow = 0;
        for (Server s : scheduler.getServers()) {
            if (s.getCurrentTask() != null) totalClientsNow++;
            totalClientsNow += s.getTasks().size();
        }

        if (totalClientsNow > maxClientsInSystem) {
            maxClientsInSystem = totalClientsNow;
            peakHour = currentGlobalTime;
        }
    }

    public void finalizeSimulation() {
        float avgWait = (clientsServed == 0) ? 0 : totalWaitingTime / (float) clientsServed;
        float avgService = (clientsServed == 0) ? 0 : totalServiceTime / (float) clientsServed;

        fileWriter.write("Average waiting time: " + avgWait);
        fileWriter.write("Average service time: " + avgService);
        fileWriter.write("Peak hour: " + peakHour + " with " + maxClientsInSystem + " clients");
        fileWriter.write("Simulation ended.");
        fileWriter.close();

        if (gui != null) {
            gui.updateLog("Average waiting time: " + avgWait +
                    "\nAverage service time: " + avgService +
                    "\nPeak hour: " + peakHour + " with " + maxClientsInSystem + " clients");
        }
    }

    public static synchronized void addTaskStats(int wait, int service) {
        totalWaitingTime += wait;
        totalServiceTime += service;
        clientsServed++;
    }

    public static synchronized int getCurrentGlobalTime() {
        return currentGlobalTime;
    }

    public String createLogString(int currentTime) {
        String log = "Time " + currentTime + "\n";
        log += "Waiting clients: " + formatWaitingClients() + "\n";

        int i = 1;
        for (Server server : scheduler.getServers()) {
            log += "Queue " + i + ": " + formatServerQueue(server) + "\n";
            i++;
        }

        return log;
    }

    public String formatWaitingClients() {
        if (generatedTasks.isEmpty()) return "none";

        String result = "";
        for (Task t : generatedTasks) {
            result += "(" + t.getId() + "," + t.getArrivalTime() + "," + t.getServiceTime() + "); ";
        }
        return result;
    }

    public String formatServerQueue(Server server) {
        String result = "";
        boolean isEmpty = true;

        Task currentTask = server.getCurrentTask();
        if (currentTask != null) {
            result += "processing:(" + currentTask.getId() + "," + currentTask.getArrivalTime() + "," + currentTask.getServiceTime() + "); ";
            isEmpty = false;
        }

        for (Task t : server.getTasks()) {
            result += "(" + t.getId() + "," + t.getArrivalTime() + "," + t.getServiceTime() + "); ";
            isEmpty = false;
        }

        return isEmpty ? "closed" : result;
    }


    public void dispatchArrivedTasks(int currentTime) {
        Iterator<Task> it = generatedTasks.iterator();
        while (it.hasNext()) {
            Task t = it.next();
            if (t.getArrivalTime() <= currentTime) {
                scheduler.dispatchTask(t);
                it.remove();
            }
        }
    }

    public Scheduler getScheduler() {
        return scheduler;
    }
}
