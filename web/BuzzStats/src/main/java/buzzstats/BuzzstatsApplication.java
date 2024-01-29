package buzzstats;

import buzzstats.jobs.CrawlerJob;
import buzzstats.jobs.OldestCheckedStoryJob;
import buzzstats.resources.IndexResource;
import buzzstats.resources.ThingsResource;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.jdbi3.JdbiFactory;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;
import org.jdbi.v3.core.Jdbi;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Map;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

/** Main application. */
public class BuzzstatsApplication extends Application<BuzzstatsConfiguration> {
    public static void main(final String[] args) throws Exception {
        new BuzzstatsApplication().run(args);
    }

    @Override
    public String getName() {
        return "buzzstats";
    }

    @Override
    public void initialize(final Bootstrap<BuzzstatsConfiguration> bootstrap) {
        // configure Java 8 date-time for Jackson
        bootstrap.getObjectMapper().registerModule(new JavaTimeModule());
        bootstrap.getObjectMapper().disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // db migrations
        bootstrap.addBundle(new MigrationsBundle<BuzzstatsConfiguration>() {
            @Override
            public PooledDataSourceFactory getDataSourceFactory(BuzzstatsConfiguration configuration) {
                return configuration.getDataSourceFactory();
            }
        });

        // views
        bootstrap.addBundle(new AssetsBundle());
        bootstrap.addBundle(new ViewBundle<BuzzstatsConfiguration>() {
            @Override
            public Map<String, Map<String, String>> getViewConfiguration(BuzzstatsConfiguration configuration) {
                return configuration.getViewRendererConfiguration();
            }
        });
    }

    @Override
    public void run(final BuzzstatsConfiguration configuration, final Environment environment)
        throws SchedulerException {

        // register db
        final JdbiFactory jdbiFactory = new JdbiFactory();
        final Jdbi jdbi               = jdbiFactory.build(environment, configuration.getDataSourceFactory(), "h2");

        // register resources
        registerResources(environment, jdbi);

        // start scheduler
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        scheduler.start();
        scheduler.getContext().put("jdbi", jdbi);

        scheduleJob(scheduler, "crawler", configuration.getCrawlerInterval(), CrawlerJob.class);
        scheduleJob(
            scheduler,
            "oldestCheckedStory",
            configuration.getOldestCheckedStoryInterval(),
            OldestCheckedStoryJob.class);
        // scheduler.shutdown();
    }

    private void scheduleJob(Scheduler scheduler, String name, int intervalInSeconds, Class<? extends Job> jobClass)
        throws SchedulerException {
        JobDetail job = newJob(jobClass).withIdentity(name).build();

        Trigger trigger = newTrigger()
                              .withIdentity(name + "Trigger")
                              .startNow()
                              .withSchedule(simpleSchedule().withIntervalInSeconds(intervalInSeconds).repeatForever())
                              .build();

        scheduler.scheduleJob(job, trigger);
    }

    private void registerResources(Environment environment, Jdbi jdbi) {
        environment.jersey().register(new IndexResource(jdbi));
        environment.jersey().register(new ThingsResource(jdbi));
    }
}
