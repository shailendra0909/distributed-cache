package sp.test.membership;

import lombok.extern.slf4j.Slf4j;
import sp.test.executers.ServiceExecutors;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/*
    this service take care of incrementing heart-beat of itself at regular interval
 */

@Slf4j
public class HeartbeatService {
    private NodeInfo self;

    public HeartbeatService(NodeInfo self) {
        this.self = self;
    }

    //start the heart beat
    public void start() {
        ServiceExecutors.getInstance().scheduleAtFixedRate(() -> {
            self.incrementHeartBeat();
            self.setLastUpdatedTime(System.currentTimeMillis());
           // log.info("heart beat for the server-id:" + self.getNodeId() + " beats:" + self.getHeartBeat());
        }, 1, 1, TimeUnit.SECONDS);
    }
}
