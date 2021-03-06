package nmd.orb.services;

import com.google.appengine.api.datastore.Transaction;
import nmd.orb.collector.fetcher.UrlFetcher;
import nmd.orb.error.ServiceException;
import nmd.orb.feed.FeedHeader;
import nmd.orb.feed.FeedItem;
import nmd.orb.reader.FeedItemsComparisonReport;
import nmd.orb.reader.ReadFeedItems;
import nmd.orb.repositories.FeedHeadersRepository;
import nmd.orb.repositories.FeedItemsRepository;
import nmd.orb.repositories.ReadFeedItemsRepository;
import nmd.orb.repositories.Transactions;
import nmd.orb.services.filter.FeedItemReportFilter;
import nmd.orb.services.report.FeedItemReport;
import nmd.orb.services.report.FeedItemsCardsReport;
import nmd.orb.services.report.FeedItemsReport;
import nmd.orb.services.report.FeedReadReport;
import nmd.orb.sources.Source;
import nmd.orb.util.Direction;
import nmd.orb.util.Page;

import java.util.*;

import static nmd.orb.collector.merger.TimestampAscendingComparator.TIMESTAMP_ASCENDING_COMPARATOR;
import static nmd.orb.collector.merger.TimestampDescendingComparator.TIMESTAMP_DESCENDING_COMPARATOR;
import static nmd.orb.feed.FeedHeader.isValidFeedHeaderId;
import static nmd.orb.feed.FeedItem.isValidFeedItemGuid;
import static nmd.orb.reader.FeedItemsComparator.compare;
import static nmd.orb.util.Assert.guard;
import static nmd.orb.util.Page.isValidKeyItemGuid;
import static nmd.orb.util.Parameter.isPositive;
import static nmd.orb.util.Parameter.notNull;
import static nmd.orb.util.TransactionTools.rollbackIfActive;

/**
 * Author : Igor Usenko ( igors48@gmail.com )
 * Date : 02.02.14
 */
public class ReadsService extends AbstractService {

    public static int MAX_SEQUENTIAL_UPDATE_ERRORS_COUNT = 2;

    private final Transactions transactions;
    private final ReadFeedItemsRepository readFeedItemsRepository;

    private final UpdateErrorRegistrationService updateErrorRegistrationService;

    public ReadsService(final FeedHeadersRepository feedHeadersRepository, final FeedItemsRepository feedItemsRepository, final ReadFeedItemsRepository readFeedItemsRepository, final UpdateErrorRegistrationService updateErrorRegistrationService, final UrlFetcher fetcher, final Transactions transactions) {
        super(feedHeadersRepository, feedItemsRepository, fetcher);

        guard(notNull(this.transactions = transactions));
        guard(notNull(this.readFeedItemsRepository = readFeedItemsRepository));
        guard(notNull(this.updateErrorRegistrationService = updateErrorRegistrationService));
    }

    public List<FeedReadReport> getFeedsReadReport() {
        Transaction transaction = null;

        try {
            transaction = this.transactions.beginOne();

            final List<FeedHeader> headers = this.feedHeadersRepository.loadHeaders();
            final List<FeedReadReport> report = new ArrayList<>();

            for (final FeedHeader header : headers) {
                final List<FeedItem> items = this.feedItemsRepository.loadItems(header.id);
                final ReadFeedItems readFeedItems = this.readFeedItemsRepository.load(header.id);
                final int sequentialErrorsCount = this.updateErrorRegistrationService.getErrorCount(header.id);

                final FeedReadReport feedReadReport = createFeedReadReport(header, items, readFeedItems, sequentialErrorsCount);

                report.add(feedReadReport);
            }

            transaction.commit();

            return report;
        } finally {
            rollbackIfActive(transaction);
        }
    }

    public FeedItemsReport getFeedItemsReport(final UUID feedId, final FeedItemReportFilter filter) throws ServiceException {
        guard(isValidFeedHeaderId(feedId));

        Transaction transaction = null;

        try {
            transaction = this.transactions.beginOne();

            final FeedHeader header = loadFeedHeader(feedId);

            final ArrayList<FeedItemReport> feedItemReports = new ArrayList<>();

            final List<FeedItem> feedItems = this.feedItemsRepository.loadItems(feedId);
            final int total = feedItems.size();
            Collections.sort(feedItems, TIMESTAMP_DESCENDING_COMPARATOR);

            final FeedItem topItem = feedItems.isEmpty() ? null : feedItems.get(0);
            final Date topItemDate = topItem == null ? new Date() : topItem.date;

            final ReadFeedItems readFeedItems = this.readFeedItemsRepository.load(feedId);

            int read = 0;
            int notRead = 0;
            int readLater = 0;
            int addedSinceLastView = 0;

            for (final FeedItem feedItem : feedItems) {
                final int index = feedItems.indexOf(feedItem);
                final FeedItemReport feedItemReport = getFeedItemReport(feedId, readFeedItems, feedItem, index, total);

                final boolean acceptable = filter.acceptable(feedItemReport);

                if (acceptable) {
                    feedItemReports.add(feedItemReport);
                }

                if (feedItemReport.read) {
                    ++read;
                } else {
                    ++notRead;
                }

                if (feedItemReport.readLater) {
                    ++readLater;
                }

                if (feedItemReport.addedSinceLastView) {
                    ++addedSinceLastView;
                }
            }

            transaction.commit();

            return new FeedItemsReport(header.id, header.title, header.feedLink, read, notRead, readLater, addedSinceLastView, feedItemReports, readFeedItems.lastUpdate, topItemDate);
        } finally {
            rollbackIfActive(transaction);
        }
    }

    public FeedItemsCardsReport getFeedItemsCardsReport(final UUID feedId, final String itemId, final int size, final Direction direction) throws ServiceException {
        guard(isValidFeedHeaderId(feedId));
        guard(isValidKeyItemGuid(itemId));
        guard(isPositive(size));
        guard(notNull(direction));

        Transaction transaction = null;

        try {
            transaction = this.transactions.beginOne();

            final FeedHeader header = loadFeedHeader(feedId);

            final ArrayList<FeedItemReport> feedItemReports = new ArrayList<>();

            final List<FeedItem> feedItems = this.feedItemsRepository.loadItems(feedId);
            final int total = feedItems.size();

            Collections.sort(feedItems, TIMESTAMP_DESCENDING_COMPARATOR);
            final Page<FeedItem> page = Page.create(feedItems, itemId, size, direction);

            final ReadFeedItems readFeedItems = this.readFeedItemsRepository.load(feedId);

            for (final FeedItem feedItem : page.items) {
                final int index = feedItems.indexOf(feedItem);
                final FeedItemReport feedItemReport = getFeedItemReport(feedId, readFeedItems, feedItem, index, total);

                feedItemReports.add(feedItemReport);
            }

            transaction.commit();

            return new FeedItemsCardsReport(header.id, header.title, page.first, page.last, feedItemReports);
        } finally {
            rollbackIfActive(transaction);
        }
    }

    public void toggleReadLaterItemMark(final UUID feedId, final String itemId) throws ServiceException {
        guard(isValidFeedHeaderId(feedId));
        guard(isValidFeedItemGuid(itemId));

        Transaction transaction = null;

        try {
            transaction = this.transactions.beginOne();

            loadFeedHeader(feedId);

            final ReadFeedItems readFeedItems = this.readFeedItemsRepository.load(feedId);
            final Set<String> storedGuids = getStoredGuids(feedId);

            final Set<String> backedReadLaterItemIds = new HashSet<>();
            backedReadLaterItemIds.addAll(readFeedItems.readLaterItemIds);
            backedReadLaterItemIds.retainAll(storedGuids);

            if (storedGuids.contains(itemId)) {

                if (backedReadLaterItemIds.contains(itemId)) {
                    backedReadLaterItemIds.remove(itemId);
                } else {
                    backedReadLaterItemIds.add(itemId);
                }

                final ReadFeedItems updatedReadFeedItems = new ReadFeedItems(feedId, readFeedItems.lastUpdate, readFeedItems.readItemIds, backedReadLaterItemIds, readFeedItems.categoryId);
                this.readFeedItemsRepository.store(updatedReadFeedItems);
            }

            transaction.commit();
        } finally {
            rollbackIfActive(transaction);
        }
    }

    public void markItemAsRead(final UUID feedId, final String itemId) throws ServiceException {
        guard(isValidFeedHeaderId(feedId));
        guard(isValidFeedItemGuid(itemId));

        Transaction transaction = null;

        try {
            transaction = this.transactions.beginOne();

            loadFeedHeader(feedId);

            final List<FeedItem> items = this.feedItemsRepository.loadItems(feedId);

            final FeedItem feedItem = find(itemId, items);

            if (feedItem == null) {
                return;
            }

            final Set<String> storedGuids = getStoredGuids(items);
            final ReadFeedItems readFeedItems = this.readFeedItemsRepository.load(feedId);

            final Set<String> readGuids = new HashSet<>();
            readGuids.addAll(readFeedItems.readItemIds);
            readGuids.add(itemId);

            final Set<String> readLaterGuids = new HashSet<>();
            readLaterGuids.addAll(readFeedItems.readLaterItemIds);
            readLaterGuids.remove(itemId);

            final FeedItemsComparisonReport comparisonReport = compare(readGuids, storedGuids);

            final Date lastUpdate = readFeedItems.lastUpdate.compareTo(feedItem.date) > 0 ? readFeedItems.lastUpdate : feedItem.date;
            final ReadFeedItems updatedReadFeedItems = new ReadFeedItems(feedId, lastUpdate, comparisonReport.readItems, readLaterGuids, readFeedItems.categoryId);

            this.readFeedItemsRepository.store(updatedReadFeedItems);

            transaction.commit();
        } finally {
            rollbackIfActive(transaction);
        }
    }

    public void markAllItemsAsRead(final UUID feedId, long topItemTimestamp) throws ServiceException {
        guard(isValidFeedHeaderId(feedId));
        guard(isPositive(topItemTimestamp));

        Transaction transaction = null;

        try {
            transaction = this.transactions.beginOne();

            loadFeedHeader(feedId);

            final Set<String> readGuids = new HashSet<>();
            final Set<String> readLaterGuids = new HashSet<>();

            final List<FeedItem> items = this.feedItemsRepository.loadItems(feedId);
            final FeedItem youngest = findYoungest(items);
            final Set<String> storedGuids = getStoredGuids(items, topItemTimestamp);
            readGuids.addAll(storedGuids);

            final ReadFeedItems readFeedItems = this.readFeedItemsRepository.load(feedId);
            readLaterGuids.addAll(readFeedItems.readLaterItemIds);

            final Date youngestDate = youngest == null ? new Date() : youngest.date;
            final Date lastUpdate = topItemTimestamp == 0 ? youngestDate : new Date(topItemTimestamp);

            final ReadFeedItems updatedReadFeedItems = new ReadFeedItems(feedId, lastUpdate, readGuids, readLaterGuids, readFeedItems.categoryId);

            this.readFeedItemsRepository.store(updatedReadFeedItems);

            transaction.commit();
        } finally {
            rollbackIfActive(transaction);
        }
    }

    private Set<String> getStoredGuids(final UUID feedId) {
        final List<FeedItem> items = this.feedItemsRepository.loadItems(feedId);

        return getStoredGuids(items);
    }

    public static FeedReadReport createFeedReadReport(final FeedHeader header, final List<FeedItem> items, final ReadFeedItems readFeedItems, final int sequentialErrorsCount) {
        guard(notNull(header));
        guard(notNull(items));
        guard(notNull(readFeedItems));

        final Set<String> storedGuids = getStoredGuids(items);

        final FeedItemsComparisonReport comparisonReport = compare(readFeedItems.readItemIds, storedGuids);

        final FeedItem topItem = findFirstNotReadFeedItem(items, readFeedItems.readItemIds, readFeedItems.lastUpdate);
        final String topItemId = topItem == null ? null : topItem.guid;
        final String topItemLink = topItem == null ? null : topItem.gotoLink;

        final int addedFromLastVisit = countYoungerItems(items, readFeedItems.lastUpdate);

        final int readLaterItemsCount = countReadLaterItems(items, readFeedItems.readLaterItemIds);

        final Source feedType = Source.detect(header.feedLink);

        final boolean hasErrors = sequentialErrorsCount >= MAX_SEQUENTIAL_UPDATE_ERRORS_COUNT;

        return new FeedReadReport(header.id, feedType, header.title, comparisonReport.readItems.size(), comparisonReport.newItems.size(), readLaterItemsCount, addedFromLastVisit, topItemId, topItemLink, readFeedItems.lastUpdate, hasErrors);
    }

    public static FeedItem findFirstNotReadFeedItem(final List<FeedItem> items, final Set<String> readGuids, final Date lastViewedItemTime) {
        guard(notNull(items));
        guard(notNull(readGuids));
        guard(notNull(lastViewedItemTime));

        final List<FeedItem> notReads = findNotReadItems(items, readGuids);

        Collections.sort(notReads, TIMESTAMP_ASCENDING_COMPARATOR);

        if (!readGuids.isEmpty()) {

            for (final FeedItem candidate : notReads) {
                final boolean youngerThanLastViewedItem = candidate.date.compareTo(lastViewedItemTime) > 0;

                if (youngerThanLastViewedItem) {
                    return candidate;
                }
            }
        }

        return notReads.isEmpty() ? null : notReads.get(notReads.size() - 1);
    }

    public static FeedItemReport getFeedItemReport(final UUID feedId, final ReadFeedItems readFeedItems, final FeedItem feedItem, final int index, final int total) {
        final boolean readItem = readFeedItems.readItemIds.contains(feedItem.guid);
        final boolean readLaterItem = readFeedItems.readLaterItemIds.contains(feedItem.guid);
        final boolean addedSinceLastView = readFeedItems.lastUpdate.compareTo(feedItem.date) < 0;

        return new FeedItemReport(feedId, feedItem.title, feedItem.description, feedItem.gotoLink, feedItem.date, feedItem.guid, readItem, readLaterItem, addedSinceLastView, index, total);
    }

    private static List<FeedItem> findNotReadItems(List<FeedItem> items, Set<String> readGuids) {
        final List<FeedItem> notReads = new ArrayList<>();

        for (final FeedItem candidate : items) {
            final boolean notRead = !readGuids.contains(candidate.guid);

            if (notRead) {
                notReads.add(candidate);
            }
        }
        return notReads;
    }

    private static int countYoungerItems(final List<FeedItem> items, final Date lastUpdate) {
        int count = 0;

        for (final FeedItem item : items) {

            if (item.date.compareTo(lastUpdate) > 0) {
                ++count;
            }
        }

        return count;
    }

    private static int countReadLaterItems(final List<FeedItem> items, final Set<String> readLaterItemIds) {
        int count = 0;

        for (final FeedItem item : items) {

            if (readLaterItemIds.contains(item.guid)) {
                ++count;
            }
        }

        return count;
    }

    private static Set<String> getStoredGuids(final List<FeedItem> items) {
        return getStoredGuids(items, 0);
    }

    private static Set<String> getStoredGuids(final List<FeedItem> items, final long beforeTimestamp) {
        final boolean filtered = beforeTimestamp != 0;
        final Date beforeDate = new Date(beforeTimestamp);

        final Set<String> storedGuids = new HashSet<>();

        for (final FeedItem item : items) {
            final boolean canBeAdded = !filtered || (item.date.compareTo(beforeDate) <= 0);

            if (canBeAdded) {
                storedGuids.add(item.guid);
            }
        }

        return storedGuids;
    }

    private static FeedItem findYoungest(final List<FeedItem> items) {

        if (items.isEmpty()) {
            return null;
        }

        FeedItem youngest = items.get(0);

        for (final FeedItem item : items) {

            if (item.date.getTime() > youngest.date.getTime()) {
                youngest = item;
            }
        }

        return youngest;
    }

    private static FeedItem find(String itemId, List<FeedItem> items) {

        for (final FeedItem candidate : items) {

            if (candidate.guid.equals(itemId)) {
                return candidate;
            }
        }

        return null;
    }

}
