/*
 * NodeType.java
 *
 * Created on July 6, 2004, 3:42 PM
 */

package org.jcms.model;

/**
 * @author ngeor
 */
public class NodeType {
    private int type;
    private String classname;
    private String name;

    /**
     * Creates a new instance of NodeType.
     */
    public NodeType(int type, String classname, String name) {
        this.type = type;
        this.classname = classname;
        this.name = name;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getClassname() {
        return this.classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof NodeType) {
            NodeType nt = (NodeType) obj;
            return nt.getType() == getType();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return type;
    }

    /**
     * Creates a new node.
     * @return
     */
    public Node newInstance() {
        try {
            Node n = (Node) Class.forName(classname).newInstance();
            n.setType(this);
            return n;
        } catch (ReflectiveOperationException ex) {
            throw new IllegalArgumentException("Misconfiguration." + ex.toString());
        }
    }

}
