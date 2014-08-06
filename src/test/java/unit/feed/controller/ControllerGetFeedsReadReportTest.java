package unit.feed.controller;

import nmd.rss.collector.controller.FeedReadReport;
import nmd.rss.collector.error.ServiceException;
import nmd.rss.collector.feed.FeedHeader;
import nmd.rss.collector.feed.FeedItem;
import nmd.rss.collector.feed.FeedItemsMergeReport;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * User: igu
 * Date: 22.10.13
 */
public class ControllerGetFeedsReadReportTest extends AbstractControllerTestBase {

    @Test
    public void whenNoFeedsThenEmptyReportReturns() throws ServiceException {
        final List<FeedReadReport> report = this.readsService.getFeedsReadReport();

        assertTrue(report.isEmpty());
    }

    @Test
    public void whenFeedExistsThenItIsParticipateInReport() throws ServiceException {
        final FeedItem feedItem = create(1);
        final FeedHeader feedHeader = createSampleFeed(feedItem);

        final List<FeedReadReport> report = this.readsService.getFeedsReadReport();

        assertEquals(1, report.size());

        final FeedReadReport reportItem = report.get(0);

        assertEquals(feedHeader.id, reportItem.feedId);
        assertEquals(feedHeader.title, reportItem.feedTitle);
        assertEquals(1, reportItem.notRead);
        assertEquals(0, reportItem.read);
    }

    @Test
    public void whenNotReadItemExistsThenItReturns() {
        final FeedItem feedItem = create(1);
        createSampleFeed(feedItem);

        final List<FeedReadReport> readReports = this.readsService.getFeedsReadReport();

        assertEquals(feedItem.guid, readReports.get(0).topItemId);
        assertEquals(feedItem.link, readReports.get(0).topItemLink);
    }

    @Test
    public void whenFeedItemAddedAfterLastVisitThenItIncludedInAddedFromLastVisitCounter() throws ServiceException {
        final FeedItem first = create(1);

        final FeedHeader feedHeader = createSampleFeed(first);

        this.readsService.markItemAsRead(feedHeader.id, first.guid);

        pauseOneMillisecond();

        final FeedItem second = create(2);

        final FeedItemsMergeReport feedItemsMergeReport = new FeedItemsMergeReport(
                new ArrayList<FeedItem>(),
                new ArrayList<FeedItem>() {{
                    add(first);
                }},
                new ArrayList<FeedItem>() {{
                    add(second);
                }}
        );
        this.feedItemsRepositoryStub.storeItems(feedHeader.id, feedItemsMergeReport.getAddedAndRetained());

        final FeedReadReport firstReport = this.readsService.getFeedsReadReport().get(0);

        assertEquals(1, firstReport.addedFromLastVisit);
    }

}
