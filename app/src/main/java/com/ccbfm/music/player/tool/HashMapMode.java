package com.ccbfm.music.player.tool;

import java.util.HashMap;

/**
 * @param <K>
 * @param <V>
 * @author ccbfm
 */
public abstract class HashMapMode<K, V> {
    private final static boolean DEBUG = false;
    private final static String TAG = "HashMapMode";

    private HashMap<K, V> mHashMap;

    protected HashMapMode() {
        this(16);
    }

    protected HashMapMode(int initialCapacity) {
        mHashMap = new HashMap<>(initialCapacity);
    }

    protected V getValue(K key) {
        V value = mHashMap.get(key);
        if (value == null) {
            synchronized (this) {
                value = mHashMap.get(key);
                if (value == null) {
                    value = createValue();
                    mHashMap.put(key, value);
                }
            }
        }
        return value;
    }

    protected V removeValue(K service) {
        synchronized (this) {
            return mHashMap.remove(service);
        }
    }

    protected void clear() {
        synchronized (this) {
            mHashMap.clear();
        }
    }

    /**
     * 创建泛型对象
     *
     * @return V
     */
    protected abstract V createValue();
}
