package sp.test.membership;

import lombok.*;

import java.util.concurrent.atomic.AtomicInteger;

/*
Represent mata-data related to a node.
 */

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class NodeInfo {
    private String host;
    private String port;
    private String nodeId;
    private final AtomicInteger heartBeat = new AtomicInteger(0);
    private volatile long incarnation = 0; //  When a node restarts or recovers, it enters a new incarnation.
    private volatile Status status = Status.ALIVE;

    public NodeInfo(String host, String port, String nodeId) {
        this.host = host;
        this.port = port;
        this.nodeId = nodeId;
    }

    public long incrementHeartBeat(){
        return heartBeat.incrementAndGet();
    }
}
