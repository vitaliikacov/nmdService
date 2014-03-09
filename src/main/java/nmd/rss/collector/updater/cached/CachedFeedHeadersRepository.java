package nmd.rss.collector.updater.cached;

import nmd.rss.collector.Cache;
import nmd.rss.collector.feed.FeedHeader;
import nmd.rss.collector.updater.FeedHeadersRepository;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import static nmd.rss.collector.util.Assert.assertNotNull;
import static nmd.rss.collector.util.Assert.assertStringIsValid;

/**
 * Author : Igor Usenko ( igors48@gmail.com )
 * Date : 06.03.14
 */
public class CachedFeedHeadersRepository implements FeedHeadersRepository {

    private static final Logger LOGGER = Logger.getLogger(CachedFeedHeadersRepository.class.getName());

    public static final String KEY = "FeedHeaders";

    private final FeedHeadersRepository repository;
    private final Cache cache;

    public CachedFeedHeadersRepository(final FeedHeadersRepository repository, final Cache cache) {
        assertNotNull(repository);
        this.repository = repository;

        assertNotNull(cache);
        this.cache = cache;
    }

    @Override
    public List<FeedHeader> loadHeaders() {
        final List<FeedHeader> cached = (List<FeedHeader>) this.cache.get(KEY);

        return cached == null ? loadCache() : cached;
    }

    @Override
    public FeedHeader loadHeader(UUID feedId) {
        assertNotNull(feedId);

        final List<FeedHeader> headers = loadHeaders();

        for (final FeedHeader header : headers) {

            if (feedId.equals(header.id)) {
                return header;
            }
        }

        return null;
    }

    @Override
    public FeedHeader loadHeader(String feedLink) {
        assertStringIsValid(feedLink);

        final List<FeedHeader> headers = loadHeaders();

        for (final FeedHeader header : headers) {

            if (feedLink.equals(header.feedLink)) {
                return header;
            }
        }

        return null;
    }

    @Override
    public void storeHeader(FeedHeader feedHeader) {
        assertNotNull(feedHeader);

        this.repository.storeHeader(feedHeader);

        this.cache.delete(KEY);
    }

    @Override
    public void deleteHeader(UUID feedId) {
        assertNotNull(feedId);

        this.repository.deleteHeader(feedId);

        this.cache.delete(KEY);
    }

    private List<FeedHeader> loadCache() {
        final List<FeedHeader> headers = this.repository.loadHeaders();

        this.cache.put(KEY, headers);

        LOGGER.info("Feed headers were loaded from datastore");

        return headers;
    }

}
