/*
 * NewFileItem.java
 *
 * Created on July 21, 2004, 7:57 PM
 */

package org.ngss.jdirdiff;

import java.io.File;

/**
 * @author ngeor
 */
public class NewFileItem extends OneFileItem {

    /**
     * Creates a new instance of NewFileItem.
     */
    public NewFileItem(File file) {
        super(file);
    }

    @Override
    public String toString() {
        return "New File: " + getFile().toString();
    }
}
