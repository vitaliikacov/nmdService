package nmd.rss.reader;

import static nmd.rss.collector.util.Assert.assertStringIsValid;

/**
 * Author : Igor Usenko ( igors48@gmail.com )
 * Date : 01.02.14
 */
public class Category {

    public static final String DEFAULT_CATEGORY_ID = "default";
    public static final Category DEFAULT = new Category(DEFAULT_CATEGORY_ID, DEFAULT_CATEGORY_ID);

    public final String uuid;
    public final String name;

    public Category(final String uuid, final String name) {
        assertStringIsValid(uuid);
        this.uuid = uuid;

        assertStringIsValid(name);
        this.name = name;
    }

}