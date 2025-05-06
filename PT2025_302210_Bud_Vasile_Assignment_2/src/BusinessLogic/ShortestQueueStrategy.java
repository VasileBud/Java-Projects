package BusinessLogic;

import Model.Server;
import Model.Task;

import java.util.List;

public class ShortestQueueStrategy implements Strategy {
    @Override
    public void addTask(List<Server> servers, Task t) {
        Server best = null;
        int minSize = Integer.MAX_VALUE;

        for (Server server : servers) {
            int size = server.getTotalTaskCount();
            if (size < minSize) {
                minSize = size;
                best = server;
            }
        }
        if (best != null) {
            best.addTask(t);
        }
    }

}
