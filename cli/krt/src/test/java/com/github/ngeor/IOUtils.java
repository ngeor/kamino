package com.github.ngeor;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public final class IOUtils {
    private IOUtils() {}

    public static Path createDirectory(Path parent, String name) {
        Path result = parent.resolve(name);
        File file = result.toFile();
        assertTrue(file.mkdir());
        file.deleteOnExit();
        return result;
    }

    @Deprecated
    public static void deleteDirectory(Path directory) throws IOException {
        List<Path> paths = Files.walk(directory).collect(Collectors.toList());
        for (Path path : paths) {
            File file = path.toFile();
            if (file.isDirectory() && !path.equals(directory)) {
                deleteDirectory(path);
            } else if (file.isFile()) {
                file.delete();
            }
        }
        directory.toFile().delete();
    }
}
