/*
 * ChangedFileItem.java
 *
 * Created on July 22, 2004, 7:30 PM
 */

package org.ngss.jdirdiff;

import java.io.File;

/**
 * @author ngeor
 */
public class ChangedFileItem extends OneFileItem {

    /**
     * Creates a new instance of ChangedFileItem.
     */
    public ChangedFileItem(File file) {
        super(file);
    }

    @Override
    public String toString() {
        return "Changed File: " + getFile().toString();
    }
}
