package nmd.rss.collector.gae.persistence;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import nmd.rss.collector.feed.FeedHeader;
import nmd.rss.collector.updater.FeedHeadersRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.google.appengine.api.datastore.FetchOptions.Builder.withLimit;
import static com.google.appengine.api.datastore.Query.FilterOperator.EQUAL;
import static java.lang.Integer.MAX_VALUE;
import static nmd.rss.collector.gae.persistence.FeedHeaderEntityConverter.FEED_LINK;
import static nmd.rss.collector.gae.persistence.FeedHeaderEntityConverter.KIND;
import static nmd.rss.collector.gae.persistence.GaeRootRepository.DATASTORE_SERVICE;
import static nmd.rss.collector.gae.persistence.GaeRootRepository.getFeedRootKey;
import static nmd.rss.collector.util.Assert.assertNotNull;

/**
 * User: igu
 * Date: 15.10.13
 */
public class GaeFeedHeadersRepository implements FeedHeadersRepository {

    @Override
    public FeedHeader loadHeader(final UUID feedId) {
        assertNotNull(feedId);

        final Entity entity = getEntity(feedId);

        return FeedHeaderEntityConverter.convert(entity);
    }

    @Override
    public List<FeedHeader> loadHeaders() {
        final Query query = new Query(KIND);
        final PreparedQuery preparedQuery = DATASTORE_SERVICE.prepare(query);

        final List<Entity> entities = preparedQuery.asList(withLimit(MAX_VALUE));

        final List<FeedHeader> headers = new ArrayList<>(entities.size());

        for (final Entity entity : entities) {
            final FeedHeader feedHeader = FeedHeaderEntityConverter.convert(entity);

            headers.add(feedHeader);
        }

        return headers;
    }

    @Override
    public void deleteHeader(final UUID feedId) {
        assertNotNull(feedId);

        final Entity entity = getEntity(feedId);

        if (entity != null) {
            DATASTORE_SERVICE.delete(entity.getKey());
        }
    }

    @Override
    public FeedHeader loadHeader(final String feedLink) {
        final Query query = new Query(KIND)
                .setFilter(new Query.FilterPredicate(FEED_LINK, EQUAL, feedLink));
        final PreparedQuery preparedQuery = DATASTORE_SERVICE.prepare(query);

        final Entity entity = preparedQuery.asSingleEntity();

        return entity == null ? null : FeedHeaderEntityConverter.convert(entity);
    }

    @Override
    public void storeHeader(final FeedHeader feedHeader) {
        assertNotNull(feedHeader);

        final Entity entity = FeedHeaderEntityConverter.convert(feedHeader, getFeedRootKey(feedHeader.id));

        DATASTORE_SERVICE.put(entity);
    }

    private Entity getEntity(final UUID feedId) {
        final Query query = new Query(KIND).setAncestor(getFeedRootKey(feedId));
        final PreparedQuery preparedQuery = DATASTORE_SERVICE.prepare(query);

        return preparedQuery.asSingleEntity();
    }

}
