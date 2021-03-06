package nmd.orb.services.report;

import java.util.Date;
import java.util.UUID;

/**
 * Author : Igor Usenko ( igors48@gmail.com )
 * Date: 13.12.13
 */
public class FeedItemReport {

    public final UUID feedId;
    public final String title;
    public final String description;
    public final String link;
    public final Date date;
    public final String itemId;
    public final boolean read;
    public final boolean readLater;
    public final boolean addedSinceLastView;
    public final int index;
    public final int total;

    public FeedItemReport(final UUID feedId, final String title, final String description, final String link, final Date date, final String itemId, final boolean read, final boolean readLater, final boolean addedSinceLastView, final int index, final int total) {
        this.feedId = feedId;
        this.title = title;
        this.description = description;
        this.link = link;
        this.date = date;
        this.itemId = itemId;
        this.read = read;
        this.readLater = readLater;
        this.addedSinceLastView = addedSinceLastView;
        this.index = index;
        this.total = total;
    }

}