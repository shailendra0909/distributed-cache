package sp.test;

import sp.test.membership.CacheNode;
import sp.test.membership.NodeInfo;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        if(args.length != 3){
            throw new IllegalArgumentException("usages: <host> <port> <nodeId>");
        }
        String host = args[0];
        String port = args[1];
        String nodeId = args[2];

        NodeInfo nodeInfo = new NodeInfo(host, port, nodeId);
        CacheNode cacheNode = new CacheNode(nodeInfo);
        cacheNode.start();
    }
}
