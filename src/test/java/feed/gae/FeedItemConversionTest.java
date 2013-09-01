package feed.gae;

import nmd.rss.collector.feed.FeedItem;
import nmd.rss.collector.gae.feed.item.FeedItemHelper;
import org.junit.Test;

import java.util.Date;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

/**
 * Author : Igor Usenko ( igors48@gmail.com )
 * Date : 08.05.13
 */
public class FeedItemConversionTest {

    @Test
    public void roundtrip() {
        FeedItem origin = new FeedItem("title", "description", "link", new Date(), UUID.randomUUID().toString());

        FeedItemHelper itemHelper = FeedItemHelper.convert(origin);
        FeedItem restored = FeedItemHelper.convert(itemHelper);

        assertEquals(origin, restored);
    }

}
