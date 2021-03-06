package unit.feed.controller.importer;

import nmd.orb.services.importer.CategoryImportContext;
import nmd.orb.services.importer.CategoryImportTaskStatus;
import nmd.orb.services.importer.FeedImportContext;
import nmd.orb.services.importer.FeedImportTaskStatus;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Created by igor on 02.12.2014.
 */
public class CategoryImportContextTest {

    public static final String CATEGORY_NAME = "categoryName";

    @Test
    public void completedContextCanNotBeExecuted() {
        final CategoryImportContext context = create(CategoryImportTaskStatus.COMPLETED);

        assertFalse(context.canBeExecuted());
    }

    @Test
    public void failedContextCanNotBeExecuted() {
        final CategoryImportContext context = create(CategoryImportTaskStatus.FAILED);

        assertFalse(context.canBeExecuted());
    }

    @Test
    public void firstExecutableTaskFound() {
        final CategoryImportContext context = create(CategoryImportTaskStatus.FEEDS_WITH_ERROR_IMPORT,
                FeedImportContextTest.create(0, FeedImportTaskStatus.COMPLETED),
                FeedImportContextTest.create(1, FeedImportTaskStatus.WAITING),
                FeedImportContextTest.create(0, FeedImportTaskStatus.FAILED),
                FeedImportContextTest.create(1, FeedImportTaskStatus.ERROR));

        assertEquals(FeedImportTaskStatus.WAITING, context.findFirstExecutableTask(FeedImportTaskStatus.WAITING).getStatus());
        assertEquals(FeedImportTaskStatus.ERROR, context.findFirstExecutableTask(FeedImportTaskStatus.ERROR).getStatus());
    }

    public static CategoryImportContext create(final CategoryImportTaskStatus status, FeedImportContext... contexts) {
        final List<FeedImportContext> feedImportContexts = Arrays.asList(contexts);

        return new CategoryImportContext(CATEGORY_NAME, feedImportContexts, status);
    }

}
