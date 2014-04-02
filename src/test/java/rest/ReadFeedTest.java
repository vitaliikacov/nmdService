package rest;

import nmd.rss.collector.error.ErrorCode;
import nmd.rss.collector.rest.responses.FeedIdResponse;
import nmd.rss.collector.rest.responses.FeedItemsReportResponse;
import nmd.rss.collector.rest.responses.FeedReadReportsResponse;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Author : Igor Usenko ( igors48@gmail.com )
 * Date : 03.12.13
 */
public class ReadFeedTest extends AbstractRestTest {

    @Test
    public void whenFeedExistsThenReadReportReturns() {
        addFirstFeed();

        final FeedReadReportsResponse response = getReadsReport();

        assertFalse(response.reports.isEmpty());
    }

    @Test
    public void whenThereAreNoFeedsThenNoReportsReturn() {
        final FeedReadReportsResponse response = getReadsReport();

        assertTrue(response.reports.isEmpty());
    }

    @Test
    public void whenFeedIdIsInvalidThenErrorReturns() {
        assertErrorResponse(getFeedItemsReportAsString("111"), ErrorCode.INVALID_FEED_ID);
    }

    @Test
    public void whenFeedIdIsNotFoundThenErrorReturns() {
        assertErrorResponse(getFeedItemsReportAsString(UUID.randomUUID().toString()), ErrorCode.WRONG_FEED_ID);
    }

    @Test
    public void whenFeedIdIsFoundThenReportReturns() {
        final FeedIdResponse feedIdResponse = addFirstFeed();

        final FeedItemsReportResponse feedItemsReportResponse = getFeedItemsReport(feedIdResponse.feedId.toString());

        assertFalse(feedItemsReportResponse.title.isEmpty());
        assertFalse(feedItemsReportResponse.reports.isEmpty());

        assertEquals(0, feedItemsReportResponse.read);
        assertEquals(0, feedItemsReportResponse.readLater);
        assertTrue(feedItemsReportResponse.notRead > 0);
    }

}
