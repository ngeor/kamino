package com.github.ngeor.markdown;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class MarkdownReader {
    public List<Item> read(File file) throws IOException {
        return read(new String(Files.readAllBytes(file.toPath())));
    }

    public List<Item> read(String input) {
        State state = new State();
        input.lines().forEach(state::visit);
        return state.toItems();
    }

    private static final class State {
        private Deque<Section> stack = new LinkedList<>(List.of(new Section(0, null, new ArrayList<>()))) {};

        private List<Item> current() {
            return stack.getLast().contents();
        }

        private void trimTrailingEmptyLinesInCurrent() {
            List<Item> current = current();
            int i = current.size() - 1;
            while (i >= 0 && current.get(i) instanceof Line l && l.line().isBlank()) {
                current.remove(i);
                i--;
            }
        }

        public void visit(String line) {
            Section section = parseSection(line);
            if (section != null) {
                trimTrailingEmptyLinesInCurrent();
                while (stack.getLast().level() >= section.level()) {
                    stack.removeLast();
                }
                current().add(section);
                stack.addLast(section);
            } else {
                if (current().isEmpty() && line.isBlank()) {
                    // ignore leading empty lines
                } else {
                    current().add(new Line(line));
                }
            }
        }

        public List<Item> toItems() {
            return stack.getFirst().contents();
        }

        private Section parseSection(String line) {
            int i = 0;
            while (i < line.length() && line.charAt(i) == '#') {
                i++;
            }
            if (i > 0) {
                return new Section(i, line.substring(i).trim(), new ArrayList<>());
            }

            return null;
        }
    }
}
