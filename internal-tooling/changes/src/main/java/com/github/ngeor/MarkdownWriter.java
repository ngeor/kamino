package com.github.ngeor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

public class MarkdownWriter {
    public static String write(Markdown markdown, String lineSeparator) throws IOException {
        try (StringWriter writer = new StringWriter()) {
            write(markdown, writer, lineSeparator);
            writer.flush();
            return writer.toString();
        }
    }

    public static void write(Markdown markdown, File changeLog) throws IOException {
        try (FileWriter writer = new FileWriter(changeLog)) {
            write(markdown, writer, System.lineSeparator());
        }
    }

    public static void write(Markdown markdown, Writer writer, String lineSeparator) throws IOException {
        writer.write(markdown.header());
        for (Markdown.Section section : markdown.sections()) {
            writer.write("## ");
            writer.write(section.title());
            writer.write(lineSeparator);
            writer.write(section.body());
        }
    }
}
