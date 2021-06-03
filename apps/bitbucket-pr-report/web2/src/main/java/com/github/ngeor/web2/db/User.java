package com.github.ngeor.web2.db;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * User entity.
 */
@Entity
public class User {
    @Id
    private String uuid;
    private String displayName;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
