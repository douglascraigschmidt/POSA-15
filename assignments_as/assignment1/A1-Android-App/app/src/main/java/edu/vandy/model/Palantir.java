package edu.vandy.model;

import java.util.Random;

/**
 * Provides an interface for gazing into a Palantir.  Plays the role
 * of a "command" in the Command pattern.
 */
public class Palantir {
    /**
     * The value used to identify the Palantir.
     */
    private final int mId;

    /**
     * Create a new Random number generator.
     */
    private final Random mRandom;

    /**
     * Constructor initializes the fields.
     */
    public Palantir(int id,
                    Random random) {
        mId = id;
        mRandom = random;
    }

   /**
     * Create a random gaze time between 1 and 5 seconds.
     *
     * @return true if gaze() returns normally, false if it is
     * interrupted or some other exception occurs.
     */
    public boolean gaze() {
        try {
            Thread.sleep(mRandom.nextInt(4000) + 1000);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Return the id of the Palantir.
     */
    public int getId() {
        return mId;
    }

    /**
     * Returns true if @a this is equal to @a other.
     */
    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (!(other instanceof Palantir))
            return false;
        final Palantir that = (Palantir) other;
        return this.mId == that.mId; 
    }

    /**
     * Returns the hashcode for the Palantir, which is simply its id.
     */
    @Override
    public int hashCode() {
        return mId;
    }
}
