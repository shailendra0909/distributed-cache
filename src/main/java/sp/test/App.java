package sp.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import sp.test.membership.CacheNode;
import sp.test.membership.NodeAddress;
import sp.test.membership.NodeInfo;
import sp.test.transport.UDPSender;
import sp.test.transport.message.HelloMessage;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

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

        //other node
        String host2 = args[3];
        String port2 = args[4];

        NodeAddress nodeAddress = new NodeAddress(host, Integer.parseInt(port));
        NodeInfo nodeInfo = new NodeInfo(nodeAddress, nodeId);
        CacheNode cacheNode = new CacheNode(nodeInfo);
        cacheNode.start();

        UDPSender sender = new UDPSender();
        try {
            ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
            executorService.scheduleAtFixedRate(() -> {
                HelloMessage msg = new HelloMessage(nodeId, host, Integer.parseInt(port), nodeInfo.getHeartBeat().get());
                sender.send(host2, Integer.parseInt(port2), msg);
            }, 1, 4, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("error in main ", e);
        }
    }
}
