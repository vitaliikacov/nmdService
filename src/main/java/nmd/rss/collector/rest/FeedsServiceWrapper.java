package nmd.rss.collector.rest;

import nmd.rss.collector.Transactions;
import nmd.rss.collector.controller.FeedsService;
import nmd.rss.collector.error.ServiceError;
import nmd.rss.collector.error.ServiceException;
import nmd.rss.collector.exporter.FeedExporterException;
import nmd.rss.collector.feed.Feed;
import nmd.rss.collector.feed.FeedHeader;
import nmd.rss.collector.gae.fetcher.GaeUrlFetcher;
import nmd.rss.collector.gae.persistence.GaeFeedHeadersRepository;
import nmd.rss.collector.gae.persistence.GaeFeedItemsRepository;
import nmd.rss.collector.gae.persistence.GaeFeedUpdateTaskRepository;
import nmd.rss.collector.gae.persistence.GaeRootRepository;
import nmd.rss.collector.gae.updater.GaeCacheFeedUpdateTaskSchedulerContextRepository;
import nmd.rss.collector.rest.responses.FeedHeaderResponse;
import nmd.rss.collector.rest.responses.FeedHeadersResponse;
import nmd.rss.collector.rest.responses.FeedIdResponse;
import nmd.rss.collector.scheduler.FeedUpdateTaskRepository;
import nmd.rss.collector.scheduler.FeedUpdateTaskSchedulerContextRepository;
import nmd.rss.collector.updater.FeedHeadersRepository;
import nmd.rss.collector.updater.FeedItemsRepository;
import nmd.rss.collector.updater.UrlFetcher;
import nmd.rss.reader.ReadFeedItemsRepository;
import nmd.rss.reader.gae.GaeReadFeedItemsRepository;

import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.String.format;
import static nmd.rss.collector.exporter.FeedExporter.export;
import static nmd.rss.collector.rest.ResponseBody.createErrorJsonResponse;
import static nmd.rss.collector.rest.ResponseBody.createJsonResponse;
import static nmd.rss.collector.rest.responses.SuccessMessageResponse.create;

/**
 * Author : Igor Usenko ( igors48@gmail.com )
 * Date : 22.06.13
 */
public class FeedsServiceWrapper {

    private static final Logger LOGGER = Logger.getLogger(FeedsServiceWrapper.class.getName());

    private static final FeedsService FEEDS_SERVICE = createFeedsService();

    public static ResponseBody addFeed(final String feedUrl) {

        try {
            final UUID feedId = FEEDS_SERVICE.addFeed(feedUrl);

            final FeedIdResponse feedIdResponse = FeedIdResponse.create(feedId);

            LOGGER.info(format("Feed [ %s ] added. Id is [ %s ]", feedUrl, feedId));

            return createJsonResponse(feedIdResponse);
        } catch (ServiceException exception) {
            LOGGER.log(Level.SEVERE, format("Error adding feed [ %s ]", feedUrl), exception);

            return createErrorJsonResponse(exception);
        }
    }

    public static ResponseBody updateFeedTitle(final UUID feedId, final String title) {

        try {
            FEEDS_SERVICE.updateFeedTitle(feedId, title);

            final String message = format("Feeds [ %s ] title changed to [ %s ]", feedId, title);

            LOGGER.info(message);

            return createJsonResponse(create(message));
        } catch (ServiceException exception) {
            LOGGER.log(Level.SEVERE, format("Error changing feed [ %s ] title", feedId), exception);

            return createErrorJsonResponse(exception);
        }
    }

    public static ResponseBody removeFeed(final UUID feedId) {
        FEEDS_SERVICE.removeFeed(feedId);

        final String message = format("Feed [ %s ] removed", feedId);

        LOGGER.info(message);

        return createJsonResponse(create(message));
    }

    public static ResponseBody getFeedHeaders() {
        final List<FeedHeader> headers = FEEDS_SERVICE.getFeedHeaders();
        final FeedHeadersResponse feedHeadersResponse = FeedHeadersResponse.convert(headers);

        LOGGER.info(format("[ %s ] feed headers found", headers.size()));

        return createJsonResponse(feedHeadersResponse);
    }

    public static ResponseBody getFeedHeader(final UUID feedId) {

        try {
            final FeedHeader header = FEEDS_SERVICE.loadFeedHeader(feedId);
            final FeedHeaderResponse response = FeedHeaderResponse.convert(header);

            LOGGER.info(format("Header for feed [ %s ] returned", feedId));

            return createJsonResponse(response);
        } catch (ServiceException exception) {
            LOGGER.log(Level.SEVERE, format("Error loading feed [ %s ] header", feedId), exception);

            return createErrorJsonResponse(exception);
        }
    }

    public static ResponseBody getFeed(final UUID feedId) {

        try {
            final Feed feed = FEEDS_SERVICE.getFeed(feedId);
            final String feedAsXml = export(feed.header, feed.items);

            LOGGER.info(format("Feed [ %s ] link [ %s ] items exported. Items count [ %d ]", feedId, feed.header.feedLink, feed.items.size()));

            return new ResponseBody(ContentType.XML, feedAsXml);
        } catch (ServiceException exception) {
            LOGGER.log(Level.SEVERE, format("Error export feed [ %s ]", feedId), exception);

            return createErrorJsonResponse(exception);
        } catch (FeedExporterException exception) {
            LOGGER.log(Level.SEVERE, format("Error export feed [ %s ]", feedId), exception);

            return createErrorJsonResponse(ServiceError.feedExportError(feedId));
        }
    }

    public static ResponseBody clear() {
        FEEDS_SERVICE.clear();

        final String message = "Service cleared";

        LOGGER.info(message);

        return createJsonResponse(create(message));
    }

    private static FeedsService createFeedsService() {
        final Transactions transactions = new GaeRootRepository();
        final UrlFetcher urlFetcher = new GaeUrlFetcher();

        final FeedUpdateTaskRepository feedUpdateTaskRepository = new GaeFeedUpdateTaskRepository();
        final FeedItemsRepository feedItemsRepository = new GaeFeedItemsRepository();
        final FeedHeadersRepository feedHeadersRepository = new GaeFeedHeadersRepository();
        final ReadFeedItemsRepository readFeedItemsRepository = new GaeReadFeedItemsRepository();
        final FeedUpdateTaskSchedulerContextRepository feedUpdateTaskSchedulerContextRepository = new GaeCacheFeedUpdateTaskSchedulerContextRepository();

        return new FeedsService(feedHeadersRepository, feedItemsRepository, feedUpdateTaskRepository, readFeedItemsRepository, feedUpdateTaskSchedulerContextRepository, urlFetcher, transactions);
    }

}