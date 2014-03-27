package nmd.rss.collector.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Pattern;

import static java.util.UUID.fromString;

/**
 * Author : Igor Usenko ( igors48@gmail.com )
 * Date : 28.04.13
 */
public final class Parameter {

    private static final Pattern FILE_NAME_CHARS = Pattern.compile("[_a-zA-Z0-9\\-\\.]+");

    public static boolean isContainOnlyFileNameChars(final String value) {
        return FILE_NAME_CHARS.matcher(value).matches();
    }

    public static boolean isValidUuid(final String value) {

        try {
            fromString(value);

            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    public static boolean isValidString(final String value) {
        return value != null && !value.isEmpty();
    }

    public static boolean isPositive(final long value) {
        return value >= 0;
    }

    public static boolean notNull(final Object value) {
        return value != null;
    }

    public static boolean isValidUrl(final String value) {

        if (!isValidString(value)) {
            return false;
        }

        try {
            new URI(value);

            return true;
        } catch (URISyntaxException e) {
            return false;
        }
    }

    private Parameter() {
        // empty
    }

}
