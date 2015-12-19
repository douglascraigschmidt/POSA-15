package vandy.mooc.common;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Timeout cache that uses thread-safe concurrent HashMap to cache
 * data and uses a ScheduledExecutorService to execute a Runnable
 * after a designated timeout to remove expired cache entries.
 */
public class ExecutorServiceTimeoutCache<K, V>
    extends RefCounted
    implements TimeoutCache<K, V> {
    /**
     * Debugging tag used by the Android logger.
     */
    protected final String TAG = getClass().getSimpleName();
 
    /**
     * A thread-safe HashMap that supports full concurrency of
     * retrievals and high expected concurrency for updates.  It store
     * CacheValues.
     */
    private ConcurrentHashMap<K, CacheValues> mResults =
        new ConcurrentHashMap<>();

    /**
     * Executor service that will execute Runnable after certain
     * timeouts to remove expired CacheValues.
     */
    private ScheduledExecutorService mScheduledExecutorService = 
        Executors.newScheduledThreadPool(1);

    /**
     * Datatype that represents the contents of the cache.  It
     * contains the value of the cache entity and a future that
     * executes a runnable after certain time period elapses to remove
     * expired CacheValue objects.
     */
    class CacheValues {
        /**
         * Value of the cache.
         */
        final public V mValue;

        /**
         * Result of an asynchronous computation.  It references a
         * runnable that has been scheduled to execute after certain
         * time period elapses.
         */
        public ScheduledFuture<?> mFuture = null;

        /**
         * Constructor for CacheValue.
         * 
         * @param value   The cache entry
         */
        public CacheValues(V value) {
            mValue = value;
        }

        /**
         * Setter for the ScheduledFuture.
         * 
         * @param future  A ScheduledFuture that can be used to cancel a Runnable.
         */
        public void setFuture(ScheduledFuture<?> future) {
            mFuture = future;
        }
    }

    /**
     * Put the @a value into the cache at the designated @a key with a
     * certain timeout after which the CacheValue will expire.
     * 
     * @param key        The key for the cache entry
     * @param value      The value of the cache entry
     * @param timeout    The timeout period in seconds
     */
    @Override
    public void put(final K key,
                    V value,
                    int timeout) {
        // Create this object here so it can be referenced in the
        // cleanupCacheRunnable below.
        final CacheValues cacheValues = new CacheValues(value);

        // Runnable that when executed will remove a CacheValues when
        // its timeout expires.
        final Runnable cleanupCacheRunnable = new Runnable() {
                @Override
                public void run() {
                    // Only remove key if it is currently associated
                    // with cacheValues.  This avoid race conditions
                    // that would otherwise occur since an mFuture to
                    // a previous CacheValues isn't canceled until
                    // after the new CacheValues is added to the map.
                    mResults.remove(key, 
                                    cacheValues);
                }
            };

        // Put a new CacheValues object into the ConcurrentHashMap
        // associated with the key and return the previous
        // CacheValues.
        CacheValues prevCacheValues =
            mResults.put(key,
                         cacheValues);

        // If there was a previous CacheValues associated with this
        // key then cancel the future immediately.  Note that there is
        // no race condition between the ScheduledExecutorService
        // running the cleanupCacheRunnable and canceling the future
        // here since the ConcurrentHashMap.remove() call won't
        // actually remove the key unless the value is equal to the
        // original cacheValues reference.
        if (prevCacheValues != null)
            prevCacheValues.mFuture.cancel(true);
        
        // Create a ScheduledFuture for the new cacheValues object that
        // will execute the cleanupCacheRunnable after the designated
        // timeout.
        ScheduledFuture<?> future =
            mScheduledExecutorService.schedule(cleanupCacheRunnable,
                                               timeout,
                                               TimeUnit.SECONDS);

        // Now that we have a future, attach it to the cacheValues object
        // that has already been safely added to the cache. The reason we
        // do not set the future before adding the cacheValues object to the
        // cache is because it is possible (but unlikely) for the future 
        // to trigger in the small time window between when it is started
        // and returned from the ScheduledExecutorService and when the 
        // put() call is made to add it to the cache.
        cacheValues.setFuture(future);
    }

    /**
     * Gets the @a value from the cache at the designated @a key.
     * 
     * @param key     The key for the cache entry
     * @return value  The value associated with the key, Which may be
     *                null if there's no key in the cache
     */
    @Override
    public final V get(K key) {
        CacheValues cacheValues = mResults.get(key);
        return cacheValues != null ? cacheValues.mValue : null;
    }

    /**
     * Removes the value associated with the designated @a key.
     * 
     * @param key     The key for the cache entry
     */
    @Override
    public void remove(K key) {
        mResults.remove(key);
    }

    /**
     * Return the current number of entries in the cache.
     * 
     * @return size
     */
    @Override
    public final int size() {
        return mResults.size();
    }
    
    /**
     * Shutdown the ScheduledExecutorService.
     */
    @Override
    protected void close() {
        // Cancel all remaining futures.
        for (CacheValues cvs : mResults.values())
            if (cvs.mFuture != null)
                cvs.mFuture.cancel(true);

        // Shutdown the ScheduledExecutorService immediately.
        mScheduledExecutorService.shutdownNow();
    }
}
