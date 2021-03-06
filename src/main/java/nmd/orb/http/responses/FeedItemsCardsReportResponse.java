package nmd.orb.http.responses;

import nmd.orb.http.responses.payload.FeedItemCardReportPayload;
import nmd.orb.services.report.FeedItemReport;
import nmd.orb.services.report.FeedItemsCardsReport;

import java.util.ArrayList;
import java.util.List;

import static nmd.orb.util.Assert.guard;
import static nmd.orb.util.Parameter.notNull;

/**
 * @author : igu
 */
public class FeedItemsCardsReportResponse extends SuccessResponse {

    public String feedId;
    public String title;
    public boolean first;
    public boolean last;
    public List<FeedItemCardReportPayload> reports;

    private FeedItemsCardsReportResponse() {
        // empty
    }

    public static FeedItemsCardsReportResponse convert(final FeedItemsCardsReport report) {
        guard(notNull(report));

        final FeedItemsCardsReportResponse response = new FeedItemsCardsReportResponse();

        response.feedId = report.feedId.toString();
        response.title = report.title;
        response.first = report.first;
        response.last = report.last;

        response.reports = new ArrayList<>();

        for (final FeedItemReport itemReport : report.reports) {
            final FeedItemCardReportPayload payload = FeedItemCardReportPayload.create(itemReport);
            response.reports.add(payload);
        }

        return response;
    }

}
