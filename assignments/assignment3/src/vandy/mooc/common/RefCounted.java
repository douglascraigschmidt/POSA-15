package vandy.mooc.common;

import java.util.concurrent.atomic.AtomicInteger;

import android.util.Log;

/**
 * A simple reference counter that can be used to close down objects
 * when they are no longer needed, e.g., to control the lifecycle of
 * singletones.
 */
public abstract class RefCounted {
    /**
     * Debugging tag used by the Android logger.
     */
    protected final String TAG = getClass().getSimpleName();
 
    /**
     * Ensure atomic increments and decrements of the reference count.
     */
    protected final AtomicInteger mRefcount =
        new AtomicInteger();

    /**
     * Return the current reference count.
     */
    public int getRefcount() {
        return mRefcount.get();
    }

    /**
     * Atomically increment the reference count by one.
     */
    public final RefCounted incrementRefCount() {
        mRefcount.incrementAndGet();
        return this;
    }

    /**
     * Atomically decrement the reference count by one and calls the 
     * close() hook method on the object so it can clean itself up.
     */
    public int decrementRefCount() {
        int count = mRefcount.decrementAndGet();
        if (count == 0)
            close();
        return count;
    }
    
    /**
     * Hook method that closes the object being reference counted.
     */
    protected abstract void close();
}
