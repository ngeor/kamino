package buzzstats.resources;

import buzzstats.api.Thing;
import buzzstats.db.ThingEntity;
import buzzstats.db.ThingsDao;
import buzzstats.views.ThingsView;
import com.codahale.metrics.annotation.Timed;
import java.util.List;
import java.util.stream.Collectors;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.jdbi.v3.core.Jdbi;

/** A resource for things. */
@Path("/things")
@Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_HTML})
public class ThingsResource {
    private static final int ROW_COUNT = 100;
    private final Jdbi jdbi;

    public ThingsResource(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    /** Gets all things. */
    @GET
    @Timed
    public ThingsView getAll() {
        ThingsDao thingsDao = jdbi.onDemand(ThingsDao.class);
        List<ThingEntity> entities = thingsDao.findOrderByTitle(0, ROW_COUNT);
        List<Thing> models = entities.stream().map(ThingEntity::toThing).collect(Collectors.toList());
        ThingsView view = new ThingsView();
        view.setThings(models);
        return view;
    }
}
