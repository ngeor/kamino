/*
 * Role.java
 *
 * Created on July 5, 2004, 7:15 PM
 */

package org.jcms.model;

/**
 * @author ngeor
 */
public class Role extends IDEntity {
    private int mask;
    private String title;

    /**
     * Creates a new instance of Role.
     */
    public Role() {
        super();
    }

    public Role(int id) {
        super(id);
    }

    public int getMask() {
        return mask;
    }

    public void setMask(int mask) {
        this.mask = mask;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
