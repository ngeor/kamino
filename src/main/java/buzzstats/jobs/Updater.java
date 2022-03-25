package buzzstats.jobs;

import buzzstats.db.ScanEntity;
import buzzstats.db.ScansDao;
import buzzstats.db.ThingEntity;
import buzzstats.db.ThingsDao;
import org.slf4j.Logger;

import java.time.Clock;
import java.time.LocalDateTime;

/** Updates existing stories. */
public class Updater {
    /** Updates existing stories. */
    public void update(
        ThingEntity existing, ThingEntity parsed, ThingsDao thingsDao, ScansDao scansDao, Logger logger) {
        boolean hadChanges = existing.getScore() != parsed.getScore() || existing.getComments() != parsed.getComments();
        if (hadChanges) {
            logger.info("Story had changes {} {} {}", existing.getId(), existing.getInternalUrl(), existing.getTitle());
            thingsDao.update(
                parsed.getScore(),
                parsed.getComments(),
                LocalDateTime.now(Clock.systemUTC()),
                LocalDateTime.now(Clock.systemUTC()),
                existing.getId());
        } else {
            logger.info(
                "Story had no changes {} {} {}", existing.getId(), existing.getInternalUrl(), existing.getTitle());
            thingsDao.update(LocalDateTime.now(Clock.systemUTC()), existing.getId());
        }

        ScanEntity scanEntity = new ScanEntity();
        scanEntity.setThingId(existing.getId());
        scanEntity.setOldScore(existing.getScore());
        scanEntity.setNewScore(parsed.getScore());
        scanEntity.setOldComments(existing.getComments());
        scanEntity.setNewComments(parsed.getComments());
        scanEntity.setHadChanges(hadChanges);
        scanEntity.setCreatedAt(LocalDateTime.now(Clock.systemUTC()));
        scansDao.insert(scanEntity);
    }
}
