package com.github.ngeor.markdown;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

public final class MarkdownWriter {
    private int previousEmptyLineCount;

    public String write(List<Item> items) throws IOException {
        try (StringWriter writer = new StringWriter()) {
            write(items, writer);
            writer.flush();
            return writer.toString();
        }
    }

    public void write(List<Item> items, File changeLog) throws IOException {
        try (FileWriter writer = new FileWriter(changeLog)) {
            write(items, writer);
        }
    }

    public void write(List<Item> items, Writer writer) throws IOException {
        previousEmptyLineCount = 1;
        doWrite(items, writer);
    }

    private void doWrite(List<Item> items, Writer writer) throws IOException {
        for (Item item : items) {
            if (item instanceof Section section) {
                // ensure blank lines before header
                while (previousEmptyLineCount < 1) {
                    writer.write(System.lineSeparator());
                    previousEmptyLineCount++;
                }

                // reset blank lines after header
                previousEmptyLineCount = 0;
                for (int i = 1; i <= section.level(); i++) {
                    writer.write('#');
                }
                writer.write(' ');
                writer.write(section.title());
                writer.write(System.lineSeparator());
                writer.write(System.lineSeparator());
                previousEmptyLineCount = 1;
                doWrite(section.contents(), writer);
            } else if (item instanceof Line line) {
                previousEmptyLineCount = 0;
                writer.write(line.line());
                writer.write(System.lineSeparator());
            } else {
                throw new IllegalStateException();
            }
        }
    }
}
