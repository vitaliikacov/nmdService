package nmd.rss.collector.rest;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

import static nmd.rss.collector.error.ServiceError.invalidFeedId;
import static nmd.rss.collector.rest.FeedsServiceWrapper.getFeed;
import static nmd.rss.collector.rest.ResponseBody.createErrorJsonResponse;
import static nmd.rss.collector.rest.ServletTools.parseFeedId;

/**
 * Author : Igor Usenko ( igors48@gmail.com )
 * Date : 02.07.13
 */
public class ExportsServlet extends AbstractRestServlet {

    // GET /{feedId} -- get feed
    @Override
    protected ResponseBody handleGet(final HttpServletRequest request) {
        final String pathInfo = request.getPathInfo();

        final UUID feedId = parseFeedId(pathInfo);

        return feedId == null ? createErrorJsonResponse(invalidFeedId(pathInfo)) : getFeed(feedId);
    }

}
