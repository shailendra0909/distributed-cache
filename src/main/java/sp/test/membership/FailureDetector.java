package sp.test.membership;

import sp.test.executers.ServiceExecutors;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class FailureDetector implements Runnable {
    private MembershipTable membershipTable;
    private NodeInfo self;
    private long suspectedTimeoutMs = 5 * 1000; //sec * 1000
    private long deadTimeoutMs = suspectedTimeoutMs + 3 * 1000;//sec * 1000

    FailureDetector(MembershipTable membershipTable, NodeInfo self) {
        this.membershipTable = membershipTable;
        this.self = self;
    }

    @Override
    public void run() {
        List<MembershipInfo> membershipInfoList = Optional.ofNullable(this.membershipTable)
                .map(membershipTable -> membershipTable.getAllNode())
                .orElse(new ArrayList<>())
                .stream()
                .map(nodeInfo -> createMemberShipInfo(nodeInfo))
                .collect(Collectors.toList());

        Optional.ofNullable(membershipInfoList)
                .orElse(new ArrayList<>())
                .stream()
                .forEach(membershipInfo -> membershipTable.merge(membershipInfo));
    }

    private MembershipInfo createMemberShipInfo(NodeInfo nodeInfo) {
        MembershipInfo membershipInfo = new MembershipInfo();
        membershipInfo.setHost(nodeInfo.getNodeAddress().getHost());
        membershipInfo.setPort(nodeInfo.getNodeAddress().getPort());
        membershipInfo.setIncarnation(nodeInfo.getIncarnation());
        membershipInfo.setHeartbeat(nodeInfo.getHeartBeat().get());
        if (NodeState.ALIVE.equals(nodeInfo.getStatus()) && getAge(nodeInfo) > suspectedTimeoutMs) {
            membershipInfo.setNodeState(NodeState.SUSPECT);
        } else if (NodeState.SUSPECT.equals(nodeInfo.getStatus()) && getAge(nodeInfo) > deadTimeoutMs)
            membershipInfo.setNodeState(NodeState.DEAD);

        return membershipInfo;
    }

    private long getAge(NodeInfo nodeInfo) {
        return System.currentTimeMillis() - nodeInfo.getLastUpdatedTime();
    }

    public void start() {
        ServiceExecutors.getInstance().scheduleWithFixedDelay(this, 3, 3, TimeUnit.SECONDS);
    }
}
