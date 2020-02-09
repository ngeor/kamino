package buzzstats.jobs;

import java.io.IOException;
import java.util.List;
import org.jdbi.v3.core.Jdbi;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import buzzstats.db.ScansDao;
import buzzstats.db.ThingEntity;
import buzzstats.db.ThingsDao;

/**
 * Updates the story which hasn't been checked the most.
 */
public class OldestCheckedStoryJob implements Job {
    private static final Logger LOGGER = LoggerFactory.getLogger(OldestCheckedStoryJob.class);
    private final Parser parser = new Parser();
    private final Updater updater = new Updater();

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        LOGGER.info("begin");
        Jdbi jdbi = null;
        try {
            jdbi = (Jdbi) context.getScheduler().getContext().get("jdbi");
        } catch (SchedulerException e) {
            throw new JobExecutionException(e);
        }

        ThingsDao thingsDao = jdbi.onDemand(ThingsDao.class);
        ScansDao scansDao = jdbi.onDemand(ScansDao.class);

        List<ThingEntity> things = thingsDao.findOrderByLastCheckedAt(0, 1);
        for (ThingEntity thing : things) {
            try {
                process(thingsDao, thing, scansDao);
            } catch (IOException e) {
                throw new JobExecutionException(e);
            }
        }

        LOGGER.info("end");
    }

    private void process(
        ThingsDao thingsDao, ThingEntity thing, ScansDao scansDao
    ) throws JobExecutionException, IOException {
        LOGGER.info("Processing {} {}", thing.getId(), thing.getTitle());
        String url = "https://news.ycombinator.com/" + thing.getInternalUrl();
        Document document = Jsoup.connect(url).get();
        Element thingElement = document.selectFirst("tr.athing");
        if (thingElement == null) {
            throw new JobExecutionException("Could not parse page " + url);
        }

        ThingEntity parsed = parser.parse(thingElement);
        if (parsed == null) {
            throw new JobExecutionException("Could not parse page " + url);
        }

        updater.update(
            thing,
            parsed,
            thingsDao,
            scansDao,
            LOGGER
        );
    }
}
