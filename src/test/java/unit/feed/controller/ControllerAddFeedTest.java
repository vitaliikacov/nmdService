package unit.feed.controller;

import nmd.orb.error.ServiceException;
import nmd.orb.feed.FeedHeader;
import nmd.orb.reader.Category;
import nmd.orb.services.report.CategoryReport;
import nmd.orb.services.report.FeedReadReport;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.UUID;

import static nmd.orb.reader.Category.MAIN_CATEGORY_ID;
import static org.junit.Assert.*;

/**
 * Author : Igor Usenko ( igors48@gmail.com )
 * Date : 25.05.13
 */
public class ControllerAddFeedTest extends AbstractControllerTestBase {

    public static final String FEED_TITLE = "feedTitle";

    @Test
    public void whenFeedFetchedOkAndParsedOkItAdds() throws ServiceException {
        final UUID id = addValidFirstRssFeedToMainCategory();

        assertNotNull(id);
    }

    @Test
    public void whenFeedWithSameLinkAddedSecondTimeThenPreviousIdReturns() throws ServiceException {
        final UUID firstId = addValidFirstRssFeedToMainCategory();
        final UUID secondId = addValidFirstRssFeedToMainCategory();

        assertEquals(firstId, secondId);
    }

    @Test
    public void whenFeedWithSameLinkButInDifferentCaseAddedSecondTimeThenPreviousIdReturns() throws ServiceException {
        this.fetcherStub.setData(FIRST_VALID_RSS_FEED);

        final UUID firstId = this.feedsService.addFeed(VALID_FIRST_RSS_FEED_LINK.toUpperCase(), MAIN_CATEGORY_ID);
        final UUID secondId = this.feedsService.addFeed(VALID_FIRST_RSS_FEED_LINK, MAIN_CATEGORY_ID);

        assertEquals(firstId, secondId);
    }

    @Test
    public void whenFeedWithSameLinkButWithSlashAtTheEndAddedSecondTimeThenPreviousIdReturns() throws ServiceException {
        this.fetcherStub.setData(FIRST_VALID_RSS_FEED);

        final UUID firstId = this.feedsService.addFeed(VALID_FIRST_RSS_FEED_LINK.toUpperCase(), MAIN_CATEGORY_ID);
        final UUID secondId = this.feedsService.addFeed(VALID_FIRST_RSS_FEED_LINK + "/", MAIN_CATEGORY_ID);

        assertEquals(firstId, secondId);
    }

    @Test(expected = ServiceException.class)
    public void whenFeedCanNotBeParsedThenExceptionOccurs() throws ServiceException {
        this.fetcherStub.setData(INVALID_RSS_FEED);

        this.feedsService.addFeed(VALID_FIRST_RSS_FEED_LINK, MAIN_CATEGORY_ID);
    }

    @Test
    public void whenFeedAddedThenNewUpdateTaskCreates() throws ServiceException {
        final UUID id = addValidFirstRssFeedToMainCategory();

        assertNotNull(this.feedUpdateTaskRepositoryStub.loadTaskForFeedId(id));
    }

    @Test
    public void whenFeedAddedThenItIsRegistered() throws ServiceException {
        final UUID feedId = addValidFirstRssFeedToMainCategory();
        final FeedHeader feedHeader = this.feedHeadersRepositoryStub.loadHeader(feedId);

        Mockito.verify(this.changeRegistrationServiceSpy).registerAddFeed(feedHeader.title, Category.MAIN.name);
    }

    @Test
    public void whenFeedAddedThenItHeaderStores() throws ServiceException {
        final UUID id = addValidFirstRssFeedToMainCategory();

        assertNotNull(this.feedHeadersRepositoryStub.loadHeader(id));
    }

    @Test
    public void whenFeedAddedThenItItemsStores() throws ServiceException {
        final UUID id = addValidFirstRssFeedToMainCategory();

        assertNotNull(this.feedItemsRepositoryStub.loadItems(id));
    }

    @Test
    public void whenFeedAddedThenReadItemsCreatedForIt() throws ServiceException {
        final UUID id = addValidFirstRssFeedToMainCategory();

        assertNotNull(this.readFeedItemsRepositoryStub.load(id));
    }

    @Test
    public void whenFeedAddedToCategoryThenItIsBecameMemberOfIt() throws ServiceException {
        final Category category = this.categoriesService.addCategory("new");

        final UUID feedId = addValidFirstRssFeed(category.uuid);

        final List<CategoryReport> categoryReports = this.categoriesService.getCategoriesReport();
        final CategoryReport report = findForCategory(category.uuid, categoryReports);

        assertNotNull(findForFeed(feedId, report.feedReadReports));
    }

    @Test(expected = ServiceException.class)
    public void whenFeedIsAddedToNotExistentCategoryThenExceptionWillBeThrown() throws ServiceException {
        addValidFirstRssFeed(UUID.randomUUID().toString());
    }

    @Test
    public void whenFeedCreatedThenCategoryAndTitleSetCorrectly() throws ServiceException {
        final Category category = this.categoriesService.addCategory("new");
        addValidFirstRssFeed(FEED_TITLE, category.uuid);

        final List<CategoryReport> categoryReports = this.categoriesService.getCategoriesReport();
        final CategoryReport categoryReport = findForCategory(category.uuid, categoryReports);
        final FeedReadReport feedReadReport = categoryReport.feedReadReports.get(0);

        assertEquals(FEED_TITLE, feedReadReport.feedTitle);
    }

    @Test
    public void whenFeedAddedWithoutTitleThenOriginalTitleIsUsed() throws ServiceException {
        this.fetcherStub.setData(FIRST_VALID_RSS_FEED);

        final UUID feedId = this.feedsService.addFeed(VALID_FIRST_RSS_FEED_LINK, "", MAIN_CATEGORY_ID);
        final FeedHeader feedHeader = this.feedHeadersRepositoryStub.loadHeader(feedId);

        assertFalse(feedHeader.title.isEmpty());
    }

    @Test
    public void whenFeedAddedWithTitleThenThisTitleIsUsed() throws ServiceException {
        this.fetcherStub.setData(FIRST_VALID_RSS_FEED);

        final UUID feedId = this.feedsService.addFeed(VALID_FIRST_RSS_FEED_LINK, "name", MAIN_CATEGORY_ID);
        final FeedHeader feedHeader = this.feedHeadersRepositoryStub.loadHeader(feedId);

        assertEquals("name", feedHeader.title);
    }

}
