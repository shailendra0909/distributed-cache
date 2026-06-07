package sp.test.membership;

import lombok.*;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/*
Represent mata-data related to a membership node
 */

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class NodeInfo {
    private NodeAddress nodeAddress;
    private String nodeId;
    private final AtomicLong heartBeat = new AtomicLong(0);
    private volatile long incarnation = System.currentTimeMillis(); // the version when node started/restarted/declared alive by itself
    private volatile NodeState status = NodeState.ALIVE;
    private volatile long lastUpdatedTime; // its local to membership table; not exchanged with other node.

    public NodeInfo(NodeAddress nodeAddress, String nodeId) {
        this.nodeAddress = nodeAddress;
        this.nodeId = nodeId;
    }

    public long incrementHeartBeat(){
        return heartBeat.incrementAndGet();
    }

    public void setHeartBeat(long heartBeat){
        this.getHeartBeat().set(heartBeat);
    }
}
