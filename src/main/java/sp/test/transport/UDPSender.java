package sp.test.transport;

import lombok.extern.slf4j.Slf4j;
import sp.test.CommonUtils;
import sp.test.executers.ServiceExecutors;
import sp.test.membership.MembershipInfo;
import sp.test.membership.MembershipTable;
import sp.test.membership.NodeInfo;
import sp.test.transport.message.MembershipMessage;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
public class UDPSender {
    private MembershipTable membershipTable;
    private NodeInfo seed;
    private NodeInfo self;
    private volatile boolean seedUsed = false;

    public UDPSender(MembershipTable membershipTable, NodeInfo self, NodeInfo seed) {
        this.membershipTable = membershipTable;
        this.seed = seed;
        this.self = self;
    }

    public void start() {
        if (!seedUsed) {
            sendInitialMessage(seed);
        } else {
            ServiceExecutors.getInstance().scheduleWithFixedDelay(this::send, 1, 4, TimeUnit.SECONDS);
        }
    }

    private void sendInitialMessage(NodeInfo seed) {
        this.seedUsed = true;
        send(seed);
        this.start();
    }

    public void send() {
        //choose random node to gossip with
        //send full membership table
        if (membershipTable.getSize() == 1) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        String randomId = getRandomKey(membershipTable);
        if (randomId.equals(self.getNodeId())) {
            return;
        }
        NodeInfo randomNode = this.membershipTable.getNode(randomId);
        send(randomNode);
    }
    /*
        send its membership tables (including itself) to seed node.
     */
    private void send(NodeInfo nodeInfo) {
        try (DatagramSocket datagramSocket = new DatagramSocket()) {
            MembershipMessage message = getMembershipMessage(membershipTable);
            byte[] payload = CommonUtils.objectMapper.writeValueAsBytes(message);
            DatagramPacket datagramPacket = new DatagramPacket(payload, payload.length,
                    InetAddress.getByName(nodeInfo.getNodeAddress().getHost()), nodeInfo.getNodeAddress().getPort());
            datagramSocket.send(datagramPacket);
        } catch (Exception e) {
            log.error("error in sending payload:", e);
        }
    }

    private MembershipMessage getMembershipMessage(MembershipTable membershipTable) {
        MembershipMessage message = new MembershipMessage();
        List<MembershipInfo> membershipInfos = membershipTable.getAllNode().stream()
                .map(nodeInfo -> getMembershipInfo(nodeInfo)).toList();
        message.setMembershipInfos(membershipInfos);
        return message;
    }

    private MembershipInfo getMembershipInfo(NodeInfo nodeInfo) {
        MembershipInfo membershipInfo = new MembershipInfo();

        membershipInfo.setNodeId(nodeInfo.getNodeId());
        membershipInfo.setHeartbeat(nodeInfo.getHeartBeat().get());
        membershipInfo.setIncarnation(nodeInfo.getIncarnation());
        membershipInfo.setHost(nodeInfo.getNodeAddress().getHost());
        membershipInfo.setPort(nodeInfo.getNodeAddress().getPort());
        membershipInfo.setNodeState(nodeInfo.getStatus());

        return membershipInfo;

    }

    private String getRandomKey(MembershipTable membershipTable) {
        List<String> keys = membershipTable.getAllNode().stream().map(nodeInfo -> nodeInfo.getNodeId()).toList();
        int index = new Random().nextInt(keys.size());
        return keys.get(index);
    }

}
