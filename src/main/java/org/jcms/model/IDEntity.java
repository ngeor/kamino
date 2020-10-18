/*
 * IDEntity.java
 *
 * Created on July 5, 2004, 7:03 PM
 */

package org.jcms.model;

/**
 * @author ngeor
 */
public abstract class IDEntity {
    private int id;

    /**
     * Creates a new instance of IDEntity.
     */
    public IDEntity() {
        this.id = 0;
    }

    public IDEntity(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
