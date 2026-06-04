package sp.test.membership;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/*
    Thread safe membership table
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

    public Collection<NodeInfo> getAllNode() {
        return members.values();
    }

    public int getSize() {
        return members.size();
    }

    public void merge(NodeInfo nodeInfo) {

        NodeInfo oldNodeInfo = members.get(nodeInfo.getNodeId());
        if (oldNodeInfo == null) {
            members.put(nodeInfo.getNodeId(), nodeInfo);
        } else {
            if(nodeInfo.getIncarnation()> oldNodeInfo.getIncarnation()){
                members.put(nodeInfo.getNodeId(), nodeInfo);
                return;
            }else if(oldNodeInfo.getIncarnation() > nodeInfo.getIncarnation()){
                // old msg
                // nothing to update
                return;
            }
            // incarnation is same
            //only new entry having higher hb is updated
            if (nodeInfo.getHeartBeat().get() > oldNodeInfo.getHeartBeat().get()) {
                oldNodeInfo.setHeartBeat(nodeInfo.getHeartBeat().get());
            }
        }
    }
}
