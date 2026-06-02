package sp.test.membership;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import sp.test.transport.UDPListener;
import sp.test.transport.message.HelloMessage;

@Slf4j
public class CacheNode {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MembershipTable membershipTable;
    private HeartbeatService heartbeatService;
    private MembershipLogger membershipLogger;
    private UDPListener udpListener;
    private NodeInfo self;

    public CacheNode(NodeInfo self) {
        this.self = self;
        this.membershipTable = new MembershipTable();
        this.heartbeatService = new HeartbeatService(self);
        this.membershipLogger = new MembershipLogger(membershipTable);
        this.udpListener = new UDPListener(self.getNodeAddress().port(), this::onMessage);
    }

    public void start() {
        this.membershipTable.usert(self);
        this.heartbeatService.start();
        this.membershipLogger.print();
        this.udpListener.start();
    }

    private void onMessage(String json) {
        try {
            HelloMessage msg = objectMapper.readValue(json, HelloMessage.class);

            NodeInfo remoteNode = new NodeInfo(new NodeAddress(msg.getHost(), msg.getPort()), msg.getNodeId());

            while (remoteNode.getHeartBeat().get() < msg.getHeartbeat()) {
                remoteNode.incrementHeartBeat();
            }

            membershipTable.usert(remoteNode);
        } catch (Exception e) {
            log.error("error in onMessage:", e);
        }
    }

}
