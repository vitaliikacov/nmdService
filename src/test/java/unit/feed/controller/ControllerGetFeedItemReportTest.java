package unit.feed.controller;

import nmd.orb.feed.FeedItem;
import nmd.orb.reader.Category;
import nmd.orb.reader.ReadFeedItems;
import nmd.orb.services.ReadsService;
import nmd.orb.services.report.FeedItemReport;
import org.junit.Test;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Author : Igor Usenko ( igors48@gmail.com )
 * Date : 05.08.2014
 */
public class ControllerGetFeedItemReportTest {

    private static final FeedItem FEED_ITEM = new FeedItem("title", "description", "http://domain.com", "http://domain-goto.com", new Date(4), true, "guid");
    private static final UUID FEED_ID = UUID.randomUUID();

    @Test
    public void whenFeedItemReportCreatedThenBasicParametersSetCorrectly() {
        final FeedItemReport report = ReadsService.getFeedItemReport(FEED_ID, ReadFeedItems.empty(UUID.randomUUID()), FEED_ITEM, 0, 0);

        assertEquals(FEED_ID, report.feedId);

        assertEquals(FEED_ITEM.title, report.title);
        assertEquals(FEED_ITEM.description, report.description);
        assertEquals(FEED_ITEM.gotoLink, report.link);
        assertEquals(FEED_ITEM.date, report.date);
        assertEquals(FEED_ITEM.guid, report.itemId);
    }

    @Test
    public void whenFeedItemIdIsContainedInReadsListThenReportIsMarkedAsRead() {
        final Set<String> readFeedItemsIds = new HashSet<>();
        readFeedItemsIds.add(FEED_ITEM.guid);

        final Set<String> readLaterFeedItemsIds = new HashSet<>();

        final ReadFeedItems readFeedItems = new ReadFeedItems(FEED_ID, new Date(), readFeedItemsIds, readLaterFeedItemsIds, Category.MAIN_CATEGORY_ID);

        final FeedItemReport report = ReadsService.getFeedItemReport(FEED_ID, readFeedItems, FEED_ITEM, 0, 0);

        assertTrue(report.read);
    }

    @Test
    public void whenFeedItemIdIsNotContainedInReadsListThenReportIsMarkedAsRead() {
        final Set<String> readFeedItemsIds = new HashSet<>();

        final Set<String> readLaterFeedItemsIds = new HashSet<>();

        final ReadFeedItems readFeedItems = new ReadFeedItems(FEED_ID, new Date(), readFeedItemsIds, readLaterFeedItemsIds, Category.MAIN_CATEGORY_ID);

        final FeedItemReport report = ReadsService.getFeedItemReport(FEED_ID, readFeedItems, FEED_ITEM, 0, 0);

        assertFalse(report.read);
    }

    @Test
    public void whenFeedItemIdIsContainedInReadsLaterListThenReportIsMarkedAsReadLater() {
        final Set<String> readFeedItemsIds = new HashSet<>();

        final Set<String> readLaterFeedItemsIds = new HashSet<>();
        readLaterFeedItemsIds.add(FEED_ITEM.guid);

        final ReadFeedItems readFeedItems = new ReadFeedItems(FEED_ID, new Date(), readFeedItemsIds, readLaterFeedItemsIds, Category.MAIN_CATEGORY_ID);

        final FeedItemReport report = ReadsService.getFeedItemReport(FEED_ID, readFeedItems, FEED_ITEM, 0, 0);

        assertTrue(report.readLater);
    }

    @Test
    public void whenFeedItemIdIsNotContainedInReadsLaterListThenReportIsMarkedAsReadLater() {
        final Set<String> readFeedItemsIds = new HashSet<>();

        final Set<String> readLaterFeedItemsIds = new HashSet<>();

        final ReadFeedItems readFeedItems = new ReadFeedItems(FEED_ID, new Date(), readFeedItemsIds, readLaterFeedItemsIds, Category.MAIN_CATEGORY_ID);

        final FeedItemReport report = ReadsService.getFeedItemReport(FEED_ID, readFeedItems, FEED_ITEM, 0, 0);

        assertFalse(report.readLater);
    }

    @Test
    public void whenFeedItemDateOlderThanLastUpdateDateThenReportIsNotMarkedAsAddedSinceLastView() {
        final Set<String> readFeedItemsIds = new HashSet<>();
        final Set<String> readLaterFeedItemsIds = new HashSet<>();

        final ReadFeedItems readFeedItems = new ReadFeedItems(FEED_ID, new Date(6), readFeedItemsIds, readLaterFeedItemsIds, Category.MAIN_CATEGORY_ID);

        final FeedItemReport report = ReadsService.getFeedItemReport(FEED_ID, readFeedItems, FEED_ITEM, 0, 0);

        assertFalse(report.addedSinceLastView);
    }

    @Test
    public void whenFeedItemDateSameAsLastUpdateDateThenReportIsNotMarkedAsAddedSinceLastView() {
        final Set<String> readFeedItemsIds = new HashSet<>();
        final Set<String> readLaterFeedItemsIds = new HashSet<>();

        final ReadFeedItems readFeedItems = new ReadFeedItems(FEED_ID, FEED_ITEM.date, readFeedItemsIds, readLaterFeedItemsIds, Category.MAIN_CATEGORY_ID);

        final FeedItemReport report = ReadsService.getFeedItemReport(FEED_ID, readFeedItems, FEED_ITEM, 0, 0);

        assertFalse(report.addedSinceLastView);
    }

    @Test
    public void whenFeedItemDateYoungerThanLastUpdateDateThenReportIsMarkedAsAddedSinceLastView() {
        final Set<String> readFeedItemsIds = new HashSet<>();
        final Set<String> readLaterFeedItemsIds = new HashSet<>();

        final ReadFeedItems readFeedItems = new ReadFeedItems(FEED_ID, new Date(3), readFeedItemsIds, readLaterFeedItemsIds, Category.MAIN_CATEGORY_ID);

        final FeedItemReport report = ReadsService.getFeedItemReport(FEED_ID, readFeedItems, FEED_ITEM, 0, 0);

        assertTrue(report.addedSinceLastView);
    }

    @Test
    public void whenReportCreatedThanIndexAndTotalValuesAreSetCorrectly() {
        final Set<String> readFeedItemsIds = new HashSet<>();
        final Set<String> readLaterFeedItemsIds = new HashSet<>();

        final ReadFeedItems readFeedItems = new ReadFeedItems(FEED_ID, new Date(3), readFeedItemsIds, readLaterFeedItemsIds, Category.MAIN_CATEGORY_ID);

        final int index = 48;
        final int total = 49;

        final FeedItemReport report = ReadsService.getFeedItemReport(FEED_ID, readFeedItems, FEED_ITEM, index, total);

        assertEquals(index, report.index);
        assertEquals(total, report.total);
    }

}
