package buzzstats.resources;

import buzzstats.db.ThingEntity;
import buzzstats.db.ThingsDao;
import buzzstats.views.IndexView;
import com.codahale.metrics.annotation.Timed;
import java.util.stream.Collectors;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.jdbi.v3.core.Jdbi;

/** Main resource of the application. */
@Path("/")
@Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_HTML})
public class IndexResource {
    private static final int ROW_COUNT = 10;
    private final Jdbi jdbi;

    public IndexResource(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    /** Gets all things. */
    @GET
    @Timed
    public IndexView getHomePage() {
        ThingsDao thingsDao = jdbi.onDemand(ThingsDao.class);
        IndexView view      = new IndexView();
        view.setTotalCount(thingsDao.countAll());
        view.setMostDiscussed(thingsDao.findOrderByCommentsDesc(0, ROW_COUNT)
                                  .stream()
                                  .map(ThingEntity::toThing)
                                  .collect(Collectors.toList()));
        view.setMostVoted(thingsDao.findOrderByScoreDesc(0, ROW_COUNT)
                              .stream()
                              .map(ThingEntity::toThing)
                              .collect(Collectors.toList()));
        view.setMostRecent(thingsDao.findOrderByPublishedAtDesc(0, ROW_COUNT)
                               .stream()
                               .map(ThingEntity::toThing)
                               .collect(Collectors.toList()));
        view.setOldestChecked(thingsDao.findOrderByLastCheckedAt(0, ROW_COUNT)
                                  .stream()
                                  .map(ThingEntity::toThing)
                                  .collect(Collectors.toList()));
        view.setRecentlyChecked(thingsDao.findOrderByLastCheckedAtDesc(0, ROW_COUNT)
                                    .stream()
                                    .map(ThingEntity::toThing)
                                    .collect(Collectors.toList()));
        view.setLastModified(thingsDao.findOrderByLastModifiedAtDesc(0, ROW_COUNT)
                                 .stream()
                                 .map(ThingEntity::toThing)
                                 .collect(Collectors.toList()));
        return view;
    }
}
