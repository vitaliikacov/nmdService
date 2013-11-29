package unit.feed.controller;

import nmd.rss.collector.controller.ControlServiceException;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertTrue;

/**
 * User: igu
 * Date: 29.11.13
 */
public class ControllerClearTest extends AbstractControllerTest {

    @Before
    @Override
    public void before() throws ControlServiceException {
        super.before();

        final UUID firstFeedId = addValidFirstRssFeed();
        final UUID secondFeedId = addValidSecondRssFeed();

        this.controlService.markItemAsRead(firstFeedId, "read_first");
        this.controlService.markItemAsRead(secondFeedId, "read_second");

        this.controlService.clear();
    }

    @Test
    public void whenClearedThenNoHeadersRemain() {
        assertTrue(this.feedHeadersRepositoryStub.isEmpty());
    }

    @Test
    public void whenClearedThenNoItemsRemain() {
        assertTrue(this.feedItemsRepositoryStub.isEmpty());
    }

    @Test
    public void whenClearedThenNoUpdateTasksRemain() {
        assertTrue(this.feedUpdateTaskRepositoryStub.isEmpty());
    }

    @Test
    public void whenClearedThenNoReadItemsRemain() {
        assertTrue(this.readFeedItemsRepositoryStub.isEmpty());
    }

    @Test
    public void whenClearedThenNoSchedulerContextRemain() {
        assertTrue(this.feedUpdateTaskSchedulerContextRepositoryStub.isEmpty());
    }

}