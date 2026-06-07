package sp.test.membership;

import sp.test.executers.ServiceExecutors;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class FailureDetector implements Runnable {
    private MembershipTable membershipTable;
    private NodeInfo self;
    private long suspectedTimeoutMs = 5 * 1000; //sec * 1000
    private long deadTimeoutMs = 3 * 1000;//sec * 1000

    FailureDetector(MembershipTable membershipTable, NodeInfo self) {
        this.membershipTable = membershipTable;
        this.self = self;
    }

    @Override
    public void run() {
        Optional.ofNullable(this.membershipTable)
                .map(membershipTable -> membershipTable.getAllNode())
                .orElse(new ArrayList<>())
                .stream()
                .forEach((nodeInfo) -> {
                    if (nodeInfo.equals(self)) {
                        return;
                    }
                    if (NodeState.ALIVE.equals(nodeInfo.getStatus()) && getAge(nodeInfo) > suspectedTimeoutMs) {
                        nodeInfo.setStatus(NodeState.SUSPECT);
                    } else if (NodeState.SUSPECT.equals(nodeInfo.getStatus()) && getAge(nodeInfo) > deadTimeoutMs) {
                        nodeInfo.setStatus(NodeState.DEAD);
                    }
                });
    }

    private long getAge(NodeInfo nodeInfo) {
        return System.currentTimeMillis() - nodeInfo.getLastUpdatedTime();
    }

    public void start() {
        ServiceExecutors.getInstance().scheduleWithFixedDelay(this, 3, 3, TimeUnit.SECONDS);
    }
}
