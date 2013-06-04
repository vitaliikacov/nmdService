package nmd.rss.collector.feed;

import java.util.UUID;

import static nmd.rss.collector.util.Assert.*;

/**
 * Author : Igor Usenko ( igors48@gmail.com )
 * Date : 28.04.13
 */
public class FeedHeader {

    public final UUID id;
    public final String feedLink;
    public final String title;
    public final String description;
    public final String link;

    public FeedHeader(final UUID id, final String feedLink, final String title, final String description, final String link) {
        assertNotNull(id);
        this.id = id;

        assertValidUrl(feedLink);
        this.feedLink = feedLink;

        assertStringIsValid(title);
        this.title = title;

        assertStringIsValid(title);
        this.description = description;

        assertValidUrl(link);
        this.link = link;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FeedHeader header = (FeedHeader) o;

        if (!description.equals(header.description)) return false;
        if (!feedLink.equals(header.feedLink)) return false;
        if (!id.equals(header.id)) return false;
        if (!link.equals(header.link)) return false;
        if (!title.equals(header.title)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();

        result = 31 * result + feedLink.hashCode();
        result = 31 * result + title.hashCode();
        result = 31 * result + description.hashCode();
        result = 31 * result + link.hashCode();

        return result;
    }

}
