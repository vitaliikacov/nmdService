package unit.feed.controller;

import nmd.rss.collector.controller.FeedItemsReport;
import nmd.rss.collector.controller.FeedReadReport;
import nmd.rss.collector.error.ServiceException;
import nmd.rss.collector.feed.FeedHeader;
import nmd.rss.collector.feed.FeedItem;
import nmd.rss.reader.Category;
import nmd.rss.reader.ReadFeedItems;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

/**
 * User: igu
 * Date: 26.11.13
 */
public class ControllerMarkItemAsReadTest extends AbstractControllerTestBase {

    private static final String NOT_EXISTS_ID = "not_exists_id";

    @Test
    public void whenItemMarkAsReadThenItDoesNotReturnAsNotRead() throws ServiceException {
        final FeedItem feedItem = create(1);
        final FeedHeader feedHeader = createSampleFeed(feedItem);

        this.readsService.markItemAsRead(feedHeader.id, feedItem.guid);
        final List<FeedReadReport> readReports = this.readsService.getFeedsReadReport();

        assertNull(readReports.get(0).topItemId);
    }

    @Test
    public void whenItemMarkAsReadThenItStoresInRepository() throws ServiceException {
        final FeedItem feedItem = create(1);
        final FeedHeader feedHeader = createSampleFeed(feedItem);

        this.readsService.markItemAsRead(feedHeader.id, feedItem.guid);

        final Set<String> readItems = this.readFeedItemsRepositoryStub.load(feedHeader.id).readItemIds;

        assertTrue(readItems.contains(feedItem.guid));
    }

    @Test
    public void whenItemIdDoesNotExistInFeedItemsIdListThenItDoesNotStore() throws ServiceException {
        final FeedItem feedItem = create(1);
        final FeedHeader feedHeader = createSampleFeed(feedItem);

        this.readsService.markItemAsRead(feedHeader.id, NOT_EXISTS_ID);

        final Set<String> readItems = this.readFeedItemsRepositoryStub.load(feedHeader.id).readItemIds;

        assertTrue(readItems.isEmpty());
    }

    @Test
    public void whenNotActualReadItemFoundWhileMarkingThenTheyRemoved() throws ServiceException {
        final FeedItem feedItem = create(1);
        final FeedHeader feedHeader = createSampleFeed(feedItem);

        this.readFeedItemsRepositoryStub.store(new ReadFeedItems(feedHeader.id, new Date(), new HashSet<String>() {{
            add(NOT_EXISTS_ID);
        }}, new HashSet<String>(), Category.MAIN_CATEGORY_ID));

        this.readsService.markItemAsRead(feedHeader.id, feedItem.guid);

        final Set<String> readItems = this.readFeedItemsRepositoryStub.load(feedHeader.id).readItemIds;

        assertFalse(readItems.contains(NOT_EXISTS_ID));
    }

    @Test
    public void whenItemMarkedReadThenReadLaterMarkResets() throws ServiceException {
        final FeedItem feedItem = create(1);
        final FeedHeader feedHeader = createSampleFeed(feedItem);

        this.readsService.toggleReadLaterItemMark(feedHeader.id, feedItem.guid);
        this.readsService.markItemAsRead(feedHeader.id, feedItem.guid);

        final FeedItemsReport readReport = this.readsService.getFeedItemsReport(feedHeader.id);

        assertEquals(1, readReport.reports.size());
        assertTrue(readReport.reports.get(0).read);
        assertFalse(readReport.reports.get(0).readLater);
    }

    @Test(expected = ServiceException.class)
    public void whenTryToMarkItemOfNotExistsFeedThenErrorReturns() throws ServiceException {
        this.readsService.markItemAsRead(UUID.randomUUID(), UUID.randomUUID().toString());
    }

    @Test
    public void whenThereIsNoReadItemsThenLatUpdateDataEqualsToEpoch() {
        final FeedItem first = create(1);
        final FeedItem second = create(2);
        final FeedHeader feedHeader = createSampleFeed(first, second);

        final Date lastUpdate = this.readFeedItemsRepositoryStub.load(feedHeader.id).lastUpdate;

        assertEquals(0, lastUpdate.getTime());
    }

    @Test
    public void whenReadItemTimeOlderThenLastUpdateThenLastUpdateTimeDoesNotUpdate() throws ServiceException {
        final FeedItem first = create(1);
        final FeedItem second = create(2);
        final FeedItem third = create(3);
        final FeedHeader feedHeader = createSampleFeed(first, second, third);

        this.readsService.markItemAsRead(feedHeader.id, second.guid);

        pauseOneMillisecond();

        this.readsService.markItemAsRead(feedHeader.id, first.guid);

        final Date secondDate = this.readFeedItemsRepositoryStub.load(feedHeader.id).lastUpdate;

        assertEquals(secondDate, second.date);
    }

    @Test
    public void whenReadItemTimeYoungerThenLastUpdateThenLastUpdateIsUpdated() throws ServiceException {
        final FeedItem first = create(1);
        final FeedItem second = create(2);
        final FeedItem third = create(3);
        final FeedHeader feedHeader = createSampleFeed(first, second, third);

        this.readsService.markItemAsRead(feedHeader.id, second.guid);

        pauseOneMillisecond();

        this.readsService.markItemAsRead(feedHeader.id, third.guid);

        final Date secondDate = this.readFeedItemsRepositoryStub.load(feedHeader.id).lastUpdate;

        assertEquals(secondDate, third.date);
    }

}
