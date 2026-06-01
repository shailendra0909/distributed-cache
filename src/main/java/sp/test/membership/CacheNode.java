package sp.test.membership;

public class CacheNode {

    private MembershipTable membershipTable;
    private HeartbeatService heartbeatService;
    private MembershipLogger membershipLogger;
    private NodeInfo self;

    public CacheNode(NodeInfo self) {
        this.self = self;
        this.membershipTable = new MembershipTable();
        this.heartbeatService = new HeartbeatService(self);
        this.membershipLogger = new MembershipLogger(membershipTable);
    }

    public void start(){
        this.membershipTable.usert(self);
        this.heartbeatService.start();
        this.membershipLogger.print();
    }
}
