package com.github.ngeor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class MarkdownReader {
    public static Markdown read(File file) throws IOException {
        return read(new String(Files.readAllBytes(file.toPath())), System.lineSeparator());
    }

    public static Markdown read(String input, String lineSeparator) {
        String header = "";
        List<Markdown.Section> sections = new ArrayList<>();
        String sectionTile = null;
        String sectionBody = "";
        for (String line : input.lines().toList()) {
            if (line.startsWith("## ")) {
                // finish previous section
                if (sectionTile != null) {
                    sections.add(new Markdown.Section(sectionTile, sectionBody));
                }

                sectionTile = line.substring("## ".length());
                sectionBody = "";
            } else {
                if (sectionTile == null) {
                    header += line + lineSeparator;
                } else {
                    sectionBody += line + lineSeparator;
                }
            }
        }

        // finish last section
        if (sectionTile != null) {
            sections.add(new Markdown.Section(sectionTile, sectionBody));
        }

        return new Markdown(header, sections);
    }
}
