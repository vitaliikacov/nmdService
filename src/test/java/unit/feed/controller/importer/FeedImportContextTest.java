package unit.feed.controller.importer;

import nmd.orb.services.importer.FeedImportContext;
import nmd.orb.services.importer.FeedImportTaskStatus;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author : igu
 */
public class FeedImportContextTest {

    public static final String HTTP_DOMAIN_COM = "http://domain.com";
    public static final String TITLE = "title";

    @Test
    public void taskCanBeExecutedWhenItHasWaitingStatus() {
        final FeedImportContext context = new FeedImportContext(HTTP_DOMAIN_COM, TITLE, 4, FeedImportTaskStatus.WAITING);

        assertTrue(context.canBeExecuted());
    }

    @Test
    public void taskCanNotBeExecutedWhenItHasErrorStatusAndSomeTriesLeft() {
        final FeedImportContext context = new FeedImportContext("http://domain.com", "title", 1, FeedImportTaskStatus.ERROR);

        assertTrue(context.canBeExecuted());
    }

    @Test
    public void taskCanNotBeExecutedWhenItHasNoTriesLeft() {
        final FeedImportContext context = new FeedImportContext("http://domain.com", "title", 0, FeedImportTaskStatus.WAITING);

        assertFalse(context.canBeExecuted());
    }

    @Test
    public void taskCanNotBeExecutedWhenItHasCompletedStatus() {
        final FeedImportContext context = new FeedImportContext("http://domain.com", "title", 4, FeedImportTaskStatus.COMPLETED);

        assertFalse(context.canBeExecuted());
    }

    @Test
    public void taskCanNotBeExecutedWhenItHasFailedStatus() {
        final FeedImportContext context = new FeedImportContext("http://domain.com", "title", 4, FeedImportTaskStatus.FAILED);

        assertFalse(context.canBeExecuted());
    }

    @Test
    public void taskCanNotBeExecutedWhenItHasErrorStatusAndNoTriesLeft() {
        final FeedImportContext context = new FeedImportContext("http://domain.com", "title", 0, FeedImportTaskStatus.ERROR);

        assertFalse(context.canBeExecuted());
    }

}
