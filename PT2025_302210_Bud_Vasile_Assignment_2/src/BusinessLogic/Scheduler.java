package BusinessLogic;

import Model.Server;
import Model.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Scheduler {
    private List<Server> servers;
    private int maxNoServers;
    private int maxTasksPerServer;
    private Strategy strategy;
    private ExecutorService executorService;

    public Scheduler(int maxNoServers, int maxTasksPerServer,SelectionPolicy policy) {
        this.maxNoServers = maxNoServers;
        this.maxTasksPerServer = maxTasksPerServer;
        this.servers = new ArrayList<>();
        this.executorService = Executors.newFixedThreadPool(maxNoServers);
        changeStrategy(policy);

        for (int i = 0; i < maxNoServers; i++) {
            Server server = new Server();
            servers.add(server);
            executorService.submit(server);
        }
    }

    public void changeStrategy(SelectionPolicy policy) {
        if (policy == SelectionPolicy.SHORTEST_QUEUE) {
            strategy = new ShortestQueueStrategy();
        } else if (policy == SelectionPolicy.SHORTEST_TIME) {
            strategy = new TimeStrategy();
        }
    }

    public void dispatchTask(Task t) {
        if (strategy != null) {
            strategy.addTask(servers, t);
        }
    }

    public void shutdown() {
        for (Server server : servers) {
            server.stop();
        }
        executorService.shutdownNow();
    }
    public List<Server> getServers() {
        return servers;
    }
}
