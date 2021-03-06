package nmd.orb.gae.repositories.converters;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Text;
import nmd.orb.gae.repositories.converters.helpers.CategoryImportContextHelper;
import nmd.orb.services.importer.CategoryImportContext;
import nmd.orb.services.importer.ImportJobContext;
import nmd.orb.services.importer.ImportJobStatus;

import java.util.List;

import static nmd.orb.gae.repositories.datastore.Kind.IMPORT;
import static nmd.orb.util.Assert.assertNotNull;

/**
 * @author : igu
 */
public class ImportJobContextConverter {

    private static final String STATUS = "status";
    private static final String CONTEXTS = "contexts";

    public static Entity convert(final ImportJobContext context, final Key key) {
        assertNotNull(context);
        assertNotNull(key);

        final Entity entity = new Entity(IMPORT.value, key);

        entity.setProperty(STATUS, context.getStatus().toString());
        entity.setProperty(CONTEXTS, new Text(CategoryImportContextHelper.convert(context.getContexts())));

        return entity;
    }

    public static ImportJobContext convert(final Entity entity) {
        assertNotNull(entity);

        final ImportJobStatus status = ImportJobStatus.valueOf((String) entity.getProperty(STATUS));
        final List<CategoryImportContext> contexts = CategoryImportContextHelper.convert(((Text) entity.getProperty(CONTEXTS)).getValue());

        return new ImportJobContext(contexts, status);
    }

}
