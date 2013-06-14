package nmd.rss.collector.controller;

import nmd.rss.collector.Transactions;
import nmd.rss.collector.feed.*;
import nmd.rss.collector.gae.feed.FeedServiceImpl;
import nmd.rss.collector.scheduler.FeedUpdateTask;
import nmd.rss.collector.scheduler.FeedUpdateTaskRepository;
import nmd.rss.collector.updater.FeedHeadersRepository;
import nmd.rss.collector.updater.FeedItemsRepository;
import nmd.rss.collector.updater.UrlFetcher;
import nmd.rss.collector.updater.UrlFetcherException;

import javax.persistence.EntityTransaction;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static nmd.rss.collector.util.Assert.assertNotNull;
import static nmd.rss.collector.util.Assert.assertStringIsValid;
import static nmd.rss.collector.util.TransactionTools.rollbackIfActive;

/**
 * Author : Igor Usenko ( igors48@gmail.com )
 * Date : 22.05.13
 */
public class ControlService {

    private static final int MAX_FEED_ITEMS_COUNT = 10;

    private final Transactions transactions;

    private final FeedHeadersRepository feedHeadersRepository;
    private final FeedItemsRepository feedItemsRepository;
    private final FeedUpdateTaskRepository feedUpdateTaskRepository;

    private final UrlFetcher fetcher;

    public ControlService(final Transactions transactions, final FeedHeadersRepository feedHeadersRepository, final FeedItemsRepository feedItemsRepository, final FeedUpdateTaskRepository feedUpdateTaskRepository, final UrlFetcher fetcher) {
        assertNotNull(transactions);
        this.transactions = transactions;

        assertNotNull(feedHeadersRepository);
        this.feedHeadersRepository = feedHeadersRepository;

        assertNotNull(feedItemsRepository);
        this.feedItemsRepository = feedItemsRepository;

        assertNotNull(feedUpdateTaskRepository);
        this.feedUpdateTaskRepository = feedUpdateTaskRepository;

        assertNotNull(fetcher);
        this.fetcher = fetcher;
    }

    public UUID addFeed(final String feedUrl) throws ControllerException {
        assertStringIsValid(feedUrl);

        EntityTransaction transaction = null;

        final Feed feed = fetchFeed(feedUrl);

        try {
            transaction = this.transactions.getOne();
            transaction.begin();

            final String feedUrlInLowerCase = feedUrl.toLowerCase();
            FeedHeader feedHeader = this.feedHeadersRepository.loadHeader(feedUrlInLowerCase);

            final UUID feedId;

            if (feedHeader == null) {
                feedId = UUID.randomUUID();
                feedHeader = feed.header;

                this.feedHeadersRepository.storeHeader(feedHeader);
            } else {
                feedId = feedHeader.id;
            }

            List<FeedItem> olds = getFeedOldItems(feedHeader);

            createFeedUpdateTask(feedId, feedHeader);

            final FeedItemsMergeReport mergeReport = FeedItemsMerger.merge(olds, feed.items, MAX_FEED_ITEMS_COUNT);
            FeedServiceImpl.updateFeedItems(feedId, mergeReport.retained, mergeReport.added, this.feedItemsRepository);

            transaction.commit();

            return feedId;
        } finally {
            rollbackIfActive(transaction);
        }
    }

    public boolean removeFeed(final UUID feedId) throws ControllerException {
        assertNotNull(feedId);

        EntityTransaction transaction = null;

        try {
            transaction = this.transactions.getOne();
            transaction.begin();

            this.feedUpdateTaskRepository.deleteTaskForFeedId(feedId);
            this.feedHeadersRepository.deleteHeader(feedId);
            this.feedItemsRepository.deleteItems(feedId);

            transaction.commit();

            return true;
        } catch (final Exception exception) {
            return false;
        } finally {
            rollbackIfActive(transaction);
        }
    }

    private void createFeedUpdateTask(final UUID feedId, final FeedHeader feedHeader) {
        FeedUpdateTask feedUpdateTask = null;

        if (feedHeader != null) {
            feedUpdateTask = this.feedUpdateTaskRepository.loadTaskForFeedId(feedHeader.id);
        }

        if (feedUpdateTask == null) {
            feedUpdateTask = new FeedUpdateTask(UUID.randomUUID(), feedId, MAX_FEED_ITEMS_COUNT);
            this.feedUpdateTaskRepository.storeTask(feedUpdateTask);
        }
    }

    private List<FeedItem> getFeedOldItems(final FeedHeader feedHeader) {
        return feedHeader == null ? new ArrayList<FeedItem>() : this.feedItemsRepository.loadItems(feedHeader.id);
    }

    private Feed fetchFeed(String feedUrl) throws ControllerException {

        try {
            final String data = this.fetcher.fetch(feedUrl);

            return FeedParser.parse(feedUrl, data);
        } catch (UrlFetcherException | FeedParserException exception) {
            throw new ControllerException(exception);
        }
    }

}