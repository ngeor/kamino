package com.github.ngeor.markdown;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class MarkdownReader {
    public static Markdown read(File file) throws IOException {
        return read(new String(Files.readAllBytes(file.toPath())));
    }

    public static Markdown read(String input) {
        String header = "";
        List<Markdown.Section> sections = new ArrayList<>();
        String sectionTile = null;
        String sectionBody = "";

        int i = 0;
        while (i < input.length()) {
            int newLineIndex = input.indexOf('\n', i);
            String line = newLineIndex >= i ? input.substring(i, newLineIndex + 1) : input.substring(i);
            i = newLineIndex >= i ? newLineIndex + 1 : input.length();

            if (line.startsWith("## ")) {
                // finish previous section
                if (sectionTile != null) {
                    sections.add(new Markdown.Section(sectionTile, sectionBody));
                }

                sectionTile = line.substring("## ".length()).stripTrailing();
                sectionBody = "";
            } else {
                if (sectionTile == null) {
                    header += line;
                } else {
                    sectionBody += line;
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
