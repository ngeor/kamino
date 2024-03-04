package com.github.ngeor.maven.document.loader;

import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.io.File;

/**
 * An object that can load a Maven POM document from a file.
 */
public interface DocumentLoader {

    // TODO return an immutable version of DocumentWrapper
    DocumentWrapper loadDocument();

    /**
     * Gets the POM file.
     * This is used to resolve a parent POM via the relative path.
     */
    File getPomFile();
}
