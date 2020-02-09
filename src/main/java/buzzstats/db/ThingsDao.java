package buzzstats.db;

import java.time.LocalDateTime;
import java.util.List;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

/** Things DAO. */
public interface ThingsDao {
  @SqlQuery("SELECT * FROM things ORDER BY title LIMIT :offset, :row_count")
  @RegisterBeanMapper(ThingEntity.class)
  List<ThingEntity> findOrderByTitle(@Bind("offset") int offset, @Bind("row_count") int rowCount);

  @SqlQuery("SELECT * FROM things ORDER BY comments DESC LIMIT :offset, :row_count")
  @RegisterBeanMapper(ThingEntity.class)
  List<ThingEntity> findOrderByCommentsDesc(
      @Bind("offset") int offset, @Bind("row_count") int rowCount);

  @SqlQuery("SELECT * FROM things ORDER BY score DESC LIMIT :offset, :row_count")
  @RegisterBeanMapper(ThingEntity.class)
  List<ThingEntity> findOrderByScoreDesc(
      @Bind("offset") int offset, @Bind("row_count") int rowCount);

  @SqlQuery("SELECT * FROM things ORDER BY published_at DESC LIMIT :offset, :row_count")
  @RegisterBeanMapper(ThingEntity.class)
  List<ThingEntity> findOrderByPublishedAtDesc(
      @Bind("offset") int offset, @Bind("row_count") int rowCount);

  @SqlQuery("SELECT * FROM things ORDER BY last_checked_at LIMIT :offset, :row_count")
  @RegisterBeanMapper(ThingEntity.class)
  List<ThingEntity> findOrderByLastCheckedAt(
      @Bind("offset") int offset, @Bind("row_count") int rowCount);

  @SqlQuery("SELECT * FROM things ORDER BY last_checked_at DESC LIMIT :offset, :row_count")
  @RegisterBeanMapper(ThingEntity.class)
  List<ThingEntity> findOrderByLastCheckedAtDesc(
      @Bind("offset") int offset, @Bind("row_count") int rowCount);

  @SqlQuery("SELECT * FROM things ORDER BY last_modified_at DESC LIMIT :offset, :row_count")
  @RegisterBeanMapper(ThingEntity.class)
  List<ThingEntity> findOrderByLastModifiedAtDesc(
      @Bind("offset") int offset, @Bind("row_count") int rowCount);

  @SqlQuery("SELECT COUNT(*) FROM things")
  long countAll();

  @SqlQuery("SELECT * FROM things WHERE internal_url=?")
  @RegisterBeanMapper(ThingEntity.class)
  List<ThingEntity> findByInternalUrl(String internalUrl);

  @SqlUpdate(
      "INSERT INTO things "
          + "(title, url, score, username, published_at, comments, internal_url, created_at, "
          + "last_modified_at, last_checked_at) "
          + "VALUES (:title, :url, :score, :username, :publishedAt, :comments, :internalUrl, "
          + ":createdAt, :lastModifiedAt, :lastCheckedAt)")
  @GetGeneratedKeys
  long insert(@BindBean ThingEntity thingEntity);

  @SqlUpdate(
      "UPDATE things SET score=:score, comments=:comments, last_modified_at=:lastModifiedAt, "
          + "last_checked_at=:lastCheckedAt WHERE id=:id")
  void update(
      @Bind("score") int score,
      @Bind("comments") int comments,
      @Bind("lastModifiedAt") LocalDateTime lastModifiedAt,
      @Bind("lastCheckedAt") LocalDateTime lastCheckedAt,
      @Bind("id") long id);

  @SqlUpdate("UPDATE things SET last_checked_at=:lastCheckedAt WHERE id=:id")
  void update(@Bind("lastCheckedAt") LocalDateTime lastCheckedAt, @Bind("id") long id);
}
