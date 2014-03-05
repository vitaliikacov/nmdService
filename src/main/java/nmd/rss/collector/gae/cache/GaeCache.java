package nmd.rss.collector.gae.cache;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import nmd.rss.collector.Cache;

import static nmd.rss.collector.util.Assert.assertNotNull;

/**
 * Author : Igor Usenko ( igors48@gmail.com )
 * Date : 05.03.14
 */
public class GaeCache implements Cache {

    public static final GaeCache GAE_CACHE = new GaeCache();

    private static final MemcacheService CACHE = MemcacheServiceFactory.getMemcacheService();

    @Override
    public void put(final Object key, final Object object) {
        assertNotNull(key);
        assertNotNull(object);

        CACHE.put(key, object);
    }

    @Override
    public Object get(final Object key) {
        assertNotNull(key);

        return CACHE.get(key);
    }

    @Override
    public boolean delete(final Object key) {
        assertNotNull(key);

        return CACHE.delete(key);
    }

    private GaeCache() {
        // empty
    }
}
