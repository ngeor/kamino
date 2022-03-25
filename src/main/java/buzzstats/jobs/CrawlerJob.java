package buzzstats.jobs;

import buzzstats.db.ScanEntity;
import buzzstats.db.ScansDao;
import buzzstats.db.ThingEntity;
import buzzstats.db.ThingsDao;
import org.jdbi.v3.core.Jdbi;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/** A job that crawls the homepage of HackerNews. */
public class CrawlerJob implements Job {
    private static final Logger LOGGER = LoggerFactory.getLogger(CrawlerJob.class);
    private final Parser parser        = new Parser();
    private final Updater updater      = new Updater();

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
        ScansDao scansDao   = jdbi.onDemand(ScansDao.class);

        try {
            Document document = Jsoup.connect("https://news.ycombinator.com/").get();
            Elements things   = document.select("tr.athing");
            for (Element thingElement : things) {
                ThingEntity parsed = parser.parse(thingElement);
                if (parsed != null) {
                    ThingEntity existing =
                        thingsDao.findByInternalUrl(parsed.getInternalUrl()).stream().findFirst().orElse(null);

                    if (existing == null) {
                        LOGGER.info("new story {} {}", parsed.getInternalUrl(), parsed.getTitle());
                        long thingId = thingsDao.insert(parsed);

                        ScanEntity scanEntity = new ScanEntity();
                        scanEntity.setThingId(thingId);
                        scanEntity.setOldScore(parsed.getScore());
                        scanEntity.setNewScore(parsed.getScore());
                        scanEntity.setOldComments(parsed.getComments());
                        scanEntity.setNewComments(parsed.getComments());
                        scanEntity.setHadChanges(true);
                        scanEntity.setCreatedAt(parsed.getCreatedAt());
                        scansDao.insert(scanEntity);
                    } else {
                        updater.update(existing, parsed, thingsDao, scansDao, LOGGER);
                    }
                }
            }
        } catch (IOException e) {
            throw new JobExecutionException(e);
        }
        LOGGER.info("end");
    }
}
