package io.sunshower.service;

/**
 * Created by haswell on 3/3/17.
 */
public interface ServiceResolver {

    /**
     *
     * @param type
     * @param key
     * @param <T>
     * @return
     */

    <T> T resolve(Class<T> type, String key);

    /**
     *
     * @param type
     * @param region
     * @param key
     * @param <T>
     * @param <U>
     * @return
     */
    <T, U> T resolve(Class<T> type, U region, String key);

    /**
     *
     * @param type
     * @param region
     * @param key
     * @param object
     * @param <T>
     * @param <U>
     * @return
     */
    <T, U> boolean register(
            Class<T> type,
            U region,
            String key,
            T object
    );

    /**
     *
     * @param type
     * @param key
     * @param object
     * @param <T>
     * @return
     */

    <T> boolean register(
            Class<T> type,
            String key,
            T object
    );
}
