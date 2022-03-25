package buzzstats;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import java.util.Map;

/** Configuration of the app. */
public class BuzzstatsConfiguration extends Configuration {
    @Valid
    @NotNull
    private DataSourceFactory database = new DataSourceFactory();

    @Valid
    @NotNull
    private int crawlerInterval = 10;

    @Valid
    @NotNull
    private int oldestCheckedStoryInterval = 10;

    @Valid
    @NotNull
    private Map<String, Map<String, String>> viewRendererConfiguration;

    @JsonProperty("database")
    public DataSourceFactory getDataSourceFactory() {
        return database;
    }

    @JsonProperty("database")
    public void setDataSourceFactory(DataSourceFactory factory) {
        this.database = factory;
    }

    @JsonProperty("crawlerInterval")
    public int getCrawlerInterval() {
        return crawlerInterval;
    }

    @JsonProperty("crawlerInterval")
    public void setCrawlerInterval(int crawlerInterval) {
        this.crawlerInterval = crawlerInterval;
    }

    @JsonProperty("oldestCheckedStoryInterval")
    public int getOldestCheckedStoryInterval() {
        return oldestCheckedStoryInterval;
    }

    @JsonProperty("oldestCheckedStoryInterval")
    public void setOldestCheckedStoryInterval(int oldestCheckedStoryInterval) {
        this.oldestCheckedStoryInterval = oldestCheckedStoryInterval;
    }

    @JsonProperty("views")
    public Map<String, Map<String, String>> getViewRendererConfiguration() {
        return viewRendererConfiguration;
    }

    @JsonProperty("views")
    public void setViewRendererConfiguration(Map<String, Map<String, String>> views) {
        this.viewRendererConfiguration = views;
    }
}
