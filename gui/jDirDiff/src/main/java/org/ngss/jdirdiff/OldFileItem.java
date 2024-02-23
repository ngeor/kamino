/*
 * OldFileItem.java
 *
 * Created on July 21, 2004, 7:56 PM
 */

package org.ngss.jdirdiff;

import java.io.File;

/**
 * @author ngeor
 */
public class OldFileItem extends OneFileItem {

    /**
     * Creates a new instance of OldFileItem.
     */
    public OldFileItem(File file) {
        super(file);
    }

    @Override
    public String toString() {
        return "Deleted File: " + getFile().toString();
    }
}
