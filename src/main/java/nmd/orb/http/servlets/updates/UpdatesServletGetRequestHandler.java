package nmd.orb.http.servlets.updates;

import nmd.orb.http.Handler;
import nmd.orb.http.tools.ResponseBody;
import nmd.orb.http.wrappers.UpdatesServiceWrapperImpl;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static nmd.orb.error.ServiceError.invalidFeedId;
import static nmd.orb.feed.FeedHeader.isValidFeedHeaderId;
import static nmd.orb.http.tools.ResponseBody.createErrorJsonResponse;
import static nmd.orb.http.tools.ServletTools.parseFeedId;
import static nmd.orb.util.Assert.guard;
import static nmd.orb.util.Parameter.notNull;

/**
 * Author : Igor Usenko ( igors48@gmail.com )
 * Date : 14.08.2014
 */
public class UpdatesServletGetRequestHandler implements Handler {

    public static final UpdatesServletGetRequestHandler UPDATES_SERVLET_GET_REQUEST_HANDLER = new UpdatesServletGetRequestHandler(UpdatesServiceWrapperImpl.UPDATES_SERVICE_WRAPPER);

    private final UpdatesServiceWrapperImpl updatesService;

    public UpdatesServletGetRequestHandler(final UpdatesServiceWrapperImpl updatesService) {
        guard(notNull(updatesService));
        this.updatesService = updatesService;
    }

    // GET -- update current feed
    // GET /{feedId} -- update feed
    @Override
    public ResponseBody handle(final List<String> elements, final Map<String, String> parameters, final String body) {
        guard(notNull(elements));
        guard(notNull(parameters));
        guard(notNull(body));

        if (elements.isEmpty()) {
            return this.updatesService.updateCurrentFeeds();
        }

        final String pathInfo = elements.get(0);
        final UUID feedId = parseFeedId(pathInfo);

        return isValidFeedHeaderId(feedId) ? this.updatesService.updateFeed(feedId) : createErrorJsonResponse(invalidFeedId(pathInfo));
    }

}