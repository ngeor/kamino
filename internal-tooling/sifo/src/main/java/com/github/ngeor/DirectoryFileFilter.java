package com.github.ngeor;

import java.io.File;
import java.io.FileFilter;

public final class DirectoryFileFilter implements FileFilter {
    @Override
    public boolean accept(File pathname) {
        return pathname.isDirectory()
                && !pathname.isHidden()
                && !pathname.getName().startsWith(".");
    }
}
