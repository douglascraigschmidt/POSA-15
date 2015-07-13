package vandy.mooc.common;

import java.util.HashMap;
import java.util.Map;

import android.util.Log;

/**
 * Defines a generic singleton class whose instance() method ensures
 * only one object of type Class<T> is created.  We need this class
 * since Java Generic don't support proper singletons (a la the
 * "Gang-of-Four" book).  More information about this approach appears
 * at http://neutrofoton.com/generic-singleton-pattern-in-java.
 */
public class GenericSingleton {
    /**
     * Debugging tag used by the Android logger.
     */
    protected final static String TAG =
        GenericSingleton.class.getCanonicalName();

    /**
     * The singleton field.
     */
    private static final GenericSingleton sInstance =
        new GenericSingleton();
	
    /**
     * This HashMap ensures only one object of type Class<T> is
     * created.
     */
    @SuppressWarnings("rawtypes")
    private Map<Class, Object> mMap =
        new HashMap<>();
	
    /**
     * Return the one and only instance of Class<T>, which is created
     * on-demand if it doesn't exist.
     */
    @SuppressWarnings("unchecked")
    public static <T> T instance(Class<T> classOf) { 
        // Ensure thread-safety.
        synchronized(sInstance) {
            // Try to get the one and only instance of Class<T> that's
            // stored in the map.
            T t = (T) sInstance.mMap.get(classOf);          

            // Check to see if this is the first time a request for an
            // instance of Class<T> has been occurred.
            if (t == null) {
                try {
                    // If this is the first time in then create a new
                    // instance of Class<T>.
                    t = classOf.newInstance();
                } catch (Exception e) {
                    Log.d(TAG,
                          "GenericSingleton.instance() "
                          + e);
                    t = null;
                }
                // Store the new instance of Class<T> in the map so
                // it'll be available next time instance() is called.
                sInstance.mMap.put(classOf, t);
            }
            // Return the one and only instance of Class<T>.
            return t;
        }
    } 

    /**
     * If @a classOf is in the singleton map then set it to null so
     * it's cleaned up properly by the garbage collector.
     *
     * @return True if @a classOf is found/removed, else false.
     */
    public static <T> boolean remove(Class<T> classOf) {
        synchronized(sInstance) {
            // Try to get the one and only instance of Class<T> that's
            // stored in the map and if it's found, set it to null so
            // it will be garbage collected.
            if (sInstance.mMap.get(classOf) != null) {
                sInstance.mMap.put(classOf, 
                                   null);
                return true;
            } else
                return false;
        }
    }

    /**
     * Disallow instantiation.
     */
    private GenericSingleton() {}
	
    /**
     * Disallow cloning.
     */
    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    } 
}
