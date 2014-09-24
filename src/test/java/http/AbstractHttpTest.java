package http;

import com.google.gson.Gson;
import com.jayway.restassured.response.Response;
import nmd.orb.error.ErrorCode;
import nmd.orb.http.requests.AddFeedRequest;
import nmd.orb.http.responses.*;
import nmd.orb.http.tools.ServletTools;
import org.junit.After;

import static com.jayway.restassured.RestAssured.given;
import static nmd.orb.reader.Category.MAIN_CATEGORY_ID;
import static nmd.orb.util.Parameter.isPositive;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * User: igu
 * Date: 28.11.13
 */
public abstract class AbstractHttpTest {

    protected static final String CLEAR_SERVLET_URL = "/secure/v01/clear/";
    protected static final String FEEDS_SERVLET_URL = "/secure/v01/feeds/";
    protected static final String UPDATES_SERVLET_URL = "/secure/v01/updates/";
    protected static final String READS_SERVLET_URL = "/secure/v01/reads/";
    protected static final String EXPORTS_SERVLET_URL = "/v01/feeds/";
    protected static final String CATEGORIES_SERVLET_URL = "/secure/v01/categories/";

    protected static final String FIRST_FEED_URL = "http://127.0.0.1:8080/feed/feed_win_1251.xml";
    protected static final String FIRST_FEED_TITLE = "Bash.im";
    protected static final String SECOND_FEED_URL = "http://127.0.0.1:8080/feed/feed_win_1251_2.xml";
    protected static final String INVALID_FEED_URL = "http://127.0.0.1:8080/feed/not_exist.xml";
    protected static final String UNREACHABLE_FEED_URL = "http://127.0.0.1:8081/feed/not_exist.xml";

    private static final Gson GSON = new Gson();

    @After
    public void after() {
        clearState();
    }

    protected static void clearState() {
        assertServerProcessingTimeHeaderValid(given().body("").post(CLEAR_SERVLET_URL));
    }

    protected static FeedIdResponse addFirstFeed() {
        return addFeedWithResponse(FIRST_FEED_URL, MAIN_CATEGORY_ID);
    }

    protected static FeedIdResponse addSecondFeed() {
        return addFeedWithResponse(SECOND_FEED_URL, MAIN_CATEGORY_ID);
    }

    protected static FeedIdResponse addFeedWithResponse(final String url, final String categoryId) {
        final String response = addFeed(url, categoryId);

        return GSON.fromJson(assertSuccessResponse(response), FeedIdResponse.class);
    }

    protected static String addFeed(final String url, final String categoryId) {
        final AddFeedRequest addFeedRequest = AddFeedRequest.create(url, categoryId);

        final String requestBody = GSON.toJson(addFeedRequest);

        return assertServerProcessingTimeHeaderValid(given().body(requestBody).post(FEEDS_SERVLET_URL)).asString();
    }

    protected static CategoryResponse addCategoryWithResponse(final String name) {
        return GSON.fromJson(assertSuccessResponse(addCategory(name)), CategoryResponse.class);
    }

    protected static String addCategory(final String name) {
        return assertServerProcessingTimeHeaderValid(given().body(name).post(CATEGORIES_SERVLET_URL)).asString();
    }

    protected static String deleteCategory(final String categoryId) {
        return assertServerProcessingTimeHeaderValid(given().delete(CATEGORIES_SERVLET_URL + categoryId)).asString();
    }

    protected static String renameCategory(final String categoryId, final String newName) {
        return assertServerProcessingTimeHeaderValid(given().body(newName).put(CATEGORIES_SERVLET_URL + categoryId)).asString();
    }

    protected static String assignFeedToCategory(final String categoryId, final String feedId) {
        return assertServerProcessingTimeHeaderValid(given().put(CATEGORIES_SERVLET_URL + categoryId + "/" + feedId)).asString();
    }

    protected static CategoriesReportResponse getCategoriesReport() {
        final String response = assertServerProcessingTimeHeaderValid(given().get(CATEGORIES_SERVLET_URL)).asString();

        return GSON.fromJson(assertSuccessResponse(response), CategoriesReportResponse.class);
    }

    protected static String getCategoryReportAsString(final String categoryId) {
        return assertServerProcessingTimeHeaderValid(given().get(CATEGORIES_SERVLET_URL + categoryId)).asString();
    }

    protected static CategoryReportResponse getCategoryReport(final String categoryId) {
        final String response = assertServerProcessingTimeHeaderValid(given().get(CATEGORIES_SERVLET_URL + categoryId)).asString();

        return GSON.fromJson(assertSuccessResponse(response), CategoryReportResponse.class);
    }

    protected static FeedHeadersResponse getFeedHeaders() {
        final String response = assertServerProcessingTimeHeaderValid(given().get(FEEDS_SERVLET_URL)).asString();

        return GSON.fromJson(assertSuccessResponse(response), FeedHeadersResponse.class);
    }

    protected static FeedHeadersResponse getFeedHeader(final String feedId) {
        final String response = getFeedHeaderAsString(feedId);

        return GSON.fromJson(assertSuccessResponse(response), FeedHeadersResponse.class);
    }

    protected static String getFeedHeaderAsString(String feedId) {
        return assertServerProcessingTimeHeaderValid(given().get(FEEDS_SERVLET_URL + feedId)).asString();
    }

    protected static String deleteFeed(final String feedId) {
        return assertServerProcessingTimeHeaderValid(given().delete(FEEDS_SERVLET_URL + feedId)).asString();
    }

    protected static String updateFeedTitle(final String feedId, final String title) {
        return assertServerProcessingTimeHeaderValid(given().body(title).put(FEEDS_SERVLET_URL + feedId)).asString();
    }

    protected static String exportFeed(final String feedId) {
        return assertServerProcessingTimeHeaderValid(given().get(EXPORTS_SERVLET_URL + feedId)).asString();
    }

    protected static String updateFeed(final String feedId) {
        return assertServerProcessingTimeHeaderValid(given().get(UPDATES_SERVLET_URL + feedId)).asString();
    }

    protected static FeedMergeReportResponse updateFeedWithReport(final String feedId) {
        return GSON.fromJson(assertSuccessResponse(updateFeed(feedId)), FeedMergeReportResponse.class);
    }

    protected static FeedSeriesUpdateResponse updateCurrentFeedWithReport() {
        return GSON.fromJson(assertSuccessResponse(updateFeed("")), FeedSeriesUpdateResponse.class);
    }

    protected String getReadsReportAsString() {
        return assertServerProcessingTimeHeaderValid(given().get(READS_SERVLET_URL)).asString();
    }

    protected FeedItemsCardsReportResponse getReadsCardsReport(final String feedId, final String offset, final String size) {
        return GSON.fromJson(assertSuccessResponse(getReadsCardsReportAsString(feedId, offset, size)), FeedItemsCardsReportResponse.class);
    }

    protected String getReadsCardsReportAsString(final String feedId, final String offset, final String size) {
        return assertServerProcessingTimeHeaderValid(given().get(READS_SERVLET_URL + feedId + "?offset=" + offset + "&size=" + size)).asString();
    }

    protected String getFeedItemsReportAsString(final String feedId) {
        return assertServerProcessingTimeHeaderValid(given().get(READS_SERVLET_URL + feedId)).asString();
    }

    protected FeedReadReportsResponse getReadsReport() {
        return GSON.fromJson(assertSuccessResponse(getReadsReportAsString()), FeedReadReportsResponse.class);
    }

    protected FeedItemsReportResponse getFeedItemsReport(final String feedId) {
        return GSON.fromJson(assertSuccessResponse(getFeedItemsReportAsString(feedId)), FeedItemsReportResponse.class);
    }

    protected String markItem(final String feedId, String itemId, String markMode) {
        final String parameter = markMode.isEmpty() ? "" : "?markAs=" + markMode;

        return assertServerProcessingTimeHeaderValid(given().put(READS_SERVLET_URL + feedId + "/" + itemId + parameter)).asString();
    }

    protected String markItemAsRead(final String feedId, String itemId) {
        return markItem(feedId, itemId, "read");
    }

    protected String markAllItemsAsRead(final String feedId) {
        return markItem(feedId, "", "");
    }

    protected String markItemAsReadLater(final String feedId, String itemId) {
        return markItem(feedId, itemId, "readLater");
    }

    protected static void assertErrorResponse(final String response, final ErrorCode errorCode) {
        final ErrorResponse errorResponse = GSON.fromJson(response, ErrorResponse.class);

        assertEquals(ResponseType.ERROR, errorResponse.getStatus());
        assertEquals(errorCode, errorResponse.error.code);
    }

    protected static String assertSuccessResponse(final String response) {
        final SuccessMessageResponse successResponse = GSON.fromJson(response, SuccessMessageResponse.class);

        assertEquals(ResponseType.SUCCESS, successResponse.getStatus());

        return response;
    }

    private static Response assertServerProcessingTimeHeaderValid(final Response response) {
        final String headerValue = response.getHeader(ServletTools.SERVER_PROCESSING_TIME_HEADER);

        assertTrue(isPositive(Integer.valueOf(headerValue)));

        return response;
    }

}