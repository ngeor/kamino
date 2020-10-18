/*
 * XMLWriter.java
 *
 * Created on July 11, 2004, 1:45 PM
 */

package org.jcms.xml;

import java.io.FilterWriter;
import java.io.Writer;

/**
 * @author ngeor
 */
public abstract class XMLWriter extends FilterWriter {

    /**
     * Creates a new instance of XMLWriter.
     */
    public XMLWriter(Writer out) {
        super(out);
    }

    /**
     * Writes an attribute.
     * @param name
     * @param value
     * @throws java.io.IOException
     */
    protected void writeAttribute(String name, String value) throws java.io.IOException {
        write(" ");
        write(name);
        write("=\"");
        write(value);
        write("\"");
    }

    protected void writeAttribute(String name, int value) throws java.io.IOException {
        writeAttribute(name, String.valueOf(value));
    }
}
