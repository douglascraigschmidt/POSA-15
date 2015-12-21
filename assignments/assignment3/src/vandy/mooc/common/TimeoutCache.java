package vandy.mooc.common;

/**
 * Define a common interface for all timedout cache implementations.
 */
public interface TimeoutCache<K, V> {
    /**
     * Gets the @a value from the cache at the designated @a key.
     * 
     * @param key
     * @return value
     */
    V get(K key);

    /**
     * Put the @a value into the cache at the designated @a key with a
     * timeout after which the data will expire.
     * 
     * @param key
     * @param value
     * @param timeout in seconds
     */
    void put(K key, V obj, int timeout);

    /**
     * Removes the value associated with a key.
     * 
     * @param key
     */
    void remove(K key);

    /**
     * Get the size of the cache.
     * 
     * @return size
     */
    int size();
}
