package sp.test.membership;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import sp.test.transport.UDPListener;
import sp.test.transport.UDPSender;
import sp.test.transport.message.MembershipMessage;

@Slf4j
public class CacheNode {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MembershipTable membershipTable;
    private HeartbeatService heartbeatService;
    private MembershipLogger membershipLogger;
    private UDPListener udpListener;
    private UDPSender udpSender;
    private NodeInfo self;
    private FailureDetector failureDetector;

    public CacheNode(NodeInfo self, NodeInfo seed) {
        this.self = self;
        this.membershipTable = new MembershipTable();
        this.heartbeatService = new HeartbeatService(self);
        this.membershipLogger = new MembershipLogger(membershipTable);
        this.udpListener = new UDPListener(self.getNodeAddress().getPort(), this::onMessage);
        this.udpSender = new UDPSender(membershipTable,  self, seed);
        this.failureDetector = new FailureDetector(membershipTable, self);
    }

    public void start() {
        this.membershipTable.usert(self);
        this.heartbeatService.start();
        this.membershipLogger.print();
        this.udpListener.start();
        this.udpSender.start();
        this.failureDetector.start();
    }

    private void onMessage(String json) {
        try {
            MembershipMessage msg = objectMapper.readValue(json, MembershipMessage.class);
            for(NodeInfo remoteNode: msg.getNodes()){
              membershipTable.merge(remoteNode);
            }
        } catch (Exception e) {
            log.error("error in onMessage:", e);
        }
    }

}
