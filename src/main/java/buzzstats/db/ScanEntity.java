package buzzstats.db;

import java.time.LocalDateTime;

/** Stores information about a story scan. */
public class ScanEntity {
  private long id;
  private long thingId;
  private int oldScore;
  private int newScore;
  private int oldComments;
  private int newComments;
  private boolean hadChanges;
  private LocalDateTime createdAt;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public long getThingId() {
    return thingId;
  }

  public void setThingId(long thingId) {
    this.thingId = thingId;
  }

  public int getOldScore() {
    return oldScore;
  }

  public void setOldScore(int oldScore) {
    this.oldScore = oldScore;
  }

  public int getNewScore() {
    return newScore;
  }

  public void setNewScore(int newScore) {
    this.newScore = newScore;
  }

  public int getOldComments() {
    return oldComments;
  }

  public void setOldComments(int oldComments) {
    this.oldComments = oldComments;
  }

  public int getNewComments() {
    return newComments;
  }

  public void setNewComments(int newComments) {
    this.newComments = newComments;
  }

  public boolean isHadChanges() {
    return hadChanges;
  }

  public void setHadChanges(boolean hadChanges) {
    this.hadChanges = hadChanges;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }
}
