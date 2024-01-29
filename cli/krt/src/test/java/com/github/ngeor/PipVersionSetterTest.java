package com.github.ngeor;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class PipVersionSetterTest {
    private Path tempDirectory;
    private PipVersionSetter pipVersionSetter;

    @BeforeEach
    void setup() throws IOException {
        tempDirectory = Files.createTempDirectory("pip");
        pipVersionSetter = new PipVersionSetter(tempDirectory);
    }

    @AfterEach
    void tearDown() throws IOException {
        IOUtils.deleteDirectory(tempDirectory);
    }

    @Test
    void test() throws IOException, InterruptedException {
        // arrange
        Files.writeString(
            tempDirectory.resolve("setup.cfg"),
            Stream.of(
                "[metadata]",
                "name = instarepo",
                "version = attr: instarepo.__version__"
            ).collect(Collectors.joining(System.lineSeparator())));
        Path moduleDirectory = tempDirectory.resolve("instarepo");
        moduleDirectory.toFile().mkdir();
        Path initPy = moduleDirectory.resolve("__init__.py");
        Files.writeString(
            initPy,
            "__version__ = \"0.1.1\""
        );

        // act
        pipVersionSetter.bumpVersion("0.1.2");

        // assert
        String newContents = Files.readString(initPy);
        assertEquals("__version__ = \"0.1.2\"" + System.lineSeparator(), newContents);
    }
}
