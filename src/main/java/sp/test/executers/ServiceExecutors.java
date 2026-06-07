package sp.test.executers;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class ServiceExecutors {
    private static ScheduledExecutorService scheduledExecutorService;
    private static AtomicLong counter = new AtomicLong(0);

    public static ScheduledExecutorService getInstance() {
        if (scheduledExecutorService == null) {
            synchronized (ServiceExecutors.class) {
                if (scheduledExecutorService == null) {
                    scheduledExecutorService = Executors.newScheduledThreadPool(5, new ThreadFactory() {
                        @Override
                        public Thread newThread(Runnable r) {
                            Thread t = new Thread(r);
                            t.setName("scheduledExecutorService-" + counter.getAndIncrement());

                            t.setUncaughtExceptionHandler((Thread th, Throwable throwable) ->{
                                log.error("Thread :{} had exception: {}", th.getName(), throwable.getStackTrace());
                            });
                            return t;
                        }
                    });
                }
            }
        }
        return scheduledExecutorService;
    }

    public static void stop(){
        getInstance().shutdown();
    }

}
