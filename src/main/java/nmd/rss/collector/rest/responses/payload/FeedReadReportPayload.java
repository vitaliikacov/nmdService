package nmd.rss.collector.rest.responses.payload;

import nmd.rss.collector.controller.FeedReadReport;

import static nmd.rss.collector.util.Assert.guard;
import static nmd.rss.collector.util.Parameter.notNull;

/**
 * Author : Igor Usenko ( igors48@gmail.com )
 * Date : 27.11.13
 */
public class FeedReadReportPayload {

    public String feedId;
    public String feedType;
    public String feedTitle;
    public int read;
    public int notRead;
    public int readLater;
    public int addedFromLastVisit;
    public String topItemId;
    public String topItemLink;
    public long lastUpdate;

    private FeedReadReportPayload() {
        // empty
    }

    public static FeedReadReportPayload create(final FeedReadReport feedReadReport) {
        guard(notNull(feedReadReport));

        final FeedReadReportPayload feedReadReportPayload = new FeedReadReportPayload();

        feedReadReportPayload.feedId = feedReadReport.feedId.toString();
        feedReadReportPayload.feedType = feedReadReport.feedType.toString();
        feedReadReportPayload.feedTitle = feedReadReport.feedTitle;
        feedReadReportPayload.read = feedReadReport.read;
        feedReadReportPayload.notRead = feedReadReport.notRead;
        feedReadReportPayload.readLater = feedReadReport.readLater;
        feedReadReportPayload.addedFromLastVisit = feedReadReport.addedFromLastVisit;
        feedReadReportPayload.topItemId = feedReadReport.topItemId == null ? "" : feedReadReport.topItemId;
        feedReadReportPayload.topItemLink = feedReadReport.topItemLink == null ? "" : feedReadReport.topItemLink;
        feedReadReportPayload.lastUpdate = feedReadReport.lastUpdate.getTime();

        return feedReadReportPayload;
    }

}
