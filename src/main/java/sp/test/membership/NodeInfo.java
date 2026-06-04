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
@NoArgsConstructor
public class NodeInfo {
    private NodeAddress nodeAddress;
    private String nodeId;
    private final AtomicInteger heartBeat = new AtomicInteger(0);
    private volatile long incarnation = System.currentTimeMillis(); // the version when node started/restarted
    private volatile Status status = Status.ALIVE;

    public NodeInfo(NodeAddress nodeAddress, String nodeId) {
        this.nodeAddress = nodeAddress;
        this.nodeId = nodeId;
    }

    public long incrementHeartBeat(){
        return heartBeat.incrementAndGet();
    }

    public void setHeartBeat(int heartBeat){
        this.getHeartBeat().set(heartBeat);
    }
}
