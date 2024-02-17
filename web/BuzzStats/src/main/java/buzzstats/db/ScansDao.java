package buzzstats.db;

import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

/** DAO for story scans. */
public interface ScansDao {
    @SqlUpdate("INSERT INTO scans "
            + "(thing_id, old_score, new_score, old_comments, "
            + "new_comments, had_changes, created_at) "
            + "VALUES (:thingId, :oldScore, :newScore, :oldComments, "
            + ":newComments, :hadChanges, :createdAt)")
    void insert(@BindBean ScanEntity scanEntity);
}
