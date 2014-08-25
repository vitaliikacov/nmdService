package nmd.rss.gae.repositories;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import nmd.rss.collector.scheduler.FeedUpdateTask;
import nmd.rss.collector.scheduler.FeedUpdateTaskRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static nmd.rss.collector.util.Assert.assertNotNull;
import static nmd.rss.gae.repositories.converters.FeedUpdateTaskEntityConverter.convert;
import static nmd.rss.gae.repositories.datastore.GaeDatastoreTools.*;
import static nmd.rss.gae.repositories.datastore.Kind.FEED_UPDATE_TASK;
import static nmd.rss.gae.repositories.datastore.RootKind.FEED;

/**
 * User: igu
 * Date: 16.10.13
 */
public class GaeFeedUpdateTaskRepository implements FeedUpdateTaskRepository {

    public static final FeedUpdateTaskRepository GAE_FEED_UPDATE_TASK_REPOSITORY = new GaeFeedUpdateTaskRepository();

    @Override
    public List<FeedUpdateTask> loadAllTasks() {
        final List<Entity> entities = loadEntities(FEED_UPDATE_TASK);

        final List<FeedUpdateTask> tasks = new ArrayList<>(entities.size());

        for (final Entity entity : entities) {
            final FeedUpdateTask feedUpdateTask = convert(entity);

            tasks.add(feedUpdateTask);
        }

        return tasks;
    }

    @Override
    public void storeTask(final FeedUpdateTask feedUpdateTask) {
        assertNotNull(feedUpdateTask);

        deleteTaskForFeedId(feedUpdateTask.feedId);

        final Key feedRootKey = getEntityRootKey(feedUpdateTask.feedId.toString(), FEED);
        final Entity entity = convert(feedUpdateTask, feedRootKey);

        DATASTORE_SERVICE.put(entity);
    }

    @Override
    public void updateTask(final FeedUpdateTask feedUpdateTask) {
        storeTask(feedUpdateTask);
    }

    @Override
    public FeedUpdateTask loadTaskForFeedId(final UUID feedId) {
        assertNotNull(feedId);

        final Entity entity = loadEntity(feedId.toString(), FEED, FEED_UPDATE_TASK, false);

        return entity == null ? null : convert(entity);
    }

    @Override
    public void deleteTaskForFeedId(final UUID feedId) {
        assertNotNull(feedId);

        deleteEntity(feedId.toString(), FEED, FEED_UPDATE_TASK);
    }

    private GaeFeedUpdateTaskRepository() {
        // empty
    }

}
