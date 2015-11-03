package edu.vandy.utils;

import android.content.Context;
import edu.vandy.common.Utils;

/**
 * This class implements the Singleton pattern to handle the
 * processing of command-line options.
 */
public class Options {
    /**
     * Maximum number of Palantiri that can be managed.
     */
    private static final int MAX_PALANTIRI = 6;

    /**
     * Maximum number of beings that can gaze at the Palantiri.
     */
    private static final int MAX_BEINGS = 10;

    /**
     * Maximum duration a Being can hold a lease on a Palantir.
     */
    private static final int MAX_DURATION = 5000;

    /**
     * Maximum number of iterations a Being can gaze.
     */
    private static final int MAX_ITERATIONS = 100;

    /** 
     * The singleton @a Options instance. 
     */
    private static Options mUniqueInstance = null;

    /** 
     * Number of Palantiri to allocate in the LeasePool;
     */
    private int mNumberOfPalantiri;

    /** 
     * Number of Beings who will gaze at the Palantiri.
     */
    private int mNumberOfBeings;

    /** 
     * Lease duration (defaults to 3000 milliseconds).
     */
    private int mLeaseDuration = 5000;

    /** 
     * Number of gazing iterations for each Being (defaults to 10).
     */
    private int mGazingIterations = 10;

    /**
     * Controls whether debugging output will be generated (defaults
     * to false).
     */
    public boolean mDiagnosticsEnabled = false;

    /** 
     * Method to return the one and only singleton uniqueInstance. 
     */
    public static Options instance() {
        if (mUniqueInstance == null)
            mUniqueInstance = new Options();

        return mUniqueInstance;
    }

    /** 
     * Returns number of Palantiri to allocate in the LeasePool.
     */
    public int numberOfPalantiri() {
        return mNumberOfPalantiri;
    }

    /** 
     * Returns the number of Beings who will gaze at the Palantiri.
     */
    public int numberOfBeings() {
        return mNumberOfBeings;
    }

    /** 
     * Returns the lease duration.
     */
    public int leaseDuration() {
        return mLeaseDuration;
    }

    /**
     * Returns number of gazing iterations for each Being.
     */
    public int gazingIterations() {
        return mGazingIterations;
    }

    /**
     * Returns whether debugging output is generated.
     */
    public boolean diagnosticsEnabled() {
        return mDiagnosticsEnabled;
    }

    /**
     * Parse command-line arguments and set the appropriate values.
     */
    public boolean parseArgs(Context context,
                             String argv[]) {
        if (argv != null) {
            for (int argc = 0; argc < argv.length; argc += 2)
                if (argv[argc].equals("-b")) {
                    mNumberOfBeings = Integer.parseInt(argv[argc + 1]);
                    if (mNumberOfBeings < 1
                        || mNumberOfBeings > MAX_BEINGS) {
                        Utils.showToast(context,
                                        "Please enter a number between 1 and "
                                        + MAX_BEINGS
                                        + " for # of Beings");
                        return false;
                    }
                } else if (argv[argc].equals("-d"))
                    mDiagnosticsEnabled = argv[argc + 1].equals("true");
                else if (argv[argc].equals("-i")) {
                    mGazingIterations = Integer.parseInt(argv[argc + 1]);
                    if (mGazingIterations < 1
                        || mGazingIterations > MAX_ITERATIONS) {
                        Utils.showToast(context,
                                        "Please enter a number between 1 and "
                                        + MAX_ITERATIONS
                                        + " for the gazing iterations");
                        return false;
                    }
                } else if (argv[argc].equals("-l")) {
                    mLeaseDuration = Integer.parseInt(argv[argc + 1]);
                    if (mLeaseDuration < 1000
                        || mLeaseDuration > MAX_DURATION) {
                        Utils.showToast(context,
                                        "Please enter a number between 1000 and "
                                        + MAX_DURATION
                                        + " for the lease duration");
                        return false;
                    }
                } else if (argv[argc].equals("-p")) {
                    mNumberOfPalantiri = Integer.parseInt(argv[argc + 1]);
                    if (mNumberOfPalantiri < 1 
                        || mNumberOfPalantiri > MAX_PALANTIRI) {
                        Utils.showToast(context,
                                        "Please enter a number between 1 and "
                                        + MAX_PALANTIRI
                                        + " for # of Palantiri");
                        return false;
                    }
                } else 
                    return false;

            return true;
        } else
            return false;
    }

    /**
     * Make the constructor private to ensure this class is only used
     * as a singleton.
     */
    private Options() {
    }
}
