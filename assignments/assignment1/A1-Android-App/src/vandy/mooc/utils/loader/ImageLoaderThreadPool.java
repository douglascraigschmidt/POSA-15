package vandy.mooc.utils.loader;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Custom Thread Pool used to load the images in the background.
 */
public class ImageLoaderThreadPool {

    private static final int CORE_POOL_SIZE = 20;
    private static final int MAXIMUM_POOL_SIZE = 256;
    private static final int KEEP_ALIVE = 1;

    /**
     * The ThreadFactory that is used to create new 
     * threads when needed.
     */
    private static final ThreadFactory sThreadFactory =
        new ThreadFactory() {
            private final AtomicInteger mCount =
                new AtomicInteger(1);

            public Thread newThread(Runnable r) {
                return new Thread(r, "AsyncTask #"
                    + mCount.getAndIncrement());
            }
        };

    /**
     * Queue of tasks to execute
     */
    private static final BlockingQueue<Runnable> sPoolWorkQueue =
        new LinkedBlockingQueue<Runnable>(MAXIMUM_POOL_SIZE);

    /**
     * An {@link Executor} that can be used to execute tasks in
     * parallel.
     */
    public static final Executor MY_THREAD_POOL_EXECUTOR =
        new ThreadPoolExecutor(CORE_POOL_SIZE,
                               MAXIMUM_POOL_SIZE,
                               KEEP_ALIVE,
                               TimeUnit.SECONDS,
                               sPoolWorkQueue,
                               sThreadFactory);
}
