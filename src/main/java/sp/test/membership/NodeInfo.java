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
    private NodeAddress nodeAddress;
    private String nodeId;
    private final AtomicInteger heartBeat = new AtomicInteger(0);
    private volatile long incarnation = 0; //  When a node restarts or recovers, it enters a new incarnation.
    private volatile Status status = Status.ALIVE;

    public NodeInfo(NodeAddress nodeAddress, String nodeId) {
        this.nodeAddress = nodeAddress;
        this.nodeId = nodeId;
    }

    public long incrementHeartBeat(){
        return heartBeat.incrementAndGet();
    }
}
