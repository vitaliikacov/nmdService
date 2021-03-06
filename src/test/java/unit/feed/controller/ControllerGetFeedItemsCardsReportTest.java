package unit.feed.controller;

import nmd.orb.error.ServiceException;
import nmd.orb.feed.FeedHeader;
import nmd.orb.feed.FeedItem;
import nmd.orb.services.report.FeedItemsCardsReport;
import nmd.orb.util.Direction;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Author : Igor Usenko ( igors48@gmail.com )
 * Date : 05.08.2014
 */
public class ControllerGetFeedItemsCardsReportTest extends AbstractControllerTestBase {

    @Test(expected = ServiceException.class)
    public void whenFeedNotFoundThenExceptionThrows() throws ServiceException {
        this.readsService.getFeedItemsCardsReport(UUID.randomUUID(), UUID.randomUUID().toString(), 5, Direction.NEXT);
    }

    @Test
    public void whenFeedItemsCardsReportReturnsThenReportItemsSortFromNewToOld() throws ServiceException {
        final FeedItem first = create(1);
        final FeedItem second = create(2);
        final FeedItem third = create(3);
        final FeedHeader feedHeader = createSampleFeed(first, second, third);

        final FeedItemsCardsReport feedItemsCardsReport = this.readsService.getFeedItemsCardsReport(feedHeader.id, second.guid, 2, Direction.NEXT);

        assertTrue(feedItemsCardsReport.reports.get(0).date.getTime() > feedItemsCardsReport.reports.get(1).date.getTime());
    }

    @Test
    public void whenFeedItemsCardsReportReturnsThenFeedTitleSetCorrectly() throws ServiceException {
        final FeedItem first = create(1);
        final FeedItem second = create(2);
        final FeedItem third = create(3);
        final FeedHeader feedHeader = createSampleFeed(first, second, third);

        final FeedItemsCardsReport feedItemsCardsReport = this.readsService.getFeedItemsCardsReport(feedHeader.id, second.guid, 2, Direction.NEXT);

        assertEquals(feedHeader.title, feedItemsCardsReport.title);
    }

    @Test
    public void whenFeedItemsCardsReportReturnsThenFeedIdSetCorrectly() throws ServiceException {
        final FeedItem first = create(1);
        final FeedItem second = create(2);
        final FeedItem third = create(3);
        final FeedHeader feedHeader = createSampleFeed(first, second, third);

        final FeedItemsCardsReport feedItemsCardsReport = this.readsService.getFeedItemsCardsReport(feedHeader.id, second.guid, 2, Direction.NEXT);

        assertEquals(feedHeader.id, feedItemsCardsReport.feedId);
    }

}
