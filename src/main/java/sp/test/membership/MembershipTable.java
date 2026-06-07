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

    public void merge(NodeInfo nodeInfo) {
        nodeInfo.setLastUpdatedTime(System.currentTimeMillis());

        NodeInfo oldNodeInfo = members.get(nodeInfo.getNodeId());
        if(oldNodeInfo.getIncarnation() == nodeInfo.getIncarnation() && NodeState.DEAD.equals(oldNodeInfo.getStatus())){
            // do not process,
            // let other node with new incarnation number
        }
        if (oldNodeInfo == null) {
            members.put(nodeInfo.getNodeId(), nodeInfo);
        } else {
            if(nodeInfo.getIncarnation()> oldNodeInfo.getIncarnation()){
                members.put(nodeInfo.getNodeId(), nodeInfo);
            }else if(oldNodeInfo.getIncarnation() > nodeInfo.getIncarnation()){
                // old msg; do not process it
            }
            // incarnation is same; a) higher hb is updated; b) marked alive if suspected
            else if(nodeInfo.getHeartBeat().get() > oldNodeInfo.getHeartBeat().get()) {
                oldNodeInfo.setHeartBeat(nodeInfo.getHeartBeat().get());
                oldNodeInfo.setLastUpdatedTime(System.currentTimeMillis());
                oldNodeInfo.setStatus(NodeState.ALIVE);
            }
        }
    }
}
