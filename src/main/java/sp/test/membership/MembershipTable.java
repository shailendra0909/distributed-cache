package sp.test.membership;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/*
    Thread safe membership table
 */
public class MembershipTable {
    private final ConcurrentHashMap<String, NodeInfo> members =
            new ConcurrentHashMap<>();

    public void usert(NodeInfo nodeInfo){
        members.put(nodeInfo.getNodeId(), nodeInfo);
    }

    public NodeInfo getNode(String nodeId){
        return members.get(nodeId);
    }

    public Collection<NodeInfo> getAllNode(){
        return members.values();
    }

    public int getSize(){
        return members.size();
    }
}
