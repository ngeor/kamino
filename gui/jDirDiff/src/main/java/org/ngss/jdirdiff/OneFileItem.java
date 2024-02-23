/*
 * OneFileItem.java
 *
 * Created on July 21, 2004, 7:54 PM
 */

package org.ngss.jdirdiff;

import java.io.File;

/**
 * @author ngeor
 */
public class OneFileItem {
    private File file;

    /**
     * Creates a new instance of OneFileItem.
     */
    public OneFileItem(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    @Override
    public String toString() {
        return file.toString();
    }
}
