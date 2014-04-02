package nmd.rss.collector.util;

import static nmd.rss.collector.util.Assert.guard;
import static nmd.rss.collector.util.Parameter.isPositive;
import static nmd.rss.collector.util.Parameter.notNull;

/**
 * Author : Igor Usenko ( igors48@gmail.com )
 * Date : 29.03.2014
 */
public final class StringTools {

    public static String cutTo(final String string, final int size) {
        guard(notNull(string));
        guard(isPositive(size));

        return string.length() > size ? string.substring(0, size) : string;
    }

}