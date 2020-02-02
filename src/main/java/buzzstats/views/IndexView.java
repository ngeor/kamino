package buzzstats.views;

import java.util.List;

import buzzstats.api.Thing;
import io.dropwizard.views.View;

public class IndexView extends View {
    private long totalCount;
    private List<Thing> mostVoted;
    private List<Thing> mostDiscussed;
    private List<Thing> mostRecent;
    private List<Thing> oldestChecked;
    private List<Thing> recentlyChecked;
    private List<Thing> lastModified;

    public IndexView() {
        super("index.mustache");
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public List<Thing> getMostVoted() {
        return mostVoted;
    }

    public void setMostVoted(List<Thing> mostVoted) {
        this.mostVoted = mostVoted;
    }

    public List<Thing> getMostDiscussed() {
        return mostDiscussed;
    }

    public void setMostDiscussed(List<Thing> mostDiscussed) {
        this.mostDiscussed = mostDiscussed;
    }

    public List<Thing> getMostRecent() {
        return mostRecent;
    }

    public void setMostRecent(List<Thing> mostRecent) {
        this.mostRecent = mostRecent;
    }

    public List<Thing> getOldestChecked() {
        return oldestChecked;
    }

    public void setOldestChecked(List<Thing> oldestChecked) {
        this.oldestChecked = oldestChecked;
    }

    public List<Thing> getRecentlyChecked() {
        return recentlyChecked;
    }

    public void setRecentlyChecked(List<Thing> recentlyChecked) {
        this.recentlyChecked = recentlyChecked;
    }

    public List<Thing> getLastModified() {
        return lastModified;
    }

    public void setLastModified(List<Thing> lastModified) {
        this.lastModified = lastModified;
    }
}
