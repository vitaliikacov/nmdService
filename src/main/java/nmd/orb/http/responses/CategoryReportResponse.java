package nmd.orb.http.responses;

import nmd.orb.http.responses.payload.CategoryExtendedReportPayload;
import nmd.orb.services.report.CategoryReport;

import static nmd.orb.util.Assert.guard;
import static nmd.orb.util.Parameter.notNull;

/**
 * Author : Igor Usenko ( igors48@gmail.com )
 * Date : 23.03.2014
 */
public class CategoryReportResponse extends SuccessResponse {

    public CategoryExtendedReportPayload report;

    private CategoryReportResponse() {
        // empty
    }

    public static CategoryReportResponse create(final CategoryReport report) {
        guard(notNull(report));

        final CategoryReportResponse categoryReportResponse = new CategoryReportResponse();

        categoryReportResponse.report = CategoryExtendedReportPayload.create(report);

        return categoryReportResponse;
    }

}
