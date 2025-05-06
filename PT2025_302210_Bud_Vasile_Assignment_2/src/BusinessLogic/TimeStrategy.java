package BusinessLogic;

import Model.Server;
import Model.Task;

import java.util.List;

public class TimeStrategy implements Strategy {

    @Override
    public void addTask(List<Server> servers, Task t) {
        Server s = servers.getFirst();
        for(Server server : servers){
            if(server.getWaitingPeriod().get()<s.getWaitingPeriod().get()){
                s = server;
            }
        }
        s.addTask(t);
    }
}
