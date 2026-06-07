package sp.test;

import lombok.extern.slf4j.Slf4j;
import sp.test.membership.CacheNode;
import sp.test.membership.NodeAddress;
import sp.test.membership.NodeInfo;

/**
 * Hello world!
 */
@Slf4j
public class App {
    public static void main(String[] args) {
        if (args.length < 3) {
            throw new IllegalArgumentException("usages: <host> <port> <nodeId>");
        }
        //self
        String host = args[0];
        String port = args[1];
        String nodeId = args[2];

        //seed node
        String seedHost = args[3];
        String seedPort = args[4];

        NodeAddress nodeAddress = new NodeAddress(host, Integer.parseInt(port));
        NodeInfo self = new NodeInfo(nodeAddress, nodeId);
        self.setLastUpdatedTime(System.currentTimeMillis());

        NodeAddress otherAddress  = new NodeAddress(seedHost, Integer.parseInt(seedPort));
        NodeInfo seed = new NodeInfo(otherAddress, null);

        CacheNode cacheNode = new CacheNode(self, seed);
        cacheNode.start();
    }
}
