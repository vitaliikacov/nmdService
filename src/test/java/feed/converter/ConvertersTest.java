package feed.converter;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import nmd.rss.collector.feed.FeedHeader;
import nmd.rss.collector.feed.FeedItem;
import nmd.rss.collector.gae.persistence.*;
import nmd.rss.collector.scheduler.FeedUpdateTask;
import nmd.rss.reader.gae.ReadFeedIdSetConverter;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * User: igu
 * Date: 18.10.13
 */
public class ConvertersTest {

    private static final Key SAMPLE_KEY = KeyFactory.stringToKey("ag9zfnJzcy1jb2xsZWN0b3JyHQsSEEZlZWRIZWFkZXJFbnRpdHkYgICAgIi0vwgM");

    private static final FeedItem FIRST_FEED_ITEM = new FeedItem("title-first", "description-first", "link-first", new Date(), "guid-first");
    private static final FeedItem SECOND_FEED_ITEM = new FeedItem("title-second", "description-second", "link-second", new Date(), "guid-second");

    private static final List<FeedItem> FEED_LIST = Arrays.asList(FIRST_FEED_ITEM, SECOND_FEED_ITEM);

    private static final String FIRST_READ_ITEM_ID = "first";
    private static final String SECOND_READ_ITEM_ID = "second";
    private static final Set<String> READ_FEED_ITEMS = new HashSet<String>() {{
        add(FIRST_READ_ITEM_ID);
        add(SECOND_READ_ITEM_ID);
    }};

    @Test
    public void feedHeaderEntityRoundtrip() {
        final FeedHeader origin = new FeedHeader(UUID.randomUUID(), "feedLink", "title", "description", "link");

        final Entity entity = FeedHeaderEntityConverter.convert(origin, SAMPLE_KEY);

        final FeedHeader restored = FeedHeaderEntityConverter.convert(entity);

        assertEquals(origin, restored);
    }

    @Test
    public void feedUpdateTaskEntityRoundtrip() {
        final FeedUpdateTask origin = new FeedUpdateTask(UUID.randomUUID(), 1000);

        final Entity entity = FeedUpdateTaskEntityConverter.convert(origin, SAMPLE_KEY);

        final FeedUpdateTask restored = FeedUpdateTaskEntityConverter.convert(entity);

        assertEquals(origin, restored);
    }

    @Test
    public void feedItemHelperRoundtrip() {
        final FeedItemHelper helper = FeedItemHelper.convert(FIRST_FEED_ITEM);
        final FeedItem restored = FeedItemHelper.convert(helper);

        assertEquals(FIRST_FEED_ITEM, restored);
    }

    @Test
    public void feedItemListRoundtrip() {
        final String converted = FeedItemListConverter.convert(FEED_LIST);
        final List<FeedItem> restored = FeedItemListConverter.convert(converted);

        assertEquals(FEED_LIST.size(), restored.size());
        assertEquals(FIRST_FEED_ITEM, restored.get(0));
        assertEquals(SECOND_FEED_ITEM, restored.get(1));
    }

    @Test
    public void feedItemListEntityRoundtrip() {
        final Entity entity = FeedItemListEntityConverter.convert(SAMPLE_KEY, UUID.randomUUID(), FEED_LIST);

        final List<FeedItem> restored = FeedItemListEntityConverter.convert(entity);

        assertEquals(FEED_LIST.size(), restored.size());
        assertEquals(FIRST_FEED_ITEM, restored.get(0));
        assertEquals(SECOND_FEED_ITEM, restored.get(1));
    }

    @Test
    public void readFeedItemsEntityRoundtrip() {
        final Entity entity = ReadFeedIdSetConverter.convert(SAMPLE_KEY, UUID.randomUUID(), READ_FEED_ITEMS);

        final Set<String> restored = ReadFeedIdSetConverter.convert(entity);

        assertEquals(READ_FEED_ITEMS.size(), restored.size());
        assertTrue(restored.contains(FIRST_READ_ITEM_ID));
        assertTrue(restored.contains(SECOND_READ_ITEM_ID));
    }

}