package com.github.ngeor.markdown;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

public class MarkdownWriter {
    public static String write(Markdown markdown) throws IOException {
        try (StringWriter writer = new StringWriter()) {
            write(markdown, writer);
            writer.flush();
            return writer.toString();
        }
    }

    public static void write(Markdown markdown, File changeLog) throws IOException {
        try (FileWriter writer = new FileWriter(changeLog)) {
            write(markdown, writer);
        }
    }

    public static void write(Markdown markdown, Writer writer) throws IOException {
        writer.write(markdown.header());
        for (Markdown.Section section : markdown.sections()) {
            writer.write("## ");
            writer.write(section.title());
            writer.write(System.lineSeparator());
            writer.write(section.body());
        }
    }
}
