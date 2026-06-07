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

    public void usert(NodeInfo nodeInfo) {
        members.put(nodeInfo.getNodeId(), nodeInfo);
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

        NodeInfo oldNodeInfo = members.get(membershipInfo.getNodeId());

        // its new node
        if (oldNodeInfo == null) {
            members.put(membershipInfo.getNodeId(), createNodeInfo(membershipInfo));
            return;
        }

        if (oldNodeInfo.getIncarnation() == membershipInfo.getIncarnation()
                && NodeState.DEAD.equals(oldNodeInfo.getStatus())) {
            // do not process,
            // let other node come up with new incarnation number
            return;
        }

        if (oldNodeInfo.getIncarnation() < membershipInfo.getIncarnation()) {
            updateMembershipInfo(oldNodeInfo, membershipInfo);
            return;
        }

        if (oldNodeInfo.getIncarnation() > membershipInfo.getIncarnation()) {
            // old msg; do not process it
            return;
        }
        if (membershipInfo.getHeartbeat() > oldNodeInfo.getHeartBeat().get()) {
            updateMembershipInfo(oldNodeInfo, membershipInfo);
            return;
        }
        // if incarnation and heartbeat is same, DEAD -> SUSPECTED -> ALIVE
        if (membershipInfo.getHeartbeat() == oldNodeInfo.getHeartBeat().get() && membershipInfo.getNodeState() != oldNodeInfo.getStatus()) {

            if (oldNodeInfo.getStatus().precedence() > membershipInfo.getNodeState().precedence()) {
                // do nothing
            } else {
                oldNodeInfo.setStatus(membershipInfo.getNodeState());
            }
        }
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
