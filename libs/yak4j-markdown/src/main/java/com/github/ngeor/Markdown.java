package com.github.ngeor;

import java.util.List;

public record Markdown(String header, List<Section> sections) {
    public record Section(String title, String body) {}
}
