package sp.test.membership;

/*
Logs the membership info for debugging purpose
 */

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class MembershipLogger {
    private MembershipTable membershipTable;
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    public MembershipLogger(MembershipTable membershipTable) {
        this.membershipTable = membershipTable;
    }

    public void print() {
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            log.info("======members=====");
            membershipTable.getAllNode().stream().forEach((node) -> log.info(node.toString()));
        }, 1, 5, TimeUnit.SECONDS);

        log.info("===========");

    }

    public void stop() {
        scheduledExecutorService.shutdown();
    }
}
