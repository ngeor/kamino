/*
 * Node.java
 *
 * Created on July 5, 2004, 7:11 PM
 */

package org.jcms.model;

import java.util.Date;

/**
 * @author ngeor
 */
public class Node extends IDEntity {
    private NodeType type;
    private String title;
    private User owner;
    private Date creationDate;

    /**
     * This variable holds the children of this node. It is used as a caching variable.
     * It is not used to store children in the database.
     */
    private Node[] children;
    private Node[] parents;

    /**
     * Creates a new instance of Node.
     */
    public Node() {
        super();
    }

    public Node(int id) {
        super(id);
    }

    public NodeType getType() {
        return type;
    }

    public void setType(NodeType type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Node[] getChildren() {
        return children;
    }

    public void setChildren(Node[] children) {
        this.children = children;
    }

    public Node[] getParents() {
        return parents;
    }

    public void setParents(Node[] parents) {
        this.parents = parents;
    }
}
