package unit.feed.twitter;

import org.junit.Test;

import static nmd.orb.sources.twitter.TwitterClientTools.getTwitterUserName;
import static nmd.orb.sources.twitter.TwitterClientTools.isItTwitterUrl;
import static org.junit.Assert.*;

/**
 * Author : Igor Usenko ( igors48@gmail.com )
 * Date : 02.03.14
 */
public class TwitterClientToolsTest {

    @Test
    public void twitterUrlDetection() {
        assertFalse(isItTwitterUrl(""));
        assertFalse(isItTwitterUrl(null));
        assertTrue(isItTwitterUrl("https://twitter.com/adme_ru"));
        assertTrue(isItTwitterUrl("https://mobile.twitter.com/adme_ru"));
        assertTrue(isItTwitterUrl("   https://mobile.twitter.com/adme_ru   "));
    }

    @Test
    public void twitterUserNameExtraction() {
        assertEquals("YourName", getTwitterUserName("http://twitter.com/#!/YourName/status/01234567890123456"));
        assertEquals("YourName", getTwitterUserName("https://twitter.com/#!/YourName/status/01234567890123456"));
        assertEquals("YourName", getTwitterUserName("http://twitter.com/YourName/status/01234567890123456"));
        assertEquals("YourName", getTwitterUserName("http://twitter.com/YourName"));
        assertEquals("YourName", getTwitterUserName("http://twitter.com/YourName/"));
        assertEquals("YourName", getTwitterUserName("http://twitter.com/YourName?source=internet"));
        assertEquals("YourName", getTwitterUserName("   http://twitter.com/YourName?source=internet    "));

        assertEquals("YourName", getTwitterUserName("http://mobile.twitter.com/#!/YourName/status/01234567890123456"));
        assertEquals("YourName", getTwitterUserName("https://mobile.twitter.com/#!/YourName/status/01234567890123456"));
        assertEquals("YourName", getTwitterUserName("http://mobile.twitter.com/YourName/status/01234567890123456"));
        assertEquals("YourName", getTwitterUserName("http://mobile.twitter.com/YourName"));
        assertEquals("YourName", getTwitterUserName("http://mobile.twitter.com/YourName/"));
        assertEquals("YourName", getTwitterUserName("http://mobile.twitter.com/YourName?source=internet"));
        assertEquals("YourName", getTwitterUserName("  http://mobile.twitter.com/YourName?source=internet     "));

        assertNull(getTwitterUserName("http://twitter.com/"));
        assertNull(getTwitterUserName("http://mobile.twitter.com/"));
        assertNull(getTwitterUserName("http://twitter.com/#!"));
        assertNull(getTwitterUserName("http://mobile.twitter.com/#!"));
        assertNull(getTwitterUserName("http://twitter.com/#!/"));
        assertNull(getTwitterUserName("http://mobile.twitter.com/#!/"));
        assertNull(getTwitterUserName("http://twitter.com"));
        assertNull(getTwitterUserName("http://mobile.twitter.com"));
        assertNull(getTwitterUserName("http://domain.com/"));
        assertNull(getTwitterUserName(""));
        assertNull(getTwitterUserName(null));

    }

}
