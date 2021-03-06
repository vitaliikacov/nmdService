package nmd.orb.gae.repositories.converters;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Text;
import nmd.orb.feed.FeedItem;

import java.util.List;
import java.util.UUID;

import static nmd.orb.gae.repositories.datastore.Kind.FEED_ITEM;
import static nmd.orb.util.Assert.assertNotNull;

/**
 * Author : Igor Usenko ( igors48@gmail.com )
 * Date: 21.10.13
 */
public class FeedItemListEntityConverter {

    private static final String FEED_ID = "feedId";
    private static final String COUNT = "count";
    private static final String ITEMS = "items";

    public static Entity convert(final Key feedKey, final UUID feedId, final List<FeedItem> feedItems) {
        assertNotNull(feedId);
        assertNotNull(feedItems);

        final Entity entity = new Entity(FEED_ITEM.value, feedKey);

        entity.setProperty(FEED_ID, feedId.toString());
        entity.setProperty(COUNT, feedItems.size());
        entity.setProperty(ITEMS, new Text(FeedItemListConverter.convert(feedItems)));

        return entity;
    }

    public static List<FeedItem> convert(final Entity entity) {
        assertNotNull(entity);

        final String items = ((Text) entity.getProperty(ITEMS)).getValue();

        return FeedItemListConverter.convert(items);
    }

}
