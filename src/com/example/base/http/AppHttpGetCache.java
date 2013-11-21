package com.example.base.http;


public class AppHttpGetCache {

	// string length
    private final static int DEFAULT_CACHE_SIZE = 1024 * 100;
    // 过期时间 60 seconds
    private final static long DEFAULT_EXPIRY_TIME = 1000 * 60; 
    private final static long MIN_EXPIRY_TIME = 200;

    private final LruMemoryCache<String, String> mMemoryCache;
    private int cacheSize = DEFAULT_CACHE_SIZE;
    private static long defaultExpiryTime = DEFAULT_EXPIRY_TIME;

    private boolean enabled = true;

    /**
     * @param AsyncHttpGetCache.DEFAULT_CACHE_SIZE  缓存大小
 	 * @param AsyncHttpGetCache.DEFAULT_EXPIRY_TIME 过期时间
     */
    public AppHttpGetCache() {
        this(AppHttpGetCache.DEFAULT_CACHE_SIZE, AppHttpGetCache.DEFAULT_EXPIRY_TIME);
    }

    public AppHttpGetCache(int strLength, long defaultExpiryTime) {
        if (strLength > DEFAULT_CACHE_SIZE) {
            this.cacheSize = strLength;
        }
        AppHttpGetCache.setDefaultExpiryTime(defaultExpiryTime);
        mMemoryCache = new LruMemoryCache<String, String>(this.cacheSize) {
            @Override
            protected int sizeOf(String key, String value) {
                if (value == null) return 0;
                return value.length();
            }
        };
    }

    public void setCacheSize(int strLength) {
        if (strLength > DEFAULT_CACHE_SIZE) {
            mMemoryCache.setMaxSize(strLength);
        }
    }

    public static void setDefaultExpiryTime(long defaultExpiryTime) {
        if (defaultExpiryTime > MIN_EXPIRY_TIME) {
            AppHttpGetCache.defaultExpiryTime = defaultExpiryTime;
        } else {
            AppHttpGetCache.defaultExpiryTime = MIN_EXPIRY_TIME;
        }
    }

    public static long getDefaultExpiryTime() {
        return AppHttpGetCache.defaultExpiryTime;
    }

    public void put(String url, String result) {
        put(url, result, defaultExpiryTime);
    }

    public void put(String url, String result, long expiry) {
        if (!enabled || url == null || result == null) return;

        if (expiry < MIN_EXPIRY_TIME) {
            expiry = MIN_EXPIRY_TIME;
        }
        mMemoryCache.put(url, result, System.currentTimeMillis() + expiry);
    }

    public String get(String url) {
        return enabled ? mMemoryCache.get(url) : null;
    }

    public void clear() {
        mMemoryCache.evictAll();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
