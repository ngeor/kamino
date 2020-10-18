/*
 * NodeWriter.java
 *
 * Created on July 13, 2004, 11:28 PM
 */

package org.jcms.xml;

import java.io.Writer;
import org.jcms.model.Node;

/**
 * @author ngeor
 */
public class NodeWriter extends XMLWriter {

    /**
     * Creates a new instance of NodeWriter.
     */
    public NodeWriter(Writer out) {
        super(out);
    }

    protected void writeNodeAttributes(Node node) throws java.io.IOException {
        writeAttribute("id", node.getId());
        writeAttribute("title", node.getTitle());
    }

    /**
     * Writes the node.
     * @param node
     * @throws java.io.IOException
     */
    public void writeNode(Node node) throws java.io.IOException {
        Node[] children = node.getChildren();
        Node[] parents = node.getParents();
        boolean hasSubNodes = (children != null && children.length > 0) || (parents != null && parents.length > 0);
        write("<");
        write(node.getType().getName());
        writeNodeAttributes(node);

        if (hasSubNodes) {
            write(">");
        }

        if (children != null) {
            for (int i = 0; i < children.length; i++) {
                writeNode(children[i]);
            }
        }

        if (parents != null) {
            for (int i = 0; i < parents.length; i++) {
                write("<parent>");
                writeNode(parents[i]);
                write("</parent>");
            }
        }

        if (hasSubNodes) {
            write("</" + node.getType().getName() + ">");
        } else {
            write(" />");
        }
    }
}
