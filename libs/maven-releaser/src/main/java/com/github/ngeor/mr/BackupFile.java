package com.github.ngeor.mr;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import org.apache.commons.lang3.Validate;

public class BackupFile implements Closeable {
    private final File original;
    private final File backup;

    public BackupFile(File original) throws IOException {
        this.original = Objects.requireNonNull(original);
        Validate.isTrue(original.isFile(), "File %s is not a file or does not exist", original);
        this.backup = File.createTempFile(original.getName(), ".bak");
        backup.deleteOnExit();
        Files.copy(original.toPath(), backup.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    @Override
    public void close() throws IOException {
        Files.copy(backup.toPath(), original.toPath(), StandardCopyOption.REPLACE_EXISTING);
        backup.delete();
    }
}
