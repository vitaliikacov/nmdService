package unit.feed.cache;

import nmd.orb.collector.scheduler.FeedUpdateTask;
import nmd.orb.repositories.cached.CachedFeedUpdateTasks;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

/**
 * Author : Igor Usenko ( igors48@gmail.com )
 * Date : 08.05.2014
 */
public class CachedFeedUpdateTasksTest {

    private static final int MAX_WRITES_COUNT = 2;

    private static final UUID FIRST_ID = UUID.randomUUID();
    private static final FeedUpdateTask FIRST_TASK = new FeedUpdateTask(FIRST_ID, 48);
    private static final FeedUpdateTask UPDATED_FIRST_TASK = new FeedUpdateTask(FIRST_ID, 48);

    private static final UUID SECOND_ID = UUID.randomUUID();
    private static final FeedUpdateTask SECOND_TASK = new FeedUpdateTask(SECOND_ID, 49);

    private CachedFeedUpdateTasks cachedTasks;

    @Before
    public void setUp() {
        final List<FeedUpdateTask> tasks = new ArrayList<FeedUpdateTask>() {{
            add(FIRST_TASK);
        }};

        this.cachedTasks = new CachedFeedUpdateTasks(tasks);
    }

    @Test
    public void whenTaskNotContainedInListThenTaskAdded() {
        this.cachedTasks.addOrUpdate(SECOND_TASK);

        final List<FeedUpdateTask> fromCache = this.cachedTasks.getTasks();

        assertEquals(2, fromCache.size());
        assertEquals(FIRST_TASK, fromCache.get(0));
        assertEquals(SECOND_TASK, fromCache.get(1));
    }

    @Test
    public void whenTaskContainedInListThenTaskUpdated() {
        this.cachedTasks.addOrUpdate(SECOND_TASK);
        this.cachedTasks.addOrUpdate(UPDATED_FIRST_TASK);

        final List<FeedUpdateTask> fromCache = this.cachedTasks.getTasks();

        assertEquals(2, fromCache.size());
        assertEquals(UPDATED_FIRST_TASK, fromCache.get(0));
        assertEquals(SECOND_TASK, fromCache.get(1));
    }

}
