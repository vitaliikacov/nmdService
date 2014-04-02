package nmd.rss.collector.rest.responses;

import nmd.rss.collector.controller.FeedSeriesUpdateReport;
import nmd.rss.collector.controller.FeedUpdateReport;
import nmd.rss.collector.error.ServiceError;
import nmd.rss.collector.rest.responses.helper.ErrorResponseHelper;
import nmd.rss.collector.rest.responses.helper.FeedMergeReportResponseHelper;

import java.util.ArrayList;
import java.util.List;

import static nmd.rss.collector.util.Assert.assertNotNull;

/**
 * Author : Igor Usenko ( igors48@gmail.com )
 * Date : 21.03.2014
 */
public class FeedSeriesUpdateResponse extends SuccessResponse {

    private List<FeedMergeReportResponseHelper> updates;
    private List<ErrorResponseHelper> errors;

    public FeedSeriesUpdateResponse(final List<FeedMergeReportResponseHelper> updates, final List<ErrorResponseHelper> errors) {
        assertNotNull(updates);
        this.updates = updates;

        assertNotNull(errors);
        this.errors = errors;
    }

    public List<FeedMergeReportResponseHelper> getUpdates() {
        return this.updates;
    }

    public List<ErrorResponseHelper> getErrors() {
        return this.errors;
    }

    public static FeedSeriesUpdateResponse convert(final FeedSeriesUpdateReport report) {
        assertNotNull(report);

        final List<FeedMergeReportResponseHelper> updates = new ArrayList<>();

        for (final FeedUpdateReport update : report.updated) {
            final FeedMergeReportResponseHelper helper = FeedMergeReportResponseHelper.convert(update);

            updates.add(helper);
        }

        final List<ErrorResponseHelper> errors = new ArrayList<>();

        for (final ServiceError error : report.errors) {
            final ErrorResponseHelper helper = ErrorResponseHelper.create(error);

            errors.add(helper);
        }

        return new FeedSeriesUpdateResponse(updates, errors);
    }

}