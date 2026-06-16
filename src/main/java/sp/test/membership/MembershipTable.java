package sp.test.membership;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/*
    Thread safe membership table.
    Keep updating the members in members table.
 */
public class MembershipTable {
    private final ConcurrentHashMap<String, NodeInfo> members =
            new ConcurrentHashMap<>();
    private NodeInfo self;

    public MembershipTable(NodeInfo self) {
        this.self = self;
        members.put(self.getNodeId(), self);
    }

    public NodeInfo getNode(String nodeId) {
        return members.get(nodeId);
    }

    public List<NodeInfo> getAllNode() {
        return members.values().stream().toList();
    }

    public int getSize() {
        return members.size();
    }

    public void merge(MembershipInfo membershipInfo) {
        members.compute(membershipInfo.getNodeId(), (key, nodeInfo) -> {
            // if it is new node
            if (nodeInfo == null) {
                return createNodeInfo(membershipInfo);
            } else if (nodeInfo.getIncarnation() < membershipInfo.getIncarnation()) {
                updateMembershipInfo(nodeInfo, membershipInfo);
            } else if (nodeInfo.getIncarnation() > membershipInfo.getIncarnation()) {
                // old membership msg; do not process it
            } else if (membershipInfo.getHeartbeat() > nodeInfo.getHeartBeat().get()) {
                updateMembershipInfo(nodeInfo, membershipInfo);
            } else if (membershipInfo.getHeartbeat() < nodeInfo.getHeartBeat().get()) {
                // do nothing
            } else if (membershipInfo.getHeartbeat() == nodeInfo.getHeartBeat().get() && membershipInfo.getNodeState() != nodeInfo.getStatus()) {
                // if incarnation and heartbeat is same, DEAD -> SUSPECTED -> ALIVE
                if (nodeInfo.getStatus().precedence() < membershipInfo.getNodeState().precedence()) {
                    // do nothing, until it's self
                    if (self.getNodeId().equals(membershipInfo.getNodeId()) &&
                            (NodeState.DEAD.equals(membershipInfo.getNodeState())
                            || NodeState.SUSPECT.equals(membershipInfo.getNodeState()))) {
                        self.setIncarnation(System.currentTimeMillis());
                        self.setStatus(NodeState.ALIVE);
                        self.incrementHeartBeat();
                    }
                    nodeInfo.setStatus(membershipInfo.getNodeState());
                }
            }
            return nodeInfo;
        });
    }

    private void updateMembershipInfo(NodeInfo oldNodeInfo, MembershipInfo membershipInfo) {
        oldNodeInfo.setHeartBeat(membershipInfo.getHeartbeat());
        oldNodeInfo.setStatus(membershipInfo.getNodeState());
        oldNodeInfo.setIncarnation(membershipInfo.getIncarnation());

        oldNodeInfo.setLastUpdatedTime(System.currentTimeMillis());
    }

    private NodeInfo createNodeInfo(MembershipInfo membershipInfo) {
        NodeInfo nodeInfo = new NodeInfo();
        nodeInfo.setNodeId(membershipInfo.getNodeId());
        nodeInfo.setHeartBeat(membershipInfo.getHeartbeat());
        nodeInfo.setNodeAddress(new NodeAddress(membershipInfo.getHost(), membershipInfo.getPort()));
        nodeInfo.setIncarnation(membershipInfo.getIncarnation());
        nodeInfo.setStatus(membershipInfo.getNodeState());

        nodeInfo.setLastUpdatedTime(System.currentTimeMillis());

        return nodeInfo;
    }
}
