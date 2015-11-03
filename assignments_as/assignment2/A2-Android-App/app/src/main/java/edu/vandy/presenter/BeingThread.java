package edu.vandy.presenter;

/**
 * This class implements a BeingThread, which provides various methods
 * for interrupting and shutting down a Java Thread.
 */
public class BeingThread 
       extends Thread {
    /**
     * Used for Android debugging.
     */
    private final static String TAG = 
        BeingThread.class.getName();

    /**
     * Used to stop the BeingThread from gazing when the lease
     * expires.
     */
    private volatile boolean mLeaseExpired;

    /**
     * Used to shutdown all the BeingThreads when the simulation is
     * done.
     */
    private static volatile boolean mShutdown;

    /**
     * Reference to the enclosing Presenter.
     */
    private final PalantiriPresenter mPresenter;

    /**
     * Constructor initializes the BeingThread.
     */
    public BeingThread(Runnable runnable,
                       int beingCount,
                       PalantiriPresenter presenter) {
        // Initialize superclass and fields.
        super(runnable,
              "Being-" 
              + beingCount);
        mLeaseExpired = false;
        mShutdown = false;
        mPresenter = presenter;

        // Set the exception handler for the new Thread.
        setUncaughtExceptionHandler
            (new Thread.UncaughtExceptionHandler() {
                    /**
                     * If an uncaught Exception occurs then shutdown!
                     */
                    @Override
                    public void uncaughtException(Thread thread,
                                                  Throwable ex) {
                        mPresenter.shutdown();
                    }
                });
    }

    /**
     * Shutdown the BeingThread.
     */
    public static void shutdown() {
        /*
        Log.d(TAG,
              "BeingThread.shutdown() called from Thread "
              + Thread.currentThread().getId());
        */
        mShutdown = true;
        // super.interrupt();
    }


    /**
     * Indicate the lease is expired.
     */
    public void leaseExpired() {
        /*
        Log.d(TAG,
              "BeingThread.leaseExpired() called from Thread "
              + Thread.currentThread().getId());
        */
        mLeaseExpired = true;
        super.interrupt();
    }

    /**
     * Return true if the thread has been shutdown, else false.
     */
    public static boolean isShutdown() {
        return mShutdown;
    }

    /**
     * Return true if the lease has expired, else false.
     */
    public boolean isExpired() {
        boolean expired = mLeaseExpired;
        mLeaseExpired = false;
        return expired;
    }
}            
