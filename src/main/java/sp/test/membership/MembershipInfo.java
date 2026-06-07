package sp.test.membership;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// msg for gossip protocol
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MembershipInfo {
    private String nodeId;
    private long heartbeat;
    private long incarnation;
    private String host;
    private int port;
    private NodeState nodeState;
}
