package sp.test.membership;

/*
Logs the membership info for debugging purpose
 */

import lombok.extern.slf4j.Slf4j;
import sp.test.executers.ServiceExecutors;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class MembershipLogger {
    private MembershipTable membershipTable;

    public MembershipLogger(MembershipTable membershipTable) {
        this.membershipTable = membershipTable;
    }

    public void print() {
        ServiceExecutors.getInstance().scheduleAtFixedRate(() -> {
            log.info("======members=====");
            membershipTable.getAllNode().stream().forEach((node) -> log.info(node.toString()));
            log.info("===========");
        }, 1, 5, TimeUnit.SECONDS);
    }
}
