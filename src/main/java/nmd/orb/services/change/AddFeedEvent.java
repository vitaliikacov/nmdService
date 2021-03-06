package nmd.orb.services.change;

import nmd.orb.feed.FeedHeader;
import nmd.orb.reader.Category;

import java.util.Objects;

import static nmd.orb.util.Assert.guard;

/**
 * @author : igu
 */
public class AddFeedEvent implements Event {

    private String feedTitle;
    private String categoryName;

    private AddFeedEvent() {
    }

    public AddFeedEvent(final String feedTitle, final String categoryName) {
        setFeedTitle(feedTitle);
        setCategoryName(categoryName);
    }

    public String getFeedTitle() {
        return this.feedTitle;
    }

    private void setFeedTitle(final String feedTitle) {
        guard(FeedHeader.isValidFeedHeaderTitle(this.feedTitle = feedTitle));
    }

    public String getCategoryName() {
        return this.categoryName;
    }

    private void setCategoryName(final String categoryName) {
        guard(Category.isValidCategoryName(this.categoryName = categoryName));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AddFeedEvent that = (AddFeedEvent) o;
        return Objects.equals(feedTitle, that.feedTitle) &&
                Objects.equals(categoryName, that.categoryName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(feedTitle, categoryName);
    }

}
