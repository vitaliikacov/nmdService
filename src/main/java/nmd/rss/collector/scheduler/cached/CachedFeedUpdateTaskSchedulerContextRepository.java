package nmd.rss.collector.scheduler.cached;

import nmd.rss.collector.Cache;
import nmd.rss.collector.scheduler.FeedUpdateTaskSchedulerContext;
import nmd.rss.collector.scheduler.FeedUpdateTaskSchedulerContextRepository;

import static nmd.rss.collector.util.Assert.assertNotNull;

/**
 * User: igu
 * Date: 14.10.13
 */
public class CachedFeedUpdateTaskSchedulerContextRepository implements FeedUpdateTaskSchedulerContextRepository {

    private static final String KEY = "FeedUpdateTaskSchedulerContext";

    private final Cache cache;

    public CachedFeedUpdateTaskSchedulerContextRepository(final Cache cache) {
        this.cache = cache;
    }

    @Override
    public synchronized void store(final FeedUpdateTaskSchedulerContext context) {
        assertNotNull(context);

        this.cache.put(KEY, context);
    }

    @Override
    public synchronized FeedUpdateTaskSchedulerContext load() {
        final FeedUpdateTaskSchedulerContext context = (FeedUpdateTaskSchedulerContext) this.cache.get(KEY);

        return context == null ? FeedUpdateTaskSchedulerContext.START_CONTEXT : context;
    }

    @Override
    public synchronized void clear() {
        this.cache.delete(KEY);
    }

}