package nmd.rss.collector.gae.task;

import com.google.appengine.api.datastore.Key;
import nmd.rss.collector.scheduler.FeedUpdateTask;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.UUID;

import static nmd.rss.collector.util.Assert.assertNotNull;
import static nmd.rss.collector.util.Assert.assertPositive;

/**
 * Author : Igor Usenko ( igors48@gmail.com )
 * Date : 31.05.13
 */
@Entity(name = FeedUpdateTaskEntity.NAME)
public class FeedUpdateTaskEntity {

    public static final String NAME = "FeedUpdateTask";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Key key;

    private UUID id;
    private UUID feedId;
    private int maxFeedItemsCount;

    private FeedUpdateTaskEntity() {
        // empty
    }

    private FeedUpdateTaskEntity(final UUID id, final UUID feedId, final int maxFeedItemsCount) {
        assertNotNull(id);
        this.id = id;

        assertNotNull(feedId);
        this.feedId = feedId;

        assertPositive(maxFeedItemsCount);
        this.maxFeedItemsCount = maxFeedItemsCount;
    }

    public static FeedUpdateTaskEntity convert(final FeedUpdateTask feedUpdateTask) {
        assertNotNull(feedUpdateTask);

        return new FeedUpdateTaskEntity(feedUpdateTask.id, feedUpdateTask.feedId, feedUpdateTask.maxFeedItemsCount);
    }

    public static FeedUpdateTask convert(final FeedUpdateTaskEntity feedUpdateTaskEntity) {
        assertNotNull(feedUpdateTaskEntity);

        return new FeedUpdateTask(feedUpdateTaskEntity.id, feedUpdateTaskEntity.feedId, feedUpdateTaskEntity.maxFeedItemsCount);
    }

}