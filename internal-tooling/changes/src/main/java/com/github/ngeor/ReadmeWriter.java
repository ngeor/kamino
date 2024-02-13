package com.github.ngeor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

public class ReadmeWriter {
    public static String write(Readme readme, String lineSeparator) throws IOException {
        try (StringWriter writer = new StringWriter()) {
            write(readme, writer, lineSeparator);
            writer.flush();
            return writer.toString();
        }
    }

    public static void write(Readme readme, File changeLog) throws IOException {
        try (FileWriter writer = new FileWriter(changeLog)) {
            write(readme, writer, System.lineSeparator());
        }
    }

    public static void write(Readme readme, Writer writer, String lineSeparator) throws IOException {
        writer.write(readme.header());
        for (Readme.Section section : readme.sections()) {
            writer.write("## ");
            writer.write(section.title());
            writer.write(lineSeparator);
            writer.write(section.body());
        }
    }
}
