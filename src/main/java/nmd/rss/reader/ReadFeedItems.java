package nmd.rss.reader;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static nmd.rss.collector.util.Assert.assertNotNull;

/**
 * User: igu
 * Date: 25.12.13
 */
public class ReadFeedItems {

    public static final ReadFeedItems EMPTY = new ReadFeedItems(new Date(), new HashSet<String>());

    public final Date lastUpdate;
    public final Set<String> itemIds;

    public ReadFeedItems(final Date lastUpdate, final Set<String> itemIds) {
        assertNotNull(lastUpdate);
        this.lastUpdate = lastUpdate;

        assertNotNull(itemIds);
        this.itemIds = itemIds;
    }

}